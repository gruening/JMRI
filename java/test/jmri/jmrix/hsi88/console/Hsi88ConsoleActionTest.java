package jmri.jmrix.hsi88.console;

import apps.tests.Log4JFixture;
import java.awt.GraphicsEnvironment;
import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

/**
 * Test simple functioning of hsi88ConsoleAction
 *
 * @author Paul Bender Copyright (C) 2016
 * @author Andre Gruening 2017: trivially adapted for Hsi88 from previous
 *         author's Sprog implementation.
 */
public class Hsi88ConsoleActionTest {

    @Test
    public void testCtor() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        Hsi88ConsoleAction action =
                new Hsi88ConsoleAction("Hsi88 Action Test", new jmri.jmrix.hsi88.Hsi88SystemConnectionMemo());
        Assert.assertNotNull("exists", action);
    }

    @Before
    public void setUp() {
        Log4JFixture.setUp();
        JUnitUtil.resetInstanceManager();
    }

    @After
    public void tearDown() {
        JUnitUtil.resetInstanceManager();
        Log4JFixture.tearDown();
    }
}
