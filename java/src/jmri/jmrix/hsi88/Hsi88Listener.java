// SprogListener.java
package jmri.jmrix.hsi88;

/**
 * Defines the interface for listening to traffic on the NCE communications
 * link.
 *
 * @author	Bob Jacobsen Copyright (C) 2001
  */
public interface Hsi88Listener extends java.util.EventListener {

    public void notifyMessage(Hsi88Message m);

    public void notifyReply(Hsi88Reply m);
}

/* @(#)SprogListener.java */
