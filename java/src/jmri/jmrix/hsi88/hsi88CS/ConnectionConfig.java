// ConnectionConfig.java
package jmri.jmrix.hsi88.hsi88CS;

import jmri.util.SystemType;

/**
 * Definition of objects to handle configuring a layout connection via an hsi88
 * SerialDriverAdapter object.
 *
 * @author Andrew Crosland Copyright (C) 2006
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
        return "Hsi88 Command Station";
    }

    public String getManufacturer() {
        return adapter.getManufacturer();
    }

    public void setManufacturer(String manu) {
        adapter.setManufacturer(manu);
    }
 

    @Override
    protected String[] getPortFriendlyNames() {
        if (SystemType.isWindows()) {
            return new String[]{"hsi88"};
        }
        return new String[]{};
    }

    protected void setInstance() {
        if(adapter == null ) {
           adapter = new Hsi88CSSerialDriverAdapter();
        } 
    }
}
