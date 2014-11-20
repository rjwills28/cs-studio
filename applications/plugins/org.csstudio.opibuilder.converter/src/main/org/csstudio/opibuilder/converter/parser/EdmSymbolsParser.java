/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.csstudio.opibuilder.converter.model.EdmException;


/**
 * Parser class which parses data on EdmColors.
 * 
 * @author Matevz
 *
 */
public class EdmSymbolsParser extends EdmParser {

	static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.parser.EdmSymbolsParser");
	private Map<String, Integer> symbolsMap;

	/**
	 * Parses data in given file name into constructed EdmColorsListParser instance.
	 * @param fileName Edm colors list file.
	 * @throws EdmException if parsing error occurs.
	 */
	public EdmSymbolsParser(String fileName) throws EdmException {
		super(fileName);
		symbolsMap = new HashMap<String, Integer>();
	}

	public void parse() {
		String content = edmData.toString();
		Scanner s = new Scanner(content);
		try {
			while (s.hasNextLine()) {
				String line = s.nextLine();
				if (line.isEmpty()) {
					continue;
				}
				String[] parts = line.split(":");
				if (parts.length == 2) {
					String key = parts[0].trim();
					String value = parts[1].trim();
					symbolsMap.put(key, Integer.valueOf(value));
				}
			}
		} finally {
			s.close();
		}
	}

	public Map<String, Integer> getMap() {
		return symbolsMap;
	}

}
