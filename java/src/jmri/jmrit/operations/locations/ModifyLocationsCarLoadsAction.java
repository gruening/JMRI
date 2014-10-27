// ModifyLocationsCarLoadsAction.java

package jmri.jmrit.operations.locations;

import java.awt.event.ActionEvent;
import java.awt.Frame;

import javax.swing.AbstractAction;

/**
 * Swing action to create and register a LocationsByCarTypeFrame object.
 * 
 * @author Daniel Boudreau Copyright (C) 2009
 * @version $Revision: 23881 $
 */
public class ModifyLocationsCarLoadsAction extends AbstractAction {

	public ModifyLocationsCarLoadsAction(Location location) {
		super(Bundle.getMessage("TitleModifyLocationLoad"));
		_location = location;
	}

	public ModifyLocationsCarLoadsAction() {
		super(Bundle.getMessage("TitleModifyLocationsLoad"));
	}

	Location _location;

	LocationsByCarLoadFrame f = null;

	public void actionPerformed(ActionEvent e) {
		// create a frame
		if (f == null || !f.isVisible()) {
			f = new LocationsByCarLoadFrame();
			f.initComponents(_location);
		}
		f.setExtendedState(Frame.NORMAL);
	   	f.setVisible(true);	// this also brings the frame into focus
	}
}

/* @(#)ModifyLocationsCarLoadsAction.java */
