/**
 * 
 */
package jmri.jmrix.hsi88;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the Hsi88ReplyClass
 * 
 * @author Andre Gruening, Copyright (C) 2017.
 * @since 4.6.x
 *
 */
public class Hsi88ReplyTest {

    @Before
    public void setUp() throws Exception {
        apps.tests.Log4JFixture.setUp();
    }

    @After
    public void tearDown() throws Exception {
        apps.tests.Log4JFixture.tearDown();
    }

    /**
     * Test method for ctor {@link jmri.jmrix.hsi88.Hsi88Reply#Hsi88Reply()}.
     */
    @Test
    public final void testHsi88Reply() {
        Hsi88Reply r = new Hsi88Reply();
        Assert.assertNotNull("Create Empty Reply.", r);
    }

    /**
     * Test method for {@link jmri.jmrix.hsi88.Hsi88Reply#toString()}.
     */
    @Test
    public final void testToString() {
        Hsi88Reply r = new Hsi88Reply();
        r.setElement(0, 'x');
        r.setElement(1, 'y');
        Assert.assertEquals("Reply as a string.", "xy", r.toString());
    }

    /**
     * Test method for {@link jmri.jmrix.hsi88.Hsi88Reply#maxSize()}. Test
     * whether it can take longest messages from Hsi88 interface.
     */
    @Test
    public final void testMaxSize() {
        Hsi88Reply r = new Hsi88Reply();
        Assert.assertTrue("Maximal Size",
                r.maxSize() >= "i00".length() + Hsi88Config.MAX_MODULES * "112233".length() + "\r".length());

    }

    /**
     * Test method for {@link jmri.jmrix.hsi88.Hsi88Reply#end()}.
     */
    @Test
    public final void testEnd() {
        Hsi88Reply r = new Hsi88Reply();
        r.setElement(0, 'x');
        r.setElement(1, 'y');
        Assert.assertFalse("Open message", r.end());
        
        r.setElement(2, '\r');
        Assert.assertTrue("End by cr", r.end());
        
        r.setElement(2, 'z');
        r.setElement(r.maxSize()-1, 'e');
        Assert.assertTrue("End by MAXSIZE", r.end());
    }

    /**
     * Test method for
     * {@link jmri.jmrix.hsi88.Hsi88Reply#getSetupReplyModules()}.
     * 
     * @todo add more negative tests.
     */
    @Test
    public final void testGetSetupReplyModules() {
        Hsi88Reply r = new Hsi88Reply();
        r.setElement(0, 's');
        r.setElement(1, '1');
        r.setElement(2, 'f');

        Assert.assertTrue("Incomplete 's' reply", r.getSetupReplyModules() < 0);

        r.setElement(3, '\r');
        Assert.assertEquals("Complete 's' reply", 31, r.getSetupReplyModules());
    }
}
