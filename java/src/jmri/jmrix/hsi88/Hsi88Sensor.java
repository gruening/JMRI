/**
 * 
 */
package jmri.jmrix.hsi88;

import jmri.Sensor;
import jmri.implementation.AbstractSensor;

/**
 * @author Andre Gruening, Copyright (C) 2017
 *
 * @todo make part of SystemConnectionMemo?
 */
public class Hsi88Sensor extends AbstractSensor implements Sensor {

    private int address;

    public Hsi88Sensor(String systemName) {
        super(systemName);
        // TODO Auto-generated constructor stub
    }

    public Hsi88Sensor(String systemName, String userName) {
        super(systemName, userName); // thi the right thing todo?
        // TODO Auto-generated constructor stub
    }

    /**
     * We currently do not request updates from the layout, but we could simply
     * send the "m" command.
     */
    @Override
    public void requestUpdateFromLayout() {
        // do nothing
    }

    public int getAddress() {
        return address;
    }
}
