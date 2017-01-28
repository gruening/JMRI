package jmri.jmrix.hsi88.console;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        Hsi88ConsoleFrameTest.class,
        Hsi88ConsoleActionTest.class
})

/**
 * Tests for the jmri.jmrix.Hsi88.console package
 *
 * @author Paul Bender Copyright (C) 2016.
 * @author Andre Gruening 2017: trivially adapted for Hsi88 from previous
 *         author's Sprog Test.
 */
public class PackageTest {

    // Main entry point
    static public void main(String[] args) {
        org.junit.runner.Result result = org.junit.runner.JUnitCore
                .runClasses(PackageTest.class);
        for (org.junit.runner.notification.Failure fail : result.getFailures()) {
            log.error(fail.toString());
        }
        if (result.wasSuccessful()) {
            log.info("Success");
        }
    }

    private final static Logger log = LoggerFactory.getLogger(PackageTest.class.getName());

}
