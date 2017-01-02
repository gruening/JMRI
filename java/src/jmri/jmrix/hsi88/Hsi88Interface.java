// SprogInterface.java
package jmri.jmrix.hsi88;

/**
 * Define interface for sending and receiving messages to the SPROG command
 * station.
 *
 * @author Bob Jacobsen Copyright (C) 2001
 * @author Andre Gruening Copyright (C) 2016: adapted for HSI88
 */
public interface Hsi88Interface {

	public void addHsi88Listener(SprogListener l);

	public void removeHsi88Listener(SprogListener l);

	/**
	 * Test operational status of interface.
	 *
	 * @return true if interface implementation is operational.
	 */
	boolean status();

	/**
	 * Send a message through the interface.
	 *
	 * @param m
	 *            Message to be sent.
	 * @param l
	 *            Listener to be notified of reply.
	 */
	void sendHsi88Message(Hsi88Message m, SprogListener l);
}

/* @(#)SprogInterface.java */