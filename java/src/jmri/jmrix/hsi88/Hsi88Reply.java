package jmri.jmrix.hsi88;

import jmri.jmrix.AbstractMRReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Response from the HSI88 interface.
 *
 * @author Bob Jacobsen Copyright (C) 2001.
 * @author Andre Gruening Copyright (C) 2017: HSI88 specific implementation, in
 *         parts based previous author's Sprog implementation.
 */
public class Hsi88Reply extends AbstractMRReply {

    /**
     * Maximal size of a valid Hsi88 reply. This size is reached in ASCII mode
     * with the "i" reply: fixed size: 4: "i" + 2 digits of total number of
     * modules + final cr per module size: 6 for each module reports: 2 digits
     * for module number, 4 for module readings.
     */
    static public final int MAXSIZE = "i00".length() + Hsi88Config.MAX_MODULES * "112233".length() + "\r".length();

    /** create a new empty reply. */
    public Hsi88Reply() {
        super();
    }

    /** no need to do anything */
    @Override
    protected int skipPrefix(int index) {
        return index;
    }

    /** @return return maximal size of Hsi88 reply */
    @Override
    public int maxSize() {
        return Math.max(super.maxSize(), Hsi88Reply.MAXSIZE);
    }

    /**
     * Create a new Hsi88Reply as a deep copy of an existing Hsi88Reply
     *
     * @param m the Hsi88Reply to copy
     * @todo rewrite nicer
     * @todo and where is it used?
     */

    /*
     * @SuppressWarnings("null") public Hsi88Reply(Hsi88Reply m) { this(); if (m
     * == null) { log.error("copy ctor of null message"); return; } _nDataChars
     * = m._nDataChars; if (m.isUnsolicited()) { super.setUnsolicited(); } for
     * (int i = 0; i < _nDataChars; i++) { _dataChars[i] = m._dataChars[i]; } }
     */

    /**
     * Create a Hsi88Reply from a String. Currently only needed for ease of
     * UnitTesting.
     *
     * @param replyString a String containing the contents of the reply.
     * 
     */
    Hsi88Reply(String replyString) {

        super(replyString.substring(0, Math.min(replyString.length(), Hsi88Reply.MAXSIZE)));
    }

    /**
     * Returns a string representation of this Hsi88Reply. Deletes a final cr.
     * (An Hsi88 message may not end with cr if it reaches MAXSIZE.)
     */
    @Override
    public String toString() {

        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < _nDataChars; i++) {
            buf.append((char) (_dataChars[i] & 0xFF)); // prevent sign expansion
        }
        // delete final cr if any
        if (_dataChars[_nDataChars - 1] == '\r') {
            buf.deleteCharAt(_nDataChars - 1);
        }
        return buf.toString();
    }

    /**
     * has the end of a reply reached? Hsi88 replies end with \r or when MAXSIZE
     * has been reached.
     * 
     * @return has the end of the message been reached?
     */
    boolean end() {

        int num = this.getNumDataElements();

        if (num == 0)
            return false;
        else if (num == Hsi88Reply.MAXSIZE)
            return true;

        return (this.getElement(num - 1) == '\r');
    }

    /**
     * checks whether this reply is an 's' reply and, if so, returns the number
     * of modules reported.
     * 
     * @return number of modules reported in the reply, or a negative number if
     *         it is not a well-formatted 's' reply.
     * 
     * @note does not check whether nonnegative reported number of modules is
     *       outside valid range.
     */
    int getSetupReplyModules() {

        if (Character.toLowerCase(this.getOpCode()) != 's' ||
                this.getNumDataElements() != 4 ||
                this.getElement(3) != '\r')
            return -1;

        String payload = this.toString().substring(1, 3);

        try {
            return Integer.parseInt(payload, 16);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private final static Logger log = LoggerFactory.getLogger(Hsi88Reply.class.getName());
}
