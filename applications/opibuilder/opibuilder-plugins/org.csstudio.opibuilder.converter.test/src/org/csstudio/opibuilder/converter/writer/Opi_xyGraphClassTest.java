/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.csstudio.opibuilder.converter.EdmConverterTest;
import org.csstudio.opibuilder.converter.model.EdmDisplay;
import org.csstudio.opibuilder.converter.model.EdmException;
import org.csstudio.opibuilder.converter.model.EdmModel;
import org.csstudio.opibuilder.converter.model.Edm_xyGraphClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Opi_xyGraphClassTest {
    Document doc;
    EdmDisplay display;
    Element root;

    @BeforeClass
    public static void oneTimeSetup() {
        System.setProperty("edm2xml.robustParsing", "false");
        System.setProperty("edm2xml.colorsFile", EdmConverterTest.COLOR_LIST_FILE);
    }

    @Before
    public void setup() throws EdmException {

        doc = XMLFileHandler.createDomDocument();
        root = doc.createElement("root");
        doc.appendChild(root);

        String edlFile = EdmConverterTest.RESOURCES_LOCATION + "xyGraph_example.edl";
        EdmModel.getInstance();
        display = EdmModel.getDisplay(edlFile);
    }


    /** Get the EDM graph widget of specified graphTitle
     *
     * @param title
     * @return GraphEntity with specified title
     */
    private Edm_xyGraphClass getEdmTitledEntity(String title) {
        Edm_xyGraphClass entity;
        for (int i = 0; i < display.getSubEntityCount(); i++) {
            if (display.getSubEntity(0) instanceof Edm_xyGraphClass) {
                entity = (Edm_xyGraphClass)display.getSubEntity(i);
                if (entity.getGraphTitle().equals(title)) {
                    return entity;
                }
            }
        }
        System.out.println("Null");
        return null;
    }

    @Test
    public void test_xygraph_basic_setup() throws EdmException {

        Edm_xyGraphClass edm = getEdmTitledEntity("BPM Line");
        assertNotNull("No XYGraph found", edm);

        populateXml(edm);

        Element e = (Element)doc.getElementsByTagName("widget").item(0);
        assertEquals("org.csstudio.opibuilder.widgets.dawn.xygraph", e.getAttribute("typeId"));
        assertEquals("1.0", e.getAttribute("version"));
        XMLFileHandler.isElementEqual("EDM xyGraph", "name", e);

    }

    /**
     * Perfrom a conversion of the input EDM entity and inject the output XML
     * into the class 'doc' object
     *
     * @param edm
     * @return Constructed Opi_xyGraphClass
     */
    private Opi_xyGraphClass populateXml(Edm_xyGraphClass edm) {
        Context context = new Context(doc, root, display, 0, 0);
        return new Opi_xyGraphClass(context, edm);
    }

    @Test
    public void test_position() throws EdmException {

        Edm_xyGraphClass edm = getEdmTitledEntity("BPM Line");
        assertNotNull("No XYGraph found", edm);

        populateXml(edm);

        Element element = (Element)doc.getElementsByTagName("widget").item(0);

        XMLFileHandler.isElementEqual("8", "x", element);
        XMLFileHandler.isElementEqual("96", "y", element);
        XMLFileHandler.isElementEqual("993", "width", element);  // width++
        XMLFileHandler.isElementEqual("281", "height", element); // height++
    }


    @Test
    public void test_two_xy_pvs_in_BPMLine_converted() throws EdmException {

        Edm_xyGraphClass edm = getEdmTitledEntity("BPM Line");
        assertNotNull("No XYGraph found", edm);

        populateXml(edm);

        Element element = (Element)doc.getElementsByTagName("widget").item(0);

        XMLFileHandler.isElementEqual("SR-DI-EBPM-01:BPMID", "trace_0_x_pv", element);
        XMLFileHandler.isElementEqual("SR-DI-EBPM-01:SA:X", "trace_0_y_pv", element);
        XMLFileHandler.isElementEqual("SR-DI-EBPM-02:BPMID", "trace_1_x_pv", element);
        XMLFileHandler.isElementEqual("SR-DI-EBPM-02:SA:Y", "trace_1_y_pv", element);
    }

    @Test
    public void test_one_xy_pv_in_TunePhase_converted() throws EdmException {

        Edm_xyGraphClass edm = getEdmTitledEntity("Tune Phase");
        assertNotNull("No XYGraph found", edm);

        populateXml(edm);

        Element element = (Element)doc.getElementsByTagName("widget").item(0);

        XMLFileHandler.isElementEqual("$(device):DET:SCALE", "trace_0_x_pv", element);
        XMLFileHandler.isElementEqual("$(device):TUNE:PHASEWF", "trace_0_y_pv", element);
        // No trace2 PVs
        assertTrue(element.getElementsByTagName("trace_1_x_pv").getLength() == 0);
        assertTrue(element.getElementsByTagName("trace_1_y_pv").getLength() == 0);
    }

}
