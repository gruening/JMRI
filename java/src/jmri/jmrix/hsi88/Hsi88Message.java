package jmri.jmrix.hsi88;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encodes a message to an HSI88 command station.
 * <P>
 * The {@link Hsi88Reply} class handles the response from the command station.
 * 
 * @author Bob Jacobsen Copyright (C) 2001: Original sprog implementation used
 *         as a template
 * 
 * @author Andre Gruening Copyright (C) 2017: HSI88-specific implementation.
 */
public class Hsi88Message extends jmri.jmrix.AbstractMRMessage {

    /** opcodes of available HSI88 commands */
    public static enum Command {
        /**
         * toggles terminal and hex modes. HSI88 after power on is in hex mode
         */
        Terminal('t', 2, 2),
        /** sets number of S88 modules on each line */
        Setup('s', 5, 8),
        /** requests all sensor states */
        Query('m', 2, 2),
        /** requests version information */
        Version('v', 2, 2);

        private final char _opcode;
        private final int _hexLength;
        private final int _terminalLength;

        Command(char opcode, int hexLength, int terminalLength) {
            _opcode = opcode;
            _hexLength = hexLength;
            _terminalLength = terminalLength;
        }

        /** Length of longest command. */
        public static final int MAXSIZE_TERMINAL = 8;

    }

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

    /**
     * Creates a new Hsi88Message containing a byte array to represent a packet
     * to output
     *
     * @param packet The contents of the packet
     * 
     * @todo needed only for HexMode.
     */
    public Hsi88Message(byte[] packet) {
        this(1 + (packet.length * 3));
        int i = 0; // counter of byte in output message
        int j = 0; // counter of byte in input packet

        this.setElement(i++, 'O'); // "O " starts output packet

        // add each byte of the input message
        for (j = 0; j < packet.length; j++) {
            this.setElement(i++, ' ');
            String s = Integer.toHexString(packet[j] & 0xFF).toUpperCase();
            if (s.length() == 1) {
                this.setElement(i++, '0');
                this.setElement(i++, s.charAt(0));
            } else {
                this.setElement(i++, s.charAt(0));
                this.setElement(i++, s.charAt(1));
            }
        }

    }

    /** create a message from a String */
    public Hsi88Message(String s) {
        _nDataChars = s.length();
        _dataChars = new int[_nDataChars];
        for (int i = 0; i < _nDataChars; i++) {
            _dataChars[i] = s.charAt(i);
        }
    }

    /**
     * create a message from an opcode
     * 
     * @param c
     * @param argv
     * @param
     * @param j
     * @param i
     */
    public Hsi88Message(Command c, int... argv) {
        // if c == Setup display error message.
        _nDataChars = c._terminalLength;
        _dataChars = new int[_nDataChars];
        _dataChars[0] = c._opcode;
        _dataChars[1] = '\r';
    }

    /** create a message from opcode and arguments */
    public Hsi88Message(Command c, char[] argv) {
        // if length are incompatible error message
        _nDataChars = c._terminalLength;
        _dataChars = new int[_nDataChars];
        _dataChars[0] = c._opcode;

        int i = 1;
        for (char arg : argv) {
            String str = Integer.toBinaryString(arg);
            if (arg < 16) {
                _dataChars[i] = '0';
                _dataChars[i + 1] = str.charAt(0);
            } else {
                _dataChars[i] = str.charAt(0);
                _dataChars[i + 1] = str.charAt(1);
            }
        }
    }

    // copy one
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
     * format of the message as a byte array
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

}
