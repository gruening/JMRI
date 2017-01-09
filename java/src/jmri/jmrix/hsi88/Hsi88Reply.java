package jmri.jmrix.hsi88;

import jmri.jmrix.AbstractMRReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * hsi88Reply.java
 *
 * Description: Carries the reply to a hsi88Message
 *
 * @author Bob Jacobsen Copyright (C) 2001
 * @author Andrew Berridge - refactored, cleaned up, Feb 2010
 * @author Andre Gruening Copyright (C) 2017 : adapted from Sprog to HSI.
 */
public class Hsi88Reply extends AbstractMRReply {

    /** @todo: what is maxSize of hsi88 reply? */
    static public final int MAXSIZE = 515;

    /** create a new one */
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
     * Is this reply a read from the sensors?
     * 
     * @todo simplify
     */
    boolean isReading() {
        return (this.toString().charAt(0) == 'i' || this.toString().charAt(0) == 'm');
    }

    /**
     * Returns a string representation of this hsi88Reply
     */
    public String toString() {

        StringBuffer buf = new StringBuffer();
        for (int i : _dataChars) {
            buf.append((char) i);
        }
        return buf.toString();
    }

    /**
     * hsi88 replies end with a reply that is \r terminated.
     */
    public boolean endReply() {

        int num = this.getNumDataElements();

        if (num == 0)
            return false;

        return this.getElement(num - 1) == '\r';
    }

    private final static Logger log = LoggerFactory.getLogger(Hsi88Reply.class.getName());
}
