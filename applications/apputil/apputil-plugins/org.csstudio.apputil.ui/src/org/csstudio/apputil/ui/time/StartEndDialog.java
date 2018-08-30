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

import org.csstudio.apputil.time.AbsoluteTimeParser;
import org.csstudio.apputil.time.RelativeTime;
import org.csstudio.apputil.time.RelativeTimeParser;
import org.csstudio.apputil.time.RelativeTimeParserResult;
import org.csstudio.apputil.time.StartEndTimeParser;
import org.csstudio.java.time.TimestampFormats;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

class TopBottomTimeWidget extends Composite {

    static final Color highlightColour = new Color(Display.getCurrent(), 255, 237, 196);
    static final Color defaultColour = Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);

    public CalendarWidget calendarTime;
    public RelativeTimeWidget relativeTime;

    private Composite topCalendarBox;
    private Composite bottomRelativeBox;

    public Label text_summary;

    public TopBottomTimeWidget(Composite parent, int style, boolean only_now) {
        super(parent, style);

        parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        final GridLayout leftLayout = new GridLayout();
        parent.setLayout(leftLayout);

        topCalendarBox = new Composite(parent, SWT.NULL);
        topCalendarBox.setBackgroundMode(SWT.INHERIT_FORCE);
        final FillLayout topLayout = new FillLayout();
        topLayout.type = SWT.VERTICAL;
        topCalendarBox.setLayout(topLayout);
        calendarTime = new CalendarWidget(topCalendarBox, SWT.NONE);

        bottomRelativeBox = new Composite(parent, SWT.NULL);
        bottomRelativeBox.setBackgroundMode(SWT.INHERIT_FORCE);
        final FillLayout bottomLayout = new FillLayout();
        bottomLayout.type = SWT.VERTICAL;
        bottomRelativeBox.setLayout(bottomLayout);
        relativeTime = new RelativeTimeWidget(bottomRelativeBox, SWT.NONE);

        relativeTime.setVisible(!only_now);

        CalendarHighlightListener calendarListener = new CalendarHighlightListener(topCalendarBox, bottomRelativeBox);

        Display.getCurrent().addFilter(SWT.MouseDown, new Listener() {
            @Override
            public void handleEvent(Event e) {
                if (CalendarHighlightListener.isChildOrSelf(e.widget, topCalendarBox)) {
                    setAbsolute();
                }
                if (CalendarHighlightListener.isChildOrSelf(e.widget, bottomRelativeBox)) {
                    setRelative();
                }
            }
        });

        calendarTime.addSelectionListener(calendarListener);

        text_summary = new Label(parent, SWT.NONE);
        text_summary.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    }

    public void setAbsolute() {
        topCalendarBox.setBackground(highlightColour);
        bottomRelativeBox.setBackground(defaultColour);
        calendarTime.externalUpdateDataFromGUI();
    }

    public void setRelative() {
        bottomRelativeBox.setBackground(highlightColour);
        topCalendarBox.setBackground(defaultColour);
        relativeTime.externalUpdateDataFromGUI();
    }

}


class CalendarHighlightListener implements SelectionListener
{

    private Composite selected;
    private Composite deselected;

    private static final Color highlightColour = new Color(Display.getCurrent(), 255, 237, 196);
    private static final Color defaultColour = Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);

    public CalendarHighlightListener(Composite s, Composite d) {
        super();
        selected = s;
        deselected = d;
    }

    @Override
    public void widgetSelected(SelectionEvent arg0) {
        selected.setBackground(highlightColour);
        deselected.setBackground(defaultColour);
   };

   static boolean isChildOrSelf(Widget child, Composite parent) {

    if (child == parent)
        return true;

    for (Control c : parent.getChildren()) {
        if (c instanceof Composite)
        {
            boolean result = isChildOrSelf(child, (Composite)c);
            if (result)
                return true;
        }
        else if (c == child)
            return false;
    }
    return false;

}

@Override
public void widgetDefaultSelected(SelectionEvent arg0) {
    // TODO Auto-generated method stub

};

}

/** Dialog for entering relative as well as absolute start and end times.
 *  @author Kay Kasemir
 */
public class StartEndDialog extends Dialog
    implements CalendarWidgetListener, RelativeTimeWidgetListener
{

    private static final Color highlightColour = new Color(Display.getCurrent(), 255, 237, 196);
    private static final Color defaultColour = Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);

    // GUI Elements

    //private Label start_text, end_text;
    private Label info;
    private TopBottomTimeWidget left, right;

    // Start and end specification strings
    private String start_specification, end_specification;
    private StartEndTimeParser start_end;

    /** Create dialog with some default start and end time. */
    @SuppressWarnings("nls")
    public StartEndDialog(Shell shell)
    {
        this(shell, "-1" + RelativeTime.DAY_TOKEN, RelativeTime.NOW);
    }

    /** Create dialog with given start and end time specification. */
    public StartEndDialog(final Shell shell, final String start, final String end)
    {
        super(shell);
        start_specification = start;
        end_specification = end;
        // Allow resize
        setShellStyle(getShellStyle() | SWT.RESIZE);
    }

    /** @return Start specification. */
    public String getStartSpecification()
    {   return start_specification;  }

    /** @return End specification. */
    public String getEndSpecification()
    {   return end_specification; }

    /** @return Calendar for start time. */
    public final Calendar getStartCalendar()
    {   return start_end.getStart();  }

    /** @return Calendar for end time. */
    public final Calendar getEndCalendar()
    {   return start_end.getEnd(); }

    /** @return <code>true</code> if end time is 'now' */
    public final boolean isEndNow()
    {   return start_end.isEndNow(); }

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

        final Composite box = new Composite(area, 0);
        box.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        final GridLayout layout = new GridLayout(2, true);
        box.setLayout(layout);
        GridData gd;

        final Composite leftBox = new Composite(box, 0);
        leftBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        left = new TopBottomTimeWidget(leftBox, 0, false);

        left.calendarTime.addListener(this);
        left.relativeTime.addListener(this);

        //leftTopBox.addSelectionListener(mouseTop);
//        date.addSelectionListener(mouseTop);
        //leftBottomBox.addMouseListener(mouseBottom);

        final Composite rightBox = new Composite(box, 0);
        rightBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        right = new TopBottomTimeWidget(rightBox, 0, true);
        right.calendarTime.addListener(this);
        right.relativeTime.addListener(this);
        //gd = new GridData();

        //gd.horizontalSpan = layout.numColumns/2;
       // gd.grabExcessHorizontalSpace = true;
       // gd.horizontalAlignment = SWT.FILL;
        //rightBox.setLayoutData(gd);
        //final Text text2 = new Text(rightBox, SWT.LEFT);
        //text2.setText("hello");

        // ---- Left -----
        /*
        left_tab = new TabFolder(box, SWT.BORDER);
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns/2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        left_tab.setLayoutData(gd);

        TabItem tab = new TabItem(left_tab, 0);
        tab.setText(Messages.StartEnd_AbsStart);
        tab.setToolTipText(Messages.StartEnd_AbsStart_TT);
        abs_start = new CalendarWidget(left_tab, 0);
        abs_start.addListener(this);
        tab.setControl(abs_start);

        tab = new TabItem(left_tab, 0);
        tab.setText(Messages.StartEnd_RelStart);
        tab.setToolTipText(Messages.StartEnd_RelStart_TT);
        rel_start = new RelativeTimeWidget(left_tab, 0);
        rel_start.addListener(this);
        tab.setControl(rel_start);

        right_tab = new TabFolder(box, SWT.BORDER);
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns/2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        right_tab.setLayoutData(gd);

        tab = new TabItem(right_tab, 0);
        tab.setText(Messages.StartEnd_AbsEnd);
        tab.setToolTipText(Messages.StartEnd_AbsEnd_TT);
        abs_end = new CalendarWidget(right_tab, 0);
        abs_end.addListener(this);
        tab.setControl(abs_end);

        tab = new TabItem(right_tab, 0);
        tab.setText(Messages.StartEnd_RelEnd);
        tab.setToolTipText(Messages.StartEnd_RelEnd_TT);
        rel_end = new RelativeTimeWidget(right_tab, 0);
        rel_end.addListener(this);
        tab.setControl(rel_end);
        */

        // New Row
        /*
        Label l = new Label(box, SWT.NULL);
        l.setText(Messages.StartEnd_StartTime);
        gd = new GridData();
        l.setLayoutData(gd);

        start_text = new Label(box, SWT.LEFT);
        start_text.setToolTipText(Messages.StartEnd_StartTime_TT);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        start_text.setLayoutData(gd);

        l = new Label(box, SWT.NULL);
        l.setText(Messages.StartEnd_EndTime);
        gd = new GridData();
        l.setLayoutData(gd);

        end_text = new Label(box, SWT.LEFT);
        end_text.setToolTipText(Messages.StartEnd_EndTime_TT);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        end_text.setLayoutData(gd);

        // New Row
        info = new Label(box, SWT.NULL);
        info.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        info.setLayoutData(gd);
        */

        // Initialize GUI content
        setFromSpecifications();

        return area;
    }

    /** If the dialog is closed via OK,
     *  update the start/end specs from the GUI.
     */
    @Override
    protected void okPressed()
    {
        start_specification = left.text_summary.getText();
        end_specification = right.text_summary.getText();
        // If the specifications don't parse, don't allow 'OK'
        try
        {
            start_end =
                new StartEndTimeParser(start_specification, end_specification);
            if (start_end.getStart().compareTo(start_end.getEnd()) >= 0)
            {
                info.setText(Messages.StartEnd_StartExceedsEnd);
                return;
            }
        }
        catch (Exception ex)
        {
            info.setText(Messages.StartEnd_Error + ex.getMessage());
            return;
        }
        // Proceed...
        super.okPressed();
    }

    /** @see #setFromSpecifications */
    private void setFromSpecification(TopBottomTimeWidget timewidget, Label text, String specification)
        throws Exception
    {
        text.setText(specification);
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
    }

    /** Set GUI from start/end strings. */
    private void setFromSpecifications()
    {
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
    }

    final static private DateTimeFormatter DATE_FORMAT =  TimestampFormats.SECONDS_FORMAT;

    // CalendarWidgetWidgetListener
    @Override
    public void updatedCalendar(CalendarWidget source, Calendar calendar)
    {
        if (source == left.calendarTime)
            left.text_summary.setText(DATE_FORMAT.format(calendar.toInstant()));
        else
            right.text_summary.setText(DATE_FORMAT.format(calendar.toInstant()));
            /*
        if (start.isGreaterOrEqual(end))
            info.setText(Messages.StartExceedsEnd);
        else
            info.setText(""); //$NON-NLS-1$
            */
    }

    // RelativeTimeWidgetListener
    @Override
    public void updatedTime(RelativeTimeWidget source, RelativeTime time)
    {
        if (source == left.relativeTime)
            left.text_summary.setText(time.toString());
        else
            right.text_summary.setText(time.toString());
    }
}
