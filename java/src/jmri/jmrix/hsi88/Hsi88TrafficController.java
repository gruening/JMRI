package jmri.jmrix.hsi88;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.io.DataInputStream;
import java.io.OutputStream;
import java.util.Vector;
import jmri.jmrix.AbstractPortController;
import jmri.jmrix.SystemConnectionMemo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts Stream-based I/O to/from Hsi88 replies/messages. The
 * "Hsi88Interface" side sends/receives message objects. The connection to a
 * Hsi88PortController is via a pair of *Streams, which then carry sequences of
 * characters for transmission. Note that this processing is handled in an
 * independent thread.
 *
 * @author Andre Gruening Copyright (C) 2017: Implementation of Hsi088 relevant
 *         stuff, change of visibilities to more private, commented. All work
 *         based on implementation for sprog by
 * @author Bob Jacobsen Copyright (C) 2001:
 * 
 */
public class Hsi88TrafficController implements Hsi88Interface, SerialPortEventListener {

    /** reply being constructed from input to the interface */
    private Hsi88Reply reply = new Hsi88Reply();

    /** are we waiting for a reply to the previously sent message? */
    private boolean waitingForReply = false;

    /** remember sender of last message */
    private Hsi88Listener lastSender = null;

    /** create new traffic controller 
     * @param systemConnectionMemo */
    public Hsi88TrafficController(SystemConnectionMemo systemConnectionMemo) {
        memo = (Hsi88SystemConnectionMemo) systemConnectionMemo;
    }

    /** list of listeners */
    private Vector<Hsi88Listener> hsi88Listeners = new Vector<Hsi88Listener>();

    /** Are we connected alright? */
    @Override
    public boolean status() {
        return (ostream != null && istream != null);
    }

    /**
     * add a listener that wants to be notified for Hsi88 replies and messages
     * other listeners send. Is is synchronized because listeners could be added
     * from different threads at the same but we manipulate an internal data
     * structure here.
     */
    public synchronized void addHsi88Listener(Hsi88Listener l) {
        // add only if not already registered
        if (l == null) {
            throw new java.lang.NullPointerException();
        }
        if (!hsi88Listeners.contains(l)) {
            hsi88Listeners.addElement(l);
        }
    }

    /**
     * remove a listener.
     */
    public synchronized void removeHsi88Listener(Hsi88Listener l) {
        if (hsi88Listeners.contains(l)) {
            hsi88Listeners.removeElement(l);
        }
    }

    /**
     * return a frozen copy of the listener. Synchronized because we need the
     * underlying data structure to not change while copying.
     * 
     * \todo restrict synchronization on hsiListeners or replace with a
     * thread-safe collection.
     * 
     * @return frozen list of listeners.
     */
    @SuppressWarnings("unchecked")
    private synchronized Vector<Hsi88Listener> getCopyOfListeners() {
        return (Vector<Hsi88Listener>) hsi88Listeners.clone();
    }

    /**
     * Distribute message to all listener except originator.
     * 
     * AG changed from protected to private. Method only to be called from
     * context synchronized on this traffic controller (to ensure that all
     * listeners always get in only one message at a time, so that they need not
     * care about multiple threads?)
     * 
     * @param m message to distribute
     * @param originator original sender of message
     */
    private void notifyMessage(Hsi88Message m, Hsi88Listener originator) {
        for (Hsi88Listener listener : this.getCopyOfListeners()) {
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
     * reply can be sent at any one time.
     * 
     * @param r reply received from HSI88 interface.
     */
    private synchronized void notifyReply(Hsi88Reply r) {
        for (Hsi88Listener listener : this.getCopyOfListeners()) {
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
     * Forward a preformatted message to the interface
     * 
     * @param m
     */
    public void sendHsi88Message(Hsi88Message m) {
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
     * Forward a preformatted message to the actual interface (by calling
     * sendHsi88Message(Hsi88Message) after notifying any listeners.
     */
    public synchronized void sendHsi88Message(Hsi88Message m, Hsi88Listener replyTo) {

        if (waitingForReply) {
            try {
                wait(100); // Will wait until notify()ed or 100ms timeout
            } catch (InterruptedException e) {
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
     * @todo Where is this used? -- Can I delete it? AG
     * 
     * @param p
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
     * return the port controller, as an SerialDriverAdapter.
     * 
     * @return
     */
    /*
     * private SerialDriverAdapter getController() { return
     * (SerialDriverAdapter) controller; }
     */

    /**
     * Break connection to existing Hsi88PortController object. Once broken,
     * attempts to send via "message" member will fail.
     * 
     * @param p
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
     *             multi-system support structure
     */
    @Deprecated
    static public Hsi88TrafficController instance() {
        return null;
    }

    void setAdapterMemo(Hsi88SystemConnectionMemo adaptermemo) {
            memo = adaptermemo;
    }
    //
    //   public Hsi88SystemConnectionMemo getAdapterMemo() {
    //       return memo;
    //   }

    private Hsi88SystemConnectionMemo memo = null;
    // data members to hold the streams
    private DataInputStream istream = null;
    private OutputStream ostream = null;

    boolean endReply(Hsi88Reply msg) {
        return msg.endReply();
    }

    /**
     * flag whether reply is unsolicited
     * 
     * @todo make use of it.
     */
    private boolean unsolicited;

    private final static Logger log = LoggerFactory.getLogger(Hsi88TrafficController.class.getName());

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
     * Handle an incoming reply.
     */
    void handleOneIncomingReply() {
        // we get here if data has been received
        // fill the current reply with any data received
        int replyCurrentSize = this.reply.getNumDataElements();
        for (int i = replyCurrentSize; i < Hsi88Reply.MAXSIZE; i++) {
            try {
                if (istream.available() == 0) {
                    break; // nothing waiting to be read
                }
                byte char1 = istream.readByte();
                this.reply.setElement(i, char1);

            } catch (Exception e) {
                log.warn("Exception in DATA_AVAILABLE state: " + e);
            }
            if (endReply(this.reply)) {
                sendreply();
                break;
            }
        }
    }

    /**
     * Send the current reply - built using data from serialEvent
     */
    private void sendreply() {
        // send the reply
        synchronized (this) {
            waitingForReply = false;
            notify();
        }
        if (log.isDebugEnabled()) {
            log.debug("dispatch reply of length " + this.reply.getNumDataElements());
        }
        {
            final Hsi88Reply thisReply = this.reply;
            if (unsolicited) { // why is this done here? Could it be done earlier?
                log.debug("Unsolicited Reply");
                thisReply.setUnsolicited();
            }
            final Hsi88TrafficController thisTC = this;
            // return a notification via the queue to ensure end
            Runnable r = new Runnable() {
                Hsi88Reply replyForLater = thisReply;
                Hsi88TrafficController myTC = thisTC;

                public void run() {
                    log.debug("Delayed notify starts");
                    myTC.notifyReply(replyForLater);
                }
            };
            javax.swing.SwingUtilities.invokeLater(r);
        }

        // Create a new reply, ready to be filled
        this.reply = new Hsi88Reply();
    }
}
