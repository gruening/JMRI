package jmri.jmrix.hsi88;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.io.DataInputStream;
import java.io.OutputStream;
import java.util.Vector;
import jmri.jmrix.AbstractPortController;
import jmri.jmrix.hsi88.Hsi88Constants.Hsi88State;
import jmri.jmrix.hsi88.serialdriver.SerialDriverAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts Stream-based I/O to/from Hsi88 messages. The "Hsi88Interface" side
 * sends/receives message objects. The connection to a Hsi88PortController is
 * via a pair of *Streams, which then carry sequences of characters for
 * transmission. Note that this processing is handled in an independent thread.
 *
 * Updated January 2010 for gnu io (RXTX) - Andrew Berridge. Comments tagged
 * with "AJB" indicate changes or observations by me
 *
 * Removed Runnable implementation and methods for it
 *
 * @author	Bob Jacobsen Copyright (C) 2001
  */
public class Hsi88TrafficController implements Hsi88Interface, SerialPortEventListener {

    private Hsi88Reply reply = new Hsi88Reply();

    private boolean waitingForReply = false;
    Hsi88Listener lastSender = null;

    private Hsi88State hsi88State = Hsi88State.NORMAL;

    public Hsi88TrafficController(Hsi88SystemConnectionMemo adaptermemo) {
       memo = adaptermemo;
    }

// The methods to implement the Hsi88Interface
    protected Vector<Hsi88Listener> cmdListeners = new Vector<Hsi88Listener>();

    public boolean status() {
        return (ostream != null && istream != null);
    }

    public synchronized void addHsi88Listener(Hsi88Listener l) {
        // add only if not already registered
        if (l == null) {
            throw new java.lang.NullPointerException();
        }
        if (!cmdListeners.contains(l)) {
            cmdListeners.addElement(l);
        }
    }

    public synchronized void removeHsi88Listener(Hsi88Listener l) {
        if (cmdListeners.contains(l)) {
            cmdListeners.removeElement(l);
        }
    }

    public Hsi88State getHsi88State() {
        return hsi88State;
    }

    public void setHsi88State(Hsi88State s) {
        this.hsi88State = s;
        if (s == Hsi88State.V4BOOTMODE) {
            // enable flow control - required for hsi88 v4 bootloader
            getController().setHandshake(SerialPort.FLOWCONTROL_RTSCTS_IN
                    | SerialPort.FLOWCONTROL_RTSCTS_OUT);

        } else {
            // disable flow control
            //AJB - removed Jan 2010 - this stops HSI88 from sending. Could cause problems with
            //serial Hsi88s, but I have no way of testing: 
            //getController().setHandshake(0);
        }
        if (log.isDebugEnabled()) {
            log.debug("Setting hsi88State " + s);
        }
    }

    public boolean isNormalMode() {
        return hsi88State == Hsi88State.NORMAL;
    }

    public boolean isSIIBootMode() {
        return hsi88State == Hsi88State.SIIBOOTMODE;
    }

    public boolean isV4BootMode() {
        return hsi88State == Hsi88State.V4BOOTMODE;
    }

    @SuppressWarnings("unchecked")
    private synchronized Vector<Hsi88Listener> getCopyOfListeners() {
        return (Vector<Hsi88Listener>) cmdListeners.clone();

    }

    protected void notifyMessage(Hsi88Message m, Hsi88Listener originator) {
        for (Hsi88Listener listener : this.getCopyOfListeners()) {
            try {
                //don't send it back to the originator!
                if (listener != originator) {
                    // skip forwarding to the last sender for now, we'll get them later
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

    protected synchronized void notifyReply(Hsi88Reply r) {
        for (Hsi88Listener listener : this.getCopyOfListeners()) {
            try {
                //if is message don't send it back to the originator!
                // skip forwarding to the last sender for now, we'll get them later
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
     */
    public void sendHsi88Message(Hsi88Message m) {
        // stream to port in single write, as that's needed by serial
        try {
            if (ostream != null) {
                ostream.write(m.getFormattedMessage(hsi88State));
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
     * SendHsi88Message(Hsi88Message) after notifying any listeners Notifies
     * listeners
     */
    public synchronized void sendHsi88Message(Hsi88Message m, Hsi88Listener replyTo) {

        if (waitingForReply) {
            try {
                wait(100);  //Will wait until notify()ed or 100ms timeout
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

    // methods to connect/disconnect to a source of data in a Hsi88PortController
    private AbstractPortController controller = null;

    /**
     * Make connection to existing PortController object.
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
     */
    protected SerialDriverAdapter getController(){
       return (SerialDriverAdapter)controller;
    }

    /**
     * Break connection to existing Hsi88PortController object. Once broken,
     * attempts to send via "message" member will fail.
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
     * @deprecated JMRI Since 4.4 instance() shouldn't be used, convert to JMRI multi-system support structure
     */
    @Deprecated
    static public Hsi88TrafficController instance() {
        return null;
    }

    static volatile protected Hsi88TrafficController self = null;

    public void setAdapterMemo(Hsi88SystemConnectionMemo adaptermemo) {
        memo = adaptermemo;
    }

    public Hsi88SystemConnectionMemo getAdapterMemo() {
        return memo;
    }

    private Hsi88SystemConnectionMemo memo = null;
    // data members to hold the streams
    DataInputStream istream = null;
    OutputStream ostream = null;

    boolean endReply(Hsi88Reply msg) {
        return msg.endNormalReply() || msg.endBootReply()
                || msg.endBootloaderReply(this.getHsi88State());
    }

    private boolean unsolicited;

    private final static Logger log = LoggerFactory.getLogger(Hsi88TrafficController.class.getName());

    /**
     * serialEvent - respond to an event triggered by RXTX. In this case we are
     * only dealing with DATA_AVAILABLE but the other events are left here for
     * reference. AJB Jan 2010
     */
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
     * Handle an incoming reply
     */
    void handleOneIncomingReply() {
        // we get here if data has been received
        //fill the current reply with any data received
        int replyCurrentSize = this.reply.getNumDataElements();
        int i;
        for (i = replyCurrentSize; i < Hsi88Reply.maxSize - replyCurrentSize; i++) {
            try {
                if (istream.available() == 0) {
                    break; //nothing waiting to be read
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
        //send the reply
        synchronized (this) {
            waitingForReply = false;
            notify();
        }
        if (log.isDebugEnabled()) {
            log.debug("dispatch reply of length " + this.reply.getNumDataElements());
        }
        {
            final Hsi88Reply thisReply = this.reply;
            if (unsolicited) {
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

        //Create a new reply, ready to be filled
        this.reply = new Hsi88Reply();

    }
}
