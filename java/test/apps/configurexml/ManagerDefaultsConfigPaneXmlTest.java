package apps.configurexml;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * ManagerDefaultsConfigPaneXmlTest.java
 *
 * Description: tests for the ManagerDefaultsConfigPaneXml class
 *
 * @author   Paul Bender  Copyright (C) 2016
 */
public class ManagerDefaultsConfigPaneXmlTest {

    @Test
    public void testCtor(){
      Assert.assertNotNull("ManagerDefaultsConfigPaneXml constructor",new ManagerDefaultsConfigPaneXml());
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

