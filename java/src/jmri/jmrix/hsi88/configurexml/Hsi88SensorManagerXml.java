package jmri.jmrix.hsi88.configurexml;

import jmri.configurexml.JmriConfigureXmlException;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides load and store functionality for configuring Hsi88SensorManagers.
 * <P>
 * Uses the store method from the abstract base class, but provides a load
 * method here.
 *
 * @author Bob Jacobsen Copyright: Copyright (c) 2002.
 * @author Andre Gruening 2017: trivally adapted for Hsi88 from previous
 *         author's Sprog implementation.
 * 
 */
public class Hsi88SensorManagerXml extends jmri.managers.configurexml.AbstractSensorManagerConfigXML {

    public Hsi88SensorManagerXml() {
        super();
    }

    public void setStoreElementClass(Element sensors) {
        sensors.setAttribute("class", "jmri.jmrix.hsi88.configurexml.Hsi88SensorManagerXml");
    }

    public void load(Element element, Object o) {
        log.error("Invalid method called");
    }

    // initialize logging
    private final static Logger log = LoggerFactory.getLogger(Hsi88SensorManagerXml.class.getName());

    @Override
    public boolean load(Element sharedSensors, Element perNodeSensors) throws JmriConfigureXmlException {
        return loadSensors(sharedSensors);
    }
}
