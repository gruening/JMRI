/**
 * 
 */
package jmri.jmrix.hsi88;

/**
 * @author gruening
 *
 */
public class Hsi88Config {
    
    public final static String LONGNAME = "Hsi88 Interface";
    public static final String NAME = "Hsi88";
    public static final String PREFIX = "H";
    

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
