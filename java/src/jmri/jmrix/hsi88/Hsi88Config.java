package jmri.jmrix.hsi88;

/**
 * This class encapsulates in one place constants for use with the Hsi88
 * interface.
 * 
 * @author Andre Gruening Copyright (C) 2017.
 *
 */
public class Hsi88Config {

    // constants for JMRI:

    /** short name of hardware */
    public static final String NAME = "Hsi88";

    /** long name of hardware */
    public final static String LONGNAME = NAME + " Interface";

    /** default prefix of the hardware */
    public static final String PREFIX = "H";

    // constants from the hardware:

    /** Maximum number of S88 modules that a HSI88 can take. */
    public static final int MAX_MODULES = 31;

    /** Default length of one S88 chain. */
    public static final int DEFAULT_MODULES = 2;

    /** HSI88 will send/read either ASCII or HEX messages. */
    public static enum Hsi88Protocol {
        HEX, ASCII, UNKNOWN;
    }

    // variables describing the setup of the Hsi88 

    /**
     * number of modules (of 16 sensors each) attached to the left, middle and
     * right chains.
     */
    private static int left = DEFAULT_MODULES;
    private static int middle = DEFAULT_MODULES;
    private static int right = DEFAULT_MODULES;

    /** @return the number of setup modules */
    static int getSetupModules() {
        return getLeft() + getMiddle() + getRight();
    }

    /**
     * @return length of left chain (as per setup).
     */
    public static int getLeft() {
        return left;
    }

    /**
     * @param left the left to set
     */
    public static void setLeft(int left) {
        Hsi88Config.left = left;
    }

    /**
     * @return the middle
     */
    public static int getMiddle() {
        return middle;
    }

    /**
     * @param middle the middle to set
     */
    public static void setMiddle(int middle) {
        Hsi88Config.middle = middle;
    }

    /**
     * @return the right
     */
    public static int getRight() {
        return right;
    }

    /**
     * @param right the right to set
     */
    public static void setRight(int right) {
        Hsi88Config.right = right;
    }
}
