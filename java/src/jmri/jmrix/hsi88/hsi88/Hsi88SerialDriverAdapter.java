package jmri.jmrix.hsi88.hsi88;

import jmri.jmrix.hsi88.Hsi88Config;
import jmri.jmrix.hsi88.Hsi88Manager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements SerialPortAdapter for the Hsi88 system.
 * <P>
 * This connects an Hsi88 interface via a serial com port. Also used for the USB
 * Hsi88, which appears to the computer as a serial port.
 * <P>
 * The current implementation only handles the 9,600 baud rate, and does not use
 * any other options at configuration time.
 *
 * @author Andrew Crosland Copyright (C) 2006
 * @author Andre Gruening, 2017: adapted for HSI88 based on previous author's
 *         Sprog implementation.
 * 
 * @since 4.6.x
 */
public class Hsi88SerialDriverAdapter extends jmri.jmrix.hsi88.serialdriver.SerialDriverAdapter {

    private final static String[] makeChainLengths() {
        String[] lengths = new String[Hsi88Config.MAX_MODULES + 1];
        for (int i = 0; i <= Hsi88Config.MAX_MODULES; i++) {
            lengths[i] = Integer.toString(i);
        }
        return lengths;
    }

    private final String[] lengths = makeChainLengths();

    /** set Hsi88 specific options */
    public Hsi88SerialDriverAdapter() {
        super();
        options.put("RightChain",
                new Option("Right Chain: # Modules:", lengths, true));
        options.put("MiddleChain",
                new Option("Middle Chain: # Modules:", lengths, true));
        options.put("LeftChain",
                new Option("Left Chain: # Modules:", lengths, true));

        // TODO Set the user name to match name, once refactored to handle multiple connections or user setable names/prefixes then this can be removed 
        this.getSystemConnectionMemo().setUserName(Hsi88Config.LONGNAME);
    }

    /**
     * @deprecated JMRI Since 4.4 instance() shouldn't be used, convert to JMRI
     *             multi-system support structure
     */
    /*
     * @Deprecated static public Hsi88SerialDriverAdapter instance() { return
     * null; }
     */

    /** read Hsi88 specific options */
    @Override
    public void configure() {
        
        String leftStr = getOptionState("LeftChain");
        if (leftStr != null) {
            Hsi88Config.setLeft(Hsi88Manager.parseChainLength(leftStr, 10));
        }
        String middleStr = getOptionState("MiddleChain");
        if (middleStr != null) {
            Hsi88Config.setMiddle(Hsi88Manager.parseChainLength(middleStr, 10));
        }
        String rightStr = getOptionState("RightChain");
        if (rightStr != null) {
            Hsi88Config.setMiddle(Hsi88Manager.parseChainLength(rightStr, 10));
        }
        super.configure();
    }

    private final static Logger log = LoggerFactory.getLogger(Hsi88SerialDriverAdapter.class.getName());

}
