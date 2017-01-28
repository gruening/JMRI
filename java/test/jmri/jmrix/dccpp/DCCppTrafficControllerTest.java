package jmri.jmrix.dccpp;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Generated by JBuilder
 * <p>
 * Title: DCCppTrafficControllerTest </p>
 * <p>
 * Description: </p>
 * <p>
 * Copyright: Copyright (c) 2002, 2015</p>
 *
 * @author Bob Jacobsen
 * @author Mark Underwood
 */
public class DCCppTrafficControllerTest extends jmri.jmrix.AbstractMRTrafficControllerTest {

    // The minimal setup for log4J
    @Override
    @Before
    public void setUp() {
        apps.tests.Log4JFixture.setUp();
        tc = new DCCppTrafficController(new DCCppCommandStation()){
            @Override
            public void sendDCCppMessage(DCCppMessage m, DCCppListener reply){
            }
        };

    }

    @Override
    @After
    public void tearDown() {
        apps.tests.Log4JFixture.tearDown();
    }

}
