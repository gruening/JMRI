package jmri.jmrix.hsi88;

import jmri.JmriException;
import jmri.PowerManager;

/**
 * PowerManager implementation for controlling HSI88 layout power.
 *
 * @author Bob Jacobsen Copyright (C) 2001
 * @author Andre Gruening Copyright (C) 2017
 *
 */
public class Hsi88PowerManager extends jmri.managers.AbstractPowerManager implements PowerManager, Hsi88Listener {

    /** holds traffic controler instance */
    private Hsi88TrafficController trafficController;

    /**
     * create new power manager
     * 
     * @param memo connection memo
     */
    public Hsi88PowerManager(Hsi88SystemConnectionMemo memo) {
        super(memo);
        // connect to the Traffic Controller
        trafficController = memo.getHsi88TrafficController();
        trafficController.addHsi88Listener(this);
    }

    /** current power state of Hsi88 device. */
    private int power = PowerManager.UNKNOWN;

    /** are we waiting for a reply from the layout? */
    private boolean waiting = false;

    /**
     * set power on and off by sending the appropriate messages to the HSI88
     * interface.
     */
    @Override
    public void setPower(int v) throws JmriException {
        power = PowerManager.UNKNOWN; // while waiting for reply
        checkTC();
        if (v == PowerManager.ON) {
            // configure to wait for reply
            trafficController.sendHsi88Message(Hsi88Message.powerOn(), this);
        } else if (v == OFF) {
            // configure to wait for reply
            trafficController.sendHsi88Message(Hsi88Message.powerOff(), this);
        }
        firePropertyChange("Power", null, null);
    }

    /** @return power state of HSi88 interface. */
    @Override
    public int getPower() {
        return power;
    }

    /** to free resources when no longer used. */
    @Override
    public void dispose() throws JmriException {
        trafficController.removeHsi88Listener(this);
        trafficController = null;
    }

    /** check whether traffic controller is attached. */
    private void checkTC() throws JmriException {
        if (trafficController == null) {
            throw new JmriException("attempt to use Hsi88PowerManager after dispose");
        }
    }

    /** to listen for status changes from the Hsi88 system */
    @Override
    public void notifyReply(Hsi88Reply m) {
        
        int modules = m.getSetupReplyModules();
        
        if(modules == 0) {
            this.power = PowerManager.OFF;
            firePropertyChange("Power", null, null);
        }
        else if (modules > 0) {
            this.power = PowerManager.ON;
            firePropertyChange("Power", null, null);
        }
        // else not for us or malformatted reply.
    }

    @Override
    public void notifyMessage(Hsi88Message m) {
        // nothing to do.
    }

}
