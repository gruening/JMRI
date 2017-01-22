package jmri.jmrix.hsi88;

/**
 * Returns a list of valid HSI88 Connection Types
 * 
 * @author Bob Jacobsen Copyright (C) 2010
 * @author Kevin Dickerson Copyright (C) 2010
 * @author Andre Gruening Copyright(C) 2017: adapted from sprog for
 *         use with HSI88.
 *
 */
public class Hsi88ConnectionTypeList implements jmri.jmrix.ConnectionTypeList {

    @Override
    public String[] getAvailableProtocolClasses() {
        return new String[]{
                "jmri.jmrix.hsi88.hsi88.ConnectionConfig",
        };
    }

    @Override
    public String[] getManufacturers() {
        return new String[]{Hsi88Config.NAME};
    }
}
