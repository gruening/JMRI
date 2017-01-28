package jmri.jmrix.hsi88.serialdriver;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * tests for the jmri.jmrix.hsi88.serialdriver package
 *
 * @author Paul Bender Copyright (C) 2016.
 * @author Andre Gruening 2017: trivially adapted for Hsi88 from previous
 *         author's Sprog test.
 * @since 4.6.x
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        SerialDriverAdapterTest.class,
        ConnectionConfigTest.class,
        jmri.jmrix.hsi88.serialdriver.configurexml.PackageTest.class

})
public class PackageTest {
}
