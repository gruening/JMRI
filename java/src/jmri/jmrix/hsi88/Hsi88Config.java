/**
 * 
 */
package jmri.jmrix.hsi88;

/**
 * @author gruening
 *
 */
public class Hsi88Config {
    
    /** long name of hardware */ 
    public final static String LONGNAME = "Hsi88 Interface";
    
    /** short name of hardware */
    public static final String NAME = "Hsi88";
    
    /** default prefix of the hardware */
    public static final String PREFIX = "H";
    
    /** maximum number of S88 module that can be connected to an HSI88 interface. */
    public static final int MAXMODULES = 31;
    
    private Hsi88Config() {
        // no objects ever to be created. All fields and methods static.
    }

    /**
     * HSI88 can communucate either via sending ASCII messages or hex message
     **/
    public static enum Hsi88Mode {
        UNKNOWN, ASCII, HEX
    }

    /** we do not know which mode HSI88 is in on start up. */
    static volatile Hsi88Mode mode = Hsi88Mode.UNKNOWN;

    /**
     * number of modules (of 16 sensors) each attached to the left middle and
     * right chains.
     */
    static int left = 2;
    static int middle = 2;
    static int right = 2;

    /** number of module reported in the last s reply (where such number is different from zero) */
    public static int reportedModules = 0;

    static int getSetupModules() {
        return left + middle + right;
    }
}
