// Hsi88Sensor.java
package jmri.jmrix.hsi88;

import jmri.implementation.AbstractSensor;

/**
 * Implement a Sensor via Hsi88 communications.
 * <P>
 * This object doesn't listen to the Hsi88 communications. This is because the
 * sensor manager will handle all the messages as some sensor updates will come
 * bundled together in one message. It also saves having multiple sensor beans
 * each having to decoder the same message which would be better off being done
 * in one location.
 *
 * @author Kevin Dickerson (C) 2009
 * 
 */
public class Hsi88Sensor extends AbstractSensor {

    /**
     *
     */
    private static final long serialVersionUID = 7029240754803015932L;

    public Hsi88Sensor(String systemName, String userName) {
        super(systemName, userName);
        init(systemName);
    }

    public Hsi88Sensor(String systemName) {
        super(systemName);
        init(systemName);
    }

    private void init(String id) {
    }

    public void requestUpdateFromLayout() {
    }

    static String[] modeNames = null;
    static int[] modeValues = null;
}

/* @(#)Hsi88Sensor.java */
