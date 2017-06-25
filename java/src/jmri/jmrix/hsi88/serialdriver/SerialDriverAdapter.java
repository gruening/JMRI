package jmri.jmrix.hsi88.serialdriver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.util.TooManyListenersException;
import jmri.jmrix.hsi88.Hsi88Config;
import jmri.jmrix.hsi88.Hsi88PortController;
import jmri.jmrix.hsi88.Hsi88SystemConnectionMemo;
import jmri.jmrix.hsi88.Hsi88TrafficController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import purejavacomm.CommPortIdentifier;
import purejavacomm.PortInUseException;
import purejavacomm.SerialPort;

/**
 * Implements SerialPortAdapter for the Hsi88 system.
 * <P>
 * This connects an Hsi88 interface via a serial port. Also used for the USB
 * Hsi88, which appears to the computer as a serial port.
 * <P>
 * The current implementation only handles the 9,600 baud rate. For options at
 * configuration time, @see Hsi88SerialDriverAdapter.
 *
 *
 * @author Bob Jacobsen Copyright (C) 2001, 2002.
 * @author Updated January 2010 for gnu io (RXTX) - Andrew Berridge. Comments
 *         tagged with "AJB" indicate changes or observations by me.
 * @author Andre Gruening 2017: adapted for Hsi88 from previous authors' Sprog
 *         implementation. Also tidied chaining of Constructors.
 * 
 * @since 4.6.x
 */
public class SerialDriverAdapter extends Hsi88PortController implements jmri.jmrix.SerialPortAdapter {

    public SerialDriverAdapter() {
        this(9600);
    }

    public SerialDriverAdapter(int baud) {
        super(new Hsi88SystemConnectionMemo());
        this.baudRate = baud;
        this.getSystemConnectionMemo().setUserName(Hsi88Config.LONGNAME);
        // create the traffic controller
        this.getSystemConnectionMemo()
                .setTrafficController(new Hsi88TrafficController(this.getSystemConnectionMemo()));
    }

    private SerialPort activeSerialPort;

    private int baudRate = -1;

    @Override
    public String openPort(String portName, String appName) {
        // open the port, check ability to set moderators
        try {
            // get and open the primary port
            CommPortIdentifier portID = CommPortIdentifier.getPortIdentifier(portName);
            try {
                activeSerialPort = (SerialPort) portID.open(appName, 2000); // name of program, msec to wait
            } catch (PortInUseException p) {
                return handlePortBusy(p, portName, log);
            }

            // try to set it for communication via SerialDriver
            try {
                activeSerialPort.setSerialPortParams(baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);
            } catch (purejavacomm.UnsupportedCommOperationException e) {
                String errMsg = "Cannot set serial parameters on port " + portName + ": " + e.getMessage();
                log.error(errMsg);
                return errMsg;
            }

            // set RTS high, DTR high
            activeSerialPort.setRTS(true); // not connected in some serial ports and adapters
            activeSerialPort.setDTR(true); // pin 1 in DIN8; on main connector, this is DTR

            // disable flow control; hardware lines used for signaling, XON/XOFF might appear in data
            // AJB: Removed Jan 2010 - 
            // TODO Setting flow control mode to zero kills comms - Sprog doesn't send data
            // Concern is that will disabling this affect other Sprogs? Serial ones? 
            // activeSerialPort.setFlowControlMode(0);

            // set timeout
            // activeSerialPort.enableReceiveTimeout(1000);
            log.debug("Serial timeout was observed as: " +
                    activeSerialPort.getReceiveTimeout() +
                    " " +
                    activeSerialPort.isReceiveTimeoutEnabled());

            // get and save stream
            serialStream = activeSerialPort.getInputStream();

            // purge contents, if any
            purgeStream(serialStream);

            // report status?
            if (log.isInfoEnabled()) {
                log.info(portName +
                        " port opened at " +
                        activeSerialPort.getBaudRate() +
                        " baud, sees " +
                        " DTR: " +
                        activeSerialPort.isDTR() +
                        " RTS: " +
                        activeSerialPort.isRTS() +
                        " DSR: " +
                        activeSerialPort.isDSR() +
                        " CTS: " +
                        activeSerialPort.isCTS() +
                        "  CD: " +
                        activeSerialPort.isCD());
            }

            //AJB - add Hsi88 Traffic Controller as event listener
            try {
                activeSerialPort.addEventListener(this.getSystemConnectionMemo().getTrafficController());
            } catch (TooManyListenersException e) {
            }

            // AJB - activate the DATA_AVAILABLE notifier
            activeSerialPort.notifyOnDataAvailable(true);

            opened = true;

        } catch (purejavacomm.NoSuchPortException p) {
            return handlePortNotFound(p, portName, log);
        } catch (Exception ex) {
            log.error("Unexpected exception while opening port " + portName + " trace follows: " + ex);
            ex.printStackTrace();
            return "Unexpected error while opening port " + portName + ": " + ex;
        }

        return null; // indicates OK return

    }

    /**
     * private void setHandshake(int mode) { try {
     * activeSerialPort.setFlowControlMode(mode); } catch (Exception ex) {
     * log.error("Unexpected exception while setting COM port handshake mode
     * trace follows: " + ex); ex.printStackTrace(); }
     * 
     * }
     */

    // base class methods for the Hsi88PortController interface
    @Override
    public DataInputStream getInputStream() {
        if (!opened) {
            log.error("getInputStream called before load(), stream not available");
            return null;
        }
        return new DataInputStream(serialStream);
    }

    @Override
    public DataOutputStream getOutputStream() {
        if (!opened) {
            log.error("getOutputStream called before load(), stream not available");
        }
        try {
            return new DataOutputStream(activeSerialPort.getOutputStream());
        } catch (java.io.IOException e) {
            log.error("getOutputStream exception: " + e);
        }
        return null;
    }

    /**
     * Get an array of valid baud rates. This is currently only 9,600 bps
     */
    @Override
    public String[] validBaudRates() {
        return new String[]{"9,600 bps"};
    }

    private InputStream serialStream;

    /**
     * @deprecated JMRI Since 4.4 instance() shouldn't be used, convert to JMRI
     *             multi-system support structure
     * 
     * @return null
     */
    /**
     * @Deprecated static public SerialDriverAdapter instance() { return null; }
     */

    /**
     * set up all the other objects to operate with a Hsi88 interface connected
     * to this port
     */
    @Override
    public void configure() {
        // connect to the traffic controller
        this.getSystemConnectionMemo().getTrafficController().connectPort(this);
        this.getSystemConnectionMemo().configureManagers();
        jmri.jmrix.hsi88.ActiveFlag.setActive();
    }

    /** Logger */
    private final static Logger log = LoggerFactory.getLogger(SerialDriverAdapter.class.getName());

}
