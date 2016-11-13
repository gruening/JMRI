package jmri.jmrix.hsi88.configurexml;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Hsi88SensorManagerXmlTest.java
 *
 * Description: tests for the Hsi88SensorManagerXml class
 *
 * @author   Paul Bender  Copyright (C) 2016
 */
public class Hsi88SensorManagerXmlTest {

    @Test
    public void testCtor(){
      Assert.assertNotNull("Hsi88SensorManagerXml constructor",new Hsi88SensorManagerXml());
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

