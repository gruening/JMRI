package jmri.jmrix.hsi88;

import java.util.HashMap;
import jmri.Sensor;
import jmri.jmrix.hsi88.Hsi88Config.Hsi88Protocol;
import jmri.managers.AbstractSensorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manager to deal with the HSI88 interface and convert HSI88 replies to HSI88
 * sensor events.
 * 
 * @author Andre Gruening, Copyright (C) 2017.
 */
public class Hsi88SensorManager extends AbstractSensorManager implements Hsi88Listener {

    /** keep connection memo */
    private Hsi88SystemConnectionMemo memo;

    /** store mapping sensor to corresponding sensor objects */
    private HashMap<Integer, Hsi88Sensor> sensors = new HashMap<Integer, Hsi88Sensor>();

    /** store 16bit state of modules. */
    private char[] moduleStates = new char[Hsi88Config.MAXMODULES];

    /**
     * create a new sensor manager for the Hsi88 interface. It connects to the
     * traffic controller and sets the Hsi88 interface up.
     * 
     * @param memo connection memo
     * 
     * @todo implement more elegant and rigorous way to wait for a reply to the "t" message.
     */
    Hsi88SensorManager(Hsi88SystemConnectionMemo memo) {

        this.memo = memo;
        Hsi88TrafficController tc = memo.getHsi88TrafficController();
        tc.addHsi88Listener(this);

        log.info("Hsi88 Sensor Manager starts.");
        tc.sendHsi88Message(Hsi88Message.cmdVersion(), this);

        // switch to ASCII mode: 
        for (int i = 0; (i < 2) && (Hsi88Config.protocol != Hsi88Protocol.ASCII); i++) {
            tc.sendHsi88Message(Hsi88Message.cmdTerminal(), this);
            try {
                // wait until reply has most probably come.
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // woken up prematurely -- do nothing.
            }
        }

        if (Hsi88Config.protocol != Hsi88Protocol.ASCII) {
            log.warn(Hsi88Config.LONGNAME +
                    " running in " +
                    Hsi88Config.protocol +
                    " mode. Message parsing in this mode is not implemented. Expect loads of errors messages.");
        }

        // stop Hsi88 if it was running -- you never know.
        tc.sendHsi88Message(Hsi88Message.powerOff(), this);
    }

    @Override
    public String getSystemPrefix() {
        return memo.getSystemPrefix();
    }

    /**
     * create a new Hsi88Sensor. Its address on the S88 chain will be derived
     * from systemName.
     * 
     * @param systemName stripped of SystemPrefix and "S" prefix, is parsed into
     *            a nonnegative integer to give sensor address on s88 chain.
     * @param userName merely passed on to ctor of Hsi88Sensor.
     * 
     * @return new Sensor with address as parsed from systemName if no sensor
     *         with the same address existed. Otherwise returns existing sensor
     *         with that address.
     * 
     * @todo find cannonical way to get length of "S" prefix (if that is
     *       changeable.)
     * 
     */
    @Override
    protected Sensor createNewSensor(String systemName, String userName) {

        // get payload from systemName.
        String addrString = systemName.substring(this.getSystemPrefix().length() + 1);

        int addr = -1;
        try {
            addr = Integer.parseInt(addrString);
        } catch (NumberFormatException e) {
            log.error("Cannot parse payload of system name into S88 sensor address:" +
                    addrString +
                    ". Use only numeric payloads that can be parsed into nonnegative integers.");
            return null;
        }

        if (addr < 0) {
            log.error("Hsi88 Sensor cannot have negative address:" + addr);
            return null;
        }

        // module number of this s88 sensor address. Modules number run from 1 and contain 16 sensors each.
        int module = (addr / 16) + 1;

        if (module > Hsi88Config.getSetupModules()) {
            log.info("Sensor address " + addr + " beyond range of registered S88 modules.");
            if (module > Hsi88Config.MAXMODULES) {
                log.error("Sensor address beyond addressable range of Hsi88 Interface. " +
                        "If you really want this address change Hsi88Condif.MAXMODULES in the source code");
                return null;
            }
        }

        // sensor with same address?
        Hsi88Sensor s = sensors.get(addr);
        if (s == null) {
            s = new Hsi88Sensor(systemName, userName, memo);
            sensors.put(addr, s);
        } else {
            log.warn("Ignored request to create new sensor: returning existing sensor with same address: " + addr);
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
                setupModules(r); // @todo change to use getSetupReplyModules?
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
                Hsi88Config.protocol = Hsi88Protocol.HEX;
                log.info("Terminal mode switched to HEX");
                break;
            case '1':
                Hsi88Config.protocol = Hsi88Protocol.ASCII;
                log.info("Terminal mode switch to ASCII");
                break;
            default:
                log.warn("Ignored response to 't' command with unknown parameter: " + r);
        }
    }

    /**
     * parses and acts on the reply to an "s" command.
     * 
     * @param r reply to the "s" command containing the number of modules
     */
    private void setupModules(Hsi88Reply r) {

        if (r.getNumDataElements() != 4) { // "sXX\r"
            log.error("Ignored malformatted response to 's' command: " + r);
            return;
        }

        String sensorStr = r.toString().substring(1, 3);

        int newModules;
        try {
            newModules = Integer.parseInt(sensorStr, 16);
            log.info("Changed number of reported modules: " + newModules);
        } catch (NumberFormatException e) {
            log.warn("Could not parse to nonnegative number: " + sensorStr);
            return;
        }

        if (newModules == 0) {
            log.info("Hsi88 power off.");
            return;
        }

        if (newModules > Hsi88Config.MAXMODULES) {
            log.error("Hsi88 reports more modules than coded in Hsi88Config.MAXMODULES");
            newModules = Hsi88Config.MAXMODULES;
        }

        if (Hsi88Config.getSetupModules() != newModules) {
            log.warn("Number of reported modules " +
                    newModules +
                    " does not agree with number of setup modules: " +
                    Hsi88Config.getSetupModules() +
                    ". Perhaps change Hsi88 setup in JMRI Preferences?");
        }

        Hsi88Config.reportedModules = newModules;
        log.info("Hsi88 power on with " + newModules + " modules.");
    }

    /**
     * parse and act on the "i" and "m" replies from the HSI88. In particular
     * update the modules for which updates are provided in the replies.
     */
    private void processSensorReading(Hsi88Reply r) {

        String payload = r.toString();

        // parse and check number of reported modules
        try {
            int nModules = Integer.parseInt(payload.substring(1, 3), 16);
            if (nModules != Hsi88Config.reportedModules) {
                log.info("Number of modules reported by previous 's' reply " +
                        Hsi88Config.reportedModules +
                        " does not agree with module update sent by 'i' reply:" +
                        nModules);
            }
        } catch (NumberFormatException e1) {
            log.warn("Could not parse number of S88 modules: " + payload);
        }

        int len = r.getNumDataElements() - 1; // ignore opcode

        // values of s88 module come in chunks of 6 ascii digits:
        int i;
        for (i = 3; i <= len - 6; i += 6) {

            String moduleUpdate = payload.substring(i, i + 6);

            try {
                int v = Integer.parseInt(moduleUpdate, 16);
                int value = v & 0xFFFF;
                int module = (v >> 16) & 0xFF;
                updateModule(module, value, r.getOpCode() == 'm');
            } catch (NumberFormatException e) {
                log.warn("Ignored malformated part of HSI88 module update: " + moduleUpdate);
            }
        }

        if (i != len) {
            log.warn("Trailing chars of sensor message not parsed: " + payload.substring(i));
        }
    }

    /**
     * process update for an s88 module. Specifically notify all changed sensors
     * in that module.
     * 
     * @param module s88 module number
     * @param value sensor readings of module
     * @param reportAll 
     */
    private void updateModule(int module, final int value, boolean reportAll) {

        if ((module > Hsi88Config.reportedModules) && (module <= 0)) {
            log.warn("Update for module " +
                    module +
                    " beyond number of " +
                    Hsi88Config.reportedModules +
                    " reported modules.");
            return;
        }

        // index into moduleStates starts at 0 whereas module number starts at 1.
        module--;

        // any bits flipped?
        final char changes = (char) (value ^ moduleStates[module]);
        if (changes == 0) {
            // no? => nothing to do
            return;
        }

        // update module states
        moduleStates[module] ^= changes;

        // address of first sensor for this module
        final int baseAddress = module * 16;

        // go through changes bit by bit
        for (int addr = baseAddress, mask = 1; addr < baseAddress + 16; addr++, mask <<= 1) {
            // has sth changed?
            if (((changes & mask) != 0) || reportAll) {
                Hsi88Sensor sensor = sensors.get(addr);
                if (sensor != null)
                    sensor.setOwnState((mask & value) != 0 ? Sensor.ACTIVE : Sensor.INACTIVE);
                else { // no sensor object -- be nice and inform user nevertheless
                    log.info("Sensor " + addr + ": " + (((mask & value) == 0)? "low." : "high."));
                }
            }
        }
    }

    private final static Logger log = LoggerFactory.getLogger(Hsi88SensorManager.class.getName());

}
