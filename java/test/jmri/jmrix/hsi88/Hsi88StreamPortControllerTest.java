package jmri.jmrix.hsi88;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import jmri.jmrix.sprog.SprogCSStreamPortController;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * SprogCSStreamPortControllerTest.java
 *
 * Description: tests for the jmri.jmrix.sprog.SprogCSStreamPortController class
 *
 * @author Paul Bender Copyrght (C) 2014-2016.
 * @author Andre Gruening 2017: trivially adapted for Hsi88 from previous
 *         author's Sprog implementation.
 * 
 * @since 4.6.x
 */
public class Hsi88StreamPortControllerTest {

    @Test
    public void testCtor() {

        try {
            PipedInputStream tempPipe;
            tempPipe = new PipedInputStream();
            DataOutputStream ostream = new DataOutputStream(new PipedOutputStream(tempPipe));
            tempPipe = new PipedInputStream();
            DataInputStream istream = new DataInputStream(tempPipe);

            SprogCSStreamPortController xspc = new SprogCSStreamPortController(istream, ostream, "Test");
            Assert.assertNotNull("exists", xspc);
        } catch (java.io.IOException ioe) {
            Assert.fail("IOException creating stream");
        }
    }

    // The minimal setup for log4J
    @Before
    public void setUp() throws Exception {
        apps.tests.Log4JFixture.setUp();
    }

    @After
    public void tearDown() throws Exception {
        apps.tests.Log4JFixture.tearDown();
    }

}
