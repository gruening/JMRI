package jmri.jmrix.hsi88;

import jmri.jmrix.AbstractMRReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: Carries a response from the HSI88 interface.
 *
 * @author Bob Jacobsen Copyright (C) 2001
 * @author Andrew Berridge - refactored, cleaned up, Feb 2010
 * @author Andre Gruening Copyright (C) 2017 :HSI88 specific implementation based on sprog implementation
 */
public class Hsi88Reply extends AbstractMRReply {

    /**
     * Maximal size of a valid Hsi88 reply. This size is reached in ASCII mode
     * with the "i" reply: fixed size: 4: "i" + 2 digits of total number of
     * modules + final cr per module size: 6 for each module reports: 2 digits
     * for module number, 4 for module readings.
     */
    static public final int MAXSIZE = 4 + 6 * Hsi88Config.MAXMODULES;

    /** create a new empty reply. */
    public Hsi88Reply() {
        super();
    }

    /** no need to do anything */
    protected int skipPrefix(int index) {
        return index;
    }

    /**
     * Create a new Hsi88Reply as a deep copy of an existing hsi88Reply
     *
     * @param m the Hsi88Reply to copy
     * @todo rewrite nicer
     */
    @SuppressWarnings("null")
    public Hsi88Reply(Hsi88Reply m) {
        this();
        if (m == null) {
            log.error("copy ctor of null message");
            return;
        }
        _nDataChars = m._nDataChars;
        if (m.isUnsolicited()) {
            super.setUnsolicited();
        }
        for (int i = 0; i < _nDataChars; i++) {
            _dataChars[i] = m._dataChars[i];
        }
    }

    /**
     * Create a Hsi88Reply from a String
     *
     * @param replyString a String containing the contents of the reply
     */
    public Hsi88Reply(String replyString) {
        super(replyString);
    }

    /**
     * Returns a string representation of this Hsi88Reply. Deletes a final cr.
     * (An Hsi88 message may not end with cr if it reaches MAXSIZE.)
     */
    public String toString() {

        StringBuffer buf = new StringBuffer();
        for (int i : _dataChars) {
            buf.append(i & 0xFF); // prevent sign expansion
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
    public boolean endReply() {

        int num = this.getNumDataElements();

        if (num == 0)
            return false;
        else if (num == Hsi88Reply.MAXSIZE)
            return true;
        
        return (this.getElement(num - 1) == '\r');
    }

    private final static Logger log = LoggerFactory.getLogger(Hsi88Reply.class.getName());

}
