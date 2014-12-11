/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import java.text.DecimalFormat;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.csstudio.opibuilder.converter.model.EdmFont;
import org.w3c.dom.Element;

/**
 * XML output class for EdmFont type.
 * @author Matevz
 */
public class OpiFont extends OpiAttribute {

	// Definitions copied from org.eclipse.swt.SWT class.
	private static final int NORMAL = 0;
	private static final int BOLD = 1 << 0;
	private static final int ITALIC = 1 << 1;
	
	// EDMs X Fonts are displayed very differently to similar fonts in SWT.
	// We need to scale them accordingly.
	private final static HashMap<String, Double> fontScales = new HashMap<String, Double>();
	static {
		fontScales.put("arial", 0.78);
		fontScales.put("helvetica", 0.75);
		fontScales.put("courier", 0.80);
		fontScales.put("default", 0.78);  // All Diamond fonts are variants of Arial
	}

	// We should replace some old fonts like courier with newer variants
	private final static HashMap<String, String> typeFaces = new HashMap<String, String>();
	static {
		typeFaces.put("arial", "arial");
		typeFaces.put("helvetica", "arial");
		typeFaces.put("courier", "monospace");
	}

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.writer.OpiFont");	

	/**
	 * Creates an element: 
	 * <tag>
	 *   	 <font fontName="fontNameValue" height="heightValue" style="styleValue" /> 
	 * </tag>
	 * styleValue is determined this way:
	 * 		0 - medium, regular
	 * 		1 - bold, regular
	 * 		2 - medium, italic
	 * 		3 - bold, italic
	 */
	public OpiFont(Context con, String tag, EdmFont f) {
		super(con, tag);

		Element fontElement = propertyContext.getDocument().createElement("fontdata");
		propertyContext.getElement().appendChild(fontElement);
		
		String fontName;
		if(typeFaces.containsKey(f.getName())) {
			fontName = typeFaces.get(f.getName());
		} else {
			fontName = f.getName();
		}

		// Round fonts down to the nearest 0.5
		Double size;
		if(fontScales.containsKey(f.getName())) {
			size = Math.floor(f.getSize() * fontScales.get(f.getName()));
		} else {
			size = Math.floor(f.getSize() * fontScales.get("default"));
		}
		String height = new DecimalFormat("#.#").format(size);

		// Style conversion copied from org.eclipse.swt.SWT class.
		int s = NORMAL;
		if (f.isBold()) {
			s |= BOLD;
		}
		if (f.isItalic()) {
			s |= ITALIC;
		}
		String style = String.valueOf(s);

		fontElement.setAttribute("fontName", fontName);
		fontElement.setAttribute("height", height);
		fontElement.setAttribute("style", style);

		log.debug("Written font property with attributes: " + fontName + ", " + height + ", " + style);
	}

}
