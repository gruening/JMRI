package jmri.jmrix.hsi88.serialdriver;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for ConnectionConfig class.
 *
 * @author Paul Bender Copyright (C) 2016
 * @author Andre Gruening 2017: trivially adapted for Hsi88 from previous
 *         author's Sprog test.
 * @since 4.6.x
 **/

public class ConnectionConfigTest {

    @Test
    public void ConstructorTest() {
        Assert.assertNotNull("ConnectionConfig constructor", new ConnectionConfig());
    }

    @Before
    public void setUp() {
        apps.tests.Log4JFixture.setUp();
        jmri.util.JUnitUtil.resetInstanceManager();
        jmri.util.JUnitUtil.initDefaultUserMessagePreferences();
    }

    @After
    public void tearDown() {
        apps.tests.Log4JFixture.tearDown();
        jmri.util.JUnitUtil.resetInstanceManager();
    }

}
