package jmri.jmrix.hsi88.console;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import jmri.jmrix.hsi88.Hsi88SystemConnectionMemo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Swing action to create and register a Hsi88ConsoleFrame object
 *
 * @author	Andrew Crosland Copyright (C) 2008
 */
public class Hsi88ConsoleAction extends AbstractAction {

    private Hsi88SystemConnectionMemo _memo;

    public Hsi88ConsoleAction(String s, Hsi88SystemConnectionMemo memo) {
        super(s);
        _memo = memo;
    }

    public void actionPerformed(ActionEvent e) {
        Hsi88ConsoleFrame f = new Hsi88ConsoleFrame(_memo);
        try {
            f.initComponents();
        } catch (Exception ex) {
            log.error("Exception: " + ex.toString());
        }
        f.setVisible(true);
    }
    private final static Logger log = LoggerFactory.getLogger(Hsi88ConsoleAction.class.getName());
}
