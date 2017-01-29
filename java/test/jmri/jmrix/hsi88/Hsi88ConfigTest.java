/**
 * 
 */
package jmri.jmrix.hsi88;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for Hsi88Config.
 * 
 * @author Andre Gruening Copyright (C) 2017.
 * 
 * @since 4.6.x
 */
public class Hsi88ConfigTest {

    @Before
    public void setUp() throws Exception {
        apps.tests.Log4JFixture.setUp();
    }

    @After
    public void tearDown() throws Exception {
        apps.tests.Log4JFixture.tearDown();

    }

    /**
     * Test method for {@link jmri.jmrix.hsi88.Hsi88Manager#getReportedModules()}.
     */
    @Test
    public void testGetSetupModules() {
        Hsi88Config.setLeft(5);
        Hsi88Config.setRight(6);
        Hsi88Config.setMiddle(7);

        int chainLength = Hsi88Manager.getReportedModules();
        Assert.assertEquals(chainLength, 18);
    }

}
