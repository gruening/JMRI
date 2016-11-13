// Hsi88ConnectionTypeList.java
package jmri.jmrix.hsi88;

/**
 * Returns a list of valid ESU Hsi88 Connection Types
 * <P>
 * @author Bob Jacobsen Copyright (C) 2010
 * @author Kevin Dickerson Copyright (C) 2010
 *
 */
public class Hsi88ConnectionTypeList implements jmri.jmrix.ConnectionTypeList {

    public static final String MARKLIN = "Hsi88";

    @Override
    public String[] getAvailableProtocolClasses() {
        return new String[]{
            "jmri.jmrix.hsi88.networkdriver.ConnectionConfig"/*,
         "jmri.jmrix.ecos.csreloaded.ConnectionConfig",*/

        };
    }

    @Override
    public String[] getManufacturers() {
        return new String[]{MARKLIN};
    }

}
