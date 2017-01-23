package jmri.jmrix.hsi88;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encodes a message to an Hsi88 interface. The {@link Hsi88Reply} class handles
 * the responses from the interface
 * 
 * @author Bob Jacobsen Copyright (C) 2001
 * @author Andre Gruening Copyright (C) 2017: HSI88-specific implementation,
 *         in parts based on previous author's Sprog implementation.
 */
public class Hsi88Message extends jmri.jmrix.AbstractMRMessage {

    /**
     * maximal length of Hsi88 message. This is attained for "s" command in
     * ASCII mode:
     */
    final static public int MAXLEN = "s112233\r".length();

    /**
     * create new empty message.
     * 
     * @param i length of message
     */
    /*
     * public Hsi88Message(int i) { if ((i < 1) || (i > Hsi88Message.MAXLEN)) {
     * log.error("invalid length in call to ctor"); i = Hsi88Message.MAXLEN; }
     * _nDataChars = i; _dataChars = new int[i]; }
     *
     * 
     * /** create a message from a String. @note String must include terminating
     * cr.
     */
    public Hsi88Message(String s) {
        _nDataChars = s.length();
        if (_nDataChars > Hsi88Message.MAXLEN) {
            _nDataChars = Hsi88Message.MAXLEN;
            log.info("Truncted message was longer than MAXLEN.");
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
    /*
     * @SuppressWarnings("null") public Hsi88Message(Hsi88Message m) { if (m ==
     * null) { log.error("copy ctor of null message"); return; } _nDataChars =
     * m._nDataChars; _dataChars = new int[_nDataChars]; for (int i = 0; i <
     * _nDataChars; i++) { _dataChars[i] = m._dataChars[i]; } }
     */

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

    /**
     * create command to request version information.
     * 
     * @return version command
     */
    public static Hsi88Message cmdVersion() {
        return new Hsi88Message("v\r");
    }

    /**
     * create command to toggle terminal mode between ASCII and HEX
     * 
     * @return terminal toggle command.
     */
    public static Hsi88Message cmdTerminal() {
        return new Hsi88Message("t\r");
    }

    /**
     * create command to request update from all sensors.
     * 
     * @return update command
     */
    public static Hsi88Message cmdQuery() {
        return new Hsi88Message("m\r");
    }

    /**
     * convert byte into 2-digit hex representation
     * 
     * @param i value to convert
     * @return string of 2-digit hex presenation of i
     */
    private static String byteToHex(byte i) {

        String str = Integer.toHexString(i);
        if (str.length() == 2)
            return str;
        else
            return "0" + str;
    }

    /**
     * create command to set up Hsi88 chain lengths.
     * 
     * @param left number of modules on left chain.
     * @param middle number of modules on middle chain.
     * @param right number of modules on right chain
     * @return command to setup chain length.
     * 
     * @note providing 0 for all chains switches the s88 chain sweeping off.
     * 
     */
    public static Hsi88Message cmdSetup(int left, int middle, int right) {

        Hsi88Message setup = new Hsi88Message(
                "s" + byteToHex((byte) left) + byteToHex((byte) middle) + byteToHex((byte) right) + '\r');
        return setup;
    }

    /**
     * create command to setup Hsi88 with chain lengths as specifies in the JMRI
     * preferences panel. Can be regarded as the powerOn command.
     * 
     * @return setup command with chain length according to JMRI preferences.
     */
    public static Hsi88Message powerOn() {
        return cmdSetup(Hsi88Config.left, Hsi88Config.middle, Hsi88Config.right);
    }

    /**
     * create command to switch off S88 chain sweeping. Can be recarded as a
     * powerOff command.
     * 
     * @return command to end s88 chain sweeping.
     */
    public static Hsi88Message powerOff() {
        return cmdSetup(0, 0, 0);
    }
}
