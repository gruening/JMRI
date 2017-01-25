package jmri.jmrix.hsi88.console;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
 *         TODO finalise adaptation for Hsi88.
 */
public class Hsi88ConsoleFrame extends jmri.jmrix.AbstractMonFrame implements Hsi88Listener {

    /** hold the connection memo. */
    private Hsi88SystemConnectionMemo _memo = null;

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
    }

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

    /**
     * creates panel to enter Hsi88 commands
     * 
     * @return
     */
    private JPanel createCommandPanel() {

        JPanel cmdPanel = new JPanel();
        cmdPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Send Command"));
        cmdPanel.setLayout(new FlowLayout());

        JLabel cmdLabel = new javax.swing.JLabel("Command");
        //cmdButton.cmdLabel.setVisible(true);

        JButton cmdButton = new javax.swing.JButton("Send");
        // cmdButton.setVisible(true);
        cmdButton.setToolTipText("Send command to Hsi88 interface.");

        JTextField cmdTextField = new javax.swing.JTextField(12);
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
                tc.sendHsi88Message(m, Hsi88ConsoleFrame.this);
            }
        };

        cmdTextField.addActionListener(cmdButtonListener);
        cmdButton.addActionListener(cmdButtonListener);

        cmdPanel.add(cmdLabel);
        cmdPanel.add(cmdTextField);
        cmdPanel.add(cmdButton);

        return cmdPanel;

    };

    private JTextField protocolField = new JTextField(Hsi88Protocol.UNKNOWN.toString());

    /**
     * @return
     */
    private JPanel createProtocolPanel() {

        // set up the panel:
        JPanel protocolPanel = new JPanel();
        protocolPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Communicaton Protocol for Hsi88 Inteface"));

        // set up elements:
        JLabel protocolLabel = new JLabel("Communication Protocol: ");
        protocolField.setText(Hsi88Config.getProtocol().toString());
        protocolField.setDisabledTextColor(Color.MAGENTA);
        protocolField.setEnabled(false);

        JButton toggleButton = new JButton("Toggle");
        toggleButton.setToolTipText("Click to set Hsi88 Communication Protocol");
        toggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tc.sendHsi88Message(Hsi88Message.cmdTerminal(), null);
            }
        });

        // add to Panel:
        protocolPanel.add(protocolLabel);
        protocolPanel.add(protocolField);
        protocolPanel.add(toggleButton);

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

    private JTextField leftField = new JTextField(4);
    private JTextField middleField = new JTextField(4);
    private JTextField rightField = new JTextField(4);

    /**
     * @return
     */
    private Component createChainPanel() {

        JPanel chainPanel = new JPanel();
        chainPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Chain Lengths"));
        chainPanel.setLayout(new FlowLayout());

        // left chain
        JLabel leftLabel = new javax.swing.JLabel("Left Chain:");
        // leftLabel.setVisible(true); // TODO this is always true on creation?
        leftField.setText("" + Hsi88Config.getLeft());
        // leftField.setEnabled(true); // TODO this is always true on creation?
        leftField.setToolTipText("Enter number of s88 modules on left chain.");
        leftField.setMaximumSize(
                new Dimension(leftField.getMaximumSize().width,
                        leftField.getPreferredSize().height));

        JLabel middleLabel = new JLabel("Middle Chain:");
        middleLabel.setVisible(true);

        // middle chain
        middleField.setText("" + Hsi88Config.getMiddle());
        // middleField.setEnabled(true);
        middleField.setToolTipText("Enter number of s88 modules on middle chain.");
        middleField.setMaximumSize(
                new Dimension(middleField.getMaximumSize().width,
                        middleField.getPreferredSize().height));

        // right chaun
        JLabel rightLabel = new JLabel("Right chain:");
        rightLabel.setVisible(true);

        rightField.setText("" + Hsi88Config.getRight());
        //rightField.setEnabled(true);
        rightField.setToolTipText("Enter number of s88 modules on right chain.");
        rightField.setMaximumSize(
                new Dimension(rightField.getMaximumSize().width,
                        rightField.getPreferredSize().height));

        JButton chainButton = new JButton("Set");
        chainButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    Hsi88Config.setLeft(Integer.parseInt(leftField.getText()));
                    Hsi88Config.setMiddle(Integer.parseInt(middleField.getText()));
                    Hsi88Config.setRight(Integer.parseInt(rightField.getText()));
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null,
                            "Invalid chain length entered.\nPlease enter an integer value in the range 0 to " +
                                    Hsi88Config.MAXMODULES +
                                    ".",
                            Hsi88Config.NAME + " Console", JOptionPane.ERROR_MESSAGE);
                    return;

                }

                tc.sendHsi88Message(Hsi88Message.cmdSetup(Hsi88Config.getLeft(), Hsi88Config.getMiddle(), Hsi88Config.getRight()),
                        null);

            }
        });

        chainPanel.add(leftLabel);
        chainPanel.add(leftField);
        chainPanel.add(middleLabel);
        chainPanel.add(middleField);
        chainPanel.add(rightLabel);
        chainPanel.add(rightField);
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

    /**
     * 
     * @note AG removed synchronisation here as from Hsi88TrafficController
     *       notifyMessage and notifyReply are synchronized (on the traffic
     *       controller instance) and in super.nextLine the critical parts of
     *       parts are synchronzied on the super (and hence this) instance.
     * 
     * @note secondly, we need to ensure that GUI manipulations run on the GUI
     *       thread. As the Hsi88Traffic controller calls this method from the
     *       layout thread now we need to take precautions to manipulate the GUI
     *       on the gui thread. super.nextLine takes care of that for itself,
     *       but the other GUI elements have to be explicitly submitted to the
     *       GUI thread.
     *
     */
    @Override
    public void notifyMessage(Hsi88Message l) { // receive a message and log it
        nextLine("cmd: \"" + l + "\"\n", "");
    }

    /**
     * Please see the note for @see notifyMessage. (non-Javadoc)
     * 
     * @see jmri.jmrix.hsi88.Hsi88Listener#notifyReply(jmri.jmrix.hsi88.Hsi88Reply)
     */
    @Override
    public void notifyReply(Hsi88Reply l) {

        // log reply message
        nextLine("rep: \"" + l + "\"\n", "");

        switch (l.getOpCode()) {
            case 's':
                updateChainPanel();
            case 't':
                updateProtocolPanel();
        }

    }

    /**
     * TODO: how to ensure that the protocol has been updated before it is being
     * displayed here if we ourselves were the sender?
     */
    private void updateProtocolPanel() {

        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                Hsi88ConsoleFrame.this.protocolField.setText(Hsi88Config.getProtocol().toString());
            }
        });
    }

    /**
     * TODO: same potential problem as @see updateProtocolPanel .
     */
    private void updateChainPanel() {

        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                Hsi88ConsoleFrame.this.leftField.setText("" + Hsi88Config.getLeft());
                Hsi88ConsoleFrame.this.middleField.setText("" + Hsi88Config.getMiddle());
                Hsi88ConsoleFrame.this.rightField.setText("" + Hsi88Config.getRight());
            }
        });

    }

    /*
     * keep to see how a timer can be used: private javax.swing.Timer timer =
     * null;
     * 
     * protected void restartTimer(int delay) { if (timer == null) { timer = new
     * javax.swing.Timer(delay, new java.awt.event.ActionListener() {
     * 
     * @Override public void actionPerformed(java.awt.event.ActionEvent e) {
     * JOptionPane.showMessageDialog(null, "Timeout talking to Hsi88",
     * "Timeout", JOptionPane.ERROR_MESSAGE); } }); } timer.stop();
     * timer.setInitialDelay(delay); timer.setRepeats(false); timer.start(); }
     */

    private final static Logger log = LoggerFactory.getLogger(Hsi88ConsoleFrame.class.getName());
}
