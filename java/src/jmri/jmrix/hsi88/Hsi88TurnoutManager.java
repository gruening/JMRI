// Hsi88TurnoutManager.java
package jmri.jmrix.hsi88;

import jmri.Turnout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implement turnout manager for Hsi88 systems.
 * <P>
 *
 * Based on work by Bob Jacobsen
 *
 * @author	Kevin Dickerson Copyright (C) 2012
 * 
 */
public class Hsi88TurnoutManager extends jmri.managers.AbstractTurnoutManager {

    public Hsi88TurnoutManager(Hsi88SystemConnectionMemo memo) {

        adaptermemo = memo;
        prefix = adaptermemo.getSystemPrefix();
        tc = adaptermemo.getTrafficController();
    }

    Hsi88TrafficController tc;
    Hsi88SystemConnectionMemo adaptermemo;

    String prefix;

    public String getSystemPrefix() {
        return prefix;
    }

    public Turnout createNewTurnout(String systemName, String userName) {
        int addr;
        try {
            addr = Integer.valueOf(systemName.substring(getSystemPrefix().length() + 1)).intValue();
        } catch (java.lang.NumberFormatException e) {
            log.error("failed to convert systemName " + systemName + " to a turnout address");
            return null;
        }
        Turnout t = new Hsi88Turnout(addr, getSystemPrefix(), tc);
        t.setUserName(userName);
        return t;
    }

    boolean noWarnDelete = false;

    private final static Logger log = LoggerFactory.getLogger(Hsi88TurnoutManager.class.getName());
}

/* @(#)Hsi88TurnoutManager.java */
