/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.vtype;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.diirt.util.text.NumberFormats;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.Display;
import org.diirt.vtype.VType;
import org.diirt.vtype.ValueFactory;
import org.junit.Test;

/** JUnit test of {@link VType}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class VTypeHelperTest
{
    @Test
    public void testFormat() throws Exception
    {
        final Date now = new Date();
        final Display display = ValueFactory.newDisplay(0.0, 1.0, 2.0, "a.u.", NumberFormats.format(3), 8.0, 9.0, 10.0, 0.0, 10.0);
        final VType num = new ArchiveVNumber(now.toInstant(), AlarmSeverity.MINOR, "Troubling", display, 3.14);

        String text = VTypeHelper.toString(num);
        System.out.println(text);
        assertThat(now, equalTo(Date.from(VTypeHelper.getTimestamp(num))));
        assertThat(text, containsString("3.14"));
        assertThat(text, containsString("a.u."));
        assertThat(text, containsString("MINOR"));
        assertThat(text, containsString("Troubling"));

        final List<String> labels = Arrays.asList("zero", "one", "two", "three");
        final VType enumerated = new ArchiveVEnum(now.toInstant(), AlarmSeverity.MINOR, "Troubling", labels, 3);
        text = VTypeHelper.toString(enumerated);
        System.out.println(text);
        assertThat(text, containsString("three"));
        assertThat(text, containsString("(3)"));
    }
}
