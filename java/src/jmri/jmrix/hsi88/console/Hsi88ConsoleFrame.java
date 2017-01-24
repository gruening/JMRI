package jmri.jmrix.hsi88.console;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import jmri.jmrix.hsi88.Hsi88Config;
import jmri.jmrix.hsi88.Hsi88Config.Hsi88Protocol;
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
 * @author Andrew Crosland Copyright (C) 2008, 2016
 * @author Andre Gruening 2017: adapted for Hsi88 from previous author's Sprog
 *         implementation.
 * 
 * @todo finalise adaptation for Hsi88.
 */
public class Hsi88ConsoleFrame extends jmri.jmrix.AbstractMonFrame implements Hsi88Listener {

    /** hold the connection memo. */
    private Hsi88SystemConnectionMemo _memo = null;

    /** TODO delete? */
    protected int modeWord;

    /** hold the traffic controller */
    private Hsi88TrafficController tc;


    /**
     * create new Swing Console Frame.
     * 
     * @param memo connection memo
     */
    public Hsi88ConsoleFrame(Hsi88SystemConnectionMemo memo) {
        super();
        _memo = memo;
        thisListener = this;
    }

    final Hsi88Listener thisListener;

    @Override
    protected String title() {
        return Hsi88Config.NAME + " Console";
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

    /** creates panel to enter Hsi88 commands */
    private JPanel createCommandPanel() {

        JPanel cmdPanel = new JPanel();
        JLabel cmdLabel = new javax.swing.JLabel();
        JButton cmdButton = new javax.swing.JButton();
        JTextField cmdTextField = new javax.swing.JTextField(12);

        cmdPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Send Command"));
        cmdPanel.setLayout(new FlowLayout());

        cmdLabel.setText("Command:");
        cmdLabel.setVisible(true);

        cmdButton.setText("Send");
        cmdButton.setVisible(true);
        cmdButton.setToolTipText("Send command to Hsi88 interface.");

        cmdTextField.setText("");
        cmdTextField.setToolTipText("Enter a Hsi88 command.");
        cmdTextField.setMaximumSize(
                new Dimension(cmdTextField.getMaximumSize().width,
                        cmdTextField.getPreferredSize().height));

        ActionListener cmdButtonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Hsi88Message m = new Hsi88Message(cmdTextField.getText() + "\r");
                // Messages sent by us will not be forwarded back so add to display manually
                nextLine("cmd: \"" + m.toString() + "\"\n", "");
                tc.sendHsi88Message(m, thisListener);
            }
        };

        cmdTextField.addActionListener(cmdButtonListener);
        cmdButton.addActionListener(cmdButtonListener);

        cmdPanel.add(cmdLabel);
        cmdPanel.add(cmdTextField);
        cmdPanel.add(cmdButton);

        return cmdPanel;

    };

    private JPanel createProtocolPanel() {

        JPanel protocolPanel = new JPanel();
        ButtonGroup protocolGroup = new ButtonGroup();
        JRadioButton asciiButton = new JRadioButton(Hsi88Protocol.ASCII.toString());
        JRadioButton hexButton = new JRadioButton(Hsi88Protocol.HEX.toString());

        protocolPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Communicaton Protocol for Hsi88 Inteface"));
        protocolPanel.add(asciiButton);
        protocolPanel.add(hexButton);
        protocolGroup.add(asciiButton);
        protocolGroup.add(hexButton);
        asciiButton.setToolTipText("Set " + Hsi88Protocol.ASCII + " protocol.");
        hexButton.setToolTipText("Set " + Hsi88Protocol.HEX + " protocol.");

        return protocolPanel;

    }

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "IS2_INCONSISTENT_SYNC")
    // Ignore unsynchronized access to state. TODO why can we do that?
    @Override
    public void initComponents() throws Exception {
        super.initComponents();

        // Add a nice border to super class
        super.jScrollPane1.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Command History"));

        // Let user press return to enter message
        super.entryField.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                enterButtonActionPerformed(e);
            }
        });

        getContentPane().add(createCommandPanel());
        getContentPane().add(createProtocolPanel());
        getContentPane().add(createChainPanel());

        pack();
    }

    /**
     * @return
     */
    private Component createChainPanel() {

        JPanel chainPanel = new JPanel();
        chainPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Chain Lengths"));
        chainPanel.setLayout(new FlowLayout());

        JTextField leftChainTextField = new JTextField(12);
        JLabel leftChainLabel = new javax.swing.JLabel();

        leftChainLabel.setText("Left chain:");
        leftChainLabel.setVisible(true);

        leftChainTextField.setText("");
        leftChainTextField.setEnabled(true);
        leftChainTextField.setToolTipText("Enter number of s88 modules on left chain.");
        leftChainTextField.setMaximumSize(
                new Dimension(leftChainTextField.getMaximumSize().width,
                        leftChainTextField.getPreferredSize().height));

        JLabel middleChainLabel = new JLabel();
        middleChainLabel.setText("Middle chain:");
        middleChainLabel.setVisible(true);

        JTextField middleChainTextField = new JTextField(12);
        middleChainTextField.setText("");
        middleChainTextField.setEnabled(true);
        middleChainTextField.setToolTipText("Enter number of s88 modules on middle chain.");
        middleChainTextField.setMaximumSize(
                new Dimension(middleChainTextField.getMaximumSize().width,
                        middleChainTextField.getPreferredSize().height));

        JLabel rightChainLabel = new JLabel();
        rightChainLabel.setText("Right chain:");
        rightChainLabel.setVisible(true);

        JTextField rightChainTextField = new JTextField(12);
        rightChainTextField.setText("");
        rightChainTextField.setEnabled(true);
        rightChainTextField.setToolTipText("Enter number of s88 modules on right chain.");
        rightChainTextField.setMaximumSize(
                new Dimension(rightChainTextField.getMaximumSize().width,
                        rightChainTextField.getPreferredSize().height));

        JButton chainButton = new JButton("Set");

        chainPanel.add(leftChainLabel);
        chainPanel.add(leftChainTextField);
        chainPanel.add(middleChainLabel);
        chainPanel.add(middleChainTextField);
        chainPanel.add(rightChainLabel);
        chainPanel.add(rightChainTextField);
        chainPanel.add(chainButton);

        return chainPanel;
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
        super.nextLine(entryField.getText() + "\n", "");
    }

    /*
     * keep this code to see how an Option Pane is generated:
     * 
     * @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value =
     * "IS2_INCONSISTENT_SYNC") // validateCurrent() is called from synchronised
     * code public void validateCurrent() { String currentRange = "200 - 996";
     * try { // currentLimit = Integer.parseInt(currentTextField.getText()); }
     * catch (NumberFormatException e) { JOptionPane.showMessageDialog(null,
     * "Invalid Current Limit Entered\n" + "Please enter a value in the range "
     * + currentRange, "hsi88 Console", JOptionPane.ERROR_MESSAGE); //
     * currentLimit = validLimit; return; }
     * 
     * }
     */

    @Override
    public synchronized void notifyMessage(Hsi88Message l) { // receive a message and log it
        nextLine("cmd: \"" + l + "\"\n", "");
    }

    @Override
    public synchronized void notifyReply(Hsi88Reply l) { // receive a reply message and log it
        nextLine("rep: \"" + l + "\"\n", "");
    }

    /**
     * Internal routine to handle a timeout
     */
    synchronized protected void timeout() {
        JOptionPane.showMessageDialog(null, "Timeout talking to Hsi88",
                "Timeout", JOptionPane.ERROR_MESSAGE);
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
     * 
     * @param delay x
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
