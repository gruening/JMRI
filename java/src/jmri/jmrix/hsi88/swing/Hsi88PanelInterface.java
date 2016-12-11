// Hsi88PanelInterface.java
package jmri.jmrix.hsi88.swing;

import jmri.jmrix.hsi88.Hsi88SystemConnectionMemo;

/**
 * JPanel interface to handle providing system connection information to a
 * panel.
 *
 * @author Kevin Dickerson Copyright 2010
 * @since 2.11.3
 */
public interface Hsi88PanelInterface {

    /**
     * 2nd stage of initialization, invoked after the constuctor is complete.
     * <p>
     * This needs to be connected to the initContext() method in implementing
     * classes.
     */
    public void initComponents(Hsi88SystemConnectionMemo memo) throws Exception;

}
