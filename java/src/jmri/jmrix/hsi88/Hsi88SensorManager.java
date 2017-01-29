package jmri.jmrix.hsi88;

import java.util.HashMap;
import jmri.Sensor;
import jmri.managers.AbstractSensorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sensor Manager to deal with updates and convert HSI88 parsed replies to HSI88
 * sensor events.
 * 
 * @author Andre Gruening, Copyright (C) 2017.
 */
public class Hsi88SensorManager extends AbstractSensorManager implements Hsi88ReplyListener {

    /** keep connection memo */
    private Hsi88SystemConnectionMemo memo;

    /** store mapping sensor to corresponding sensor objects */
    private HashMap<Integer, Hsi88Sensor> sensors = new HashMap<Integer, Hsi88Sensor>();

    @Override
    public void notifyReply(int reply, int payload) {
        // is it a sensor update?
        if (reply >= 0) {
            Hsi88Sensor sensor = sensors.get(reply);
            if (sensor != null) {
                sensor.setOwnState(payload);
            }
        }
    }

    /**
     * create a new sensor manager for the Hsi88 interface. It connects to the
     * traffic controller and sets the Hsi88 interface up.
     * 
     * @param memo connection memo
     * 
     */
    Hsi88SensorManager(Hsi88SystemConnectionMemo memo) {

        this.memo = memo;
        memo.getManager().addSensorListener(this);
        log.debug("Hsi88 Sensor Manager starts.");
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
     *            * @param userName merely passed on to ctor of Hsi88Sensor.
     * 
     * @return new Sensor with address as parsed from systemName if no sensor
     *         with the same address existed. Otherwise returns existing sensor
     *         with that address.
     * 
     */
    @Override
    protected Sensor createNewSensor(String systemName, String userName) {

        String payload = systemName.substring(this.getSystemPrefix().length() + 1);

        int addr = -1;
        try {
            addr = Integer.parseInt(payload);
        } catch (NumberFormatException e) {
            log.warn(
                    "Cannot parse payload of system name into S88 sensor address: {}." +
                            "Only use numeric payloads that can be parsed into nonnegative integers.",
                    payload);
            return null;
        }

        if (addr < 0) {
            log.warn("Hsi88 Sensor cannot have negative address:" + addr);
            return null;
        }

        // module number of this s88 sensor address. Module numbers run from 1 and contain 16 sensors each.
        int module = (addr / 16) + 1;

        if (module > Hsi88Config.MAX_MODULES) {
            log.warn("Sensor address beyond addressable range of Hsi88 Interface. " +
                    "If you really want this change Hsi88Config.MAX_MODULES in the source code.");
            return null;
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
    public void dispose() {
        memo.getManager().removeSensorListener(this);
        super.dispose();
    }

    final static Logger log = LoggerFactory.getLogger(Hsi88SensorManager.class.getName());
}
