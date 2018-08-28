/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.propsheet;

import org.eclipse.jface.action.Action;

/** Action to add new value axis to model
 *  @author Kay Kasemir
 */
public class LabelAction extends Action
{

    public LabelAction(String label)
    {
        super(label);
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void run()
    {
    }

}
