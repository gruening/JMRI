package jmri.jmrix.hsi88;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * <P>
 * Tests for Hsi88PowerManager.
 * </P>
 * 
 * @author Paul Bender Copyright (C) 2016.
 * @author Andre Gruening 2017: trivially adapted for Hsi88 based on previous
 *         author's Sprog implementation.
 * 
 * @since 4.6.x
 * 
 */
public class Hsi88PowerManagerTest {

    @Test
    public void ConstructorTest() {
        Hsi88SystemConnectionMemo m = new Hsi88SystemConnectionMemo();
        m.setTrafficController(new Hsi88TrafficController(m)); // constructor calls getHsi88TrafficController.
        Hsi88PowerManager tc = new Hsi88PowerManager(m);
        Assert.assertNotNull(tc);
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
