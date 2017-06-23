package jmri.jmrix.hsi88.hsi88;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * tests for the jmri.jmrix.hsi88.hsi88CS package
 *
 * @author Paul Bender Copyright (C) 2016
 * @author Andre Gruening 2017: trivially adapted from previous author's Sprog
 *         test.
 * 
 * @since 4.6.x
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        ConnectionConfigTest.class,
        Hsi88SerialDriverAdapterTest.class,
        jmri.jmrix.hsi88.hsi88.configurexml.PackageTest.class
})
public class PackageTest {
}
