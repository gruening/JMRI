package jmri.jmrix.hsi88.swing;

import apps.tests.Log4JFixture;
import java.awt.GraphicsEnvironment;
import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

/**
 * Test simple functioning of Hsi88ComponentFactory
 *
 * @author Paul Bender Copyright (C) 2016.
 * @author Andre Gruening 2017: trivially adapted for Hsi88 from previous
 *         author's Sprog test.
 * @since 4.6.x
 */
public class Hsi88ComponentFactoryTest {

    private jmri.jmrix.hsi88.Hsi88SystemConnectionMemo m = null;

    @Test
    public void testCtor() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        Hsi88ComponentFactory action = new Hsi88ComponentFactory(m);
        Assert.assertNotNull("exists", action);
    }

    @Before
    public void setUp() {
        Log4JFixture.setUp();
        JUnitUtil.resetInstanceManager();
        m = new jmri.jmrix.hsi88.Hsi88SystemConnectionMemo();
    }

    @After
    public void tearDown() {
        JUnitUtil.resetInstanceManager();
        Log4JFixture.tearDown();
    }
}
