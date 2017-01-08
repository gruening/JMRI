package jmri.jmrix.hsi88;

import java.util.ResourceBundle;
import javax.swing.JMenu;

/**
 * Create a "Systems" menu containing the Jmri hsi88-specific tools
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

        add(new jmri.jmrix.hsi88.hsi88mon.Hsi88MonAction(rb.getString("MenuItemCommandMonitor"),_memo));
        add(new jmri.jmrix.hsi88.packetgen.Hsi88PacketGenAction(rb.getString("MenuItemSendCommand"),_memo));
        add(new jmri.jmrix.hsi88.console.Hsi88ConsoleAction(rb.getString("MenuItemConsole"),_memo));
        // add(new jmri.jmrix.hsi88.update.hsi88VersionAction("Get hsi88 Firmware Version",memo));
        // add(new jmri.jmrix.hsi88.update.hsi88v4UpdateAction("hsi88 v3/v4 Firmware Update",memo));
        // add(new jmri.jmrix.hsi88.update.hsi88IIUpdateAction("hsi88 II/hsi88 3 Firmware Update",memo));

    }

}
