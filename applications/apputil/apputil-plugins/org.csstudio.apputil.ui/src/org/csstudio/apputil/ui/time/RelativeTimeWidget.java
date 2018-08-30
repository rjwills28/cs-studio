/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.ui.time;

import java.util.ArrayList;

import org.csstudio.apputil.time.RelativeTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


/** Widget for displaying and selecting a relative date and time.
 *  <p>
 *  @author Helge Rickens
 *  @author Kay Kasemir
 */
public class RelativeTimeWidget extends Composite
{
    /** Widgets for date pieces. */
    private Text year, month, day;
    /** Widgets for time pieces. */
    private Text hour, minute, second;

    /** The relative time pieces for year, month, day, hour, minute, second. */
    private RelativeTime relative_time;

    /** Used to prevent recursion when the widget updates the GUI,
     *  which in turn fires listener notifications...
     */
    private boolean in_GUI_update = false;

    final private ArrayList<RelativeTimeWidgetListener> listeners
       = new ArrayList<RelativeTimeWidgetListener>();

    /** Construct widget, initialized to zero offsets.
     *  @param parent Widget parent.
     *  @param flags SWT widget flags.
     */
    public RelativeTimeWidget(Composite parent, int flags)
    {
        this(parent, flags, new RelativeTime());
    }

    void prepareTimeDigitInput(Text input, String tooltiptext) {

            GridData fd = new GridData();
            fd.widthHint = 30;
            input.setLayoutData(fd);

            input.setToolTipText(tooltiptext);

            input.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent arg0) {
                }
                @Override
                public void focusLost(FocusEvent e) {
                    String newText = ((Text)e.widget).getText();
                    try {
                        Integer.valueOf(newText);
                    }
                    catch (NumberFormatException ex) {
                        if (!newText.equals("")) {
                            ((Text)e.widget).setText("0");
                        }
                    }
                }
            });

        }

    /** Construct widget, initialized to given time.
     *  @param parent Widget parent.
     *  @param flags SWT widget flags.
     */
    public RelativeTimeWidget(Composite parent, int flags, RelativeTime relative_time)
    {
        super(parent, flags);
        final GridLayout layout = new GridLayout(4,true);
        setLayout(layout);
        GridData gd;

        // Years: (year)+-  Hours: (hour)+-
        // Month: (month)+- Minutes: (minute)+-
        // Days: (day)+-    Secs: (second)+-

        // New row (Years / Hours)
        Label l = new Label(this, SWT.NONE);
        l.setText(Messages.Time_Years);
        gd = new GridData(SWT.LEFT, SWT.CENTER, true, true);
        l.setLayoutData(gd);
        year = new Text(this, SWT.BORDER | SWT.WRAP);
        prepareTimeDigitInput(year, Messages.Time_SelectYear);
        l = new Label(this, SWT.NONE);
        l.setText(Messages.Time_Hours);
        l.setLayoutData(gd);
        hour = new Text(this, SWT.BORDER | SWT.WRAP);
        prepareTimeDigitInput(hour, Messages.Time_SelectHour);

        // New row (Month / Minutes)
        l = new Label(this, SWT.NONE);
        l.setText(Messages.Time_Months);
        month = new Text(this, SWT.BORDER | SWT.WRAP);
        prepareTimeDigitInput(month, Messages.Time_SelectMonth);
        l = new Label(this, SWT.NONE);
        l.setText(Messages.Time_Minutes);
        gd = new GridData();
        l.setLayoutData(gd);
        minute = new Text(this, SWT.BORDER | SWT.WRAP);
        prepareTimeDigitInput(minute, Messages.Time_SelectMinute);

        // New row (Days / Secs)
        l = new Label(this, SWT.NONE);
        l.setText(Messages.Time_Days);
        gd = new GridData();
        l.setLayoutData(gd);
        day = new Text(this, SWT.BORDER | SWT.WRAP);
        prepareTimeDigitInput(day, Messages.Time_SelectDay);
        l = new Label(this, SWT.NONE);
        second = new Text(this, SWT.BORDER | SWT.WRAP);
        prepareTimeDigitInput(second, Messages.Time_SelectSeconds);


        // Initialize to given relative time pieces
        setRelativeTime(relative_time);


        final FocusAdapter update = new FocusAdapter()
        {
            @Override
            public void focusLost(FocusEvent e)
            {
                if (!in_GUI_update)
                    updateDataFromGUI();
            }
        };

        year.addFocusListener(update);
        month.addFocusListener(update);
        day.addFocusListener(update);
        hour.addFocusListener(update);
        minute.addFocusListener(update);
        second.addFocusListener(update);

    }

    /** Add given listener. */
    public void addListener(RelativeTimeWidgetListener listener)
    {
        if (!listeners.contains(listener))
            listeners.add(listener);
    }

    /** Remove given listener. */
    public void removeListener(RelativeTimeWidgetListener listener)
    {
        listeners.remove(listener);
    }

    /** Set the widget to display the given time.
     *  @see #setNow()
     */
    public void setRelativeTime(RelativeTime relative_time)
    {
        this.relative_time = relative_time;
        updateGUIfromData();
    }

    /** @return Returns the currently selected time. */
    public RelativeTime getRelativeTime()
    {
        return (RelativeTime) relative_time.clone();
    }

    public void externalUpdateDataFromGUI() {
        updateDataFromGUI();
    }

    /** Update the data from the interactive GUI elements. */
    private void updateDataFromGUI()
    {
        //final int sign = before.getSelection() ? -1 : 1;
        final int ymdhms[] = new int[]
        {
            Integer.parseInt(year.getText()),
            Integer.parseInt(month.getText()),
            Integer.parseInt(day.getText()),
            Integer.parseInt(hour.getText()),
            Integer.parseInt(minute.getText()),
            Integer.parseInt(second.getText())
        };
        relative_time = new RelativeTime(ymdhms);
        updateGUIfromData();
    }

    /** Display the current value of the data on the GUI. */
    private void updateGUIfromData()
    {
        in_GUI_update = true;
        final int vals[] = new int[]
        {
            relative_time.get(RelativeTime.YEARS),
            relative_time.get(RelativeTime.MONTHS),
            relative_time.get(RelativeTime.DAYS),
            relative_time.get(RelativeTime.HOURS),
            relative_time.get(RelativeTime.MINUTES),
            relative_time.get(RelativeTime.SECONDS)
        };
        // In principle, the signs could differ "-1years +5days",
        // but in reality that's most often a typo.
        // So check if anything's negative or all is null.
        boolean anything_negative = false;
        boolean all_null = true;
        for (int i=0; i<vals.length; ++i)
        {
            if (vals[i] > 0)
                all_null = false;
            if (vals[i] < 0)
            {
                anything_negative = true;
                all_null = false;
            }
        }
        //final boolean negative = all_null  ||  anything_negative;
        // Apply sign to all
        //before.setSelection(negative);
        //for (int i=0; i<vals.length; ++i)
        //    if (vals[i] < 0)
        //        vals[i] = -vals[i];
        year.setText(String.valueOf(vals[0]));
        month.setText(String.valueOf(vals[1]));
        day.setText(String.valueOf(vals[2]));
        hour.setText(String.valueOf(vals[3]));
        minute.setText(String.valueOf(vals[4]));
        second.setText(String.valueOf(vals[5]));
        in_GUI_update = false;
        // fireUpdatedTimestamp
        for (RelativeTimeWidgetListener l : listeners)
            l.updatedTime(this, relative_time);
    }

}
