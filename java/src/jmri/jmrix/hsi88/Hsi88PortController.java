// SprogPortController.java
package jmri.jmrix.hsi88;

/*
 * Identifying class representing a ECOS communications port
 * @author			Bob Jacobsen    Copyright (C) 2001, 2008
 */
public abstract class Hsi88PortController extends jmri.jmrix.AbstractSerialPortController {

    // base class. Implementations will provide InputStream and OutputStream
    // objects to SprogTrafficController classes, who in turn will deal in messages.
    protected Hsi88PortController(Hsi88SystemConnectionMemo connectionMemo) {
        super(connectionMemo);
    }

    @Override
    public Hsi88SystemConnectionMemo getSystemConnectionMemo() {
        return (Hsi88SystemConnectionMemo) super.getSystemConnectionMemo();
    }
}


/* @(#)SprogPortController.java */
