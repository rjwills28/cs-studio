package org.csstudio.opibuilder.converter.model;

/**EDM Menu Mux widget
 * @author Xihui Chen
 *
 */
public class Edm_menuMuxClass extends EdmWidget {

	public static final int MAX_SETS = 8;
	
	@EdmAttributeAn private int numItems;
	@EdmAttributeAn @EdmOptionalAn private EdmMultiStrings symbolTag;
	@EdmAttributeAn @EdmOptionalAn private EdmMultiStrings symbol0;
	@EdmAttributeAn @EdmOptionalAn private EdmMultiStrings value0;
	@EdmAttributeAn @EdmOptionalAn private EdmMultiStrings symbol1;
	@EdmAttributeAn @EdmOptionalAn private EdmMultiStrings value1;
	@EdmAttributeAn @EdmOptionalAn private EdmMultiStrings symbol2;
	@EdmAttributeAn @EdmOptionalAn private EdmMultiStrings value2;
	@EdmAttributeAn @EdmOptionalAn private EdmMultiStrings symbol3;
	@EdmAttributeAn @EdmOptionalAn private EdmMultiStrings value3;
	@EdmAttributeAn @EdmOptionalAn private EdmMultiStrings symbol4;
	@EdmAttributeAn @EdmOptionalAn private EdmMultiStrings value4;
	@EdmAttributeAn @EdmOptionalAn private EdmMultiStrings symbol5;
	@EdmAttributeAn @EdmOptionalAn private EdmMultiStrings value5;
	@EdmAttributeAn @EdmOptionalAn private EdmMultiStrings symbol6;
	@EdmAttributeAn @EdmOptionalAn private EdmMultiStrings value6;
	@EdmAttributeAn @EdmOptionalAn private EdmMultiStrings symbol7;
	@EdmAttributeAn @EdmOptionalAn private EdmMultiStrings value7;
	@EdmAttributeAn @EdmOptionalAn private EdmString initialState;

	public Edm_menuMuxClass(EdmEntity genericEntity) throws EdmException {
		super(genericEntity);
	}

	public int getNumItems() {
		return numItems;
	}

	public EdmString getInitialState() {
		return initialState;
	}

	public EdmMultiStrings getSymbolTags() {
		return symbolTag;
	}

	public EdmMultiStrings getSymbols(int idx) {
		switch (idx) {
			case 0:
				return symbol0;
			case 1:
				return symbol1;
			case 2:
				return symbol2;
			case 3:
				return symbol3;
			case 4:
				return symbol4;
			case 5:
				return symbol5;
			case 6:
				return symbol6;
			case 7:
				return symbol7;
			default:
				throw new IllegalArgumentException("Invalid symbol index: " + idx);
		}
	}

	public EdmMultiStrings getValues(int idx) {
		switch (idx) {
			case 0:
				return value0;
			case 1:
				return value1;
			case 2:
				return value2;
			case 3:
				return value3;
			case 4:
				return value4;
			case 5:
				return value5;
			case 6:
				return value6;
			case 7:
				return value7;
			default:
				throw new IllegalArgumentException("Invalid value index: " + idx);
		}
	}
}
