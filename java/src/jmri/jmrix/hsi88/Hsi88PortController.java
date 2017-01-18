package jmri.jmrix.hsi88;

/**
 * Identifying class representing a Hsi88 communications port
 * 
 * @author Bob Jacobsen Copyright (C) 2001, 2008
 * 
 *         trivially adapted for HSI88 Andre Gruening 2017
 * 
 */
public abstract class Hsi88PortController extends jmri.jmrix.AbstractSerialPortController {

    // base class. Implementations will provide InputStream and OutputStream
    // objects to Hsi88TrafficController classes, who in turn will deal in messages.
    protected Hsi88PortController(Hsi88SystemConnectionMemo connectionMemo) {
        super(connectionMemo);
    }

    @Override
    public Hsi88SystemConnectionMemo getSystemConnectionMemo() {
        return (Hsi88SystemConnectionMemo) super.getSystemConnectionMemo();
    }
}
