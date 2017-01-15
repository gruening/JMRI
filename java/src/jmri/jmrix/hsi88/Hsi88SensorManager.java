package jmri.jmrix.hsi88;

import java.util.HashMap;
import jmri.Sensor;
import jmri.jmrix.hsi88.Hsi88Setup.Hsi88Mode;
import jmri.managers.AbstractSensorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manager to deal with the HSI88 interface and convert HSI88 replies to HSI88
 * sensor events.
 */
public class Hsi88SensorManager extends AbstractSensorManager implements Hsi88Listener {

    /** keep connection memo */
    private Hsi88SystemConnectionMemo memo;

    /** store sensor addresses and corresponding sensor objects */
    private HashMap<Integer, Hsi88Sensor> sensors = new HashMap<Integer, Hsi88Sensor>();

    Hsi88SensorManager(Hsi88SystemConnectionMemo memo) {

        this.memo = memo;
        this.memo.getHsi88TrafficController().addHsi88Listener(this);
        log.info("started");
        // probably do the setup of the HSI88 here, ie set chain length, and send v command, and t command
    }

    @Override
    public String getSystemPrefix() {
        return memo.getSystemPrefix();
    }

    @Override
    protected Sensor createNewSensor(String systemName, String userName) {

        // pay-load from systemName.
        String addrString = systemName.substring(this.getSystemPrefix().length() + 1);
        // @todo cannonical way to get legnth of "S" prefix (if that is changeable?  

        int addr = -1;
        try {
            addr = Integer.parseInt(addrString);
        } catch (NumberFormatException e) {
            log.error("Cannot parse pay-load of system name to s88 sensor address: " + addrString);
            return null;
        }

        // module number of this s88 sensor address. Modules number from 1 and contain 16 sensors each.
        int module = (addr / 16) + 1;
        if (module > Hsi88Setup.getNumModules()) {
            log.info("Sensor address beyond range of currently registered s88 modules: " + addr);
        }

        Hsi88Sensor s = sensors.get(addr);
        if (s == null) {
            s = new Hsi88Sensor(systemName, userName);
            sensors.put(addr, s);
        } else {
            log.info("Ignore request to create new sensor: returining existing sensor with address: " + addr);
        }
        return s;
    }

    @Override
    public void notifyMessage(Hsi88Message m) {
        // only listening for *replies*, not messages.
    }

    @Override
    public synchronized void notifyReply(Hsi88Reply r) {

        switch (Character.toLowerCase(r.getOpCode())) {
            case 'i':
            case 'm':
                processSensorReading(r);
                break;
            case 't':
                switchTerminalMode(r);
                break;
            case 's':
                checkNumModules(r);
                break;
            case 'v':
                // Version information.
                log.info(r.toString());
                break;
            default:
                log.warn("Unknown opcode of message: " + r);
        }
    }

    /**
     * parse and act on the payload of a reply to a "t" command.
     * 
     * @param r the HSI88 reply to "t" command.
     */
    private void switchTerminalMode(Hsi88Reply r) {

        if (r.getNumDataElements() != 3) {
            log.warn("Ignored malformatted response to 't' command: " + r);
            return;
        }

        switch ((char) r.getElement(1)) {
            case '0':
                Hsi88Setup.mode = Hsi88Mode.HEX;
                log.info("Switching terminal mode to HEX");
                break;
            case '1':
                Hsi88Setup.mode = Hsi88Mode.ASCII;
                log.info("Switching terminal mode to ASCII");
                break;
            default:
                log.warn("Ignored response to 't' command with wrong parameter: " + r);
        }

    }

    /**
     * parses and acts on the reply to an "s" command.
     * 
     * @param r reply to the "s" command containing the number of modules
     */
    private void checkNumModules(Hsi88Reply r) {

        if (r.getNumDataElements() != 4) { // "sXX\r"
            log.error("Ignored malformatted response to 's' command: " + r);
            return;
        }

        String sensorStr = r.toString().substring(1, 3);

        int i;
        try {
            i = Integer.parseInt(sensorStr, 16);
            log.info("Number of modules: " + i);
        } catch (NumberFormatException e) {
            log.warn("Could not parse: " + sensorStr);
            return;
        }

        if (Hsi88Setup.getNumModules() != i) {
            log.warn("Number of reported modules does not agree with number of registered modules: " + i);
        }

    }

    /**
     * parse and act on the "i" and "m" replies from the HSI88. In particular
     * update the modules for which updates are provided in the replies.
     */
    private void processSensorReading(Hsi88Reply r) {

        int len = r.getNumDataElements() - 1; // ignore opcode
        String s = r.toString();

        // parse and check number of reported modules
        try {
            int nSensors = Integer.parseInt(s.substring(1, 3), 16);
            if (nSensors != Hsi88Setup.getNumModules()) {
                log.info("Number of reported and registered s88 modules does not agree: " + nSensors);
            }
        } catch (NumberFormatException e1) {
            log.warn("Could not parse number of s88 modules: " + s);
        }

        // values of s88 module come in chunks of 6 ascii digits:
        int i;
        for (i = 3; i <= len - 6; i += 6) {

            String moduleUpdate = s.substring(i, i + 6);

            try {
                int v = Integer.parseInt(moduleUpdate, 16);
                int value = v & 0xFFFF;
                int module = (v >> 16) & 0xFF;
                log.info("Update for module " + module + " : " + Integer.toHexString(value));
                updateModule(module, value);
            } catch (NumberFormatException e) {
                log.warn("Ignored malformated part of HSI88 module update: " + moduleUpdate);
            }
        }

        if (i != len) {
            log.warn("Trailing chars of sensor message not parsed: " + s.substring(i));
        }
    }

    /**
     * process update for an s88 module. Specifically notify all sensor in that
     * module.
     * 
     * @param module s88 module number
     * @param value sensor readings of module
     */
    private void updateModule(int module, int value) {

        // address of first sensor for this module
        final int baseAddress = (module - 1) * 16;

        // go through value bit by bit
        for (int addr = baseAddress, mask = 1; addr < baseAddress + 16; addr++, mask <<= 1) {

            Hsi88Sensor sensor = sensors.get(addr);
            if (sensor != null) {
                sensor.setOwnState((mask & value) != 0 ? Sensor.ACTIVE : Sensor.INACTIVE);
            } else {
                // unregistered sensor
                log.info("Unregistered sensor address: " + addr);
                // have we seen this sensor before?
                if (!sensors.containsKey(addr)) {
                    sensors.put(addr, null);
                    log.info("Unseen sensor address seen first time: " + addr);
                }
            }
        }
    }

    private final static Logger log = LoggerFactory.getLogger(Hsi88SensorManager.class.getName());

}
