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
    
    /** maximum number of S88 module that can be connected to a HSI88 interface. */
    public static final int MAXMODULES = 31;
    

    private Hsi88Config() {
        // no objects ever to be created. All fields and methods static.
    }

    /**
     * HSI88 can communucate either via sending ASCII messages or hex message
     **/
    public static enum Hsi88Mode {
        ASCII, HEX
    }

    /** assume HSI88 is in hex mode on start up */
    static Hsi88Mode mode = Hsi88Mode.HEX;

    /**
     * number of moduls (of 16 sensors) each attached to the left middle and
     * right chains
     */
    static int left = 0;
    static int middle = 0;
    static int right = 0;

    static int getNumModules() {
        return left + middle + right;
    }
}
