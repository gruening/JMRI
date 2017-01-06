package jmri.jmrix.hsi88;

import java.util.ResourceBundle;
import jmri.InstanceManager;
// import jmri.ProgrammerManager; // to go
// import jmri.ThrottleManager; // to go
// import jmri.TurnoutManager; // to go
import jmri.SensorManager;
import jmri.jmrix.hsi88.Hsi88Constants.Hsi88Mode; // to be modified
// import jmri.jmrix.hsi88.update.Hsi88Type; // to be modified
// import jmri.jmrix.hsi88.update.Hsi88Version; // to go
// import jmri.jmrix.hsi88.update.Hsi88VersionQuery; // to go
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
        hsi88Version = new Hsi88Version(new Hsi88Type(Hsi88Type.UNKNOWN));
        register();
        InstanceManager.store(this, Hsi88SystemConnectionMemo.class); // also register as specific type
        InstanceManager.store(cf = new jmri.jmrix.hsi88.swing.Hsi88ComponentFactory(this),
                jmri.jmrix.swing.ComponentFactory.class);
    }

    public Hsi88SystemConnectionMemo(Hsi88Mode sm) {
        super("H", "Hsi88");
        hsi88Mode = sm; // static
        hsi88Version = new Hsi88Version(new Hsi88Type(Hsi88Type.UNKNOWN));
        register();
        InstanceManager.store(this, Hsi88SystemConnectionMemo.class); // also register as specific type
        InstanceManager.store(cf = new jmri.jmrix.hsi88.swing.Hsi88ComponentFactory(this),
                jmri.jmrix.swing.ComponentFactory.class);
    }

    public Hsi88SystemConnectionMemo(Hsi88Mode sm, Hsi88Type type) {
        super("H", "Hsi88");
        hsi88Mode = sm; // static
        hsi88Version = new Hsi88Version(type);
        register();
        InstanceManager.store(this, Hsi88SystemConnectionMemo.class); // also register as specific type
        InstanceManager.store(cf = new jmri.jmrix.hsi88.swing.Hsi88ComponentFactory(this),
                jmri.jmrix.swing.ComponentFactory.class);
    }

    public Hsi88SystemConnectionMemo() {
        super("H", "Hsi88");
        register(); // registers general type
        hsi88Version = new Hsi88Version(new Hsi88Type(Hsi88Type.UNKNOWN));
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

    private Hsi88Mode hsi88Mode;// = Hsi88Mode.SERVICE;

    /**
     * Return the HSI88 version object for this connection
     * 
     * @return Hsi88Version
     */
    public Hsi88Version getHsi88Version() {
        return hsi88Version;
    }

    /**
     * Set the HSI88 version object for this connection
     * @param version 
     */
    public void setHsi88Version(Hsi88Version version) {
        hsi88Version = version;
    }

    private Hsi88Version hsi88Version;

    /**
     * Return the type of HSI88 connected
     * 
     * @return Hsi88Type
     */
    public Hsi88Type getHsi88Type() {
        return hsi88Version.hsi88Type;
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
    private Hsi88CommandStation commandStation;

    private Thread slotThread;

    /**
     * Configure the programming manager and "command station" objects
     */
    public void configureCommandStation() {
        log.debug("start command station queuing thread");
        commandStation = new jmri.jmrix.hsi88.Hsi88CommandStation(st);
        commandStation.setSystemConnectionMemo(this);
        jmri.InstanceManager.setCommandStation(commandStation);
        switch (hsi88Mode) {
            case OPS:
                slotThread = new Thread(commandStation);
                slotThread.start();
                break;
            case SERVICE: // to go
                break;
        }
    }

    /*
     * Get the command station object associated with this connection
     */
    public Hsi88CommandStation getCommandStation() {
        return commandStation;
    }

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
            if (hsi88Mode == null) {
                return false;
            }
            switch (hsi88Mode) {
                case OPS:
                    return true;
                case SERVICE:
                    return false;
            }
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
        if (T.equals(jmri.CommandStation.class)) {
            return (T) getCommandStation();
        }
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
