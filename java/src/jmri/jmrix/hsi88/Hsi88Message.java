// Hsi88Message.java
package jmri.jmrix.hsi88;


/**
 * Encodes a message to an Hsi88 command station.
 * <P>
 * The {@link Hsi88Reply} class handles the response from the command station.
 * <P>
 *
 * @author	Kevin Dickerson Copyright (C) 2001, 2008
 */
/*Packages of length 13 are interpreted as can-bus packages: 4 bytes
 Can-bus-ID (BigEndian or network order), 1-byte length and 8 bytes of data, if necessary with null bytes
 to fill in are.*/

/*The message ID is divided into the areas of lower priority (priority), command (command), response
 and hash. The communication is based on the following format:
 Prio - 2 +2bit
 Command 8 bit
 Resp - 1 bit
 Hash - 16bit
 DLC - 4bit (ie CAN message length)
 CAN message 8 BYTES
 Can Message Bytes 0 to 3 are the address bytes, with byte 0 High, byte 3 low
 */
public class Hsi88Message extends jmri.jmrix.AbstractMRMessage {

    static int MY_UID = 0x12345678;

    Hsi88Message() {
        _dataChars = new int[13];
        _nDataChars = 13;
        setBinary(true);
        for (int i = 0; i < 13; i++) {
            _dataChars[i] = 0x00;
        }
    }

    // create a new one from an array
    public Hsi88Message(int[] d) {
        this();
        for (int i = 0; i < d.length; i++) {
            _dataChars[i] = d[i];
        }
    }

    // create a new one from a byte array, as a service
    public Hsi88Message(byte[] d) {
        this();
        for (int i = 0; i < d.length; i++) {
            _dataChars[i] = d[i] & 0xFF;
        }
    }

    // create a new one
    public Hsi88Message(int i) {
        this();
    }

    // copy one
    public Hsi88Message(Hsi88Message m) {
        super(m);
    }

    // from String
    /*public  Hsi88Message(String m) {
     super(m);
     }*/
    // static methods to return a formatted message
    static public Hsi88Message getEnableMain() {
        Hsi88Message m = new Hsi88Message();
        m.setElement(0, Hsi88Constants.SYSCOMMANDSTART & 0xFF);
        m.setElement(1, 0x00 & 0xFF);
        m.setElement(2, Hsi88Constants.HASHBYTE1 & 0xFF);
        m.setElement(3, Hsi88Constants.HASHBYTE2 & 0xFF);
        m.setElement(4, 0x05 & 0xFF); //five bytes;
        //5, 6, 7, 8 Address but this is a global command
        m.setElement(9, Hsi88Constants.CMDGOSYS & 0xFF); //Turn main on 0x01
        return m;
    }

    static public Hsi88Message getKillMain() {
        Hsi88Message m = new Hsi88Message();
        m.setElement(0, Hsi88Constants.SYSCOMMANDSTART & 0xFF);
        m.setElement(1, 0x00 & 0xFF);
        m.setElement(2, Hsi88Constants.HASHBYTE1 & 0xFF);
        m.setElement(3, Hsi88Constants.HASHBYTE2 & 0xFF);
        m.setElement(4, 0x05 & 0xFF); //five bytes;
        //5, 6, 7, 8 Address but this is a global command
        m.setElement(9, Hsi88Constants.CMDSTOPSYS & 0xFF); //Turn main off 0x00
        return m;
    }

    //static public Hsi88Message get
    static public Hsi88Message getSetTurnout(int addr, int state, int power) {
        Hsi88Message m = new Hsi88Message();
        m.setElement(0, (Hsi88Constants.ACCCOMMANDSTART >> 7) & 0xFF);
        m.setElement(1, (Hsi88Constants.ACCCOMMANDSTART << 1) & 0xFF);
        m.setElement(2, Hsi88Constants.HASHBYTE1 & 0xFF);
        m.setElement(3, Hsi88Constants.HASHBYTE2 & 0xFF);
        m.setElement(4, 0x06 & 0xFF); //five bytes;
        m.setElement(Hsi88Constants.CANADDRESSBYTE1, (addr >> 24) & 0xFF);
        m.setElement(Hsi88Constants.CANADDRESSBYTE2, (addr >> 16) & 0xFF);
        m.setElement(Hsi88Constants.CANADDRESSBYTE3, (addr >> 8) & 0xFF);
        m.setElement(Hsi88Constants.CANADDRESSBYTE4, (addr) & 0xFF);
        m.setElement(9, state & 0xff);
        m.setElement(10, power & 0xff);
        return m;
    }

    static public Hsi88Message getQryLocoSpeed(int addr) {
        Hsi88Message m = new Hsi88Message();
        m.setElement(0, (Hsi88Constants.LOCOSPEED >> 7) & 0xFF);
        m.setElement(1, (Hsi88Constants.LOCOSPEED << 1) & 0xFF);
        m.setElement(2, Hsi88Constants.HASHBYTE1 & 0xFF);
        m.setElement(3, Hsi88Constants.HASHBYTE2 & 0xFF);
        m.setElement(4, 0x04 & 0xFF);
        m.setElement(Hsi88Constants.CANADDRESSBYTE1, (addr >> 24) & 0xFF);
        m.setElement(Hsi88Constants.CANADDRESSBYTE2, (addr >> 16) & 0xFF);
        m.setElement(Hsi88Constants.CANADDRESSBYTE3, (addr >> 8) & 0xFF);
        m.setElement(Hsi88Constants.CANADDRESSBYTE4, (addr) & 0xFF);
        return m;
    }

    static public Hsi88Message setLocoSpeed(int addr, int speed) {
        Hsi88Message m = new Hsi88Message();
        m.setElement(0, (Hsi88Constants.LOCOSPEED >> 7) & 0xFF);
        m.setElement(1, (Hsi88Constants.LOCOSPEED << 1) & 0xFF);
        m.setElement(2, Hsi88Constants.HASHBYTE1 & 0xFF);
        m.setElement(3, Hsi88Constants.HASHBYTE2 & 0xFF);
        m.setElement(4, 0x06 & 0xFF);
        m.setElement(Hsi88Constants.CANADDRESSBYTE1, (addr >> 24) & 0xFF);
        m.setElement(Hsi88Constants.CANADDRESSBYTE2, (addr >> 16) & 0xFF);
        m.setElement(Hsi88Constants.CANADDRESSBYTE3, (addr >> 8) & 0xFF);
        m.setElement(Hsi88Constants.CANADDRESSBYTE4, (addr) & 0xFF);
        m.setElement(9, (speed >> 8) & 0xff);
        m.setElement(10, speed & 0xff);
        return m;
    }

    static public Hsi88Message setLocoEmergencyStop(int addr) {
        Hsi88Message m = new Hsi88Message();
        m.setElement(0, Hsi88Constants.SYSCOMMANDSTART & 0xFF);
        m.setElement(1, 0x00 & 0xFF);
        m.setElement(2, Hsi88Constants.HASHBYTE1 & 0xFF);
        m.setElement(3, Hsi88Constants.HASHBYTE2 & 0xFF);
        m.setElement(4, 0x05 & 0xFF); //five bytes;
        m.setElement(Hsi88Constants.CANADDRESSBYTE1, (addr >> 24) & 0xFF);
        m.setElement(Hsi88Constants.CANADDRESSBYTE2, (addr >> 16) & 0xFF);
        m.setElement(Hsi88Constants.CANADDRESSBYTE3, (addr >> 8) & 0xFF);
        m.setElement(Hsi88Constants.CANADDRESSBYTE4, (addr) & 0xFF);
        m.setElement(9, Hsi88Constants.LOCOEMERGENCYSTOP & 0xFF);
        return m;
    }

    static public Hsi88Message setLocoSpeedSteps(int addr, int step) {
        Hsi88Message m = new Hsi88Message();
        m.setElement(0, Hsi88Constants.SYSCOMMANDSTART & 0xFF);
        m.setElement(1, 0x00 & 0xFF);
        m.setElement(2, Hsi88Constants.HASHBYTE1 & 0xFF);
        m.setElement(3, Hsi88Constants.HASHBYTE2 & 0xFF);
        m.setElement(4, 0x05 & 0xFF); //five bytes;
        m.setElement(Hsi88Constants.CANADDRESSBYTE1, (addr >> 24) & 0xFF);
        m.setElement(Hsi88Constants.CANADDRESSBYTE2, (addr >> 16) & 0xFF);
        m.setElement(Hsi88Constants.CANADDRESSBYTE3, (addr >> 8) & 0xFF);
        m.setElement(Hsi88Constants.CANADDRESSBYTE4, (addr) & 0xFF);
        m.setElement(9, 0x05 & 0xFF);
        m.setElement(10, step & 0xFF);
        return m;
    }

    static public Hsi88Message getQryLocoDirection(int addr) {
        Hsi88Message m = new Hsi88Message();
        m.setElement(0, (Hsi88Constants.LOCODIRECTION >> 7) & 0xFF);
        m.setElement(1, (Hsi88Constants.LOCODIRECTION << 1) & 0xFF);
        m.setElement(2, Hsi88Constants.HASHBYTE1 & 0xFF);
        m.setElement(3, Hsi88Constants.HASHBYTE2 & 0xFF);
        m.setElement(4, 0x04 & 0xFF);
        m.setElement(Hsi88Constants.CANADDRESSBYTE1, (addr >> 24) & 0xFF);
        m.setElement(Hsi88Constants.CANADDRESSBYTE2, (addr >> 16) & 0xFF);
        m.setElement(Hsi88Constants.CANADDRESSBYTE3, (addr >> 8) & 0xFF);
        m.setElement(Hsi88Constants.CANADDRESSBYTE4, (addr) & 0xFF);
        return m;
    }

    static public Hsi88Message setLocoDirection(int addr, int dir) {
        Hsi88Message m = new Hsi88Message();
        m.setElement(0, (Hsi88Constants.LOCODIRECTION >> 7) & 0xFF);
        m.setElement(1, (Hsi88Constants.LOCODIRECTION << 1) & 0xFF);
        m.setElement(2, Hsi88Constants.HASHBYTE1 & 0xFF);
        m.setElement(3, Hsi88Constants.HASHBYTE2 & 0xFF);
        m.setElement(4, 0x05 & 0xFF);
        m.setElement(Hsi88Constants.CANADDRESSBYTE1, (addr >> 24) & 0xFF);
        m.setElement(Hsi88Constants.CANADDRESSBYTE2, (addr >> 16) & 0xFF);
        m.setElement(Hsi88Constants.CANADDRESSBYTE3, (addr >> 8) & 0xFF);
        m.setElement(Hsi88Constants.CANADDRESSBYTE4, (addr) & 0xFF);
        m.setElement(9, dir & 0xff);
        return m;
    }

    static public Hsi88Message getQryLocoFunction(int addr, int funct) {
        Hsi88Message m = new Hsi88Message();
        m.setElement(0, (Hsi88Constants.LOCOFUNCTION >> 7) & 0xFF);
        m.setElement(1, (Hsi88Constants.LOCOFUNCTION << 1) & 0xFF);
        m.setElement(2, Hsi88Constants.HASHBYTE1 & 0xFF);
        m.setElement(3, Hsi88Constants.HASHBYTE2 & 0xFF);
        m.setElement(4, 0x05 & 0xFF);
        m.setElement(Hsi88Constants.CANADDRESSBYTE1, (addr >> 24) & 0xFF);
        m.setElement(Hsi88Constants.CANADDRESSBYTE2, (addr >> 16) & 0xFF);
        m.setElement(Hsi88Constants.CANADDRESSBYTE3, (addr >> 8) & 0xFF);
        m.setElement(Hsi88Constants.CANADDRESSBYTE4, (addr) & 0xFF);
        m.setElement(9, (funct) & 0xFF);
        return m;
    }

    static public Hsi88Message setLocoFunction(int addr, int funct, int state) {
        Hsi88Message m = new Hsi88Message();
        m.setElement(0, (Hsi88Constants.LOCOFUNCTION >> 7) & 0xFF);
        m.setElement(1, (Hsi88Constants.LOCOFUNCTION << 1) & 0xFF);
        m.setElement(2, Hsi88Constants.HASHBYTE1 & 0xFF);
        m.setElement(3, Hsi88Constants.HASHBYTE2 & 0xFF);
        m.setElement(4, 0x06 & 0xFF);
        m.setElement(Hsi88Constants.CANADDRESSBYTE1, (addr >> 24) & 0xFF);
        m.setElement(Hsi88Constants.CANADDRESSBYTE2, (addr >> 16) & 0xFF);
        m.setElement(Hsi88Constants.CANADDRESSBYTE3, (addr >> 8) & 0xFF);
        m.setElement(Hsi88Constants.CANADDRESSBYTE4, (addr) & 0xFF);
        m.setElement(9, funct & 0xff);
        m.setElement(10, state & 0xff);
        m.getAddress();
        return m;
    }

    static public Hsi88Message sensorPollMessage(int module) {
        Hsi88Message m = new Hsi88Message();
        m.setElement(0, (Hsi88Constants.FEECOMMANDSTART >> 7) & 0xFF);
        m.setElement(1, (Hsi88Constants.FEECOMMANDSTART << 1) & 0xFF);
        m.setElement(2, Hsi88Constants.HASHBYTE1 & 0xFF);
        m.setElement(3, Hsi88Constants.HASHBYTE2 & 0xFF);
        m.setElement(4, 0x05 & 0xFF); //five bytes;
        m.setElement(Hsi88Constants.CANADDRESSBYTE1, (MY_UID >> 24) & 0xFF);
        m.setElement(Hsi88Constants.CANADDRESSBYTE2, (MY_UID >> 16) & 0xFF);
        m.setElement(Hsi88Constants.CANADDRESSBYTE3, (MY_UID >> 8) & 0xFF);
        m.setElement(Hsi88Constants.CANADDRESSBYTE4, (MY_UID) & 0xFF);
        m.setElement(9, module & 0xFF);
        return m;
    }

    public long getAddress() {
        long addr = getElement(Hsi88Constants.CANADDRESSBYTE1);
        addr = (addr << 8) + getElement(Hsi88Constants.CANADDRESSBYTE2);
        addr = (addr << 8) + getElement(Hsi88Constants.CANADDRESSBYTE3);
        addr = (addr << 8) + getElement(Hsi88Constants.CANADDRESSBYTE4);

        return addr;
    }

    static public Hsi88Message getProgMode() {
        return new Hsi88Message();
    }

    static public Hsi88Message getExitProgMode() {
        return new Hsi88Message();
    }

    static public Hsi88Message getReadPagedCV(int cv) { //Rxxx
        return new Hsi88Message();
    }

    static public Hsi88Message getWritePagedCV(int cv, int val) { //Pxxx xxx
        return new Hsi88Message();
    }

    static public Hsi88Message getReadRegister(int reg) { //Vx
        return new Hsi88Message();
    }

    static public Hsi88Message getWriteRegister(int reg, int val) { //Sx xxx
        return new Hsi88Message();
    }

    static public Hsi88Message getReadDirectCV(int cv) { //Rxxx
        return new Hsi88Message();
    }

    static public Hsi88Message getWriteDirectCV(int cv, int val) { //Pxxx xxx
        return new Hsi88Message();
    }
}

/* @(#)Hsi88Message.java */
