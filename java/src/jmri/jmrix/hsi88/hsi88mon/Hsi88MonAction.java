package jmri.jmrix.hsi88.hsi88mon;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import jmri.jmrix.hsi88.Hsi88SystemConnectionMemo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Swing action to create and register a hsi88MonFrame object
 *
 * @author	Bob Jacobsen Copyright (C) 2001
 */
public class Hsi88MonAction extends AbstractAction {

    private Hsi88SystemConnectionMemo _memo = null;

    public Hsi88MonAction(String s, Hsi88SystemConnectionMemo memo) {
        super(s);
        _memo = memo;
    }

    public void actionPerformed(ActionEvent e) {
        // create a hsi88MonFrame
        Hsi88MonFrame f = new Hsi88MonFrame(_memo);
        try {
            f.initComponents();
        } catch (Exception ex) {
            log.warn("hsi88MonAction starting hsi88MonFrame: Exception: " + ex.toString());
        }
        f.setVisible(true);
    }

    private final static Logger log = LoggerFactory.getLogger(Hsi88MonAction.class.getName());

}