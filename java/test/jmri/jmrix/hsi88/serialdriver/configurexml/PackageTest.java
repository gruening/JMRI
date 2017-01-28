package jmri.jmrix.hsi88.serialdriver.configurexml;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(Suite.class)
@Suite.SuiteClasses({
   ConnectionConfigXmlTest.class
})
/**
 * Tests for the jmri.jmrix.hsi88.serialdriver.configurexml package.
 *
 * @author Paul Bender Copyright (C) 2016
 * @author Andre Gruening 2017: trivially adapted for Hsi88 from previous
 *         author's Sprog test.
 * @since 4.6.xS
 */
public class PackageTest {

    // Main entry point
    static public void main(String[] args) {
        org.junit.runner.Result result = org.junit.runner.JUnitCore
                 .runClasses(PackageTest.class);
        for(org.junit.runner.notification.Failure fail: result.getFailures()) {
            log.error(fail.toString());
        }
        //junit.textui.TestRunner.main(testCaseName);
        if (result.wasSuccessful()) {
            log.info("Success");
        }
    }

    private final static Logger log = LoggerFactory.getLogger(PackageTest.class.getName());

}
