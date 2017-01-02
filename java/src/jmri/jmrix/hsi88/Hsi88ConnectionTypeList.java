// SprogConnectionTypeList.java
package jmri.jmrix.hsi88;

/**
 * Returns a list of valid HSI88 Connection Types
 * <P>
 * 
 * @author Bob Jacobsen Copyright (C) 2010
 * @author Kevin Dickerson Copyright (C) 2010
 * @author Andre Gruening Copyright (C) 2016: adapted from sprog for use with
 *         HSI88
 *
 */
public class Hsi88ConnectionTypeList implements jmri.jmrix.ConnectionTypeList {

	public static final String HSI88 = "HSI 88";

	@Override
	public String[] getAvailableProtocolClasses() {
		return new String[] {
				// "jmri.jmrix.sprog.sprog.ConnectionConfig",
				"jmri.jmrix.sprog.sprogCS.ConnectionConfig",
				// "jmri.jmrix.sprog.sprognano.ConnectionConfig",
				// "jmri.jmrix.sprog.pi.pisprogone.ConnectionConfig",
				// "jmri.jmrix.sprog.pi.pisprogonecs.ConnectionConfig",
				// "jmri.jmrix.sprog.pi.pisprognano.ConnectionConfig"
		};
	}

	@Override
	public String[] getManufacturers() {
		return new String[] { HSI88 };
	}
}