package jmri.jmrix.hsi88;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encodes a message to an Hsi88 interface.
 * <P>
 * The {@link Hsi88Reply} class handles the responses from the interface
 * 
 * @author Bob Jacobsen Copyright (C) 2001: Original sprog implementation used
 *         as a template
 * 
 * @author Andre Gruening Copyright (C) 2017: HSI88-specific implementation.
 */
public class Hsi88Message extends jmri.jmrix.AbstractMRMessage {

    /**
     * maximal length of Hsi88 message. This is attained for "s" command in
     * ASCII mode.
     */
    final static public int MAXLEN = 8;

    /**
     * create new empty message.
     * 
     * @param i length of message
     */
    public Hsi88Message(int i) {
        if (i < 1) {
            log.error("invalid length in call to ctor");
        }
        _nDataChars = i;
        _dataChars = new int[i];
    }

    /** create a message from a String. String must include terminating cr. */
    public Hsi88Message(String s) {
        _nDataChars = s.length();
        if (_nDataChars > Hsi88Message.MAXLEN) {
            _nDataChars = Hsi88Message.MAXLEN;
            log.info("Truncted message that was longer than MAXLEN.");
        }
        _dataChars = new int[_nDataChars];
        for (int i = 0; i < _nDataChars; i++) {
            _dataChars[i] = s.charAt(i);
        }
    }

    /**
     * Copy one.
     * 
     * @todo where/why do we need this?
     * 
     * @param m
     */
    @SuppressWarnings("null")
    public Hsi88Message(Hsi88Message m) {
        if (m == null) {
            log.error("copy ctor of null message");
            return;
        }
        _nDataChars = m._nDataChars;
        _dataChars = new int[_nDataChars];
        for (int i = 0; i < _nDataChars; i++) {
            _dataChars[i] = m._dataChars[i];
        }
    }

    /**
     * Get formatted message for direct output to stream - this is the final
     * format of the message as a byte array. Note the terminating cr must be
     * contained in the message already. It will not be appended here.
     *
     * @return the formatted message as a byte array
     */
    public byte[] getFormattedMessage() {
        int len = this.getNumDataElements();

        byte msg[] = new byte[len];

        for (int i = 0; i < len; i++) {
            msg[i] = (byte) this.getElement(i);
        }

        return msg;
    }

    private final static Logger log = LoggerFactory.getLogger(Hsi88Message.class.getName());

    public static Hsi88Message cmdVersion() {
        return new Hsi88Message("v\r");
    }

    public static Hsi88Message cmdTerminal() {
        return new Hsi88Message("t\r");
    }

    public static Hsi88Message cmdQuery() {
        return new Hsi88Message("m\r");
    }

    private static String byteTo2Hex(byte i) {

        String str = Integer.toHexString(i);
        if (str.length() == 2)
            return str;
        else
            return "0" + str;
    }

    public static Hsi88Message cmdSetup(int left, int middle, int right) {

        Hsi88Message setup = new Hsi88Message(
                "s" + byteTo2Hex((byte) left) + byteTo2Hex((byte) middle) + byteTo2Hex((byte) right) + '\r');
        return setup;
    }

}
