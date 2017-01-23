package jmri.jmrix.hsi88;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Tests for the jmri.jmrix.hsi88 package.
 *
 * @author Bob Jacobsen.
 * @author Andre Gruening 2017: adapted for Hsi88, based on previous author's
 *         Sprog implementation.
 * 
 * @since 4.6.x
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        Hsi88SystemConnectionMemoTest.class,
        Hsi88TrafficControllerTest.class,
        Hsi88MessageTest.class,
        Hsi88StreamPortControllerTest.class,
        Hsi88PowerManagerTest.class,
        Hsi88SensorManagerTest.class,
        Hsi88SensorTest.class,
        Hsi88ConnectionTypeListTest.class,
        jmri.jmrix.hsi88.pi.PackageTest.class,
        jmri.jmrix.hsi88.serialdriver.PackageTest.class,
        jmri.jmrix.hsi88.hsi88.PackageTest.class,
        jmri.jmrix.hsi88.hsi88CS.PackageTest.class,
        jmri.jmrix.hsi88.hsi88nano.PackageTest.class,
        jmri.jmrix.hsi88.configurexml.PackageTest.class,
        jmri.jmrix.hsi88.swing.PackageTest.class,
        jmri.jmrix.hsi88.packetgen.PackageTest.class,
        jmri.jmrix.hsi88.console.PackageTest.class,
        jmri.jmrix.hsi88.hsi88mon.PackageTest.class,
        jmri.jmrix.hsi88.hsi88slotmon.PackageTest.class,
        Hsi88MenuTest.class,
})
public class PackageTest {
}
