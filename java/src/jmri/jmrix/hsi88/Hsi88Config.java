/**
 * 
 */
package jmri.jmrix.hsi88;

/**
 * This class encapsulates in one place 1. constants for use with the Hsi88
 * interface 2. the state of the Hsi88 interface.
 * 
 * @author Andre Gruening Copyright (C) 2017.
 *
 */
public class Hsi88Config {

    /** long name of hardware */
    public final static String LONGNAME = "Hsi88 Interface";

    /** short name of hardware */
    public static final String NAME = "Hsi88";

    /** default prefix of the hardware */
    public static final String PREFIX = "H";

    /**
     * maximum number of S88 modules that can be connected to an HSI88 interface.
     */
    public static final int MAXMODULES = 31;

    /**
     * HSI88 can communicate either via sending ASCII messages or HEX message.
     **/
    public static enum Hsi88Protocol {
        HEX, ASCII, UNKNOWN;
    }
    
    /** we do not know which mode HSI88 is in on start up. */
    private static Hsi88Protocol protocol = Hsi88Protocol.UNKNOWN;

    /**
     * number of modules (of 16 sensors each) attached to the left, middle and
     * right chains.
     */
    private static int left = 2;
    private static int middle = 2;
    private static int right = 2;

    /**
     * number of modules reported in the last s reply (where such number is
     * different from zero)
     */
    private static int reportedModules = 0;

    /** @return the number of setup modules */
    static int getSetupModules() {
        return getLeft() + getMiddle() + getRight();
    }

    /**
     * @return the protocol
     */
    public static Hsi88Protocol getProtocol() {
        return protocol;
    }

    /**
     * @param protocol the protocol to set
     */
    public static void setProtocol(Hsi88Protocol protocol) {
        Hsi88Config.protocol = protocol;
    }

    /**
     * @return the left
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

    /**
     * @return the reportedModules
     */
    public static int getReportedModules() {
        return reportedModules;
    }

    /**
     * @param reportedModules the reportedModules to set
     */
    public static void setReportedModules(int reportedModules) {
        Hsi88Config.reportedModules = reportedModules;
    }
}
