// Hsi88PowerManager.java
package jmri.jmrix.hsi88;

import jmri.JmriException;
import jmri.PowerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PowerManager implementation for controlling layout power.
 *
 * @author	Kevin Dickerson (C) 2012
 */
public class Hsi88PowerManager implements PowerManager, Hsi88Listener {

    public Hsi88PowerManager(Hsi88TrafficController etc) {
        // connect to the TrafficManager
        tc = etc;
        tc.addHsi88Listener(this);

    }

    Hsi88TrafficController tc;

    public String getUserName() {
        return "Hsi88";
    }

    int power = UNKNOWN;

    public void setPower(int v) throws JmriException {
        power = UNKNOWN; // while waiting for reply
        checkTC();
        if (v == ON) {
            // send message to turn on
            Hsi88Message l = Hsi88Message.getEnableMain();
            tc.sendHsi88Message(l, this);
        } else if (v == OFF) {
            // send message to turn off
            Hsi88Message l = Hsi88Message.getKillMain();
            tc.sendHsi88Message(l, this);
        }
        firePropertyChange("Power", null, null);
    }

    public int getPower() {
        return power;
    }

    // to free resources when no longer used
    public void dispose() throws JmriException {
        tc.removeHsi88Listener(this);
        tc = null;
    }

    private void checkTC() throws JmriException {
        if (tc == null) {
            throw new JmriException("attempt to use Hsi88PowerManager after dispose");
        }
    }

    // to hear of changes
    java.beans.PropertyChangeSupport pcs = new java.beans.PropertyChangeSupport(this);

    public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    protected void firePropertyChange(String p, Object old, Object n) {
        pcs.firePropertyChange(p, old, n);
    }

    public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    // to listen for status changes from Hsi88 system
    public void reply(Hsi88Reply m) {
        // power message?
        if (m.getPriority() == Hsi88Constants.PRIO_1 && m.getCommand() == Hsi88Constants.SYSCOMMANDSTART && m.getAddress() == 0x0000) {
            switch (m.getElement(9)) {
                case Hsi88Constants.CMDGOSYS:
                    power = ON;
                    break;
                case Hsi88Constants.CMDSTOPSYS:
                    power = OFF;
                    break;
                case Hsi88Constants.CMDHALTSYS:
                    power = OFF;
                    break;
                default:
                    log.warn("Unknown sub command " + m.getElement(9));
            }
            firePropertyChange("Power", null, null);
        }
    }

    public void message(Hsi88Message m) {
        // messages are ignored
    }
    private final static Logger log = LoggerFactory.getLogger(Hsi88PowerManager.class.getName());
}


/* @(#)Hsi88PowerManager.java */
