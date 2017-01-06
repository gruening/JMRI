package jmri.jmrix.hsi88.packetgen;

import java.awt.Dimension;
import javax.swing.BoxLayout;
import jmri.jmrix.hsi88.Hsi88Message;
import jmri.jmrix.hsi88.Hsi88SystemConnectionMemo;

/**
 * Frame for user input of hsi88 messages.
 *
 * @author	Bob Jacobsen Copyright (C) 2001, 2010
 */
public class Hsi88PacketGenFrame extends jmri.util.JmriJFrame {

    private Hsi88SystemConnectionMemo _memo = null;
    // member declarations
    javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
    javax.swing.JButton sendButton = new javax.swing.JButton();
    javax.swing.JTextField packetTextField = new javax.swing.JTextField(12);

    public Hsi88PacketGenFrame(Hsi88SystemConnectionMemo memo) {
        super();
        _memo = memo;
    }

    public void initComponents() throws Exception {
        // the following code sets the frame's initial state

        jLabel1.setText("Command:");
        jLabel1.setVisible(true);

        sendButton.setText("Send");
        sendButton.setVisible(true);
        sendButton.setToolTipText("Send packet");

        packetTextField.setText("");
        packetTextField.setToolTipText("Enter command as ASCII string (hex not yet available)");
        packetTextField.setMaximumSize(
                new Dimension(packetTextField.getMaximumSize().width,
                        packetTextField.getPreferredSize().height
                )
        );

        setTitle("Send hsi88 command");
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        getContentPane().add(jLabel1);
        getContentPane().add(packetTextField);
        getContentPane().add(sendButton);

        sendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                sendButtonActionPerformed(e);
            }
        });

        // pack for display
        pack();
    }

    public void sendButtonActionPerformed(java.awt.event.ActionEvent e) {
        Hsi88Message m = new Hsi88Message(packetTextField.getText());
        _memo.getHsi88TrafficController().sendHsi88Message(m);
    }

}
