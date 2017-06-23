/**
 * 
 */
package jmri.jmrix.hsi88;

import static org.junit.Assert.assertNotNull;

import jmri.Sensor;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Andre Gruning, Copyright 2017.
 * 
 *         Tests for the Hsi88Manager class, the core class for parsing messages
 *         from the Hsi88 and to keep track of the Hsi88's state. *
 */
public class Hsi88ManagerTest {

    private static Hsi88SystemConnectionMemo memo;
    private static Hsi88Manager manager;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUp() throws Exception {
        memo = new Hsi88SystemConnectionMemo();
        memo.setTrafficController(new Hsi88TrafficController(memo));
        memo.configureManagers();
        manager = memo.getManager();
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDown() throws Exception {
        manager = null;
        memo = null;
    }

    /**
     * Test the constructor and field initialisation.
     */
    private void testConstrutor() {
        assertNotNull(manager);
        Assert.assertEquals("Initial Protocol is not UNKNOWN", manager.getProtocol(),
                Hsi88Config.Hsi88Protocol.UNKNOWN);
        Assert.assertEquals("Initial number of modules is not zero.", manager.getReportedModules(), 0);
        Assert.assertEquals("Initial version should be \"Version unknown\".", manager.getVersion(), "Version unknown");
    }

    /**
     * Auxiliary Error that is thrown during testNotify
     * 
     * @author gruening
     *
     */
    private static class Called extends Error {
        Called(String s) {
            super(s);
        }
    };

    /**
     * Test we can add listeners and that they are called.
     */
    @Test(expected = Called.class)
    public void testNotify() {

        // We want to raise a reply, but one that does not change the state of the manager.
        Hsi88Reply dummy = new Hsi88Reply("Version unknown\r");

        Hsi88ReplyListener explosive = new Hsi88ReplyListener() {

            @Override
            public void notifyReply(int event, int payload) {
                throw new Called(
                        "We have sucessfully been added and called. If you see this more than once we have not been removed successfully.");
            }
        };
        manager.addSensorListener(explosive);
        manager.notifyReply(dummy);
        // TODO: is this code at all executed after the previous line triggers the exception?
        manager.removeSensorListener(explosive);

        // test notify on empty list of listener and that remove has worked.
        manager.notifyReply(dummy);
    }

    /**
     * Test correct parsing of a version reply sent from the HSI88.
     */
    @Test
    public void testVersionReply() {
        final String versionStr = "Version 3.14159265358979";

        Hsi88ReplyListener versionListener = new Hsi88ReplyListener() {

            @Override
            public void notifyReply(int event, int payload) {
                Assert.assertEquals("Version string not set correctly.", versionStr, manager.getVersion());

                Assert.assertEquals("Version opcode wrong.", event, Hsi88Manager.ResponseType.VERSION);
                Assert.assertEquals("Payload must be 0.", payload, 0);
            }
        };
        manager.addSensorListener(versionListener);
        Hsi88Reply versionReply = new Hsi88Reply(versionStr + '\r');
        manager.notifyReply(versionReply);
        manager.removeSensorListener(versionListener);
    }

    /**
     * Test whether a protocol reply is correctly parsed and switches the
     * protocol to ASCII.
     */
    private void testProtocolReply() {

        // switch to ASCII mode
        Hsi88Reply toggleReading = new Hsi88Reply("t1\r");
        Hsi88ReplyListener toggleListener = new Hsi88ReplyListener() {
            @Override
            public void notifyReply(int event, int payload) {

                Assert.assertEquals(
                        "Protocol not set to ASCII.",
                        manager.getProtocol(),
                        Hsi88Config.Hsi88Protocol.ASCII);

                Assert.assertEquals(
                        "Expected protocol toggle event",
                        event,
                        Hsi88Manager.ResponseType.TERMINAL);
                Assert.assertEquals(
                        "Expected ASCII protocol.",
                        payload,
                        Hsi88Config.Hsi88Protocol.ASCII.ordinal());
            }
        };

        manager.addSensorListener(toggleListener);
        manager.notifyReply(toggleReading);
        manager.removeSensorListener(toggleListener);
    }

    /**
     * In ASCII mode, test whether a setup reply correctly set the chain
     * lengths.
     */
    private void testSetupReply() {

        // reply with maximal possible number (31 = 0x1F) of sensors. 
        Hsi88Reply setupReply = new Hsi88Reply("s1F\r");
        Hsi88ReplyListener setupListener = new Hsi88ReplyListener() {

            @Override
            public void notifyReply(int event, int payload) {

                Assert.assertEquals("Modules not setup.", 0x1F, manager.getReportedModules());

                Assert.assertEquals("Event code wrong.", event, Hsi88Manager.ResponseType.SETUP);
                Assert.assertEquals("Payload wrong.", payload, 0x1F);
            }
        };
        manager.addSensorListener(setupListener);
        manager.notifyReply(setupReply);
        manager.removeSensorListener(setupListener);

    }

    /**
     * In ASCII mode, and with chain length set to 0x1F, test whether a sensor
     * reply is parsed and sets the internal state for that sensor correctly.
     */
    private void testSensorReply() {

        // switch on sensor with highest address
        Hsi88Reply sensorReading = new Hsi88Reply("i1F1F8000\r");

        Hsi88ReplyListener sensorListener = new Hsi88ReplyListener() {

            @Override
            public void notifyReply(int event, int payload) {

                Assert.assertEquals(
                        "Expected different sensor address.",
                        event,
                        (Hsi88Config.MAX_MODULES - 1) * 16 + 15); // address of last sensor.
                Assert.assertTrue(
                        "Expected sensor to be on.",
                        payload == Sensor.ACTIVE);
            }
        };
        manager.addSensorListener(sensorListener);
        manager.notifyReply(sensorReading);
        manager.removeSensorListener(sensorListener);
    }

    /**
     * run listed tests in required order.
     */
    @Test
    public void testAll() {
        testConstrutor();
        testProtocolReply();
        testSetupReply();
        testSensorReply();
    }
}
