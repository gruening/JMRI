package jmri.jmrix.hsi88.hsi88;

import jmri.jmrix.hsi88.Hsi88Config;
import jmri.util.SystemType;

/**
 * Definition of objects to handle configuring a layout connection via an HSI88
 * SerialDriverAdapter object.
 *
 * @author Andrew Crosland Copyright (C) 2006.
 * @author Andre Gruening, 2017: trivially adapted for Hsi88 from previous
 *         author's Sprog implementation.
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
     * Ctor for a functional Swing object with no pre-existing adapter
     */
    public ConnectionConfig() {
        super();
    }

    public String name() {
        return Hsi88Config.LONGNAME;
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
            return new String[]{Hsi88Config.NAME};
        }
        return new String[]{};
    }

    protected void setInstance() {
        if (adapter == null) {
            adapter = new Hsi88SerialDriverAdapter();
        }
    }
}
