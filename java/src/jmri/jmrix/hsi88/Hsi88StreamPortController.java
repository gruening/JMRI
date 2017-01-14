package jmri.jmrix.hsi88;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import jmri.jmrix.AbstractStreamPortController;
import jmri.jmrix.hsi88.Hsi88Setup.Hsi88Mode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract base for classes representing an hsi88 Command Station
 * communications port
 * <p>
 * NOTE: This currently only supports the hsi88 Command Station interfaces.
 * <p>
 *
 * @author	Paul Bender Copyright (C) 2014
 */
public class Hsi88StreamPortController extends AbstractStreamPortController implements Hsi88Interface {
    private Thread rcvNotice = null;

    public Hsi88StreamPortController(DataInputStream in, DataOutputStream out, String pname) {
        super(new Hsi88SystemConnectionMemo(Hsi88Mode.ASCII), in, out, pname);
    }

    @Override
    public void configure() {
        log.debug("configure() called.");
        Hsi88TrafficController control = new Hsi88TrafficController(this.getSystemConnectionMemo());
        //        Hsi88TrafficController control = new Hsi88TrafficController(this.hsi88Memo());

        // connect to the traffic controller
        this.getSystemConnectionMemo().setHsi88TrafficController(control);
        control.setAdapterMemo(this.getSystemConnectionMemo());
        this.getSystemConnectionMemo().configureCommandStation();
        this.getSystemConnectionMemo().configureManagers();
        control.connectPort(this);

        // start thread to notify controller when data is available
        rcvNotice = new Thread(new rcvCheck(input, control));
        rcvNotice.start();

    }

    /**
     * Check that this object is ready to operate. This is a question of
     * configuration, not transient hardware status.
     */
    @Override
    public boolean status() {
        return true;
    }

    /**
     * Can the port accept additional characters?
     *
     * @return true
     */
    public boolean okToSend() {
        return (true);
    }

    // hsi88 Interface methods.
    @Override
    public void addHsi88Listener(Hsi88Listener l) {
        this.getSystemConnectionMemo().getHsi88TrafficController().addHsi88Listener(l);
    }

    @Override
    public void removeHsi88Listener(Hsi88Listener l) {
        this.getSystemConnectionMemo().getHsi88TrafficController().removeHsi88Listener(l);
    }

    @Override
    public void sendHsi88Message(Hsi88Message m, Hsi88Listener l) {
        this.getSystemConnectionMemo().getHsi88TrafficController().sendHsi88Message(m, l);
    }

    @Override
    public Hsi88SystemConnectionMemo getSystemConnectionMemo() {
        return (Hsi88SystemConnectionMemo) super.getSystemConnectionMemo();
    }

    // internal thread to check to see if the stream has data and
    // notify the Traffic Controller.
    static protected class rcvCheck implements Runnable {

        private Hsi88TrafficController control;
        private DataInputStream in;

        public rcvCheck(DataInputStream in, Hsi88TrafficController control) {
            this.in = in;
            this.control = control;
        }

        public void run() {
            do {
                try {
                    if (in.available() > 0) {
                        control.handleOneIncomingReply();
                    }
                } catch (java.io.IOException ioe) {
                    log.error("Error reading data from stream");
                }
                // need to sleep here?
            } while (true);
        }
    }

    private final static Logger log = LoggerFactory.getLogger(Hsi88StreamPortController.class.getName());

}
