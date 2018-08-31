/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.ui.time;

import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import org.csstudio.apputil.time.RelativeTime;
import org.csstudio.apputil.time.StartEndTimeParser;
import org.csstudio.java.time.TimestampFormats;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/** Dialog for entering relative as well as absolute start and end times.
 *  @author Kay Kasemir
 */
public class StartEndDialog extends Dialog
{

    private String start_specification, end_specification;
    private StartEndTimeParser start_end;

    private final String start, end;

    private StartEndWidget startEndWidget;

    /** Create dialog with some default start and end time. */
    @SuppressWarnings("nls")
    public StartEndDialog(Shell shell)
    {
        this(shell, "-1" + RelativeTime.DAY_TOKEN, RelativeTime.NOW);
    }

    /** Create dialog with given start and end time specification. */
    public StartEndDialog(final Shell shell, final String st, final String dd)
    {
        super(shell);
        //start_specification = start;
        //end_specification = end;
        // Allow resize
        start = st;
        end = dd;
        setShellStyle(getShellStyle() | SWT.RESIZE);

        //startEndWidget = new StartEndWidget()
    }

    /** @return Start specification. */
    public String getStartSpecification()
    {   return startEndWidget.getStartSpecification();  }

    /** @return End specification. */
    public String getEndSpecification()
    {   return startEndWidget.getEndSpecification(); }

    /** @return Calendar for start time. */
    public final Calendar getStartCalendar()
    {   return startEndWidget.getStartCalendar();  }

    /** @return Calendar for end time. */
    public final Calendar getEndCalendar()
    {   return startEndWidget.getEndCalendar(); }

    /** @return <code>true</code> if end time is 'now' */
    public final boolean isEndNow()
    {   return startEndWidget.isEndNow(); }

    @Override
    protected void configureShell(Shell shell)
    {
        super.configureShell(shell);
        shell.setText(Messages.StartEnd_Title);
    }

    @Override
    protected Control createDialogArea(Composite parent)
    {

        final Composite area = (Composite) super.createDialogArea(parent);

        final GridLayout layout = new GridLayout(1, false);
        area.setLayout(layout);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false);
        gd.grabExcessVerticalSpace = true;
        area.setLayoutData(gd);

        startEndWidget = new StartEndWidget(area, SWT.NONE, start, end);

        return area;
    }

    /** If the dialog is closed via OK,
     *  update the start/end specs from the GUI.
     */
    @Override
    protected void okPressed()
    {
        start_specification = startEndWidget.getStartSpecification();
        end_specification = startEndWidget.getEndSpecification();
        // If the specifications don't parse, don't allow 'OK'
        try
        {
            start_end =
                new StartEndTimeParser(start_specification, end_specification);
            if (start_end.getStart().compareTo(start_end.getEnd()) >= 0)
            {
                return;
            }
        }
        catch (Exception ex)
        {
            return;
        }
        // Proceed...
        super.okPressed();
    }

    /** @see #setFromSpecifications */
    private void setFromSpecification(TopBottomTimeWidget timewidget, Label text, String specification)
        throws Exception
    {
        /*text.setText(specification);
        RelativeTimeParserResult result = RelativeTimeParser.parse(specification);
        if (result.isAbsolute())
        {
            timewidget.setAbsolute();
            timewidget.calendarTime.setCalendar(AbsoluteTimeParser.parse(specification));
        }
        else
        {
            timewidget.setRelative();
            timewidget.relativeTime.setRelativeTime(result.getRelativeTime());
        }
        */
    }

    /** Set GUI from start/end strings. */
    private void setFromSpecifications()
    {
        /*
        try
        {
            setFromSpecification(left, left.text_summary, start_specification);
        }
        catch (Exception ex)
        {
            info.setText(Messages.StartEnd_StartError);
        }
        try
        {
            setFromSpecification(right, right.text_summary, end_specification);
        }
        catch (Exception ex)
        {
            info.setText(Messages.StartEnd_EndError);
        }
        */
    }

    final static private DateTimeFormatter DATE_FORMAT =  TimestampFormats.SECONDS_FORMAT;

}
