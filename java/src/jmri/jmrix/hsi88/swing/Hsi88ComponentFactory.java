package jmri.jmrix.hsi88.swing;

import jmri.jmrix.hsi88.Hsi88Menu;
import jmri.jmrix.hsi88.Hsi88Constants.Hsi88Mode;
import jmri.jmrix.hsi88.Hsi88SystemConnectionMemo;

/**
 * Provide access to Swing components for the hsi88 subsystem.
 *
 * @author	Bob Jacobsen Copyright (C) 2010
 * @author	Paul Bender Copyright (C) 2010
 * @since 3.5.1
 */
public class Hsi88ComponentFactory extends jmri.jmrix.swing.ComponentFactory {

    public Hsi88ComponentFactory(Hsi88SystemConnectionMemo memo) {
        this.memo = memo;
    }

    Hsi88SystemConnectionMemo memo;

    /**
     * Provide a menu with all items attached to this system connection
     */
    public javax.swing.JMenu getMenu() {
        if (memo.getDisabled()) {
            return null;
        }
        if(memo.getHsi88Mode() == Hsi88Mode.SERVICE) {
            return new HSI88Menu(memo);
        } else {  // must be command station mode.
            // return new HSI88CSMenu(memo);
            return new HSI88Menu(memo);
        }
    }
}
