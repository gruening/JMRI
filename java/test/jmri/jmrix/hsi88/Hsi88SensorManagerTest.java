package jmri.jmrix.hsi88;

import jmri.Sensor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * <P>
 * Tests for Hsi88SensorManager.
 * </P>
 * 
 * @author Paul Bender Copyright (C) 2016
 * @author Andre Gruening Copyright (C) 2017: implemented for Hsi88 based in
 *         part on previous author's Sprog implementation.
 * 
 * @since 4.6.x
 */
public class Hsi88SensorManagerTest extends jmri.managers.AbstractSensorMgrTest {
    private Hsi88SystemConnectionMemo m = null;

    @Override
    public String getSystemName(int i) {
        return "HS" + i;
    }

    @Test
    public void ConstructorTest() {
        Assert.assertNotNull(l);
    }
    
    @Test
    public void createSensorTest() {
        Hsi88SensorManager h = (Hsi88SensorManager) l;
        
        Sensor s = h.createNewSensor("HS0", null); 
        Assert.assertNotNull("Sensor with lowest address.", s);

        int highestAddr = Hsi88Config.MAXMODULES*16 - 1; // 495
        String systemName = "HS" + highestAddr;
        s = h.createNewSensor(systemName, null);
        Assert.assertNotNull("Sensor with highest address.", s);
        
        // address not parseable to nonnegative int:
        s = h.createNewSensor("HSxxxx", null);
        Assert.assertNull("Noninteger address must not yield sensor", s);
    }

    // The minimal setup for log4J
    @Override
    @Before
    public void setUp() {
        apps.tests.Log4JFixture.setUp();
        jmri.util.JUnitUtil.resetInstanceManager();
        m = new Hsi88SystemConnectionMemo();
        m.setHsi88TrafficController(new Hsi88TrafficController(m));
        l = new Hsi88SensorManager(m);
    }

    @After
    public void tearDown() {
        m = null;
        l = null;
        jmri.util.JUnitUtil.resetInstanceManager();
        apps.tests.Log4JFixture.tearDown();
    }

}
