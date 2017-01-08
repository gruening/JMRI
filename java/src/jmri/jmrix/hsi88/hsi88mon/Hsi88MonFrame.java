package jmri.jmrix.hsi88.hsi88mon;

import jmri.jmrix.hsi88.Hsi88Listener;
import jmri.jmrix.hsi88.Hsi88Message;
import jmri.jmrix.hsi88.Hsi88Reply;
import jmri.jmrix.hsi88.Hsi88TrafficController;
import jmri.jmrix.hsi88.Hsi88SystemConnectionMemo;

/**
 * Frame displaying (and logging) hsi88 command messages
 *
 * @author	Bob Jacobsen Copyright (C) 2001
 */
public class Hsi88MonFrame extends jmri.jmrix.AbstractMonFrame implements Hsi88Listener {

    private Hsi88SystemConnectionMemo _memo = null;

    public Hsi88MonFrame(Hsi88SystemConnectionMemo memo) {
        super();
        _memo = memo;
    }

    protected String title() {
        return "Hsi88 Command Monitor";
    }

    protected void init() {
        // connect to TrafficController
        _memo.getHsi88TrafficController().addHsi88Listener(this);
    }

    public void dispose() {
        _memo.getHsi88TrafficController().removeHsi88Listener(this);
        super.dispose();
    }

    public synchronized void notifyMessage(Hsi88Message l) {  // receive a message and log it
        nextLine("cmd: \"" + l.toString(_memo.getHsi88TrafficController().isSIIBootMode()) + "\"\n", "");

    }

    public synchronized void notifyReply(Hsi88Reply l) {  // receive a message and log it
        nextLine("rep: \"" + l.toString() + "\"\n", "");

    }

}
