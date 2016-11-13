package jmri.jmrix.hsi88;

import jmri.DccLocoAddress;
import jmri.DccThrottle;
import jmri.LocoAddress;
import jmri.jmrix.AbstractThrottleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hsi88DCC implementation of a ThrottleManager.
 * <P>
 * Based on early NCE code.
 *
 *
 * Based on work by Bob Jacobsen
 *
 * @author	Kevin Dickerson Copyright (C) 2012
 */
public class Hsi88ThrottleManager extends AbstractThrottleManager implements Hsi88Listener {

    /**
     * Constructor.
     */
    public Hsi88ThrottleManager(Hsi88SystemConnectionMemo memo) {
        super(memo);
    }

    /**
     * @deprecated JMRI Since 4.4 instance() shouldn't be used, convert to JMRI multi-system support structure
     */
    @Deprecated
    static private Hsi88ThrottleManager mInstance = null;

    /**
     * @deprecated JMRI Since 4.4 instance() shouldn't be used, convert to JMRI multi-system support structure
     */
    @Deprecated
    static public Hsi88ThrottleManager instance() {
        return mInstance;
    }

    public void reply(Hsi88Reply m) {
        //We are not sending commands from here yet!
    }

    public void message(Hsi88Message m) {
        // messages are ignored
    }

    public void requestThrottleSetup(LocoAddress address, boolean control) {
        /*Here we do not set notifythrottle, we simply create a new Hsi88 throttle.
         The Hsi88 throttle in turn will notify the throttle manager of a successful or
         unsuccessful throttle connection. */
        log.debug("new Hsi88Throttle for " + address);
        notifyThrottleKnown(new Hsi88Throttle((Hsi88SystemConnectionMemo) adapterMemo, (DccLocoAddress) address), address);
    }

    @Override
    public boolean hasDispatchFunction() {
        return false;
    }

    /**
     * Address 100 and above is a long address
     *
     */
    public boolean canBeLongAddress(int address) {
        return isLongAddress(address);
    }

    /**
     * Address 99 and below is a short address
     *
     */
    public boolean canBeShortAddress(int address) {
        return !isLongAddress(address);
    }

    /**
     * Are there any ambiguous addresses (short vs long) on this system?
     */
    public boolean addressTypeUnique() {
        return false;
    }

    @Override
    protected boolean singleUse() {
        return false;
    }

    public String[] getAddressTypes() {
        return new String[]{
            LocoAddress.Protocol.DCC.getPeopleName(),
            LocoAddress.Protocol.MFX.getPeopleName(),
            LocoAddress.Protocol.MOTOROLA.getPeopleName()};
    }

    public LocoAddress.Protocol[] getAddressProtocolTypes() {
        return new LocoAddress.Protocol[]{
            LocoAddress.Protocol.DCC,
            LocoAddress.Protocol.MFX,
            LocoAddress.Protocol.MOTOROLA};
    }

    /*
     * Local method for deciding short/long address
     */
    static boolean isLongAddress(int num) {
        return (num >= 100);
    }

    @Override
    public int supportedSpeedModes() {
        return (DccThrottle.SpeedStepMode128 | DccThrottle.SpeedStepMode28);
    }

    public boolean disposeThrottle(jmri.DccThrottle t, jmri.ThrottleListener l) {
        if (super.disposeThrottle(t, l)) {
            Hsi88Throttle lnt = (Hsi88Throttle) t;
            lnt.throttleDispose();
            return true;
        }
        return false;
    }

    private final static Logger log = LoggerFactory.getLogger(Hsi88ThrottleManager.class.getName());

}
