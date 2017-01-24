package jmri.jmrix.hsi88;

/**
 * Provide a flag to indicate that the subsystem provided by this package is
 * active.
 * <P>
 * This is a very light-weight class, carrying only the flag, so as to limit the
 * number of unneeded class loadings.
 *
 * @author Bob Jacobsen Copyright (C) 2003
 * @author Andre Gruening 2017: trivially updated for Hsi88 from previous
 *         author's Sprog implementation.
 * @deprecated since 4.5.1
 */
@Deprecated
abstract public class ActiveFlag {

    static private boolean flag = false;

    static public void setActive() {
        flag = true;
    }

    static public boolean isActive() {
        return flag;
    }
}