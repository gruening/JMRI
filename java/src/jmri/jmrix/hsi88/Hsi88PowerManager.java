package jmri.jmrix.hsi88;

import jmri.JmriException;
import jmri.PowerManager;

/**
 * PowerManager implementation for controlling HSI88 layout power.
 *
 * @author Bob Jacobsen Copyright (C) 2001.
 * @author Andre Gruening Copyright (C) 2017 : Hsi88 specific implementation, in
 *         parts based on previous author's Sprog implementation.
 *
 */
public class Hsi88PowerManager extends jmri.managers.AbstractPowerManager implements PowerManager, Hsi88ReplyListener {

    /** holds traffic controller instance */
    private Hsi88TrafficController tc;
    /** holds Hsi88 Manager */
    private Hsi88ReplyManager rm;

    /**
     * create new power manager
     * 
     * @param memo connection memo
     */
    public Hsi88PowerManager(Hsi88SystemConnectionMemo memo) {
        super(memo);
        tc = memo.getTrafficController();
        rm = memo.getReplyManager();
    }

    /** current power state of Hsi88 device. */
    private int power = PowerManager.UNKNOWN;

    /**
     * set power on and off by sending the appropriate messages to the HSI88
     * interface.
     */
    @Override
    public void setPower(int v) throws JmriException {
        power = PowerManager.UNKNOWN; // while waiting for reply
        checkTC();
        if (v == PowerManager.ON) {
            tc.sendHsi88Message(Hsi88Message.powerOn(), null);
        } else if (v == OFF) {
            tc.sendHsi88Message(Hsi88Message.powerOff(), null);
        }
        firePropertyChange("Power", null, null);
    }

    /** @return power state of Hsi88 interface. */
    @Override
    public int getPower() {
        return power;
    }

    /** free resources when no longer used. */
    @Override
    public void dispose() throws JmriException {
        rm.removeSensorListener(this);
        tc = null;
    }

    /** check whether traffic controller is attached. */
    private void checkTC() throws JmriException {
        if (tc == null) {
            throw new JmriException("attempt to use Hsi88PowerManager after dispose");
        }
    }

    /** listen for status changes from the Hsi88 system */
    @Override
    public void notifyReply(int reply, int modules) {

        if (reply == Hsi88ReplyManager.ResponseType.SETUP) {

            if (modules <= 0) {
                this.power = PowerManager.OFF;
            } else if (modules > 0) {
                this.power = PowerManager.ON;
            }
            firePropertyChange("Power", null, null);
        }
        // else not for us
    }
}
