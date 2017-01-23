package jmri.jmrix.hsi88.swing;

import jmri.jmrix.hsi88.Hsi88Menu;
import jmri.jmrix.hsi88.Hsi88SystemConnectionMemo;

/**
 * Provide access to Swing components for the Hsi88 subsystem.
 *
 * @author Bob Jacobsen Copyright (C) 2010
 * @author Paul Bender Copyright (C) 2010
 * @author Andre Gruening 2017: trivially adapted for Hsi88 from previous
 *         authors' Sprog implementation.
 * @since 3.5.1
 */
public class Hsi88ComponentFactory extends jmri.jmrix.swing.ComponentFactory {

    public Hsi88ComponentFactory(Hsi88SystemConnectionMemo memo) {
        this.memo = memo;
    }

    private Hsi88SystemConnectionMemo memo;

    /**
     * Provide a menu with all items attached to this system connection
     */
    public javax.swing.JMenu getMenu() {
        if (memo.getDisabled()) {
            return null;
        }
        return new Hsi88Menu(memo);
    }
}
