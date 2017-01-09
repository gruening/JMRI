package jmri.jmrix.hsi88;

import jmri.Sensor;
import jmri.managers.AbstractSensorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Hsi88SensorManager extends AbstractSensorManager implements Hsi88Listener {

    Hsi88SystemConnectionMemo _memo;

    Hsi88SensorManager(Hsi88SystemConnectionMemo memo) {
        _memo = memo;
        _memo.getHsi88TrafficController().addHsi88Listener(this);
    }

    @Override
    public String getSystemPrefix() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Sensor createNewSensor(String systemName, String userName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void notifyMessage(Hsi88Message m) {
        // nothing to do -- we only listen for replies
    }

    @Override
    public void notifyReply(Hsi88Reply m) {
        log.info("Got Reply: " + m);
        if (m.isReading()) {
            log.info("Got sensor reading");
        }

    }

    private final static Logger log = LoggerFactory.getLogger(Hsi88SensorManager.class.getName());

}