/**
 * 
 */
package jmri.jmrix.hsi88;

/**
 * @author gruening
 *
 */
public class Hsi88Setup {
    
    private Hsi88Setup() {
        // no objects ever to be created. All fields and methods static.
    }

    /** HSI88 can communucate either via sending ASCII messages or hex message **/
    public static enum Hsi88Mode {
        ASCII, HEX
    }
    
    /** assume HSI88 is in hex mode on start up */
    static Hsi88Mode mode = Hsi88Mode.HEX;
    
}
