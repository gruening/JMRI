package jmri.jmrix.hsi88.hsi88;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Andre Gruening Copyright (C) 2017.
 *
 */
public class Hsi88SerialDriverAdapterTest {

    @Before
    public void setUp() throws Exception {
        apps.tests.Log4JFixture.setUp();
    }

    @After
    public void tearDown() throws Exception {
        apps.tests.Log4JFixture.tearDown();
    }

    /**
     * Test method for
     * {@link jmri.jmrix.hsi88.hsi88.Hsi88SerialDriverAdapter#Hsi88SerialDriverAdapter()}.
     */
    @Test
    public final void testHsi88SerialDriverAdapter() {
        Hsi88SerialDriverAdapter sda = new Hsi88SerialDriverAdapter();
        Assert.assertNotEquals("Constructor", sda);
    }
}
