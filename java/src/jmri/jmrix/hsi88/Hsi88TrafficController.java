package jmri.jmrix.hsi88;

import java.util.concurrent.ConcurrentLinkedQueue;
import jmri.CommandStation;
import jmri.jmrix.AbstractMRListener;
import jmri.jmrix.AbstractMRMessage;
import jmri.jmrix.AbstractMRReply;
import jmri.jmrix.AbstractMRTrafficController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts Stream-based I/O to/from Hsi88 CS2 messages. The
 * "Hsi88Interface" side sends/receives message objects.
 * <P>
 * The connection to a Hsi88PortController is via a pair of UDP Streams, which
 * then carry sequences of characters for transmission. Note that this
 * processing is handled in an independent thread.
 * <P>
 * This handles the state transistions, based on the necessary state in each
 * message.
 *
 * Based on work by Bob Jacobsen
 *
 * @author	Kevin Dickerson Copyright (C) 2012
 */
public class Hsi88TrafficController extends AbstractMRTrafficController implements Hsi88Interface, CommandStation {

    public Hsi88TrafficController() {
        super();
        if (log.isDebugEnabled()) {
            log.debug("creating a new Hsi88TrafficController object");
        }
        // set as command station too
        jmri.InstanceManager.setCommandStation(this);
        this.setAllowUnexpectedReply(true);
    }

    public void setAdapterMemo(Hsi88SystemConnectionMemo memo) {
        adaptermemo = memo;
    }

    Hsi88SystemConnectionMemo adaptermemo;

    // The methods to implement the Hsi88Interface
    public synchronized void addHsi88Listener(Hsi88Listener l) {
        this.addListener(l);
    }

    public synchronized void removeHsi88Listener(Hsi88Listener l) {
        this.removeListener(l);
    }

    @Override
    protected int enterProgModeDelayTime() {
        // we should to wait at least a second after enabling the programming track
        return 1000;
    }

    /**
     * CommandStation implementation, not yet supported
     */
    public void sendPacket(byte[] packet, int count) {

    }

    /**
     * Forward a Hsi88Message to all registered Hsi88Interface listeners.
     */
    protected void forwardMessage(AbstractMRListener client, AbstractMRMessage m) {
        ((Hsi88Listener) client).message((Hsi88Message) m);
    }

    /**
     * Forward a Hsi88Reply to all registered Hsi88Interface listeners.
     */
    protected void forwardReply(AbstractMRListener client, AbstractMRReply r) {
        ((Hsi88Listener) client).reply((Hsi88Reply) r);
    }

    /**
     * Forward a preformatted message to the actual interface.
     */
    public void sendHsi88Message(Hsi88Message m, Hsi88Listener reply) {
        sendMessage(m, reply);
    }

    /*@Override
     protected void forwardToPort(AbstractMRMessage m, AbstractMRListener reply) {
     super.forwardToPort(m, reply);
     }*/
    //Hsi88 doesn't support this function.
    protected AbstractMRMessage enterProgMode() {
        return Hsi88Message.getProgMode();
    }

    //Hsi88 doesn't support this function!
    protected AbstractMRMessage enterNormalMode() {
        return Hsi88Message.getExitProgMode();
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
        return self;
    }

    /**
     * @deprecated JMRI Since 4.4 instance() shouldn't be used, convert to JMRI multi-system support structure
     */
    @Override
    @Deprecated
    //This can be removed once multi-connection is complete
    public void setInstance() {
    }

    /**
     * @deprecated JMRI Since 4.4 instance() shouldn't be used, convert to JMRI multi-system support structure
     */
    @Deprecated
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "MS_PKGPROTECT")
    // FindBugs wants this package protected, but we're removing it when multi-connection
    // migration is complete
    final static protected Hsi88TrafficController self = null;

    protected AbstractMRReply newReply() {
        Hsi88Reply reply = new Hsi88Reply();
        return reply;
    }

    // for now, receive always OK
    @Override
    protected boolean canReceive() {
        return true;
    }

    //In theory the replies should only be 13bytes long, so the EOM is completed when the reply can take no more data
    protected boolean endOfMessage(AbstractMRReply msg) {
        return false;
    }

    static class PollMessage {

        Hsi88Listener ml;
        Hsi88Message mm;

        PollMessage(Hsi88Message mm, Hsi88Listener ml) {
            this.mm = mm;
            this.ml = ml;
        }

        Hsi88Listener getListener() {
            return ml;
        }

        Hsi88Message getMessage() {
            return mm;
        }
    }

    ConcurrentLinkedQueue<PollMessage> pollQueue = new ConcurrentLinkedQueue<PollMessage>();

    boolean disablePoll = false;

    public boolean getPollQueueDisabled() {
        return disablePoll;
    }

    public void setPollQueueDisabled(boolean poll) {
        disablePoll = poll;
    }

    /**
     * As we have to poll the tams system to get updates we put request into a
     * queue and allow the the abstrct traffic controller to handle them when it
     * is free.
     */
    public void addPollMessage(Hsi88Message mm, Hsi88Listener ml) {
        mm.setTimeout(500);
        for (PollMessage pm : pollQueue) {
            if (pm.getListener() == ml && pm.getMessage().toString().equals(mm.toString())) {
                log.debug("Message is already in the poll queue so will not add");
                return;
            }
        }
        PollMessage pm = new PollMessage(mm, ml);
        pollQueue.offer(pm);
    }

    /**
     * Removes a message that is used for polling from the queue.
     */
    public void removePollMessage(Hsi88Message mm, Hsi88Listener ml) {
        for (PollMessage pm : pollQueue) {
            if (pm.getListener() == ml && pm.getMessage().toString().equals(mm.toString())) {
                pollQueue.remove(pm);
            }
        }
    }

    /**
     * Check Tams MC for updates.
     */
    protected AbstractMRMessage pollMessage() {
        if (disablePoll) {
            return null;
        }
        if (!pollQueue.isEmpty()) {
            PollMessage pm = pollQueue.peek();
            if (pm != null) {
                return pm.getMessage();
            }
        }
        return null;
    }

    protected AbstractMRListener pollReplyHandler() {
        if (disablePoll) {
            return null;
        }
        if (!pollQueue.isEmpty()) {
            PollMessage pm = pollQueue.poll();
            if (pm != null) {
                pollQueue.offer(pm);
                return pm.getListener();
            }
        }
        return null;
    }

    public String getUserName() {
        if (adaptermemo == null) {
            return "Hsi88-CS2";
        }
        return adaptermemo.getUserName();
    }

    public String getSystemPrefix() {
        if (adaptermemo == null) {
            return "MC";
        }
        return adaptermemo.getSystemPrefix();
    }

    private final static Logger log = LoggerFactory.getLogger(Hsi88TrafficController.class.getName());
}
