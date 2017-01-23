package jmri.jmrix.hsi88;

import apps.tests.Log4JFixture;
import java.awt.GraphicsEnvironment;
import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

/**
 * Test simple functioning of hsi88CSMenu
 *
 * @author Paul Bender Copyright (C) 2016
 * @author Andre Gruening 2017: trivially adapted for Hsi88 from previous
 *         author's Sprog implementation.
 * 
 * @since 4.6.x
 */
public class Hsi88MenuTest {

    @Test
    public void testCtor() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        // the constructor looks for the default ListedTableFrame class, 
        // which is set by the ListedTableFrame constructor.
        new jmri.jmrit.beantable.ListedTableFrame();
        Hsi88Menu action = new Hsi88Menu(new jmri.jmrix.hsi88.Hsi88SystemConnectionMemo());
        Assert.assertNotNull("exists", action);
    }

    @Before
    public void setUp() {
        Log4JFixture.setUp();
        JUnitUtil.resetInstanceManager();
        JUnitUtil.initDefaultUserMessagePreferences();
    }

    @After
    public void tearDown() {
        JUnitUtil.resetInstanceManager();
        Log4JFixture.tearDown();
    }
}
