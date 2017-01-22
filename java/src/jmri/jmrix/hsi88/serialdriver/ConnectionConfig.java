// ConnectionConfig.java
package jmri.jmrix.hsi88.serialdriver;

import javax.swing.JLabel;
import javax.swing.JTextField;
import jmri.jmrix.hsi88.Hsi88Config;
import jmri.util.SystemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Definition of objects to handle configuring a layout connection via an HSI88
 * SerialDriverAdapter object.
 *
 * @author Bob Jacobsen Copyright (C) 2001, 2003
 */
public class ConnectionConfig extends jmri.jmrix.AbstractSerialConnectionConfig {

    /**
     * Ctor for an object being created during load process; Swing init is
     * deferred.
     */
    public ConnectionConfig(jmri.jmrix.SerialPortAdapter p) {
        super(p);
    }
    
    

    @Override
    protected int addStandardDetails(boolean incAdvanced, int i) {
    
        i = super.addStandardDetails(incAdvanced, i);

        log.warn("I am being called.");

        JTextField left = new JTextField();
        JLabel leftLabel = new JLabel("Left: ");
        
        left.setToolTipText("Number of S88 modules on the left chain.");
        left.setEnabled(true);
        
        cR.gridy = i;
        cL.gridy = i;
        gbLayout.setConstraints(leftLabel, cL);
        gbLayout.setConstraints(left, cR);
        _details.add(leftLabel);
        _details.add(left);
        
        return i++;
    }

    /**
     * Ctor for a functional Swing object with no prexisting adapter
     */
    public ConnectionConfig() {
        super();
    }

    public String name() {
        return Hsi88Config.NAME;
    }

    @Override
    protected String[] getPortFriendlyNames() {
        if (SystemType.isWindows()) {
            return new String[]{Hsi88Config.NAME};
        }
        return new String[]{};
    }

    protected void setInstance() {
        if(adapter == null) {
           adapter = new SerialDriverAdapter();
        }
    }
    
    private final static Logger log = LoggerFactory.getLogger(ConnectionConfig.class.getName());

}
