package jmri.jmrix.hsi88;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * <P>
 * Tests for Hsi88ConnectionTypeList
 * </P>
 * 
 * @author Paul Bender Copyright (C) 2016
 * @author Andre Gruening 2017: trivially adapted for Hsi88 from previous
 *         author's implementation for Sprog.
 * 
 * @since 4.6.x
 */
public class Hsi88ConnectionTypeListTest {

    @Test
    public void ConstructorTest() {
        Hsi88ConnectionTypeList ct = new Hsi88ConnectionTypeList();
        Assert.assertNotNull(ct);
    }

    @Test
    public void ManfacturerString() {
        Hsi88ConnectionTypeList ct = new Hsi88ConnectionTypeList();
        Assert.assertEquals("Manufacturers", new String[]{"HSI88"}, ct.getManufacturers());
    }

    @Test
    public void ProtocolClassList() {
        Hsi88ConnectionTypeList ct = new Hsi88ConnectionTypeList();
        Assert.assertEquals("Protocol Class List", new String[]{
                "jmri.jmrix.hsi88.hsi88.ConnectionConfig"},
                ct.getAvailableProtocolClasses());
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
