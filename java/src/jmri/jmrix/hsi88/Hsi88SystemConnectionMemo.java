package jmri.jmrix.hsi88;

import java.util.ResourceBundle;
import jmri.InstanceManager;
import jmri.SensorManager;
import jmri.jmrix.hsi88.Hsi88Setup.Hsi88Mode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lightweight class to denote that a system is active, and provide general
 * information.
 * <p>
 * Objects of specific subtypes are registered in the instance manager to
 * activate their particular system.
 *
 * @author Bob Jacobsen Copyright (C) 2010
 */
public class Hsi88SystemConnectionMemo extends jmri.jmrix.SystemConnectionMemo {

    public Hsi88SystemConnectionMemo(Hsi88TrafficController st, Hsi88Mode sm) {
        super("H", "Hsi88");
        this.st = st;
        hsi88Mode = sm; // static
        register();
        InstanceManager.store(this, Hsi88SystemConnectionMemo.class); // also register as specific type
        InstanceManager.store(cf = new jmri.jmrix.hsi88.swing.Hsi88ComponentFactory(this),
                jmri.jmrix.swing.ComponentFactory.class);
    }

    public Hsi88SystemConnectionMemo(Hsi88Mode sm) {
        super("H", "Hsi88");
        hsi88Mode = sm; // static
        register();
        InstanceManager.store(this, Hsi88SystemConnectionMemo.class); // also register as specific type
        InstanceManager.store(cf = new jmri.jmrix.hsi88.swing.Hsi88ComponentFactory(this),
                jmri.jmrix.swing.ComponentFactory.class);
    }

    public Hsi88SystemConnectionMemo(Hsi88Mode sm, Hsi88Mode type) {
        super("H", "Hsi88");
        hsi88Mode = sm; // static
        register();
        InstanceManager.store(this, Hsi88SystemConnectionMemo.class); // also register as specific type
        InstanceManager.store(cf = new jmri.jmrix.hsi88.swing.Hsi88ComponentFactory(this),
                jmri.jmrix.swing.ComponentFactory.class);
    }

    public Hsi88SystemConnectionMemo() {
        super("H", "Hsi88");
        register(); // registers general type
        InstanceManager.store(this, Hsi88SystemConnectionMemo.class); // also register as specific type
        InstanceManager.store(cf = new jmri.jmrix.hsi88.swing.Hsi88ComponentFactory(this),
                jmri.jmrix.swing.ComponentFactory.class);
    }

    /**
     * Set the HSI88 mode for this connection
     * 
     */
    public void setHsi88Mode(Hsi88Mode mode) {
        hsi88Mode = mode;
    }

    /**
     * Return the HSI88 mode for this connection
     * 
     * @return Hsi88Mode
     */
    public Hsi88Mode getHsi88Mode() {
        return hsi88Mode;
    }

    private Hsi88Mode hsi88Mode;

    /**
     * Return the type of HSI88 connected
     * 
     * @return Hsi88Type
     */
    public Hsi88Mode getHsi88Mode1() {
        return Hsi88Setup.mode;
    }

    jmri.jmrix.swing.ComponentFactory cf = null;

    /**
     * Provides access to the TrafficController for this particular connection.
     */
    public Hsi88TrafficController getHsi88TrafficController() {
        return st;
    }

    public void setHsi88TrafficController(Hsi88TrafficController st) {
        this.st = st;
    }

    private Hsi88TrafficController st;
    // private Hsi88CommandStation commandStation;

    private Thread slotThread;

    /*
     * /** Configure the programming manager and "command station" objects
     * 
     * public void configureCommandStation() {
     * log.debug("start command station queuing thread"); commandStation = new
     * jmri.jmrix.hsi88.Hsi88CommandStation(st);
     * commandStation.setSystemConnectionMemo(this);
     * jmri.InstanceManager.setCommandStation(commandStation); slotThread = new
     * Thread(commandStation); slotThread.start(); }
     */

    /*
     * Get the command station object associated with this connection
     */
    /**
     * public Hsi88CommandStation getCommandStation() { return commandStation; }
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
        if ((type.equals(jmri.CommandStation.class))) {
            return true;
        }
        return false; // nothing, by default
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Class<?> T) {
        if (getDisabled()) {
            return null;
        }
        if (T.equals(jmri.PowerManager.class)) {
            return (T) getPowerManager();
        }
        if (T.equals(jmri.SensorManager.class)) {
            return (T) getSensorManager();
        }
        //       if (T.equals(jmri.CommandStation.class)) {
        //          return (T) getCommandStation();
        //        }
        return null; // nothing, by default
    }

    /**
     * Configure the common managers for Hsi88 connections. This puts the common
     * manager config in one place. This method is static so that it can be
     * referenced from classes that don't inherit, including
     * hexfile.HexFileFrame and locormi.LnMessageClient
     */
    public void configureManagers() {

        powerManager = new jmri.jmrix.hsi88.Hsi88PowerManager(this);
        jmri.InstanceManager.store(powerManager, jmri.PowerManager.class);

        sensorManager = new jmri.jmrix.hsi88.Hsi88SensorManager(this);
        jmri.InstanceManager.setSensorManager(sensorManager);
    }

    private Hsi88PowerManager powerManager;
    private Hsi88SensorManager sensorManager;

    public Hsi88PowerManager getPowerManager() {
        return powerManager;
    }

    public SensorManager getSensorManager() {
        return sensorManager;
    }

    protected ResourceBundle getActionModelResourceBundle() {
        //No actions that can be loaded at startup
        return null;
    }

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
