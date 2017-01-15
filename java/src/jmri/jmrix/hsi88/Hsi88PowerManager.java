package jmri.jmrix.hsi88;

import jmri.JmriException;
import jmri.PowerManager;
import jmri.jmrix.AbstractMessage;
import org.python.jline.internal.Log;

/**
 * PowerManager implementation for controlling HSI88 layout power.
 *
 * @author Bob Jacobsen Copyright (C) 2001
 *
 */
public class Hsi88PowerManager extends jmri.managers.AbstractPowerManager implements PowerManager, Hsi88Listener {

    Hsi88TrafficController trafficController = null;

    public Hsi88PowerManager(Hsi88SystemConnectionMemo memo) {
        super(memo);
        // connect to the TrafficManager
        trafficController = memo.getHsi88TrafficController();
        trafficController.addHsi88Listener(this);
    }

    int power = UNKNOWN;

    boolean waiting = false;
    int onReply = UNKNOWN;

    public void setPower(int v) throws JmriException {
        power = UNKNOWN; // while waiting for reply
        checkTC();
        if (v == ON) {
            // configure to wait for reply
            waiting = true;
            onReply = PowerManager.ON;
            // enable S88 polling
            Hsi88Message l = new Hsi88Message(Hsi88Message.Command.Setup, 2, 2, 2);
            trafficController.sendHsi88Message(l, this);
        } else if (v == OFF) {
            // configure to wait for reply
            waiting = true;
            onReply = PowerManager.OFF;
            firePropertyChange("Power", null, null);
            // disable S88 polling
            Hsi88Message l = new Hsi88Message(Hsi88Message.Command.Setup, 0, 0, 0);
            trafficController.sendHsi88Message(l, this);

        }
        firePropertyChange("Power", null, null);
    }

    /*
     * Used to update power state after service mode programming operation
     * without sending a message to the SPROG
     */
    public void notePowerState(int v) {
        power = v;
        firePropertyChange("Power", null, null);
    }

    public int getPower() {
        return power;
    }

    // to free resources when no longer used
    public void dispose() throws JmriException {
        trafficController.removeHsi88Listener(this);
        trafficController = null;
    }

    private void checkTC() throws JmriException {
        if (trafficController == null) {
            throw new JmriException("attempt to use Hsi88PowerManager after dispose");
        }
    }

    // to listen for status changes from Hsi88 system
    public void notifyReply(Hsi88Reply m) {
        Log.warn("notified");
        if (waiting) {
            power = onReply;
            firePropertyChange("Power", null, null);
        }
        waiting = false;
    }

    /** @todo What could we do here? */
    public void notifyMessage(Hsi88Message m) {
        // nothing to do.
    }

    public void notify(AbstractMessage m) {
        if (m instanceof Hsi88Message) {
            this.notifyMessage((Hsi88Message) m);
        } else {
            this.notifyReply((Hsi88Reply) m);
        }
    }
}
