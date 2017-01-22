package jmri.jmrix.hsi88;

import java.util.ResourceBundle;
import javax.swing.JMenu;

/**
 * Create a "Systems" menu containing the Jmri Hsi88-specific tools
 *
 * @author Bob Jacobsen Copyright 2003
 * @author Andre Gruening, Copyright 2017: adapted for use with HSI88:
 */
public class Hsi88Menu extends JMenu {

    Hsi88SystemConnectionMemo _memo = null;

    public Hsi88Menu(Hsi88SystemConnectionMemo memo) {

        super();
        _memo = memo;
        ResourceBundle rb = ResourceBundle.getBundle("jmri.jmrix.JmrixSystemsBundle");
        setText(memo.getUserName());
        add(new jmri.jmrix.hsi88.console.Hsi88ConsoleAction(rb.getString("MenuItemConsole"), _memo));
    }
}
