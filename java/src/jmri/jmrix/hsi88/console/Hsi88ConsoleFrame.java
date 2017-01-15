package jmri.jmrix.hsi88.console;

import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import jmri.jmrix.hsi88.Hsi88Listener;
import jmri.jmrix.hsi88.Hsi88Message;
import jmri.jmrix.hsi88.Hsi88Reply;
// import jmri.jmrix.hsi88.update.Hsi88Type;
// import jmri.jmrix.hsi88.update.hsi88Version;
// import jmri.jmrix.hsi88.update.hsi88VersionListener;
// import jmri.jmrix.hsi88.update.hsi88VersionQuery;
import jmri.jmrix.hsi88.Hsi88SystemConnectionMemo;
import jmri.jmrix.hsi88.Hsi88TrafficController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Frame for Hsi88 Console
 *
 * Updated Jan 2010 by Andrew Berridge - fixed errors caused by trying to send
 * some commands while slot manager is active
 * 
 * Updated April 2016 by Andrew Crosland remove the checks on slot manager
 * status, implement a timeout and look for the correct replies which may be
 * delayed by replies for slot manager.
 *
 * Refactored.
 *
 * @author Andrew Crosland Copyright (C) 2008, 2016
 */
public class Hsi88ConsoleFrame extends jmri.jmrix.AbstractMonFrame implements Hsi88Listener {

    private Hsi88SystemConnectionMemo _memo = null;
    // member declarations
    protected javax.swing.JLabel cmdLabel = new javax.swing.JLabel();
    protected javax.swing.JLabel currentLabel = new javax.swing.JLabel();
    protected javax.swing.JButton sendButton = new javax.swing.JButton();
    protected javax.swing.JButton saveButton = new javax.swing.JButton();
    protected javax.swing.JTextField cmdTextField = new javax.swing.JTextField(12);
    protected javax.swing.JTextField currentTextField = new javax.swing.JTextField(12);

    protected JCheckBox ztcCheckBox = new JCheckBox();
    protected JCheckBox blueCheckBox = new JCheckBox();
    protected JCheckBox unlockCheckBox = new JCheckBox();

    protected ButtonGroup speedGroup = new ButtonGroup();
    protected JRadioButton speed14Button = new JRadioButton("14 step");
    protected JRadioButton speed28Button = new JRadioButton("28 step");
    protected JRadioButton speed128Button = new JRadioButton("128 step");

    protected int modeWord;

    // members for handling the hsi88 interface
    Hsi88TrafficController tc = null;
    Hsi88Message msg;
    String replyString;
    String tmpString = null;
    State state = State.IDLE;

    enum State {

        IDLE,
        CURRENTQUERYSENT, // awaiting reply to "I"
        MODEQUERYSENT, // awaiting reply to "M"
        CURRENTSENT, // awaiting reply to "I xxx"
        MODESENT, // awaiting reply to "M xxx"
        WRITESENT // awaiting reply to "W"
    }

    public Hsi88ConsoleFrame(Hsi88SystemConnectionMemo memo) {
        super();
        _memo = memo;
    }

    @Override
    protected String title() {
        return "Hsi88 Console";
    }

    @Override
    protected void init() {
        // connect to TrafficController
        tc = _memo.getHsi88TrafficController();
        tc.addHsi88Listener(this);
    }

    @Override
    public void dispose() {
        tc.removeHsi88Listener(this);
        super.dispose();
    }

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "IS2_INCONSISTENT_SYNC")
    // Ignore unsynchronized access to state
    @Override
    public void initComponents() throws Exception {
        super.initComponents();

        // Add a nice border to super class
        super.jScrollPane1.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Command History"));

        // Let user press return to enter message
        entryField.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                enterButtonActionPerformed(e);
            }
        });

        /*
         * Command panel
         */
        JPanel cmdPane1 = new JPanel();
        cmdPane1.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Send Command"));
        cmdPane1.setLayout(new FlowLayout());

        cmdLabel.setText("Command:");
        cmdLabel.setVisible(true);

        sendButton.setText("Send");
        sendButton.setVisible(true);
        sendButton.setToolTipText("Send packet");

        cmdTextField.setText("");
        cmdTextField.setToolTipText("Enter a Hsi88 command");
        cmdTextField.setMaximumSize(
                new Dimension(cmdTextField.getMaximumSize().width,
                        cmdTextField.getPreferredSize().height));

        cmdTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                sendButtonActionPerformed(e);
            }
        });

        sendButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                sendButtonActionPerformed(e);
            }
        });

        cmdPane1.add(cmdLabel);
        cmdPane1.add(cmdTextField);
        cmdPane1.add(sendButton);

        getContentPane().add(cmdPane1);

        /*
         * Address Panel
         */
        JPanel speedPanel = new JPanel();
        speedPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Speed Step Mode for hsi88 Throttle"));
        speedPanel.add(speed14Button);
        speedPanel.add(speed28Button);
        speedPanel.add(speed128Button);
        speedGroup.add(speed14Button);
        speedGroup.add(speed28Button);
        speedGroup.add(speed128Button);
        speed14Button.setToolTipText("Set 14 speed steps for hsi88 throttle");
        speed28Button.setToolTipText("Set 28 speed steps for hsi88 throttle");
        speed128Button.setToolTipText("Set 128 speed steps for hsi88 throttle");

        getContentPane().add(speedPanel);

        /*
         * Configuration panel
         */
        JPanel configPanel = new JPanel();
        configPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Configuration"));
        configPanel.setLayout(new FlowLayout());

        // *** Which versions support current limit ???
        currentLabel.setText("Current Limit (mA):");
        currentLabel.setVisible(true);

        currentTextField.setText("");
        currentTextField.setEnabled(false);
        currentTextField.setToolTipText("Enter new current limit in milliAmps (less than 1000)");
        currentTextField.setMaximumSize(
                new Dimension(currentTextField.getMaximumSize().width,
                        currentTextField.getPreferredSize().height));

        ztcCheckBox.setText("Set ZTC mode");
        ztcCheckBox.setVisible(true);
        ztcCheckBox.setToolTipText("Use this when programming older ZTC decoders");

        blueCheckBox.setText("Set Blueline mode");
        blueCheckBox.setVisible(true);
        blueCheckBox.setEnabled(false);
        blueCheckBox.setToolTipText("Use this when programming blueline decoders - programming will be slower");

        unlockCheckBox.setText("Unlock firmware");
        unlockCheckBox.setVisible(true);
        unlockCheckBox.setEnabled(false);
        unlockCheckBox.setToolTipText("Use this only if you are about to update the hsi88 firmware");

        configPanel.add(currentLabel);
        configPanel.add(currentTextField);
        configPanel.add(ztcCheckBox);
        configPanel.add(blueCheckBox);
        configPanel.add(unlockCheckBox);

        getContentPane().add(configPanel);

        /*
         * Status Panel
         */
        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Save/Load Configuration"));
        statusPanel.setLayout(new FlowLayout());

        saveButton.setText("Save");
        saveButton.setVisible(true);
        saveButton.setToolTipText("Save hsi88 configuration (in the hsi88 EEPROM)");

        saveButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                saveButtonActionPerformed(e);
            }
        });

        statusPanel.add(saveButton);

        getContentPane().add(statusPanel);

        // pack for display
        pack();

        // Now the GUI is all setup we can get the hsi88 version
        // _memo.getHsi88VersionQuery().requestVersion(this);
    }

    /**
     * Define help menu for this window.
     * <p>
     * By default, provides a generic help page that covers general features.
     * Specific implementations can override this to show their own help page if
     * desired.
     */
    @Override
    protected void addHelpMenu() {
        addHelpMenu("package.jmri.jmrix.hsi88.console.Hsi88ConsoleFrame", true);
    }

    // Override superclass to append return
    @Override
    public void enterButtonActionPerformed(java.awt.event.ActionEvent e) {
        nextLine(entryField.getText() + "\n", "");
    }

    public void sendButtonActionPerformed(java.awt.event.ActionEvent e) {
        Hsi88Message m = new Hsi88Message(cmdTextField.getText() + "\r");
        // Messages sent by us will not be forwarded back so add to display manually
        nextLine("cmd: \"" + m.toString() + "\"\n", "");
        tc.sendHsi88Message(m, this);
    }

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "IS2_INCONSISTENT_SYNC")
    // validateCurrent() is called from synchronised code
    public void validateCurrent() {
        String currentRange = "200 - 996";
        int validLimit = 996;
        try {
            // currentLimit = Integer.parseInt(currentTextField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null,
                    "Invalid Current Limit Entered\n" + "Please enter a value in the range " + currentRange,
                    "hsi88 Console", JOptionPane.ERROR_MESSAGE);
            // currentLimit = validLimit;
            return;
        }
 
       
    }

    synchronized public void saveButtonActionPerformed(java.awt.event.ActionEvent e) {
        Hsi88Message saveMsg;
        int currentLimitForHardware;
        // Send Current Limit if possible
        state = State.CURRENTSENT;
        if (isCurrentLimitPossible()) {
            validateCurrent();
            // Value written is scaled from mA to hardware units
            // currentLimitForHardware = (int) (currentLimit);
            // tmpString = String.valueOf(currentLimitForHardware);
            saveMsg = new Hsi88Message("I " + tmpString);
        } else {
            // Else send blank message to kick things off
            saveMsg = new Hsi88Message(" " + tmpString);
        }
        nextLine("cmd: \"" + saveMsg.toString() + "\"\n", "");
        tc.sendHsi88Message(saveMsg, this);

        // Further messages will be sent from state machine
    }

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "IS2_INCONSISTENT_SYNC")
    // Called from synchronised code
    public boolean isCurrentLimitPossible() {
        return true;
    }

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "IS2_INCONSISTENT_SYNC")
    // Called from synchronised code
    public boolean isBlueLineSupportPossible() {
        return false;
    }

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "IS2_INCONSISTENT_SYNC")
    // Called from synchronised code
    public boolean isFirmwareUnlockPossible() {
        return false;
    }

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "IS2_INCONSISTENT_SYNC")
    // Called from synchronised code
    public boolean isZTCModePossible() {
        return false;
    }

    @Override
    public synchronized void notifyMessage(Hsi88Message l) { // receive a message and log it
        nextLine("cmd: \"" + l.toString() + "\"\n", "");
    }

    @Override
    public synchronized void notifyReply(Hsi88Reply l) { // receive a reply message and log it
        Hsi88Message msg;
        int currentLimitFromHardware;
        replyString = l.toString();
        nextLine("rep: \"" + replyString + "\"\n", "");

        if (1 == 2) {
            // *** Check for error reply
            switch (state) {
                case IDLE:
                    log.debug("reply in IDLE state: " + replyString);
                    break;
                case CURRENTQUERYSENT:
                    // Look for an "I=" reply
                    log.debug("reply in CURRENTQUERYSENT state: " + replyString);
                    if (replyString.contains("I=")) {
                        stopTimer();
                        int valueLength = 4;

                        tmpString = replyString.substring(replyString.indexOf("=") + 1,
                                replyString.indexOf("=") + valueLength);
                        log.debug("Current limit string: " + tmpString);
                        try {
                            currentLimitFromHardware = Integer.parseInt(tmpString);
                        } catch (NumberFormatException e) {
                            JOptionPane.showMessageDialog(null, "Malformed Reply for current limit",
                                    "hsi88 Console", JOptionPane.ERROR_MESSAGE);
                            state = State.IDLE;
                            return;
                        }
                        // Value written is scaled from hardware units to mA
                        // currentLimit = (int) (currentLimitFromHardware);
                        log.debug("Current limit from hardware: " +
                                currentLimitFromHardware +
                                " scaled to: " +
                           //     currentLimit +
                                "mA");
                        // currentTextField.setText(String.valueOf(currentLimit));
                        currentTextField.setEnabled(true);

                        // Next get the mode word
                        state = State.MODEQUERYSENT;
                        msg = new Hsi88Message(1);
                        msg.setOpCode('M');
                        nextLine("cmd: \"" + msg + "\"\n", "");
                        tc.sendHsi88Message(msg, this);
                        startTimer();
                    }
                    break;
                case MODEQUERYSENT:
                    log.debug("reply in MODEQUERYSENT state: " + replyString);
                    if (replyString.contains("M=")) {
                        stopTimer();
                        tmpString = replyString.substring(replyString.indexOf("=") + 2, replyString.indexOf("=") + 6);
                        // Value returned is in hex
                        try {
                            modeWord = Integer.parseInt(tmpString, 16);
                        } catch (NumberFormatException e) {
                            JOptionPane.showMessageDialog(null, "Malformed Reply for mode word",
                                    "hsi88 Console", JOptionPane.ERROR_MESSAGE);
                            state = State.IDLE;
                            return;
                        }
                        state = State.IDLE;
                        // Set Speed step radio buttons, etc., according to mode word
                        /*
                        if ((modeWord & Hsi88Constants.STEP14_BIT) != 0) {
                            speed14Button.setSelected(true);
                        } else if ((modeWord & Hsi88Constants.STEP28_BIT) != 0) {
                            speed28Button.setSelected(true);
                        } else {
                            speed128Button.setSelected(true);
                        }
                        if ((modeWord & Hsi88Constants.ZTC_BIT) != 0) {
                            ztcCheckBox.setSelected(true);
                        }
                        if ((modeWord & Hsi88Constants.BLUE_BIT) != 0) {
                            blueCheckBox.setSelected(true);
                        }
                        */
                    }
                    break;
                case CURRENTSENT:
                    // Any reply will do here
                    log.debug("reply in CURRENTSENT state: " + replyString);
                    // Get new mode word - assume 128 steps
                    // modeWord = Hsi88Constants.STEP128_BIT;
                    if (speed14Button.isSelected()) {
                        // modeWord = modeWord & ~Hsi88Constants.STEP_MASK | Hsi88Constants.STEP14_BIT;
                    } else if (speed28Button.isSelected()) {
                        // modeWord = modeWord & ~Hsi88Constants.STEP_MASK | Hsi88Constants.STEP28_BIT;
                    }

                    // ZTC mode
                    if (ztcCheckBox.isSelected() == true) {
                        // modeWord = modeWord | Hsi88Constants.ZTC_BIT;
                    }

                    // Blueline mode
                    if (blueCheckBox.isSelected() == true) {
                        // modeWord = modeWord | Hsi88Constants.BLUE_BIT;
                    }

                    // firmware unlock
                    if (unlockCheckBox.isSelected() == true) {
                        // modeWord = modeWord | Hsi88Constants.UNLOCK_BIT;
                    }

                    // Send new mode word
                    state = State.MODESENT;
                    msg = new Hsi88Message("M " + modeWord);
                    nextLine("cmd: \"" + msg.toString() + "\"\n", "");
                    tc.sendHsi88Message(msg, this);
                    break;
                case MODESENT:
                    // Any reply will do here
                    log.debug("reply in MODESENT state: " + replyString);
                    // Write to EEPROM
                    state = State.WRITESENT;
                    msg = new Hsi88Message("W");
                    nextLine("cmd: \"" + msg.toString() + "\"\n", "");
                    tc.sendHsi88Message(msg, this);
                    break;
                case WRITESENT:
                    // Any reply will do here
                    log.debug("reply in WRITESENT state: " + replyString);
                    // All done
                    state = State.IDLE;
            }
        }
    }

    /**
     * Internal routine to handle a timeout
     */
    synchronized protected void timeout() {
        JOptionPane.showMessageDialog(null, "Timeout talking to Hsi88",
                "Timeout", JOptionPane.ERROR_MESSAGE);
        state = State.IDLE;
    }

    protected int TIMEOUT = 1000;

    javax.swing.Timer timer = null;

    /**
     * Internal routine to start timer to protect the mode-change.
     */
    protected void startTimer() {
        restartTimer(TIMEOUT);
    }

    /**
     * Internal routine to stop timer, as all is well
     */
    protected void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }

    /**
     * Internal routine to handle timer starts {@literal &} restarts
     */
    protected void restartTimer(int delay) {
        if (timer == null) {
            timer = new javax.swing.Timer(delay, new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    timeout();
                }
            });
        }
        timer.stop();
        timer.setInitialDelay(delay);
        timer.setRepeats(false);
        timer.start();
    }

    private final static Logger log = LoggerFactory.getLogger(Hsi88ConsoleFrame.class.getName());

}
