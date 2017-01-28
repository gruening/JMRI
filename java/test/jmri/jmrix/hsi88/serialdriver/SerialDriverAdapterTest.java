package jmri.jmrix.hsi88.serialdriver;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * <P>
 * Tests for SerialDriverAdapter
 * </P>
 * 
 * @author Paul Bender Copyright (C) 2016.
 * @author Andre Gruening 2017: trivially adapted for Hsi88 from previous
 *         author's Sprog test.
 */
public class SerialDriverAdapterTest {

    @Test
    public void ConstructorTest() {
        SerialDriverAdapter a = new SerialDriverAdapter();
        Assert.assertNotNull(a);
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
