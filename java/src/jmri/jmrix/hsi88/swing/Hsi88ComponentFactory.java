// Hsi88ComponentFactory.java
package jmri.jmrix.hsi88.swing;

import jmri.jmrix.hsi88.Hsi88SystemConnectionMemo;

/**
 * Provide access to Swing components for the Hsi88 subsystem.
 *
 * @author Kevin Dickerson 2010
 */
public class Hsi88ComponentFactory extends jmri.jmrix.swing.ComponentFactory {

    public Hsi88ComponentFactory(Hsi88SystemConnectionMemo memo) {
        this.memo = memo;
    }
    Hsi88SystemConnectionMemo memo;

    /**
     * Provide a menu with all items attached to this system connection
     */
    //JMenu currentMenu;
    public javax.swing.JMenu getMenu() {
        if (memo.getDisabled()) {
            return null;
        }
        return new Hsi88Menu(memo);
    }
}
