// Hsi88Turnout.java
package jmri.jmrix.hsi88;

import jmri.Turnout;
import jmri.implementation.AbstractTurnout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implement a Turnout via Hsi88 communications.
 * <P>
 * This object doesn't listen to the Hsi88 communications. This is because it
 * should be the only object that is sending messages for this turnout; more
 * than one Turnout object pointing to a single device is not allowed.
 *
 * Based on work by Bob Jacobsen
 *
 * @author	Kevin Dickerson Copyright (C) 2012
 * 
 */
public class Hsi88Turnout extends AbstractTurnout
        implements Hsi88Listener {

    /**
     *
     */
    private static final long serialVersionUID = -8288482023350129321L;
    String prefix;

    /**
     * Hsi88 turnouts use the NMRA number (0-2040) as their numerical
     * identification in the system name.
     *
     * @param number address of the turnout
     */
    public Hsi88Turnout(int number, String prefix, Hsi88TrafficController etc) {
        super(prefix + "T" + number);
        _number = number;
        this.prefix = prefix;
        tc = etc;
        tc.addHsi88Listener(this);
    }

    Hsi88TrafficController tc;

    // Handle a request to change state by sending a turnout command
    protected void forwardCommandChangeToLayout(int s) {
        // implementing classes will typically have a function/listener to get
        // updates from the layout, which will then call
        //		public void firePropertyChange(String propertyName,
        //										Object oldValue,
        //										Object newValue)
        // _once_ if anything has changed state (or set the commanded state directly)

        // sort out states
        if ((s & Turnout.CLOSED) != 0) {
            // first look for the double case, which we can't handle
            if ((s & Turnout.THROWN) != 0) {
                // this is the disaster case!
                log.error("Cannot command both CLOSED and THROWN " + s);
                return;
            } else {
                // send a CLOSED command
                sendMessage(true ^ getInverted());
            }
        } else {
            // send a THROWN command
            sendMessage(false ^ getInverted());
        }
    }

    // data members
    int _number;   // turnout number

    /**
     * Set the turnout known state to reflect what's been observed from the
     * command station messages. A change there means that somebody commanded a
     * state change (e.g. somebody holding a throttle), and that command has
     * already taken effect. Hence we use "newCommandedState" to indicate it's
     * taken place. Must be followed by "newKnownState" to complete the turnout
     * action.
     *
     * @param state Observed state, updated state from command station
     */
    synchronized void setCommandedStateFromCS(int state) {
        if ((getFeedbackMode() != DIRECT)) {
            return;
        }

        newCommandedState(state);
    }

    /**
     * Set the turnout known state to reflect what's been observed from the
     * command station messages. A change there means that somebody commanded a
     * state change (e.g. somebody holding a throttle), and that command has
     * already taken effect. Hence we use "newKnownState" to indicate it's taken
     * place.
     * <P>
     * @param state Observed state, updated state from command station
     */
    synchronized void setKnownStateFromCS(int state) {
        newCommandedState(state);
        if (getFeedbackMode() == DIRECT) {
            newKnownState(state);
        }
    }

    public void turnoutPushbuttonLockout(boolean b) {
    }

    /**
     * Hsi88 turnouts can be inverted
     */
    @Override
    public boolean canInvert() {
        return true;
    }

    final static int UNKNOWN = Hsi88Constants.PROTOCOL_UNKNOWN;
    final static int DCC = Hsi88Constants.PROTOCOL_DCC;
    final static int MM2 = Hsi88Constants.PROTOCOL_MM2;
    final static int SFX = Hsi88Constants.PROTOCOL_SX;

    int protocol = UNKNOWN;

    /**
     * Tell the layout to go to new state.
     *
     * @param newstate State of the turnout to be sent to the command station
     */
    protected void sendMessage(final boolean newstate) {
        Hsi88Message m = Hsi88Message.getSetTurnout(getCANAddress(), (newstate ? 1 : 0), 0x01);
        tc.sendHsi88Message(m, this);

        meterTimer.schedule(new java.util.TimerTask() {
            boolean state = newstate;

            public void run() {
                try {
                    sendOffMessage((state ? 1 : 0));
                } catch (Exception e) {
                    log.error("Exception occured while sending delayed off to turnout: " + e);
                }
            }
        }, METERINTERVAL);
    }

    int getCANAddress() {
        switch (protocol) {
            case DCC:
                return _number + Hsi88Constants.DCCACCSTART - 1;
            default:
                return _number + Hsi88Constants.MM1ACCSTART - 1;
        }
    }

    // to listen for status changes from Hsi88 system
    public void reply(Hsi88Reply m) {
        if (m.getPriority() == Hsi88Constants.PRIO_1 && m.getCommand() >= Hsi88Constants.ACCCOMMANDSTART && m.getCommand() <= Hsi88Constants.ACCCOMMANDEND) {
            if (protocol == UNKNOWN) {
                if (m.getAddress() == _number + Hsi88Constants.MM1ACCSTART - 1) {
                    protocol = MM2;
                } else if (m.getAddress() == _number + Hsi88Constants.DCCACCSTART - 1) {
                    protocol = DCC;
                } else {
                    //Message is not for us.
                    return;
                }
            }
            if (m.getAddress() == getCANAddress()) {
                switch (m.getElement(9)) {
                    case 0x00:
                        setKnownStateFromCS(Turnout.THROWN);
                        break;
                    case 0x01:
                        setKnownStateFromCS(Turnout.CLOSED);
                        break;
                    default:
                        log.warn("Unknown state command " + m.getElement(9));
                }
            }
        }
    }

    public void message(Hsi88Message m) {
        // messages are ignored
    }

    protected void sendOffMessage(int state) {
        Hsi88Message m = Hsi88Message.getSetTurnout(getCANAddress(), state, 0x00);
        tc.sendHsi88Message(m, this);
    }

    static final int METERINTERVAL = 100;  // msec wait before closed
    static java.util.Timer meterTimer = new java.util.Timer(true);

    private final static Logger log = LoggerFactory.getLogger(Hsi88Turnout.class.getName());
}

/* @(#)Hsi88Turnout.java */
