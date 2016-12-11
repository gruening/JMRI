// Hsi88SystemConnectionMemo.java
package jmri.jmrix.hsi88;

import java.util.ResourceBundle;
import jmri.InstanceManager;

/**
 * Lightweight class to denote that a system is active, and provide general
 * information.
 * <p>
 * Objects of specific subtypes are registered in the instance manager to
 * activate their particular system.
 *
 * @author	Bob Jacobsen Copyright (C) 2010
 * @author	Kevin Dickerson Copyright (C) 2012
 * 
 */
public class Hsi88SystemConnectionMemo extends jmri.jmrix.SystemConnectionMemo {

    public Hsi88SystemConnectionMemo(Hsi88TrafficController et) {
        super("MC", "Hsi88-CS2");
        this.et = et;
        et.setAdapterMemo(this);
        register();
        InstanceManager.store(this, Hsi88SystemConnectionMemo.class); // also register as specific type
        InstanceManager.store(cf = new jmri.jmrix.hsi88.swing.Hsi88ComponentFactory(this),
                jmri.jmrix.swing.ComponentFactory.class);
    }

    public Hsi88SystemConnectionMemo() {
        super("MC", "Hsi88-CS2");
        register(); // registers general type
        InstanceManager.store(this, Hsi88SystemConnectionMemo.class); // also register as specific type
        //Needs to be implemented
        InstanceManager.store(cf = new jmri.jmrix.hsi88.swing.Hsi88ComponentFactory(this),
                jmri.jmrix.swing.ComponentFactory.class);
    }

    jmri.jmrix.swing.ComponentFactory cf = null;

    /**
     * Provides access to the TrafficController for this particular connection.
     */
    public Hsi88TrafficController getTrafficController() {
        return et;
    }

    public void setHsi88TrafficController(Hsi88TrafficController et) {
        this.et = et;
        et.setAdapterMemo(this);
    }
    private Hsi88TrafficController et;

    /**
     * This puts the common manager config in one place.
     */
    public void configureManagers() {

        powerManager = new jmri.jmrix.hsi88.Hsi88PowerManager(getTrafficController());
        jmri.InstanceManager.store(powerManager, jmri.PowerManager.class);

        turnoutManager = new jmri.jmrix.hsi88.Hsi88TurnoutManager(this);
        jmri.InstanceManager.setTurnoutManager(turnoutManager);

        /*locoManager = new jmri.jmrix.hsi88.Hsi88LocoAddressManager(this);*/
        throttleManager = new jmri.jmrix.hsi88.Hsi88ThrottleManager(this);
        jmri.InstanceManager.setThrottleManager(throttleManager);

        sensorManager = new jmri.jmrix.hsi88.Hsi88SensorManager(this);
        jmri.InstanceManager.setSensorManager(sensorManager);

        /*reporterManager = new jmri.jmrix.hsi88.Hsi88ReporterManager(this);
         jmri.InstanceManager.setReporterManager(reporterManager);*/
    }

    protected ResourceBundle getActionModelResourceBundle() {
        return ResourceBundle.getBundle("jmri.jmrix.hsi88.Hsi88ActionListBundle");
    }

    private Hsi88SensorManager sensorManager;
    private Hsi88TurnoutManager turnoutManager;
    /*private Hsi88LocoAddressManager locoManager;
     private Hsi88Preferences prefManager;*/
    private Hsi88ThrottleManager throttleManager;
    private Hsi88PowerManager powerManager;
    //private Hsi88ReporterManager reporterManager;

    /*public Hsi88LocoAddressManager getLocoAddressManager() { return locoManager; }*/
    public Hsi88TurnoutManager getTurnoutManager() {
        return turnoutManager;
    }

    public Hsi88SensorManager getSensorManager() {
        return sensorManager;
    }
    /*public Hsi88Preferences getPreferenceManager() { return prefManager; }*/

    public Hsi88ThrottleManager getThrottleManager() {
        return throttleManager;
    }

    public Hsi88PowerManager getPowerManager() {
        return powerManager;
    }
    //public Hsi88ReporterManager getReporterManager() { return reporterManager; }

    /**
     * Tells which managers this provides by class
     */
    public boolean provides(Class<?> type) {
        if (getDisabled()) {
            return false;
        }
        if (type.equals(jmri.ThrottleManager.class)) {
            return true;
        }
        if (type.equals(jmri.PowerManager.class)) {
            return true;
        }
        if (type.equals(jmri.SensorManager.class)) {
            return true;
        }
        if (type.equals(jmri.TurnoutManager.class)) {
            return true;
        }
        /*if (type.equals(jmri.ReporterManager.class))
         return true;*/
        return false; // nothing, by default
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<?> T) {
        if (getDisabled()) {
            return null;
        }
        if (T.equals(jmri.ThrottleManager.class)) {
            return (T) getThrottleManager();
        }
        if (T.equals(jmri.PowerManager.class)) {
            return (T) getPowerManager();
        }
        if (T.equals(jmri.SensorManager.class)) {
            return (T) getSensorManager();
        }
        if (T.equals(jmri.TurnoutManager.class)) {
            return (T) getTurnoutManager();
        }
        /*if (T.equals(jmri.ReporterManager.class))
         return (T)getReporterManager();*/
        return null; // nothing, by default
    }

    @Override
    public void dispose() {
        if (sensorManager != null) {
            sensorManager.dispose();
            sensorManager = null;
        }
        if (turnoutManager != null) {
            turnoutManager.dispose();
            turnoutManager = null;
        }
        /*if(reporterManager!=null){
         reporterManager.dispose();
         reporterManager=null;
         }*/

        if (powerManager != null) {
            InstanceManager.deregister(powerManager, jmri.jmrix.hsi88.Hsi88PowerManager.class);
        }

        if (throttleManager != null) {
            InstanceManager.deregister(throttleManager, jmri.jmrix.hsi88.Hsi88ThrottleManager.class);
        }

        et = null;
        InstanceManager.deregister(this, Hsi88SystemConnectionMemo.class);
        if (cf != null) {
            InstanceManager.deregister(cf, jmri.jmrix.swing.ComponentFactory.class);
        }

        super.dispose();
    }
}


/* @(#)InternalSystemConnectionMemo.java */
