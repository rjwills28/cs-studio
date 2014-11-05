package org.csstudio.opibuilder.converter.writer;

import org.csstudio.opibuilder.converter.model.Edm_ExtendedRelatedDisplayClass;

/**
 * This class is Diamond specific. In the DLS fork of EDM there is an
 * Extended Related Display Widget, however it seems to offer now
 * additional functionality. We use the existing related display conversion
 * to convert the 'extended' EDM class.
 */
public class Opi_ExtendedRelatedDisplayClass extends Opi_relatedDisplayClass {

	public Opi_ExtendedRelatedDisplayClass(Context con, Edm_ExtendedRelatedDisplayClass r) {
		super(con, r);
	}

}
