/**
 * 
 */
package jmri.jmrix.hsi88;

import jmri.Sensor;
import jmri.implementation.AbstractSensor;

/**
 * Light weight class to represent a sensor connected to an HSI88 interface. The
 * actual processing is done in Hsi88SensorManager as the HSI88 protocol is s88
 * module oriented, not sensor oriented.
 * 
 * @author Andre Gruening, Copyright (C) 2017
 *
 */
public class Hsi88Sensor extends AbstractSensor implements Sensor {

    private Hsi88SystemConnectionMemo memo;

    public Hsi88Sensor(String systemName, String userName, Hsi88SystemConnectionMemo memo) {
        super(systemName, userName);
        this.memo = memo;
    }

    /**
     * send the "m" command to layout.
     */
    @Override
    public void requestUpdateFromLayout() {
        memo.getHsi88TrafficController().sendHsi88Message(Hsi88Message.cmdQuery(), null);
    }
}