package jmri.jmrix.hsi88.serialdriver.configurexml;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * ConnectionConfigXmlTest.java
 *
 * Description: tests for the ConnectionConfigXml class
 *
 * @author Paul Bender Copyright (C) 2016.
 * @author Andre Gruening 2017: trivially adapted for Hsi88 from previous
 *         author's Sprog test.
 * @since 4.6.x
 */
public class ConnectionConfigXmlTest {

    @Test
    public void testCtor() {
        Assert.assertNotNull("ConnectionConfigXml constructor", new ConnectionConfigXml());
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
