package jmri.jmrix.hsi88.hsi88;

import jmri.jmrix.hsi88.Hsi88Config;
import jmri.jmrix.hsi88.Hsi88Config.Hsi88Mode;
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
 * 
 *         Andre Gruening, 2017: adapted for HSI88 based on sprog
 *         implementation.
 */
public class Hsi88SerialDriverAdapter extends jmri.jmrix.hsi88.serialdriver.SerialDriverAdapter {

    public Hsi88SerialDriverAdapter() {
        super(Hsi88Mode.ASCII);
        options.put("PowerState",
                new Option("Power At StartUp:", new String[]{"Powered Off", "Powered On"}, true));
        options.put("InitialTerminalMode",
                new Option("Initial Terminal Mode:", new String[]{Hsi88Mode.ASCII.toString(), Hsi88Mode.HEX.toString()},
                        false));
        options.put("NewOption",
                new Option("New Option:", new String[]{"eins", "zwei"}));
        //Set the user name to match name, once refactored to handle multiple connections or user setable names/prefixes then this can be removed 
        this.getSystemConnectionMemo().setUserName(Hsi88Config.LONGNAME);
    }

    /**
     * @deprecated JMRI Since 4.4 instance() shouldn't be used, convert to JMRI
     *             multi-system support structure
     */
    @Deprecated
    static public Hsi88SerialDriverAdapter instance() {
        return null;
    }

    private final static Logger log = LoggerFactory.getLogger(Hsi88SerialDriverAdapter.class.getName());

}
