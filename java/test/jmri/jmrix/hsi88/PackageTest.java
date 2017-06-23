package jmri.jmrix.hsi88;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Tests for the jmri.jmrix.hsi88 package. 
 * 
 * TODO add a Hsi88 specific component
 * to jmri/configurexml/valid/osj_v3.8no_interlock.xml, just a there is for
 * Sprog?
 * 
 * TODO include tests for new classes in this file.
 * 
 * @author Bob Jacobsen.
 * @author Andre Gruening 2017: adapted for Hsi88, based on previous author's
 *         Sprog implementation.
 * 
 *
 * 
 * @since 4.6.x
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        Hsi88ConfigTest.class,
        Hsi88SystemConnectionMemoTest.class,
        Hsi88TrafficControllerTest.class,
        Hsi88MessageTest.class,
        Hsi88ReplyTest.class,
        Hsi88StreamPortControllerTest.class,
        Hsi88PowerManagerTest.class,
        Hsi88SensorManagerTest.class,
        Hsi88SensorTest.class,
        Hsi88ConnectionTypeListTest.class,
        jmri.jmrix.hsi88.serialdriver.PackageTest.class,
        jmri.jmrix.hsi88.hsi88.PackageTest.class,
        jmri.jmrix.hsi88.configurexml.PackageTest.class,
        jmri.jmrix.hsi88.swing.PackageTest.class,
        jmri.jmrix.hsi88.console.PackageTest.class,
        // Hsi88PortControllerTest.class, // abstract class.
        Hsi88MenuTest.class,
})
public class PackageTest {}
