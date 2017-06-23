/**
 * 
 */
package jmri.jmrix.hsi88;

import java.util.ArrayList;
import java.util.List;
import jmri.Sensor;
import jmri.jmrix.hsi88.Hsi88Config.Hsi88Protocol;
import org.python.jline.internal.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages the Hsi88 interface. It parses a hardware reply into a better
 * processable reply and holds the internal state of an Hsi88 as inferred from
 * received messages.
 * 
 * TODO implement more elegant and rigorous way to wait for a reply to the "t"
 * message.
 * 
 * @author Andre Gruening Copyright (C) 2017.
 */

public class Hsi88Manager implements Hsi88Listener {

    /**
     * Types of Hsi88 Responses -- their value is the negative value of the
     * corresponding opcode.
     */
    public final static class ResponseType {
        public static final int TERMINAL = -'t';
        public static final int SETUP = -'s';
        public static final int VERSION = -'v';
        // DON'T USE THE FOLLOWING TWO -- sensor update will get their nonnegative address as response type:
        public static final int SENSORS = -'i';
        public static final int ALL_SENSORS = -'m';
    }

    /** list of listeners for parsed updates from the layout */
    private List<Hsi88ReplyListener> listeners = new ArrayList<Hsi88ReplyListener>();
    /** holds the connection memo */
    private Hsi88SystemConnectionMemo memo;

    /**
     * create a new Hsi88 manager and put Hsi88 interface into defined state.
     * 
     * @param memo connection memo
     */
    public Hsi88Manager(Hsi88SystemConnectionMemo memo) {

        this.memo = memo;
        Hsi88TrafficController tc = memo.getTrafficController();
        tc.addHsi88Listener(this);

        log.debug("Hsi88 Manager starts.");
        tc.sendHsi88Message(Hsi88Message.cmdVersion(), this);

        // try switch to ASCII mode: 
        for (int i = 0; (i < 2) && protocol != Hsi88Protocol.ASCII; i++) {
            tc.sendHsi88Message(Hsi88Message.cmdTerminal(), this);
            try {
                // wait until reply has most probably come.
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // woken up prematurely -- do nothing.
            }
        }

        if (protocol != Hsi88Protocol.ASCII) {
            log.warn(Hsi88Config.LONGNAME +
                    " running in {} mode. Reply and message parsing " +
                    "in this mode is not implemented. " +
                    "Expect error messages.", protocol);
        }

        // start s88 operation
        tc.sendHsi88Message(Hsi88Message.powerOn(), this);
    }

    /**
     * add @param l listener for sensor updates.
     */
    public synchronized void addSensorListener(Hsi88ReplyListener l) {
        if (!listeners.contains(l))
            listeners.add(l);
    }

    /**
     * remove @param l listener for sensor updates.
     */
    public synchronized void removeSensorListener(Hsi88ReplyListener l) {
        if (listeners.contains(l))
            listeners.remove(l);
    }

    /**
     * Notify all listeners of events parsed from Hsi88 interface replies.
     * 
     * @param reply the type of reply, @see ResponseType.
     * @param payload any pay load the response transports. Its interpretation
     *            depends on the response type.
     */
    private synchronized void notifyListeners(int reply, int payload) {
        for (Hsi88ReplyListener l : this.listeners) {
            l.notifyReply(reply, payload);
        }
    }

    /**
     * parses Hsi88 replies and sends meaningful higher level events. This is
     * necessary due to the Hsi88 communication protocol, which can be in either
     * HEX or ASCII mode and more importantly does not deal with "sensors", but
     * with "modules" of 16 sensors.
     * 
     * @param r the reply to parse.
     */
    @Override
    public void notifyReply(Hsi88Reply r) {
        switch (Character.toLowerCase(r.getOpCode())) {
            case 'i':
            case 'm':
                parseModuleReading(r);
                break;
            case 't':
                parseTerminalModeReply(r);
                break;
            case 's':
                parseSetupReply(r); // 
                break;
            case 'v': // Version string
                this.versionString = r.toString();
                log.debug("Hsi88 Hardware Version: {}", this.versionString);
                this.notifyListeners(ResponseType.VERSION, 0);
                break;
            default:
                log.warn("Unknown opcode: <{}>", r);
        }
    }

    /**
     * parse the payload of a reply to a "t" command, and notify listeners of
     * terminal mode.
     * 
     * @param r reply to "t" command.
     */
    private void parseTerminalModeReply(Hsi88Reply r) {

        if (r.getNumDataElements() != 3) {
            log.error("Ignored malformatted response to 't' command: {}", r);
            return;
        }

        switch ((char) r.getElement(1)) {
            case '0':
                protocol = Hsi88Protocol.HEX;
                break;
            case '1':
                protocol = Hsi88Protocol.ASCII;
                break;
            default:
                log.warn("Ignored response to 't' command with unknown parameter: {}.", r);
                return;
        }

        log.debug("Terminal mode switched to {}.", protocol);
        this.notifyListeners(ResponseType.TERMINAL, protocol.ordinal());
    }

    /**
     * parses and acts on the reply to an "s" command.
     * 
     * TODO: issue debug message instead of info.
     * 
     * @param r reply to the "s" command containing the number of modules.
     */
    void parseSetupReply(Hsi88Reply r) {

        if (r.getNumDataElements() != 4) { // "sXX\r"
            log.error("Ignored malformatted response to 's' command: {}", r);
            return;
        }

        String payload = r.toString().substring(1, 3);

        // parse pay load
        int newModules;
        try {
            newModules = Integer.parseInt(payload, 16);
            log.debug("Reported modules: {}.", newModules);
        } catch (NumberFormatException e) {
            log.error("Could not parse to integer: {}.", payload);
            return;
        }

        // validate number of modules reported
        if (newModules > Hsi88Config.MAX_MODULES) {
            log.error("Hsi88 reports more modules than {}. ", Hsi88Config.MAX_MODULES);
            newModules = Hsi88Config.MAX_MODULES;
        } else if (newModules < 0) {
            log.error("Hsi88 reports negative number of modules: {}.", newModules);
            newModules = 0;
        }

        // update configuration
        reportedModules = newModules;

        log.debug("s88 operation started with {} modules.", newModules);
        this.notifyListeners(ResponseType.SETUP, newModules);
    }

    /**
     * parse the "i" and "m" replies from the HSI88 and convert them to sensor
     * responses.
     * 
     * @param r "i" or "m" reply to parse.
     */
    private void parseModuleReading(Hsi88Reply r) {

        int len = r.getNumDataElements() - 1; // ignore opcode
        if (len < 3) {
            log.error("Ignored malformatted '{}' message: {}", r.getOpCode(), r);
            return;
        }
        String payload = r.toString();

        // parse and check number of reported modules
        try {
            int nModules = Integer.parseInt(payload.substring(1, 3), 16);
            if (nModules != 0 && nModules != reportedModules) {
                log.warn("Number of modules reported by previous 's' reply  {} " +
                         "does not agree with 'i' reply: {}.",
                        reportedModules,
                        nModules);
            }
        } catch (NumberFormatException e1) {
            log.warn("Could not parse number of S88 modules: " + payload);
        }

        // values of s88 module come in chunks of 6 ASCII digits:
        int i;
        for (i = 3; i <= len - 6; i += 6) {

            String moduleUpdate = payload.substring(i, i + 6);

            try {
                int v = Integer.parseInt(moduleUpdate, 16);
                int value = v & 0xFFFF;
                int module = (v >> 16) & 0xFF;
                updateModule(module, value, r.getOpCode() == 'm');
            } catch (NumberFormatException e) {
                log.warn("Ignored malformated part of HSI88 module update: {}.", moduleUpdate);
            }
        }

        if (i != len) {
            log.warn("Trailing chars of sensor message not parsed: {}.", payload.substring(i));
        }
    }

    /** store 16bit state of modules. */
    private char[] moduleStates = new char[Hsi88Config.MAX_MODULES];
    /** number of modules reported in last "s" reply: */
    private int reportedModules = 0;
    /** we do not know which mode HSI88 is in on start up. */
    private Hsi88Protocol protocol = Hsi88Config.Hsi88Protocol.UNKNOWN;

    /**
     * process update for an s88 module. Specifically extract all changed
     * sensors in that module.
     * 
     * @param module s88 module number
     * @param value sensor readings of module
     * @param reportAll whether to notify all sensor, or only those for which
     *            layout state changed.
     */
    private void updateModule(int module, final int value, boolean reportAll) {

        if ((module > reportedModules) && (module <= 0)) {
            log.error("Update for module {} beyond number of {} reported modules.",
                    module, reportedModules);
            return;
        }

        // index into moduleStates starts at 0 whereas module numbers start at 1.
        module--;

        // any bits flipped?
        final char changes = (char) (value ^ moduleStates[module]);
        if (changes == 0 && !reportAll) {
            // no changes? => nothing to do
            return;
        }

        // update module state
        moduleStates[module] ^= changes;

        // address of first sensor for this module
        final int baseAddress = module * 16;

        // go through changes bit by bit
        for (int addr = baseAddress, mask = 1; addr < baseAddress + 16; addr++, mask <<= 1) {
            // has sth changed for this addr?
            if (((changes & mask) != 0) || reportAll) {
                int state = (mask & value) != 0 ? Sensor.ACTIVE : Sensor.INACTIVE;
                this.notifyListeners(addr, state);
            }
        }
    }

    /** logger */
    final static Logger log = LoggerFactory.getLogger(Hsi88Manager.class.getName());

    @Override
    public void notifyMessage(Hsi88Message m) {
        // we only listen to replies, not messages.
    }

    /**
     * @param str to be parsed into number of modules.
     * @param modules
     * @return The number of modules parsed form the string.
     */
    public static int parseChainLength(String str, int modulus) {

        try {
            int length = Integer.parseInt(str, modulus);
            if (length < 0 || length > Hsi88Config.MAX_MODULES)
                throw new NumberFormatException();
            return length;
        } catch (NumberFormatException e) {
            Log.warn("Could not parse chain length. Must be integer between 0 and {}.", Hsi88Config.MAX_MODULES);
            return Hsi88Config.DEFAULT_MODULES;
        }
    }

    /**
     * @return the reportedModules
     */
    public int getReportedModules() {
        return reportedModules;
    }

    /**
     * @return the protocol
     */
    public Hsi88Protocol getProtocol() {
        return protocol;
    }

    private String versionString = "Version unknown";
    
    /**
     * @return
     */
    public String getVersion() {
        return versionString;
    }
}