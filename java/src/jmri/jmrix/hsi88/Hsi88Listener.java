// Hsi88Listener.java
package jmri.jmrix.hsi88;

/**
 * Defines the interface for listening to traffic on the Hsi88 communications
 * link.
 *
 * @author	Bob Jacobsen Copyright (C) 2001
 */
public interface Hsi88Listener extends jmri.jmrix.AbstractMRListener {

    public void message(Hsi88Message m);

    public void reply(Hsi88Reply m);
}

/* @(#)Hsi88Listener.java */
