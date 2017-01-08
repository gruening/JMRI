package jmri.jmrix.hsi88.hsi88CS;

import jmri.jmrix.hsi88.Hsi88Constants.Hsi88Mode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements SerialPortAdapter for the hsi88 system.
 * <P>
 * This connects an hsi88 command station via a serial com port. Also used for
 * the USB hsi88, which appears to the computer as a serial port.
 * <P>
 * The current implementation only handles the 9,600 baud rate, and does not use
 * any other options at configuration time.
 *
 * @author Andrew Crosland Copyright (C) 2006
 */
public class Hsi88CSSerialDriverAdapter extends jmri.jmrix.hsi88.serialdriver.SerialDriverAdapter {

    public Hsi88CSSerialDriverAdapter() {
        super(Hsi88Mode.OPS);
        options.put("TrackPowerState",
                new Option("Track Power At StartUp:", new String[]{"Powered Off", "Powered On"}, true));
        //Set the username to match name, once refactored to handle multiple connections or user setable names/prefixes then this can be removed 
        this.getSystemConnectionMemo().setUserName("Hsi88 Command Station");
    }

    /**
     * @deprecated JMRI Since 4.4 instance() shouldn't be used, convert to JMRI
     *             multi-system support structure
     */
    @Deprecated
    static public Hsi88CSSerialDriverAdapter instance() {
        return null;
    }

    private final static Logger log = LoggerFactory.getLogger(Hsi88CSSerialDriverAdapter.class.getName());

}
