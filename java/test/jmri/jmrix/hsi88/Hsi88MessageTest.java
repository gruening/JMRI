package jmri.jmrix.hsi88;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit tests for the Hsi88Message class
 *
 * @author Bob Jacobsen Copyright 2012.
 * @author Andre Gruening 2017: adapted for Hsi88 based on previous author's
 *         Sprog implementation.
 */
public class Hsi88MessageTest {

    @Test
    public void testCreate() {
        Hsi88Message m = new Hsi88Message("s112233\r");
        Assert.assertNotNull("exists", m);
    }

    @Test
    public void testPowerOffMsg() {
        Hsi88Message m = Hsi88Message.powerOff();
        Assert.assertEquals("string compare ", "s000000\r", m.toString());
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
