/**
 * Hsi88MonPane.java
 *
 * Description:	Swing action to create and register a MonFrame object
 *
 * @author	Bob Jacobsen Copyright (C) 2001, 2008
 */
package jmri.jmrix.hsi88.swing.monitor;

import jmri.jmrix.hsi88.Hsi88Listener;
import jmri.jmrix.hsi88.Hsi88Message;
import jmri.jmrix.hsi88.Hsi88Reply;
import jmri.jmrix.hsi88.Hsi88SystemConnectionMemo;
import jmri.jmrix.hsi88.swing.Hsi88PanelInterface;

public class Hsi88MonPane extends jmri.jmrix.AbstractMonPane implements Hsi88Listener, Hsi88PanelInterface {

    /**
     *
     */
    private static final long serialVersionUID = -3683278624916620459L;

    public Hsi88MonPane() {
        super();
    }

    public String getHelpTarget() {
        return null;
    }

    public String getTitle() {
        if (memo != null) {
            return memo.getUserName() + " Command Monitor";
        }
        return "CS2 Command Monitor";
    }

    public void dispose() {
        // disconnect from the LnTrafficController
        memo.getTrafficController().removeHsi88Listener(this);
        // and unwind swing
        super.dispose();
    }

    public void init() {
    }

    Hsi88SystemConnectionMemo memo;

    public void initContext(Object context) {
        if (context instanceof Hsi88SystemConnectionMemo) {
            initComponents((Hsi88SystemConnectionMemo) context);
        }
    }

    public void initComponents(Hsi88SystemConnectionMemo memo) {
        this.memo = memo;
        // connect to the Hsi88TrafficController
        memo.getTrafficController().addHsi88Listener(this);
    }

    public synchronized void message(Hsi88Message l) {  // receive a message and log it
        if (l.isBinary()) {
            nextLine("binary cmd: " + l.toString() + "\n", null);
        } else {
            nextLine("cmd: \"" + l.toString() + "\"\n", null);
        }
    }

    public synchronized void reply(Hsi88Reply l) {  // receive a reply message and log it
        String raw = "";
        for (int i = 0; i < l.getNumDataElements(); i++) {
            if (i > 0) {
                raw += " ";
            }
            raw = jmri.util.StringUtil.appendTwoHexFromInt(l.getElement(i) & 0xFF, raw);
        }

        if (l.isUnsolicited()) {
            nextLine("msg: \"" + Hsi88Mon.displayReply(l) + "\"\n", raw);
        } else {
            nextLine("rep: \"" + Hsi88Mon.displayReply(l) + "\"\n", raw);
        }
    }

    /**
     * Nested class to create one of these using old-style defaults
     */
    static public class Default extends jmri.jmrix.hsi88.swing.Hsi88NamedPaneAction {

        /**
         *
         */
        private static final long serialVersionUID = -4899436240553324573L;

        public Default() {
            super("CS2 Command Monitor",
                    new jmri.util.swing.sdi.JmriJFrameInterface(),
                    Hsi88MonPane.class.getName(),
                    jmri.InstanceManager.getDefault(Hsi88SystemConnectionMemo.class));
        }
    }

}


/* @(#)MonAction.java */
