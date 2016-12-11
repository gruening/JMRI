// Hsi88NamedPaneAction.java
package jmri.jmrix.hsi88.swing;

import javax.swing.Icon;
import jmri.jmrix.hsi88.Hsi88SystemConnectionMemo;
import jmri.util.swing.JmriPanel;
import jmri.util.swing.WindowInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Action to create and load a JmriPanel from just its name.
 *
 * @author	Bob Jacobsen Copyright (C) 2010
 */
public class Hsi88NamedPaneAction extends jmri.util.swing.JmriNamedPaneAction {

    /**
     *
     */
    private static final long serialVersionUID = 4877105383231547187L;

    /**
     * Enhanced constructor for placing the pane in various GUIs
     */
    public Hsi88NamedPaneAction(String s, WindowInterface wi, String paneClass, Hsi88SystemConnectionMemo memo) {
        super(s, wi, paneClass);
        this.memo = memo;
    }

    public Hsi88NamedPaneAction(String s, Icon i, WindowInterface wi, String paneClass, Hsi88SystemConnectionMemo memo) {
        super(s, i, wi, paneClass);
        this.memo = memo;
    }

    Hsi88SystemConnectionMemo memo;

    @Override
    public JmriPanel makePanel() {
        JmriPanel p = super.makePanel();
        if (p == null) {
            return null;
        }

        try {
            ((Hsi88PanelInterface) p).initComponents(memo);
            return p;
        } catch (Exception ex) {
            log.warn("could not init pane class: " + paneClass + " due to:" + ex);
            ex.printStackTrace();
        }

        return p;
    }

    private final static Logger log = LoggerFactory.getLogger(Hsi88NamedPaneAction.class.getName());
}

/* @(#)Hsi88NamedPaneAction.java */
