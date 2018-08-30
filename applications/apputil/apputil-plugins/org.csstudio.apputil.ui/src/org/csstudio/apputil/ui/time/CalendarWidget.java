/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.ui.time;

import java.util.ArrayList;
import java.util.Calendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;

/** Widget for displaying and selecting an absolute date and time.
 *  @author Kay Kasemir
 */
public class CalendarWidget extends Composite
{

    /** Widget for date. */
    private DateTime date;

    /** Widget for time. */
    private DateTime time;

    /** The currently configed calendar (date and time). */
    private Calendar calendar;

    /** Used to prevent recursion when the widget updates the GUI,
     *  which in turn fires listener notifications...
     */
    private boolean in_GUI_update = false;

    private ArrayList<CalendarWidgetListener> listeners
       = new ArrayList<CalendarWidgetListener>();

    /** Construct widget, initialized to the 'current' time.
     *  @param parent Widget parent.
     *  @param flags SWT widget flags.
     */
    public CalendarWidget(Composite parent, int flags)
    {
        this(parent, flags, Calendar.getInstance());
    }

    /** Construct widget, initialized to given time.
     *  @param parent Widget parent.
     *  @param flags SWT widget flags.
     */
    public CalendarWidget(final Composite parent, int flags, Calendar calendar)
    {
        super(parent, flags);
        GridLayout layout = new GridLayout();
        setLayout(layout);
        GridData gd;

        // |               |
        // |    Calendar   |
        // |               |
        // Time: ---time---
        // [Today] [Midnight] [Noon]

        Composite box = new Composite(this, 0);
        box.setLayout(new RowLayout());
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.LEFT;
        gd.grabExcessVerticalSpace = true;
        gd.verticalAlignment = SWT.CENTER;
        box.setLayoutData(gd);

        CLabel l = new CLabel(box, SWT.CENTER);
        l.setText("Date:");
        date = new DateTime(box, SWT.DROP_DOWN | SWT.DATE | SWT.LONG);
        date.setToolTipText(Messages.Time_SelectDate);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.LEFT;
        gd.grabExcessVerticalSpace = true;
        gd.verticalAlignment = SWT.FILL;
        box.setLayoutData(gd);

        // New row
        box = new Composite(this, 0);
        box.setLayout(new RowLayout());
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.LEFT;
        box.setLayoutData(gd);

        l = new CLabel(box, SWT.CENTER);
        l.setText(Messages.Time_Time);
        // SWT.MEDIUM to include seconds as needed by BOY
        time = new DateTime(box, SWT.TIME | SWT.MEDIUM);
        gd = new GridData();

        // New row
        box = new Composite(this, 0);
        box.setLayout(new RowLayout());
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.CENTER;
        gd.verticalAlignment = SWT.CENTER;
        box.setLayoutData(gd);

        // Initialize to 'now'
        setCalendar(calendar);

        final SelectionAdapter update = new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                if (!in_GUI_update)
                    updateDataFromGUI();
            }
        };
        date.addSelectionListener(update);
        time.addSelectionListener(update);
    }

    /** Add given listener. */
    public void addListener(CalendarWidgetListener listener)
    {
        if (!listeners.contains(listener))
            listeners.add(listener);
    }

    /** Remove given listener. */
    public void removeListener(CalendarWidgetListener listener)
    {
        listeners.remove(listener);
    }

    /** Set the widget to display the given time.
     *  @see #setNow()
     */
    public void setCalendar(final Calendar calendar)
    {
        this.calendar = calendar;
        this.calendar.set(Calendar.SECOND, 0);
        this.calendar.set(Calendar.MILLISECOND, 0);
        updateGUIfromData();
    }

    /** @return Returns the currently selected time. */
    public Calendar getCalendar()
    {
        return (Calendar) calendar.clone();
    }

    public void externalUpdateDataFromGUI() {
        updateDataFromGUI();
    }

    /** Update the data from the interactive GUI elements. */
    private void updateDataFromGUI()
    {
        calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, date.getYear());
        calendar.set(Calendar.MONTH, date.getMonth());
        calendar.set(Calendar.DAY_OF_MONTH, date.getDay());
        calendar.set(Calendar.HOUR_OF_DAY, time.getHours());
        calendar.set(Calendar.MINUTE, time.getMinutes());
        calendar.set(Calendar.SECOND, time.getSeconds());
        calendar.set(Calendar.MILLISECOND, 0);
        updateGUIfromData();
    }

    /** Display the current value of the data on the GUI. */
    private void updateGUIfromData()
    {
        in_GUI_update = true;
        date.setYear(calendar.get(Calendar.YEAR));
        date.setMonth(calendar.get(Calendar.MONTH));
        date.setDay(calendar.get(Calendar.DAY_OF_MONTH));
        time.setHours(calendar.get(Calendar.HOUR_OF_DAY));
        time.setMinutes(calendar.get(Calendar.MINUTE));
        time.setSeconds(calendar.get(Calendar.SECOND));
        in_GUI_update = false;

        // fireUpdatedTimestamp
        for (CalendarWidgetListener l : listeners)
            l.updatedCalendar(this, calendar);
    }

    public void addSelectionListener(SelectionListener mouseTop) {
        date.addSelectionListener(mouseTop);
    }
}
