package jmri.jmrix.hsi88;

import java.util.ResourceBundle;
import jmri.InstanceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lightweight class to denote that a system is active, and provide general
 * information.
 * <p>
 * Objects of specific subtypes are registered in the instance manager to
 * activate their particular system.
 *
 * @author Bob Jacobsen Copyright (C) 2010.
 * @author Andre Gruening, 2017: trivially adapted for Hsi88 from previous
 *         author's Sprog implementation.
 * 
 * @since 4.6.x
 */
public class Hsi88SystemConnectionMemo extends jmri.jmrix.SystemConnectionMemo {

    /*
     * public Hsi88SystemConnectionMemo(Hsi88TrafficController st, Hsi88Protocol
     * sm) { this(); this.st = st; }
     */

    public Hsi88SystemConnectionMemo() {
        super(Hsi88Config.PREFIX, Hsi88Config.NAME);
        register();
        InstanceManager.store(this, Hsi88SystemConnectionMemo.class); // also register as specific type
        InstanceManager.store(cf = new jmri.jmrix.hsi88.swing.Hsi88ComponentFactory(this),
                jmri.jmrix.swing.ComponentFactory.class);
    }

    /**
     * keep reference to Component Factory.
     */
    private jmri.jmrix.swing.ComponentFactory cf = null;

    /**
     * Provides access to the TrafficController for this particular connection.
     * 
     * @return the Traffic controller.
     */
    public Hsi88TrafficController getTrafficController() {
        return st;
    }

    /**
     * Set Traffic Controller.
     * 
     * @param st
     */
    public void setTrafficController(Hsi88TrafficController st) {
        this.st = st;
    }

    /** keep reference to Traffic Controller */
    private Hsi88TrafficController st;

    /**
     * The Hsi88 implementation provides a Sensor Manager and a Power Manager
     */
    @Override
    public boolean provides(Class<?> type) {
        if (getDisabled()) {
            return false;
        }
        if (type.equals(jmri.PowerManager.class)) {
            return true;
        }
        if (type.equals(jmri.SensorManager.class)) {
            return true;
        }
        return false;
    }

    /**
     * @return the manager the Hsi88 implementation provides.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Class<?> T) {
        if (getDisabled()) {
            return null;
        } else if (T.equals(jmri.PowerManager.class)) {
            return (T) getPowerManager();
        } else if (T.equals(jmri.SensorManager.class)) {
            return (T) getSensorManager();
        }
        return null; // nothing, by default
    }

    /**
     * Configure the common managers for Hsi88 connections. This puts the common
     * manager config in one place.
     */
    public void configureManagers() {

        // must be initialised before the other managers as they rely on this one.
        hsi88Manager = new Hsi88Manager(this);

        powerManager = new jmri.jmrix.hsi88.Hsi88PowerManager(this);
        jmri.InstanceManager.store(powerManager, jmri.PowerManager.class);

        sensorManager = new jmri.jmrix.hsi88.Hsi88SensorManager(this);
        jmri.InstanceManager.setSensorManager(sensorManager);

    }

    /** keep reference to Power Manager. */
    private Hsi88PowerManager powerManager;

    /** keep reference to Sensor Manager. */
    private Hsi88SensorManager sensorManager;

    /** keep reference to Reply Manager */
    private Hsi88Manager hsi88Manager;

    /** @return the Power Manager */
    public Hsi88PowerManager getPowerManager() {
        return powerManager;
    }

    /** @return the Sensor Manager */
    public Hsi88SensorManager getSensorManager() {
        return sensorManager;
    }

    /** @return the Reply Manager */
    public Hsi88Manager getManager() {
        return hsi88Manager;
    }

    @Override
    protected ResourceBundle getActionModelResourceBundle() {
        // No actions that can be loaded at startup
        return null;
    }

    /** free resources */
    public void dispose() {
        st = null;
        InstanceManager.deregister(this, Hsi88SystemConnectionMemo.class);
        if (cf != null) {
            InstanceManager.deregister(cf, jmri.jmrix.swing.ComponentFactory.class);
        }
        super.dispose();
    }

    private final static Logger log = LoggerFactory.getLogger(Hsi88SystemConnectionMemo.class.getName());
}
