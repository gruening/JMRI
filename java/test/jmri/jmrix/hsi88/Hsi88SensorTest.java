package jmri.jmrix.hsi88;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * <P>
 * Tests for Hsi88Sensor. Hsi88Sensor is very light-weight -- so not many tests
 * can be run.
 * </P>
 * 
 * @author Paul Bender Copyright (C) 2016.
 * @author Andre Gruening 2017: trivially adapted from previous author's
 *         Sprog implementation.
 * @since 4.6.x
 */
public class Hsi88SensorTest {

    @Test
    public void ConstructorTest() {
        Hsi88SystemConnectionMemo m = new Hsi88SystemConnectionMemo();
        m.setTrafficController(new Hsi88TrafficController(m));
        Hsi88Sensor t = new Hsi88Sensor("systemName", "userName", m);
        Assert.assertNotNull(t);
    }

    // The minimal setup for log4J
    @Before
    public void setUp() {
        apps.tests.Log4JFixture.setUp();
    }

    @After
    public void tearDown() {
        apps.tests.Log4JFixture.tearDown();
    }

}
