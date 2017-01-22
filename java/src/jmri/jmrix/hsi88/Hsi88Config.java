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
        UNKNOWN, ASCII, HEX
    }
    
    /** we do not know which mode HSI88 is in on start up. */
    static volatile Hsi88Protocol protocol = Hsi88Protocol.UNKNOWN;

    /**
     * number of modules (of 16 sensors each) attached to the left, middle and
     * right chains.
     */
    static int left = 2;
    static int middle = 2;
    static int right = 2;

    /**
     * number of modules reported in the last s reply (where such number is
     * different from zero)
     */
    public static int reportedModules = 0;

    /** @return the number of setup modules */
    static int getSetupModules() {
        return left + middle + right;
    }
}
