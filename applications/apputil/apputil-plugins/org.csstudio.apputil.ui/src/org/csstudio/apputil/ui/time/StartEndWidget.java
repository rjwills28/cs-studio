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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
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
import org.eclipse.swt.widgets.Widget;

class TopBottomTimeWidget extends Composite {

    private Listener listener;

    static final Color highlightColour = new Color(Display.getCurrent(), 255, 237, 196);
    static final Color defaultColour = Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);

    public CalendarWidget calendarTime;
    public RelativeTimeWidget relativeTime;

    private Composite topCalendarBox;
    private Composite bottomRelativeBox;

    public Label text_summary;

    public Listener getListener() { return listener; }

    public TopBottomTimeWidget(Composite parent, int style, boolean only_now) {
        super(parent, style);

        setLayout(new GridLayout(1, false));

        topCalendarBox = new Composite(this, SWT.WRAP);
        topCalendarBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        topCalendarBox.setBackgroundMode(SWT.INHERIT_FORCE);
        final FillLayout topLayout = new FillLayout();
        topLayout.type = SWT.VERTICAL;
        topCalendarBox.setLayout(topLayout);
        calendarTime = new CalendarWidget(topCalendarBox, SWT.NONE);

        bottomRelativeBox = new Composite(this, SWT.WRAP);
        bottomRelativeBox.setBackgroundMode(SWT.INHERIT_FORCE);
        bottomRelativeBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        final FillLayout bottomLayout = new FillLayout();
        bottomLayout.type = SWT.VERTICAL;
        bottomRelativeBox.setLayout(bottomLayout);
        relativeTime = new RelativeTimeWidget(bottomRelativeBox, SWT.NONE);

        relativeTime.setVisible(!only_now);

        CalendarHighlightListener calendarListener = new CalendarHighlightListener(topCalendarBox, bottomRelativeBox);

        listener = new Listener() {
            @Override
            public void handleEvent(Event e) {
                if (CalendarHighlightListener.isChildOrSelf(e.widget, topCalendarBox)) {
                    setAbsolute();
                }
                if (CalendarHighlightListener.isChildOrSelf(e.widget, bottomRelativeBox)) {
                    setRelative();
                }
            }
        };

        Display.getCurrent().addFilter(SWT.MouseDown, listener);

        calendarTime.addSelectionListener(calendarListener);

        text_summary = new Label(this, SWT.NONE);
        text_summary.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        this.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent arg0) {
                Display.getCurrent().removeFilter(SWT.MouseDown, listener);
            }

        });

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
public class StartEndWidget extends Composite
    implements CalendarWidgetListener, RelativeTimeWidgetListener
{

    public Listener getListener() { return left.getListener(); }

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
    public StartEndWidget(Composite parent, int flags)
    {
        this(parent, flags, "-1" + RelativeTime.DAY_TOKEN, RelativeTime.NOW);
    }

    /** Create dialog with given start and end time specification. */
    public StartEndWidget(Composite parent, int flags, final String start, final String end)
    {
        super(parent, flags);
        start_specification = start;
        end_specification = end;
        createComposite();
        // Allow resize
        //setShellStyle(getShellStyle() | SWT.RESIZE);
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

    //@Override
    //protected void configureShell(Shell shell)
    //{
    //    super.configureShell(shell);
    //    shell.setText(Messages.StartEnd_Title);
   // }

    //@Override
    private void createComposite()
    //protected Control createDialogArea(Composite parent)
    {
        //final Composite area = new Composite(parent, SWT.NONE);//(Composite) super.createDialogArea(parent);

        final GridLayout layout = new GridLayout(2, true);
        this.setLayout(layout);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        this.setLayoutData(gd);

        final Composite leftBox = new Composite(this, 0);
        leftBox.setLayout(new GridLayout(1, false));
        leftBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        left = new TopBottomTimeWidget(leftBox, 0, false);
        left.setLayout(new GridLayout(1, false));
        left.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        left.calendarTime.addListener(this);
        left.relativeTime.addListener(this);

        final Composite rightBox = new Composite(this, 0);
        rightBox.setLayout(new GridLayout(1, false));
        rightBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        right = new TopBottomTimeWidget(rightBox, 0, true);
        right.setLayout(new GridLayout(1, false));
        right.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        right.calendarTime.addListener(this);
        right.relativeTime.addListener(this);

        // New Row
        info = new Label(this, SWT.NULL);
        info.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
        gd = new GridData();
        //gd.horizontalSpan = layout.numColumns;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        info.setLayoutData(gd);

        // Initialize GUI content
        setFromSpecifications();

        return;
    }

    /** If the dialog is closed via OK,
     *  update the start/end specs from the GUI.
     */
   @Override
public void update()
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
                info.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
                info.setText(Messages.StartEnd_StartExceedsEnd);
                return;
            }
        }
        catch (Exception ex)
        {
            info.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
            info.setText(Messages.StartEnd_Error + ex.getMessage());
            return;
        }
        // Proceed...
        info.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
        info.setText("Times accepted: Start: " + start_specification + " End: " + end_specification);
        //super.okPressed();
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
        update();
    }

    // RelativeTimeWidgetListener
    @Override
    public void updatedTime(RelativeTimeWidget source, RelativeTime time)
    {
        if (source == left.relativeTime)
            left.text_summary.setText(time.toString());
        else
            right.text_summary.setText(time.toString());
        update();
    }

    public void addSelectionListener(SelectionAdapter times_entered) {
        left.calendarTime.addSelectionListener(times_entered);
        right.calendarTime.addSelectionListener(times_entered);
        left.relativeTime.addSelectionListener(times_entered);
        right.relativeTime.addSelectionListener(times_entered);
    }

    public void setStartSpecification(String startSpecification) {
        try {
            setFromSpecification(left, left.text_summary, startSpecification);
        } catch (Exception e) {
        }
    }

    public void setEndSpecification(String endSpecification) {
        try {
            setFromSpecification(right, right.text_summary, endSpecification);
        } catch (Exception e) {
        }
    }

    public void addModifyListener(ModifyListener times_entered_modify) {
        left.relativeTime.addModifyListener(times_entered_modify);
        right.relativeTime.addModifyListener(times_entered_modify);
    }
}
