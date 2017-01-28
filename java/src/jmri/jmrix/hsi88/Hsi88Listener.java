package jmri.jmrix.hsi88;

/**
 * Defines the interface for listening to traffic on the Hsi88 communications
 * link.
 *
 * @author Andre Gruening Copyright (C) 2017. Based on Sprog implementation by
 *         their respective authors.
 */
public interface Hsi88Listener extends java.util.EventListener {

    public void notifyMessage(Hsi88Message m);

    public void notifyReply(Hsi88Reply m);
}
