package jmri.jmrix.hsi88;

import java.util.ResourceBundle;
import javax.swing.JMenu;

/**
 * Create a "Systems" menu containing the Jmri SPROG-specific tools
 *
 * @author	Bob Jacobsen Copyright 2003
 */
public class HSI88Menu extends JMenu {

    Hsi88SystemConnectionMemo _memo = null;

    public HSI88Menu(Hsi88SystemConnectionMemo memo) {

        super();
        _memo = memo;

        ResourceBundle rb = ResourceBundle.getBundle("jmri.jmrix.JmrixSystemsBundle");

        setText(memo.getUserName());

        add(new jmri.jmrix.hsi88.sprogmon.SprogMonAction(rb.getString("MenuItemCommandMonitor"),_memo));
        add(new jmri.jmrix.hsi88.packetgen.SprogPacketGenAction(rb.getString("MenuItemSendCommand"),_memo));
        add(new jmri.jmrix.hsi88.console.SprogConsoleAction(rb.getString("MenuItemConsole"),_memo));
        // add(new jmri.jmrix.sprog.update.SprogVersionAction("Get SPROG Firmware Version",memo));
        // add(new jmri.jmrix.sprog.update.Sprogv4UpdateAction("SPROG v3/v4 Firmware Update",memo));
        // add(new jmri.jmrix.sprog.update.SprogIIUpdateAction("SPROG II/SPROG 3 Firmware Update",memo));

    }

}
