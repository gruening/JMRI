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
     * Test method for {@link jmri.jmrix.hsi88.Hsi88Config#getSetupModules()}.
     */
    @Test
    public void testGetSetupModules() {
        Hsi88Config.left = 5;
        Hsi88Config.right = 6;
        Hsi88Config.middle = 7;

        int chainLength = Hsi88Config.getSetupModules();
        Assert.assertEquals(chainLength, 18);
    }

}
