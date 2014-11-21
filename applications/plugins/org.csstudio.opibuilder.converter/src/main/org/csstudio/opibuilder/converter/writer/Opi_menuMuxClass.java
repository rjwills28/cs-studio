package org.csstudio.opibuilder.converter.writer;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.csstudio.opibuilder.converter.model.EdmMultiStrings;
import org.csstudio.opibuilder.converter.model.Edm_menuMuxClass;
import org.w3c.dom.Element;


public class Opi_menuMuxClass extends OpiWidget {

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.writer.Opi_menuMuxClass");
	private static final String typeId = "edm.menumux";
	private static final String name = "EDM MenuMux";
	private static final String version = "1.0";


	/**
	 * Converts the Edm_menuMuxClass to OPI combobox widget XML.
	 */
	public Opi_menuMuxClass(Context con, Edm_menuMuxClass r) {
		super(con, r);

		setTypeId(typeId);
		setName(name);
		setVersion(version);

		Element widget = widgetContext.getElement();

		if (r.getAttribute("initialState").isExistInEDL()) {
			new OpiString(widgetContext, "initial", r.getInitialState());
		}

		Element itemsElement = con.getDocument().createElement("items");
		widget.appendChild(itemsElement);

		EdmMultiStrings edmSymbols = r.getSymbolTags();

		for (int index = 0; index < r.getNumItems(); index++)
		{
			String symbol = edmSymbols.getValue(index).split("\"")[1];
			Element itemElement = con.getDocument().createElement("s");
			itemElement.appendChild(con.getDocument().createTextNode(symbol));
			itemsElement.appendChild(itemElement);
		}

		int num_sets = 0;
		for (int set_index = 0; set_index < Edm_menuMuxClass.MAX_SETS; set_index++) {
			// Compress the list of symbols to a single value
			// If multiple exist abort and log an error

			if (r.getAttribute("symbol" + set_index).isExistInEDL()) {
				Set<String> symbols = new HashSet<String>();
				EdmMultiStrings edmVals = r.getSymbols(set_index);
				for (int index = 0; index < r.getNumItems(); index++)
				{
					String symbol = edmVals.getValue(index).split("\"")[1];
					symbols.add(symbol);
				}
				if (symbols.size() == 1) {
					new OpiString(widgetContext, "target" + set_index, symbols.iterator().next());
					// increment the number-of-sets counter
					num_sets++;
				}
				else {
					System.err.println("Multiple EDM MenuMux symbols defined, cannot convert: 'symbol" + set_index + "'");
					break;
				}
			}

			if (r.getAttribute("value" + set_index).isExistInEDL()) {
				Element valuesElement = con.getDocument().createElement("values" + set_index);
				widget.appendChild(valuesElement);
				EdmMultiStrings edmZero = r.getValues(set_index);
				for (int index = 0; index < r.getNumItems(); index++)
				{
					String symbol = edmZero.getValue(index).split("\"")[1];
					Element valueElement = con.getDocument().createElement("s");
					valueElement.appendChild(con.getDocument().createTextNode(symbol));
					valuesElement.appendChild(valueElement);
				}
			}
		}

		Element setsElement = con.getDocument().createElement("num_sets");
		setsElement.appendChild(con.getDocument().createTextNode(Integer.toString(num_sets)));
		widget.appendChild(setsElement);

		log.debug("Edm_menuMuxClass written");
	}
}
