package jmri.jmrix.hsi88;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import jmri.jmrix.AbstractPortController;
import jmri.jmrix.SystemConnectionMemo;
import jmri.util.ThreadingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import purejavacomm.SerialPortEvent;
import purejavacomm.SerialPortEventListener;

/**
 * Converts Stream-based I/O to/from Hsi88 replies/messages. The
 * "Hsi88Interface" side sends/receives message objects. The connection to a
 * Hsi88PortController is via a pair of *Streams, which then carry sequences of
 * characters for transmission. Note that this processing is handled in an
 * independent thread.
 *
 * Synchronization is used for two purposes here: 1. to prevent concurrent
 * reading/writing of listeners 2. to prevent concurrent sending of replies
 * and/or messages. In principle 2 different locks could be used for this, but
 * for sake of simplicity we lock on this object only.
 * 
 * @author Bob Jacobsen Copyright (C) 2001:
 * @author Andre Gruening Copyright (C) 2017: adapted for Hsi088 based on
 *         previous author's Sprog implementation. Tidied synchonization and
 *         java doc.
 *
 */
public class Hsi88TrafficController implements Hsi88Interface, SerialPortEventListener {

    /** reply being constructed from input to the interface */
    private Hsi88Reply reply = new Hsi88Reply();

    /** are we waiting for a reply to the previously sent message? */
    private boolean waitingForReply = false;

    /** remember sender of last message */
    private Hsi88Listener lastSender = null;

    /**
     * create new traffic controller.
     * 
     * @param systemConnectionMemo information about the connection to the HSI88
     *            device.
     */
    public Hsi88TrafficController(SystemConnectionMemo systemConnectionMemo) {
        memo = (Hsi88SystemConnectionMemo) systemConnectionMemo;
    }

    /** list of listeners. Should be protect against concurrent use. */
    private List<Hsi88Listener> hsi88Listeners = new LinkedList<Hsi88Listener>();

    /** Are we connected alright? */
    @Override
    public boolean status() {
        return (ostream != null && istream != null);
    }

    /**
     * add a listener that wants to be notified for Hsi88 replies and messages
     * other listeners send. It is synchronized because listeners could be added
     * from different threads at the same time, however we manipulate an
     * internal data structure here.
     * 
     * @param l listener to add.
     */
    @Override
    public synchronized void addHsi88Listener(Hsi88Listener l) {
        // add only if not already registered
        if (l == null) {
            throw new java.lang.NullPointerException();
        }
        if (!hsi88Listeners.contains(l)) {
            hsi88Listeners.add(l);
        }
    }

    /**
     * remove a listener. @param l listener to remove.
     */
    @Override
    public synchronized void removeHsi88Listener(Hsi88Listener l) {
        if (hsi88Listeners.contains(l)) {
            hsi88Listeners.remove(l);
        }
    }

    /**
     * Distribute message to all listeners except originator.
     * 
     * @note Method only to be called from context synchronized on this traffic
     *       controller (to ensure that all listeners always get in only one
     *       message at a time, so that they need not care about multiple
     *       threads?)
     * 
     * @param m message to distribute
     * @param originator original sender of message
     */
    private void notifyMessage(Hsi88Message m, Hsi88Listener originator) {
        for (Hsi88Listener listener : hsi88Listeners) {
            try {
                // don't send it back to the originator!
                if (listener != originator) {
                    // skip forwarding to the last sender for now, we'll get
                    // them later
                    if (lastSender != listener) {
                        listener.notifyMessage(m);
                    }
                }
            } catch (Exception e) {
                log.warn("notify: During dispatch to " + listener + "\nException " + e);
            }
        }
        // forward to the last listener who sent a message
        // this is done _second_ so monitoring can have already stored the reply
        // before a response is sent
        if (lastSender != null && lastSender != originator) {
            lastSender.notifyMessage(m);
        }
    }

    /**
     * Notify all listeners of an incoming hardware reply.
     * 
     * Synchronized on this traffic controller so that either one message or one
     * reply can be sent at any one time. Ensures also that listeners do not
     * need to think about threading as synchronisation here ensures that also
     * their notifyReply methods are run one at a time.
     * 
     * @param r reply received from HSI88 interface.
     */
    private synchronized void notifyReply(Hsi88Reply r) {
        for (Hsi88Listener listener : hsi88Listeners) {
            try {
                // if is message don't send it back to the originator!
                // skip forwarding to the last sender for now, we'll get them
                // later
                if (lastSender != listener) {
                    listener.notifyReply(r);
                }

            } catch (Exception e) {
                log.warn("notify: During dispatch to " + listener + "\nException " + e);
            }
        }
        // forward to the last listener who sent a message
        // this is done _second_ so monitoring can have already stored the reply
        // before a response is sent
        if (lastSender != null) {
            lastSender.notifyReply(r);
        }
    }

    /**
     * Write a message into to the Hsi88 interface. Called from context
     * synchronised on this traffic controller.
     * 
     * @param m message to send to Hsi88.
     */
    private void sendHsi88Message(Hsi88Message m) {
        // stream to port in single write, as that's needed by serial
        try {
            if (ostream != null) {
                ostream.write(m.getFormattedMessage());
            } else {
                // no stream connected
                log.warn("sendMessage: no connection established");
            }
        } catch (Exception e) {
            log.warn("sendMessage: Exception: " + e.toString());
        }
    }

    /**
     * Forward a message to the actual interface (by calling
     * sendHsi88Message(Hsi88Message) after notifying any listeners.
     */
    @Override
    public synchronized void sendHsi88Message(Hsi88Message m, Hsi88Listener replyTo) {

        if (waitingForReply) {
            try {
                wait(100); //wait until notify()ed or 100ms timeout
            } catch (InterruptedException e) {
                // got reply to previous sent message within 100ms -- that's good.
            }
        }
        waitingForReply = true;

        if (log.isDebugEnabled()) {
            log.debug("sendHsi88Message message: [" + m + "]");
        }
        // remember who sent this
        lastSender = replyTo;
        // notify all _other_ listeners
        notifyMessage(m, replyTo);
        this.sendHsi88Message(m);
    }

    // methods to connect/disconnect to a source of data in a
    // Hsi88PortController

    /** holds port controller */
    private AbstractPortController controller = null;

    /**
     * Make connection to existing PortController object.
     * 
     * @param p port to connect to
     */
    public void connectPort(AbstractPortController p) {
        istream = p.getInputStream();
        ostream = p.getOutputStream();
        if (controller != null) {
            log.warn("connectPort: connect called while connected");
        }
        controller = p;
    }

    /**
     * Break connection to existing Hsi88PortController object. Once broken,
     * attempts to send will fail.
     * 
     * @param p port to disconnect rp
     */
    public void disconnectPort(AbstractPortController p) {
        istream = null;
        ostream = null;
        if (controller != p) {
            log.warn("disconnectPort: disconnect called from non-connected Hsi88PortController");
        }
        controller = null;
    }

    /**
     * static function returning the Hsi88TrafficController instance to use.
     *
     * @return The registered Hsi88TrafficController instance for general use,
     *         if need be creating one.
     * @deprecated JMRI Since 4.4 instance() shouldn't be used, convert to JMRI
     *             multi-system support structure.
     */
    /*
     * @Deprecated static public Hsi88TrafficController instance() { return
     * null; }
     */

    /**
     * set adapter memo
     * 
     * @param adaptermemo memo to set.
     */
    void setAdapterMemo(Hsi88SystemConnectionMemo adaptermemo) {
        memo = adaptermemo;
    }

    /** hold memo */
    private Hsi88SystemConnectionMemo memo = null;
    /** hold input stream */
    private DataInputStream istream = null;
    /** hold output stream */
    private OutputStream ostream = null;

    /**
     * serialEvent - respond to an event triggered by RXTX. In this case we are
     * only dealing with DATA_AVAILABLE but the other events are left here for
     * reference. AJB Jan 2010
     */
    @Override
    public void serialEvent(SerialPortEvent event) {
        switch (event.getEventType()) {
            case SerialPortEvent.BI:
            case SerialPortEvent.OE:
            case SerialPortEvent.FE:
            case SerialPortEvent.PE:
            case SerialPortEvent.CD:
            case SerialPortEvent.CTS:
            case SerialPortEvent.DSR:
            case SerialPortEvent.RI:
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                break;
            case SerialPortEvent.DATA_AVAILABLE:
                log.debug("Data Available");
                handleOneIncomingReply();
                break;
        }
    }

    /**
     * Handle an incoming reply from the hardware and if the reply is complete
     * send the reply to listeners.
     */
    void handleOneIncomingReply() {
        // we get here if data has been received
        // fill the current reply with any data received
        int i = this.reply.getNumDataElements();

        while (!this.reply.end()) {
            try {
                byte ch;
                try {
                    if (istream.available() == 0) {
                        return; // nothing waiting to be read
                    }
                    ch = istream.readByte();
                } catch (EOFException e) {
                    log.error("Input streams has reached end: {}" + e);
                    return;
                } catch (IOException e) {
                    log.error("IOException while reading from input stream: {}", e);
                    return;
                }

                try {
                    this.reply.setElement(i, ch);
                } catch (ArrayIndexOutOfBoundsException e) {
                    log.error("Reply longer than space to hold it.");
                    return;
                }
            } catch (Exception e) {
                log.warn("Exception in DATA_AVAILABLE state: " + e);
                return;
            }
            i++;
        }
        // we only reach here if we have reached end of reply. 
        sendreply();
    }

    /**
     * Send the current reply - built using data from serialEvent. notifyReply
     * of the traffic controllers list
     */
    private void sendreply() {

        if (log.isDebugEnabled()) {
            log.debug("dispatch reply of length " + this.reply.getNumDataElements());
        }

        if (waitingForReply == false) {
            this.reply.setUnsolicited();
            log.debug("Unsolicited Reply");
        }

        synchronized (this) {
            waitingForReply = false;
            notifyAll();
        }

        {
            final Hsi88Reply thisReply = this.reply;
            final Hsi88TrafficController thisTC = this;
            // return a notification via the queue
            ThreadingUtil.ThreadAction r = new ThreadingUtil.ThreadAction() {

                Hsi88Reply replyForLater = thisReply;
                Hsi88TrafficController myTC = thisTC;

                public void run() {
                    log.debug("Delayed notify starts");
                    myTC.notifyReply(replyForLater);
                }
            };
            ThreadingUtil.runOnLayoutEventually(r);
        }

        // Create a new reply, ready to be filled
        this.reply = new Hsi88Reply();
    }

    private final static Logger log = LoggerFactory.getLogger(Hsi88TrafficController.class.getName());

}
