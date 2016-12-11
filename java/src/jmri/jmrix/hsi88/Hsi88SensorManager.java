package jmri.jmrix.hsi88;

import java.util.Hashtable;
import jmri.JmriException;
import jmri.Sensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implement sensor manager for Hsi88 systems. The Manager handles all the
 * state changes.
 * <P>
 * System names are "USnnn:yy", where nnn is the Hsi88 Object Number for a
 * given s88 Bus Module and yy is the port on that module.
 *
 * @author	Kevin Dickerson Copyright (C) 2009
 */
public class Hsi88SensorManager extends jmri.managers.AbstractSensorManager
        implements Hsi88Listener {

    public Hsi88SensorManager(Hsi88SystemConnectionMemo memo) {
        this.memo = memo;
        tc = memo.getTrafficController();
        // connect to the TrafficManager
        tc.addHsi88Listener(this);
    }

    Hsi88SystemConnectionMemo memo;
    Hsi88TrafficController tc;
    //The hash table simply holds the object number against the Hsi88Sensor ref.
    private Hashtable<Integer, Hashtable<Integer, Hsi88Sensor>> _thsi88 = new Hashtable<Integer, Hashtable<Integer, Hsi88Sensor>>();   // stores known Hsi88 Obj

    public String getSystemPrefix() {
        return memo.getSystemPrefix();
    }

    public Sensor createNewSensor(String systemName, String userName) {
        Hsi88Sensor s = new Hsi88Sensor(systemName, userName);
        if (systemName.contains(":")) {
            int board = 0;
            int channel = 0;

            String curAddress = systemName.substring(getSystemPrefix().length() + 1, systemName.length());
            int seperator = curAddress.indexOf(":");
            try {
                board = Integer.valueOf(curAddress.substring(0, seperator)).intValue();
                if (!_thsi88.containsKey(board)) {
                    _thsi88.put(board, new Hashtable<Integer, Hsi88Sensor>());
                    Hsi88Message m = Hsi88Message.sensorPollMessage(board);
                    tc.sendHsi88Message(m, this);
                }
            } catch (NumberFormatException ex) {
                log.error("Unable to convert " + curAddress + " into the Module and port format of nn:xx");
                return null;
            }
            Hashtable<Integer, Hsi88Sensor> sensorList = _thsi88.get(board);
            try {
                channel = Integer.valueOf(curAddress.substring(seperator + 1)).intValue();
                if (!sensorList.containsKey(channel)) {
                    sensorList.put(channel, s);
                }
            } catch (NumberFormatException ex) {
                log.error("Unable to convert " + curAddress + " into the Module and port format of nn:xx");
                return null;
            }
        }

        return s;
    }

    public String createSystemName(String curAddress, String prefix) throws JmriException {
        if (!curAddress.contains(":")) {
            log.error("Unable to convert " + curAddress + " into the Module and port format of nn:xx");
            throw new JmriException("Hardware Address passed should be past in the form 'Module:port'");
        }

        //Address format passed is in the form of board:channel or T:turnout address
        int seperator = curAddress.indexOf(":");
        try {
            board = Integer.valueOf(curAddress.substring(0, seperator)).intValue();
        } catch (NumberFormatException ex) {
            log.error("Unable to convert " + curAddress + " into the Module and port format of nn:xx");
            throw new JmriException("Module Address passed should be a number");
        }
        try {
            port = Integer.valueOf(curAddress.substring(seperator + 1)).intValue();
        } catch (NumberFormatException ex) {
            log.error("Unable to convert " + curAddress + " into the Module and port format of nn:xx");
            throw new JmriException("Port Address passed should be a number");
        }

        if (port == 0 || port > 16) {
            log.error("Port number must be between 1 and 16");
            throw new JmriException("Port number must be between 1 and 16");
        }
        StringBuilder sb = new StringBuilder();
        sb.append(getSystemPrefix());
        sb.append("S");
        sb.append(board);
        sb.append(":");
        //Little work around to pad single digit address out.
        padPortNumber(port, sb);
        return sb.toString();
    }

    int board = 0;
    int port = 0;

    public String getNextValidAddress(String curAddress, String prefix) {

        String tmpSName = "";

        try {
            tmpSName = createSystemName(curAddress, prefix);
        } catch (JmriException ex) {
            jmri.InstanceManager.getDefault(jmri.UserPreferencesManager.class).
                    showErrorMessage("Error", "Unable to convert " + curAddress + " to a valid Hardware Address", "" + ex, "", true, false);
            return null;
        }

        //Check to determine if the systemName is in use, return null if it is,
        //otherwise return the next valid address.
        Sensor s = getBySystemName(tmpSName);
        if (s != null) {
            port++;
            while (port < 17) {
                try {
                    tmpSName = createSystemName(board + ":" + port, prefix);
                } catch (Exception e) {
                    log.error("Error creating system name for " + board + ":" + port);
                }
                s = getBySystemName(tmpSName);
                if (s == null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(board);
                    sb.append(":");
                    //Little work around to pad single digit address out.
                    padPortNumber(port, sb);
                    return sb.toString();
                }
                port++;
            }
            return null;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(board);
            sb.append(":");
            //Little work around to pad single digit address out.
            padPortNumber(port, sb);
            return sb.toString();
        }

    }

    void padPortNumber(int portNo, StringBuilder sb) {
        if (portNo < 10) {
            sb.append("0");
        }
        sb.append(portNo);
    }

    // to listen for status changes from Hsi88 system
    public void reply(Hsi88Reply r) {
        if (r.getPriority() == Hsi88Constants.PRIO_1 && r.getCommand() >= Hsi88Constants.FEECOMMANDSTART && r.getCommand() <= Hsi88Constants.FEECOMMANDEND) {
            if (r.getCommand() == Hsi88Constants.S88EVENT) {
                int module = (r.getElement(Hsi88Constants.CANADDRESSBYTE1));
                module = (module << 8) + (r.getElement(Hsi88Constants.CANADDRESSBYTE2));
                int contact = (r.getElement(Hsi88Constants.CANADDRESSBYTE3));
                contact = (contact << 8) + (r.getElement(Hsi88Constants.CANADDRESSBYTE4));
                String sensorprefix = getSystemPrefix() + "S" + module + ":";
                Hashtable<Integer, Hsi88Sensor> sensorList = _thsi88.get(module);
                if (sensorList == null) {
                    //Module does not exist, so add it
                    sensorList = new Hashtable<Integer, Hsi88Sensor>();
                    _thsi88.put(module, sensorList);
                    Hsi88Message m = Hsi88Message.sensorPollMessage(module);
                    tc.sendHsi88Message(m, this);
                    if (log.isDebugEnabled()) {
                        log.debug("New module added " + module);
                    }
                }
                Hsi88Sensor ms = sensorList.get(contact);
                if (ms == null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(sensorprefix);
                    //Little work around to pad single digit address out.
                    padPortNumber(contact, sb);
                    if (log.isDebugEnabled()) {
                        log.debug("New sensor added " + contact + " : " + sb.toString());
                    }
                    ms = (Hsi88Sensor) provideSensor(sb.toString());
                }
                if (r.getElement(9) == 0x01) {
                    ms.setOwnState(Sensor.INACTIVE);
                    return;
                }
                if (r.getElement(10) == 0x01) {
                    ms.setOwnState(Sensor.ACTIVE);
                    return;
                }
                log.error("state not found " + ms.getDisplayName() + " " + r.getElement(9) + " " + r.getElement(10));
                log.error(r.toHexString());
            } else {
                int s88Module = r.getElement(9);
                if (_thsi88.containsKey(s88Module)) {
                    int status = r.getElement(10);
                    status = (status << 8) + (r.getElement(11));
                    decodeSensorState(s88Module, status);
                    return;
                }
                if (log.isDebugEnabled()) {
                    log.debug("State s88Module not registered " + s88Module);
                }
            }
        }
    }

    public void message(Hsi88Message m) {
        // messages are ignored
    }

    private void decodeSensorState(int board, int intState) {
        Hsi88Sensor ms;
        int k = 1;
        int result;

        String sensorprefix = getSystemPrefix() + "S" + board + ":";
        Hashtable<Integer, Hsi88Sensor> sensorList = _thsi88.get(board);
        for (int portNo = 1; portNo < 17; portNo++) {
            result = intState & k;
            ms = sensorList.get(portNo);
            if (ms == null) {
                StringBuilder sb = new StringBuilder();
                sb.append(sensorprefix);
                //Little work around to pad single digit address out.
                padPortNumber(portNo, sb);
                ms = (Hsi88Sensor) provideSensor(sb.toString());
            }
            if (result == 0) {
                ms.setOwnState(Sensor.INACTIVE);
            } else {
                ms.setOwnState(Sensor.ACTIVE);
            }
            k = k * 2;
        }
    }

    private final static Logger log = LoggerFactory.getLogger(Hsi88SensorManager.class.getName());
}

/* @(#)Hsi88SensorManager.java */
