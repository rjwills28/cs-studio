package org.csstudio.opibuilder.converter.model;

/**
 * This class is Diamond specific. In the DLS fork of EDM there is an
 * Extended Related Display Widget, however it seems to offer now
 * additional functionality. We use the existing related display conversion
 * to convert the 'extended' EDM class.
 */
public class Edm_ExtendedRelatedDisplayClass extends Edm_relatedDisplayClass {

    public Edm_ExtendedRelatedDisplayClass(EdmEntity genericEntity)
            throws EdmException {
        super(genericEntity);
    }

}
