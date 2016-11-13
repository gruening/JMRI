// Hsi88Interface.java
package jmri.jmrix.hsi88;

/**
 * Define interface for sending and receiving messages to the ECOS command
 * station.
 *
 * @author	Bob Jacobsen Copyright (C) 2001, 2008
 */
public interface Hsi88Interface {

    public void addHsi88Listener(Hsi88Listener l);

    public void removeHsi88Listener(Hsi88Listener l);

    /**
     * Test operational status of interface.
     *
     * @return true is interface implementation is operational.
     */
    boolean status();

    /**
     * Send a message through the interface.
     *
     * @param m Message to be sent.
     * @param l Listener to be notified of reply.
     */
    void sendHsi88Message(Hsi88Message m, Hsi88Listener l);
}

/* @(#)Hsi88Interface.java */
