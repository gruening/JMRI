package jmri.jmrix.hsi88;

import jmri.CommandStation;
// import jmri.DccLocoAddress;
// import jmri.jmrix.hsi88.hsi88slotmon.Hsi88SlotMonFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controls a collection of slots, acting as a soft command station for hsi88
 * <P>
 * A SlotListener can register to hear changes. By registering here, the
 * SlotListener is saying that it wants to be notified of a change in any slot.
 * Alternately, the SlotListener can register with some specific slot, done via
 * the hsi88Slot object itself.
 * <P>
 * This Programmer implementation is single-user only. It's not clear whether
 * the command stations can have multiple programming requests outstanding (e.g.
 * service mode and ops mode, or two ops mode) at the same time, but this code
 * definitely can't.
 * <P>
 * Updated by Andrew Berridge, January 2010 - state management code now safer,
 * uses enum, etc. Amalgamated with hsi88 Slot Manager into a single class -
 * reduces code duplication </P>
 * <P>
 * Updated by Andrew Crosland February 2012 to allow slots to hold 28 step speed
 * packets</P>
 * 
 * Re-written by Andrew Crosland to send the next packet as soon as a reply is 
 * notified. This removes a race between the old state machine running before 
 * the traffic controller despatches a reply, missing the opportunity to send a 
 * new packet to the layout until the next JVM time slot, which can be 15ms on 
 * Windows platforms.
 *
 * @author	Bob Jacobsen Copyright (C) 2001, 2003 
 * @author      Andrew Crosland (C) 2006 ported to hsi88, 2012, 2016
 */
public class Hsi88CommandStation implements CommandStation, Hsi88Listener, Runnable {

    private boolean running = false;
    
    protected int currentSlot = 0;
    protected int currenthsi88Address = -1;

    // protected LinkedList<Hsi88Slot> slots;
    // protected Queue<Hsi88Slot> sendNow;

    javax.swing.Timer timer = null;

    private Hsi88TrafficController tc = null;

    public Hsi88CommandStation(Hsi88TrafficController controller) {
        tc = controller;
        tc.addHsi88Listener(this);
    }

    /**
     * Send a specific packet to the rails.
     *
     * Call to sendhsi88Message seems to get delayed if this thread sleeps, so
     * create a new runnable object to despatch the message to the traffic
     * controller.
     *
     * @param packet  Byte array representing the packet, including the
     *                error-correction byte. Must not be null.
     * @param repeats number of times to repeat the packet
     */
    @Override
    public void sendPacket(byte[] packet, int repeats) {
        if (packet.length <= 1) {
            log.error("Invalid DCC packet length: " + packet.length);
        }
        if (packet.length >= 7) {
            log.error("Maximum 6-byte packets accepted: " + packet.length);
        }
        final Hsi88Message m = new Hsi88Message(packet);
        if (log.isDebugEnabled()) {
            log.debug("Sending packet " + m.toString(tc.isSIIBootMode()));
        }
        for (int i = 0; i < repeats; i++) {
            final Hsi88TrafficController thisTC = tc;

            Runnable r;
            r = new Runnable() {
                Hsi88TrafficController myTC = thisTC;

                @Override
                public void run() {
                    myTC.sendHsi88Message(m, null);
                }
            };
            javax.swing.SwingUtilities.invokeLater(r);
        }
    }

    private int statusDue = 0;
    private int[] statusA = new int[4];

    @Override
    /**
     * The run() method will only be called (from hsi88SystemconnecionMemo 
     * ConfigureCommandStation()) if the connected hsi88 is in OPS mode.
     * 
     */
    public void run() {
        log.debug("Slot thread starts");
        running = true;
        // Send a CR to prompt a reply and start things running
        tc.sendHsi88Message(new Hsi88Message("ABCDEFG"));
    }

    /*
     * Needs to listen to replies
     * Need to implement asynch replies for overload & notify power manager
     *
     * How does POM work??? how does programmer send packets??
     *
     * @param m the hsi88 message received
     */
    @Override
    public void notifyMessage(Hsi88Message m) {
//        log.error("message received unexpectedly: "+m.toString(tc.isSIIBootMode()));
    }

    private Hsi88Reply replyForMe;

    /**
     * The thread will only run when the connected hsi88 is in OPS mode.
     * 
     * @param m The hsi88Reply to be handled
     */
    @Override
    public void notifyReply(Hsi88Reply m) {
        // Here we wou;d need to act on an reply from the HSI88 device!
        if (running == true) {
            byte[] p;
            statusA = new int[4];

            replyForMe = m;
            log.debug("reply received: "+m.toString() + "StatusDue = " + statusDue);
            if (m.isUnsolicited() && m.isOverload()) {
                log.error("Overload");
                // *** turn power off
            }
            
            // Is it time to send a status request?
            if (statusDue == 40) {
                // Only ask for status if it's actually being displayed
                log.debug("Sending status request");
                tc.sendHsi88Message(Hsi88Message.getStatus(), this);
                statusDue++;
            } else {
                // Are we waiting for a status reply
                if (statusDue == 41) {
                    // Handle the status reply
                    String s = replyForMe.toString();
                    log.debug("Reply received whilst expecting status");
                    int i = s.indexOf('h');
                    //Check that we got a status message before acting on it
                    //by checking that "h" was found in the reply
                    if (i > -1) {
                        int milliAmps = (int) ((Integer.decode("0x" + s.substring(i + 7, i + 11))));
                        statusA[0] = milliAmps;
                        String ampString;
                        ampString = Float.toString((float) statusA[0] / 1000);
                        statusDue = 0;
                    }
                } else {
                    // Get next packet to send
                    log.debug("Get next packet to send");
                    // Hsi88Slot s = sendNow.poll();
                    
                    // Or take the next one from the stack
                        
                        // Do nothing for a while
                    log.debug("Start timeout");
                    
                }   
            }
            restartTimer(100);
        }
    }

    /**
     *
     * @return a boolean if the command station is busy - i.e. it has at least
     *         one occupied slot
     */
    public boolean isBusy() {
        return true;
    }

    public void setSystemConnectionMemo(Hsi88SystemConnectionMemo memo) {
        adaptermemo = memo;
    }

    Hsi88SystemConnectionMemo adaptermemo;

    /**
     * Get user name
     * 
     * @return the user name
     */
    @Override
    public String getUserName() {
        if (adaptermemo == null) {
            return "hsi88";
        }
        return adaptermemo.getUserName();
    }

    /**
     * Get system prefix
     * 
     * @return the system prefix
     */
    @Override
    public String getSystemPrefix() {
        if (adaptermemo == null) {
            return "H";
        }
        return adaptermemo.getSystemPrefix();
    }

    /**
     * Internal routine to handle a timeout
     */
    synchronized protected void timeout() {
        Runnable r = () -> {
            log.debug("Send CR due to timeout");
            // Send a CR to prompt a reply from hardware and keep things running
            tc.sendHsi88Message(new Hsi88Message(""));
        };
        javax.swing.SwingUtilities.invokeLater(r);
    }

    /**
     * Internal routine to handle timer starts {@literal &} restarts
     * 
     * @param delay timer delay
     */
    protected void restartTimer(int delay) {
        log.debug("Restart timer");
        if (timer == null) {
            timer = new javax.swing.Timer(delay, (java.awt.event.ActionEvent e) -> {
                timeout();
            });
        }
        timer.stop();
        timer.setInitialDelay(delay);
        timer.setRepeats(false);
        timer.start();
    }


    /**
     * Set the slot hsi88SlotMonFrame associated with this Command Station
     * There can currently be only one hsi88SlotMonFrame.
     */
    public void setHsi88SlotMonFrame(Object s) {
    }

  


    // initialize logging
    private final static Logger log = LoggerFactory.getLogger(Hsi88CommandStation.class.getName());
}