// ConnectionConfig.java
package jmri.jmrix.hsi88.serialdriver;

import jmri.jmrix.hsi88.Hsi88Config;
import jmri.util.SystemType;

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
}
