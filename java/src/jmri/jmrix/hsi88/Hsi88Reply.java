// Hsi88Reply.java
package jmri.jmrix.hsi88;


/**
 * Carries the reply to an Hsi88Message.
 * <P>
 *
 * @author Bob Jacobsen Copyright (C) 2001, 2008
 * @author Kevin Dickerson Copyright (C) 2007
 * 
 */
public class Hsi88Reply extends jmri.jmrix.AbstractMRReply {

    // create a new one
    public Hsi88Reply() {
        super();
    }

    public Hsi88Reply(String s) {
        super(s);
    }

    public Hsi88Reply(Hsi88Reply l) {
        super(l);
    }

    // create a new one from an array
    public Hsi88Reply(int[] d) {
        //this(header);
        this();
        _nDataChars = d.length;
        for (int i = 0; i < d.length; i++) {
            _dataChars[i] = d[i];
        }
    }

    //Maximum size of a reply packet is 13 bytes.
    public int maxSize() {
        return 13;
    }

    // no need to do anything
    protected int skipPrefix(int index) {
        return index;
    }

    public int value() {
        if (isBinary()) {
            return getElement(0);
        } else {
            return super.value();
        }
    }

    @Override  // avoid stupid sign extension
    public int getElement(int n) {
        return super.getElement(n) & 0xff;
    }

    //knowing where the end is we can then determine the error code
    int endAtElement = -1;

    //An event message is Unsolicited
    public boolean isUnsolicited() {
        return !isResponse();
    }

    /**
     * Returns a hex string representation of this Hsi88Reply
     */
    public String toHexString() {

        StringBuffer buf = new StringBuffer();
        buf.append("0x" + Integer.toHexString(_dataChars[0]));
        for (int i = 1; i < _nDataChars; i++) {
            buf.append(", 0x" + Integer.toHexString(_dataChars[i]));
        }
        return buf.toString();
    }

    public boolean isResponse() {
        if ((getElement(1) & 0x01) == 0x01) {
            return true;
        }
        return false;
    }

    public int getCanDataLength() {
        return getElement(4);
    }

    public int[] getCanData() {
        int[] arr = new int[maxSize() - 5];
        for (int i = 5; i < maxSize(); i++) {
            arr[i - 5] = getElement(i);
        }
        return arr;
    }

    public int[] getCanAddress() {
        int[] arr = new int[4];
        for (int i = 5; i < 9; i++) {
            arr[i - 5] = getElement(i);
        }
        return arr;
    }

    public long getAddress() {
        long addr = (getElement(Hsi88Constants.CANADDRESSBYTE1));
        addr = (addr << 8) + (getElement(Hsi88Constants.CANADDRESSBYTE2));
        addr = (addr << 8) + (getElement(Hsi88Constants.CANADDRESSBYTE3));
        addr = (addr << 8) + (getElement(Hsi88Constants.CANADDRESSBYTE4));
        return addr;
    }

    public int getPriority() {
        return (getElement(0) >> 4);
    }

    public int getCommand() {
        int result = getElement(0) << 7;
        result = result + getElement(1) >> 1;
        return result;
    }

    public int[] getHash() {
        int[] arr = new int[2];
        arr[0] = getElement(2);
        arr[1] = getElement(3);
        return arr;
    }
}


/* @(#)Hsi88Reply.java */
