package jmri.jmrix.hsi88;

import java.util.Hashtable;
import jmri.Sensor;
import jmri.jmrix.hsi88.Hsi88Setup.Hsi88Mode;
import jmri.managers.AbstractSensorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Hsi88SensorManager extends AbstractSensorManager implements Hsi88Listener {

    /** keep connection memo */
    private Hsi88SystemConnectionMemo memo;
    /** store address and corresponding sensor object */
    private Hashtable<Integer, Hsi88Sensor> sensors = new Hashtable<Integer, Hsi88Sensor>();

    Hsi88SensorManager(Hsi88SystemConnectionMemo memo) {
        this.memo = memo;
        this.memo.getHsi88TrafficController().addHsi88Listener(this);
        // probably do the set up of the HSI88 here, ie set chain length, and send v command, and t command
    }

    @Override
    public String getSystemPrefix() {
        return memo.getSystemPrefix();
    }

    @Override
    protected Sensor createNewSensor(String systemName, String userName) {
        // probably check whether the current chain can supply this sensor and supply
        Hsi88Sensor s = new Hsi88Sensor(systemName, userName);
        log.info("Systemname: " + systemName + "\tUsername: " + userName);
        // parse user name to extract address.
        sensors.put(0, s);
        return s;
    }

    @Override
    public void notifyMessage(Hsi88Message m) {
        // nothing to do -- only listening for replies
    }

    @Override
    public synchronized void notifyReply(Hsi88Reply r) {

        switch ( Character.toLowerCase(r.getOpCode())) {
            case 'i':
            case 'm':
                log.info("Got sensor reading");
                processSensorReading(r);
                break;
            case 't':
                switchTerminalMode(r);
                break;
            case 's':
                checkNumSensor(r);
                break;
            case 'v':
                log.info(r.toString());
                break;
            default:
                log.warn("Unknown opcode of message: " + r);
        }
    }

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

    private void checkNumSensor(Hsi88Reply r) {

        if (r.getNumDataElements() != 4) { // "sXX\r"
            log.error("Ignored malformatted response to 's' command: " + r);
            return;
        }

        String sensorStr = r.toString().substring(1,3);

        int i = -1; // or get from our setup
        try {
            i = Integer.parseInt(sensorStr, 16);
            log.info("Number of modules: " + i);
        } catch (NumberFormatException e) {
            log.warn("Could not parse " + sensorStr);
        }


        // compare to sum of sensors given in the setup -- and act?

    }

    private void processSensorReading(Hsi88Reply r) {

        int len = r.getNumDataElements() - 1; // ignore opcode
        String s = r.toString();
        // create StringStream and consume it.

        String numSensors = s.substring(1, 3);
        int nSensors = Integer.parseInt(numSensors, 16); // add try catch!
        log.info("Number of Modules: " + nSensors);
        
        // \todo update iav to send the number of registered modules! Not the module number!
        
        
        int i = 3;
                 
        
        while (i <= len - 6) {

            String moduleUpdate = s.substring(i, i + 6);
            log.info("Dealing with: " + moduleUpdate);

            try {
                int v = Integer.parseInt(moduleUpdate, 16);
                int value = v & 0xFFFF;
                int module = (v >> 16) & 0xFF;
                log.info("Update for module " + module + " : " + Integer.toHexString(value));
                // trigger sensor here.

            } catch (NumberFormatException e) {
                log.warn("Ignored malformated part of module update: " + moduleUpdate);
            }

            i += 6;
        }

        if (i != len) {
            log.warn("Trailing chars of sensor message not parsed: " + s.substring(i));
        }

        /*
         * char moduleH = (char) r.getElement(i++); char moduleL = (char)
         * r.getElement(i++);
         * 
         * 
         * 
         * char valueHH = (char) r.getElement(i++); char valueHL = (char)
         * r.getElement(i++);
         * 
         * char valueLH = (char) r.getElement(i++); char valueLL = (char)
         * r.getElement(i++);
         */

    }

    private final static Logger log = LoggerFactory.getLogger(Hsi88SensorManager.class.getName());

}
