// PacketGenFrame.java
package jmri.jmrix.hsi88.swing.packetgen;

import java.awt.Dimension;
import javax.swing.BoxLayout;
import jmri.jmrix.hsi88.Hsi88Listener;
import jmri.jmrix.hsi88.Hsi88Message;
import jmri.jmrix.hsi88.Hsi88Reply;
import jmri.jmrix.hsi88.Hsi88SystemConnectionMemo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Frame for user input of Hsi88 messages
 *
 * @author	Bob Jacobsen Copyright (C) 2001, 2008
 * @author Dan Boudreau Copyright (C) 2007
 */
public class PacketGenPanel extends jmri.jmrix.hsi88.swing.Hsi88Panel implements Hsi88Listener {

    /**
     *
     */
    private static final long serialVersionUID = 3967326301653114203L;
    // member declarations
    javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
    javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
    javax.swing.JButton sendButton = new javax.swing.JButton();
    javax.swing.JTextField packetTextField = new javax.swing.JTextField(20);
    javax.swing.JTextField packetReplyField = new javax.swing.JTextField(20);

    public PacketGenPanel() {
        super();
    }

    public void initComponents() throws Exception {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        // the following code sets the frame's initial state
        {
            jLabel1.setText("Command: ");
            jLabel1.setVisible(true);

            jLabel2.setText("Reply: ");
            jLabel2.setVisible(true);

            sendButton.setText("Send");
            sendButton.setVisible(true);
            sendButton.setToolTipText("Send packet");

            packetTextField.setText("");
            packetTextField.setToolTipText("Enter command");
            packetTextField.setMaximumSize(new Dimension(packetTextField
                    .getMaximumSize().width, packetTextField.getPreferredSize().height));

            add(jLabel1);
            add(packetTextField);
            add(jLabel2);
            add(packetReplyField);
            add(sendButton);

            sendButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    sendButtonActionPerformed(e);
                }
            });
        }
    }

    public String getHelpTarget() {
        return "package.jmri.jmrix.hsi88.swing.packetgen.PacketGenFrame";
    }

    public String getTitle() {
        return "Send CS2 command";
    }

    public void initComponents(Hsi88SystemConnectionMemo memo) {
        super.initComponents(memo);
    }

    public void sendButtonActionPerformed(java.awt.event.ActionEvent e) {
        if (packetTextField.getText() != null || !packetTextField.getText().equals("")) {
            String text = packetTextField.getText();
            if (text.startsWith("0x")) { //We want to send a hex message

                text = text.replaceAll("\\s", "");
                text = text.substring(2);
                String[] arr = text.split(",");
                byte[] msgArray = new byte[arr.length];
                int pos = 0;
                for (String s : arr) {
                    msgArray[pos++] = (byte) (Integer.parseInt(s, 16) & 0xFF);
                }

                Hsi88Message m = new Hsi88Message(msgArray);
                memo.getTrafficController().sendHsi88Message(m, this);
            } else {
                log.error("Binary commands are only supported");
            }
        }

    }

    public void message(Hsi88Message m) {
    }  // ignore replies

    public void reply(Hsi88Reply r) {
        packetReplyField.setText(r.toHexString());
    } // ignore replies
    private final static Logger log = LoggerFactory.getLogger(PacketGenPanel.class.getName());
}
