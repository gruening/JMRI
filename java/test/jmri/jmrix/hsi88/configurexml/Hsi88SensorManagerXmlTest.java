package jmri.jmrix.hsi88.configurexml;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * hsi88TurnoutManagerXmlTest.java
 *
 * Description: tests for the hsi88TurnoutManagerXml class
 *
 * @author Paul Bender Copyright (C) 2016.
 * @author Andre Gruening 2017: trivially adapted for Hsi88 from previous
 *         author's Sprog implementation.
 * @since 4.6.x
 */
public class Hsi88SensorManagerXmlTest {

    @Test
    public void testCtor() {
        Assert.assertNotNull("Hsi88SensorManagerXml constructor", new Hsi88SensorManagerXml());
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
