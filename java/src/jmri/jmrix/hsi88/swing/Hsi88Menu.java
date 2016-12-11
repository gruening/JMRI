package jmri.jmrix.hsi88.swing;

import java.util.ResourceBundle;
import javax.swing.JMenu;
import jmri.jmrix.hsi88.Hsi88SystemConnectionMemo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kevin Dickerson
 */
public class Hsi88Menu extends JMenu {

    /**
     *
     */
    private static final long serialVersionUID = -7952313409946046904L;

    public Hsi88Menu(Hsi88SystemConnectionMemo memo) {
        super();

        ResourceBundle rb = ResourceBundle.getBundle("jmri.jmrix.hsi88.Hsi88Bundle");
        String title;
        if (memo != null) {
            title = memo.getUserName();
        } else {
            title = rb.getString("MenuHsi88");
        }

        setText(title);

        jmri.util.swing.WindowInterface wi = new jmri.util.swing.sdi.JmriJFrameInterface();

        for (Item item : panelItems) {
            if (item == null) {
                add(new javax.swing.JSeparator());
            } else {
                add(new Hsi88NamedPaneAction(rb.getString(item.name), wi, item.load, memo));
            }
        }

        if (jmri.InstanceManager.getNullableDefault(jmri.jmrit.beantable.ListedTableFrame.class) == null) {
            try {
                new jmri.jmrit.beantable.ListedTableFrame();
            } catch (java.lang.NullPointerException ex) {
                log.error("Unable to register Hsi88 table");
            }
        }

    }

    Item[] panelItems = new Item[]{
        new Item("MenuItemHsi88Monitor", "jmri.jmrix.hsi88.swing.monitor.Hsi88MonPane"),
        new Item("MenuItemSendPacket", "jmri.jmrix.hsi88.swing.packetgen.PacketGenPanel"),};

    static class Item {

        Item(String name, String load) {
            this.name = name;
            this.load = load;
        }

        String name;
        String load;
    }

    private final static Logger log = LoggerFactory.getLogger(Hsi88Menu.class.getName());
}
