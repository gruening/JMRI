package jmri.jmrix.hsi88.packetgen;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import jmri.jmrix.hsi88.Hsi88SystemConnectionMemo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Swing action to create and register a hsi88PacketGenFrame object
 *
 * @author	Bob Jacobsen Copyright (C) 2001
  */
public class Hsi88PacketGenAction extends AbstractAction {

    private Hsi88SystemConnectionMemo _memo;

    public Hsi88PacketGenAction(String s,Hsi88SystemConnectionMemo memo) {
        super(s);
        _memo = memo;
    }

    public void actionPerformed(ActionEvent e) {
        Hsi88PacketGenFrame f = new Hsi88PacketGenFrame(_memo);
        try {
            f.initComponents();
        } catch (Exception ex) {
            log.error("Exception: " + ex.toString());
        }
        f.setVisible(true);
    }
    private final static Logger log = LoggerFactory.getLogger(Hsi88PacketGenAction.class.getName());
}