/**
 * 
 */
package jmri.jmrix.hsi88;

import jmri.Sensor;
import jmri.implementation.AbstractSensor;

/**
 * Light weight class to represent a sensor connected to an HSI88 interface. The
 * actual processing is done in Hsi88SensorManager as the HSI88 protocal is s88
 * module oriented, not sensor oriented.
 * 
 * @author Andre Gruening, Copyright (C) 2017
 *
 */
public class Hsi88Sensor extends AbstractSensor implements Sensor {

    public Hsi88Sensor(String systemName) {
        super(systemName);
    }

    public Hsi88Sensor(String systemName, String userName) {
        super(systemName, userName);
    }

    /**
     * We currently do not request updates from the layout, but we could simply
     * send the "m" command. (
     * 
     * @todo On which thread should we do it -- invoke later?
     */
    @Override
    public void requestUpdateFromLayout() {
        // do nothing
    }
}