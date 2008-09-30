package org.csstudio.diag.probe;

import java.text.NumberFormat;

import org.csstudio.platform.data.IMetaData;
import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.model.CentralItemFactory;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableDragSource;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableDropTarget;
import org.csstudio.util.swt.ComboHistoryHelper;
import org.csstudio.util.swt.meter.MeterWidget;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.csstudio.utility.pv.PVListener;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.CommandEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.ICommandListener;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.Parameterization;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

/**
 * Main Eclipse ViewPart of the Probe plug-in.
 *
 * @author Original by Ken Evans (APS)
 * @author Kay Kasemir
 * @author Jan Hatje
 * @author Helge Rickens
 * @author Joerg Rathlev
 */
public class Probe extends ViewPart implements PVListener
{
    /** Multiple Probe views are allowed.
     *  Their ID has to be ID + ":<instance>"
     */
    final public static String ID = "org.csstudio.diag.probe.Probe"; //$NON-NLS-1$

    /** Memento tag */
    final private static String PV_LIST_TAG = "pv_list"; //$NON-NLS-1$
    /** Memento tag */
    final private static String PV_TAG = "PVName"; //$NON-NLS-1$
    /** Memento tag */
    final private static String METER_TAG = "meter"; //$NON-NLS-1$
    
	/**
	 * Id of the save value command.
	 */
	private static final String SAVE_VALUE_COMMAND_ID =
		"org.csstudio.platform.ui.commands.saveValue"; //$NON-NLS-1$
	/**
	 * Id of the PV parameter to the save value command.
	 */
	private static final String PV_PARAMETER_ID =
		"org.csstudio.platform.ui.commands.saveValue.pv"; //$NON-NLS-1$
	/**
	 * Id of the value parameter to the save value command.
	 */
	private static final String VALUE_PARAMETER_ID =
		"org.csstudio.platform.ui.commands.saveValue.value"; //$NON-NLS-1$

	/** Instance number, used to create a unique ID
     *  @see #createNewInstance()
     */
    private static int instance = 0;
    
    /** Memento used to preserve the PV name. */
    private IMemento memento = null;

    // GUI
    private ComboViewer cbo_name;
    private ComboHistoryHelper name_helper;
    private Label lbl_value;
    private Label lbl_time;
    private Label lbl_status;
    private MeterWidget meter;

    /** The process variable that we monitor. */
    private PV pv = null;
    
    /** Most recent value of the pv */
    private ValueInfo value = new ValueInfo();
    
    private NumberFormat period_format;
    
    /** Is this a new channel where we never received a value? */
    private boolean new_channel = true;
    
    final Runnable update_value = new Runnable()
    {
        public void run()
        {   // Might run after the view is already disposed...
            if (lbl_value.isDisposed())
                return;
            lbl_value.setText(value.getValueDisplayText());
            lbl_time.setText(value.getTimeText());

            INumericMetaData meta = value.getNumericMetaData();
            if (meta == null)
                meter.setEnabled(false);
            else
            {   // Configure on first value from new channel
                if (new_channel)
                {
                    if (meta.getDisplayLow() < meta.getDisplayHigh())
                    {
                        meter.configure(meta.getDisplayLow(),
                                        meta.getAlarmLow(),
                                        meta.getWarnLow(),
                                        meta.getWarnHigh(),
                                        meta.getAlarmHigh(),
                                        meta.getDisplayHigh(),
                                        meta.getPrecision());
                        meter.setEnabled(true);
                    }
                    else
                        meter.setEnabled(false);
                }
                meter.setValue(value.getDouble());
            }
            Plugin.getLogger().debug("Probe displays " //$NON-NLS-1$
                                + lbl_time.getText()
                                + " " + lbl_value.getText()); //$NON-NLS-1$

            final double period = value.getUpdatePeriod();
            if (period > 0)
                lbl_status.setText(Messages.S_Period
                            + period_format.format(period)
                            + Messages.S_Seconds);
            else
                lbl_status.setText(Messages.S_OK);
            new_channel = false;
        }
    };
    private Composite top_box;
    private Composite bottom_box;
    private Button show_meter;
    private Button btn_save_to_ioc;
    private ICommandListener saveToIocCmdListener;


    /** Create or re-display a probe view with the given PV name.
     *  <p>
     *  Invoked by the PVpopupAction.
     *
     *  @param pv_name The PV to 'probe'
     *  @return Returns <code>true</code> when successful.
     */
    public static boolean activateWithPV(IProcessVariable pv_name)
    {
        try
        {
            IWorkbench workbench = PlatformUI.getWorkbench();
            IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
            IWorkbenchPage page = window.getActivePage();
            Probe probe = (Probe) page.showView(ID, createNewInstance(),
                                                IWorkbenchPage.VIEW_ACTIVATE);
            probe.setPVName(pv_name.getName());
            return true;
        }
        catch (Exception e)
        {
            Plugin.getLogger().error("activateWithPV", e); //$NON-NLS-1$
            e.printStackTrace();
        }
        return false;
    }
    
    /** @return a new view instance */
    public static String createNewInstance()
    {
        ++instance;
        return Integer.toString(instance);
    }

    public Probe()
    {
        period_format = NumberFormat.getNumberInstance();
        period_format.setMinimumFractionDigits(2);
        period_format.setMaximumFractionDigits(2);
    }

    /** ViewPart interface, keep the memento. */
    @Override
    public void init(IViewSite site, IMemento memento) throws PartInitException
    {
        super.init(site, memento);
        this.memento = memento;
    }

    /** ViewPart interface, persist state */
    @Override
    public void saveState(IMemento memento)
    {
        super.saveState(memento);
        memento.putString(PV_TAG, cbo_name.getCombo().getText());
        memento.putString(METER_TAG,
                        Boolean.toString(show_meter.getSelection()));
    }

    /** ViewPart interface, create UI. */
    @Override
    public void createPartControl(Composite parent)
    {
        createGUI(parent);

        // Enable 'Drop'
        new ProcessVariableDropTarget(cbo_name.getControl())
        {
            @Override
            public void handleDrop(IProcessVariable name,
                                   DropTargetEvent event)
            {
                setPVName(name.getName());
            }
        };

        // In principle, this could allow 'dragging' of PV names.
        // In practice, however, any mouse click & drag only selects
        // portions of the text and moves the cursor. It won't
        // initiate a 'drag'.
        // Maybe it works on some OS? Maybe there's another magic
        // modifier key to force a 'drag'?
        new ProcessVariableDragSource(cbo_name.getControl(), cbo_name);

        makeContextMenu();
    }

    // ViewPart interface
    @Override
    public void setFocus()
    {
        cbo_name.getCombo().setFocus();
    }

    /** Construct GUI. */
    private void createGUI(final Composite parent)
    {
        final FormLayout layout = new FormLayout();
        parent.setLayout(layout);

        // 3 Boxes, connected via form layout: Top, meter, bottom
        //
        // PV Name: ____ name ____________________ [Info]
        // +---------------------------------------------------+
        // |                    Meter                          |
        // +---------------------------------------------------+
        // Value     : ____ value ________________ [x] meter
        // Timestamp : ____ time _________________ [Save to IOC]
        //                                         [x] Adjust
        // ---------------
        // Status: ...
        //
        // Inside top & bottom, it's a grid layout
        top_box = new Composite(parent, 0);
        GridLayout grid = new GridLayout();
        grid.numColumns = 3;
        top_box.setLayout(grid);

        Label label = new Label(top_box, SWT.READ_ONLY);
		label.setText(Messages.S_PVName);
		label.setLayoutData(new GridData());

        cbo_name = new ComboViewer(top_box, SWT.SINGLE | SWT.BORDER);
        cbo_name.getCombo().setToolTipText(Messages.S_EnterPVName);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        cbo_name.getCombo().setLayoutData(gd);

        final Button btn_info = new Button(top_box, SWT.PUSH);
        btn_info.setText(Messages.S_Info);
        btn_info.setToolTipText(Messages.S_ObtainInfo);
        btn_info.setLayoutData(new GridData());

        // New Box with only the meter
        meter = new MeterWidget(parent, 0);
        meter.setEnabled(false);

        // Button Box
        bottom_box = new Composite(parent, 0);
        grid = new GridLayout();
        grid.numColumns = 3;
        bottom_box.setLayout(grid);
        
        label = new Label(bottom_box, 0);
        label.setText(Messages.S_Value);
        label.setLayoutData(new GridData());
        
        lbl_value = new Label(bottom_box, SWT.BORDER);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        lbl_value.setLayoutData(gd);
        
        show_meter = new Button(bottom_box, SWT.CHECK);
        show_meter.setText(Messages.S_Meter);
        show_meter.setToolTipText(Messages.S_Meter_TT);
        show_meter.setSelection(true);
        show_meter.setLayoutData(new GridData());

        // New Row
        label = new Label(bottom_box, 0);
        label.setText(Messages.S_Timestamp);
        label.setLayoutData(new GridData());

        lbl_time = new Label(bottom_box, SWT.BORDER);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        lbl_time.setLayoutData(gd);
        
        btn_save_to_ioc = new Button(bottom_box, SWT.PUSH);
        btn_save_to_ioc.setText(Messages.S_SaveToIoc);
        btn_save_to_ioc.setToolTipText(Messages.S_SaveToIocTooltip);
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        btn_save_to_ioc.setLayoutData(gd);

        // New Row
        final Label new_value_label = new Label(bottom_box, 0);
        new_value_label.setText(Messages.S_NewValueLabel);
        new_value_label.setLayoutData(new GridData());
        new_value_label.setVisible(false);
        
        final Text new_value = new Text(bottom_box, SWT.BORDER);
        new_value.setToolTipText(Messages.S_NewValueTT);
        new_value.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        new_value.setVisible(false);
        
        final Button btn_adjust = new Button(bottom_box, SWT.CHECK);
        btn_adjust.setText(Messages.S_Adjust);
        btn_adjust.setToolTipText(Messages.S_ModValue);
        btn_adjust.setLayoutData(new GridData());
        
        // Status bar
        label = new Label(bottom_box, SWT.SEPARATOR | SWT.HORIZONTAL);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.horizontalSpan = grid.numColumns;
        label.setLayoutData(gd);

        label = new Label(bottom_box, 0);
        label.setText(Messages.S_Status);
        label.setLayoutData(new GridData());

        lbl_status = new Label(bottom_box, SWT.BORDER);
        lbl_status.setText(Messages.S_Waiting);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.horizontalSpan = grid.numColumns - 1;
        lbl_status.setLayoutData(gd);

        // Connect the 3 boxes in form layout
        FormData fd = new FormData();
        fd.left = new FormAttachment(0, 0);
        fd.top = new FormAttachment(0, 0);
        fd.right = new FormAttachment(100, 0);
        top_box.setLayoutData(fd);

        fd = new FormData();
        fd.left = new FormAttachment(0, 0);
        fd.top = new FormAttachment(top_box);
        fd.right = new FormAttachment(100, 0);
        fd.bottom = new FormAttachment(bottom_box);
        meter.setLayoutData(fd);

        fd = new FormData();
        fd.left = new FormAttachment(0, 0);
        fd.right = new FormAttachment(100, 0);
        fd.bottom = new FormAttachment(100, 0);
        bottom_box.setLayoutData(fd);
        
        // Connect actions
        name_helper = new ComboHistoryHelper(
                        Plugin.getDefault().getDialogSettings(),
                        PV_LIST_TAG, cbo_name)
        {
            @Override
            public void newSelection(String pv_name)
            { 
                setPVName(pv_name);   
            }
        };
        
        cbo_name.getCombo().addDisposeListener(new DisposeListener()
        {
            public void widgetDisposed(DisposeEvent e)
            {
                disposeChannel();
                name_helper.saveSettings();
            }
        });

        btn_info.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent ev)
            {
                showInfo();
            }
        });

        btn_adjust.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent ev)
            {
                final boolean enable = btn_adjust.getSelection();
                new_value_label.setVisible(enable);
                new_value.setVisible(enable);
            }
        });
        
        new_value.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
                adjustValue(new_value.getText().trim());
            }
        });
        
        btn_save_to_ioc.addSelectionListener(new SelectionAdapter()
        {
        	@Override
        	public void widgetSelected(SelectionEvent e)
        	{
        		saveToIoc();
        	}
        });
        // Create a listener to enable/disable the Save to IOC button based on
        // the availability of a command handler.
        saveToIocCmdListener = new ICommandListener()
        {
			public void commandChanged(CommandEvent commandEvent)
			{
				if (commandEvent.isEnabledChanged())
				{
					btn_save_to_ioc.setVisible(
							commandEvent.getCommand().isEnabled());
				}
			}
        };
        // Set the initial vilibility of the button 
        updateSaveToIocButtonVisibility();

        show_meter.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent ev)
            {   showMeter(show_meter.getSelection());   }
        });

        name_helper.loadSettings();

        if (memento != null)
        {
        	setPVName(memento.getString(PV_TAG));
        	// Per default, the meter is shown.
        	// Hide according to memento.
        	final String show = memento.getString(METER_TAG);
        	if (show != null  &&  show.equals("false")) //$NON-NLS-1$
        	{
        	    show_meter.setSelection(false);
        	    showMeter(false);
        	}
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
	   	if (saveToIocCmdListener != null) {
    		Command svc = getSaveValueCommand();
    		svc.removeCommandListener(saveToIocCmdListener);
    	}
    }

    /**
     * Saves the current value to the IOC.
     */
    private void saveToIoc()
    {
		IHandlerService handlerService =
			(IHandlerService) getSite().getService(IHandlerService.class);
		try {
			ParameterizedCommand cmd = createParameterizedSaveValueCommand();
			handlerService.executeCommand(cmd, null);
		} catch (ExecutionException e) {
			// Execution of the command handler failed.
			Plugin.getLogger().error("Error executing save value command.", e); //$NON-NLS-1$
			MessageDialog.openError(getSite().getShell(),
					Messages.S_ErrorDialogTitle,
					Messages.S_SaveToIocExecutionError);
		} catch (NotDefinedException e) {
			// Thrown if the command or one of the parameters is undefined.
			// This should never happen (the command id is defined in the
			// platform). Log an error, disable the button, and return.
			Plugin.getLogger().error("Save value command is not defined.", e); //$NON-NLS-1$
			MessageDialog.openError(getSite().getShell(),
					Messages.S_ErrorDialogTitle,
					Messages.S_SaveToIocNotDefinedError);
			btn_save_to_ioc.setEnabled(false);
		} catch (NotEnabledException e) {
			MessageDialog.openWarning(getSite().getShell(),
					Messages.S_ErrorDialogTitle,
					Messages.S_SaveToIocNotEnabled);
			updateSaveToIocButtonVisibility();
		} catch (NotHandledException e) {
			MessageDialog.openWarning(getSite().getShell(),
					Messages.S_ErrorDialogTitle,
					Messages.S_SaveToIocNotEnabled);
			updateSaveToIocButtonVisibility();
		}
	}

	/**
	 * Updates the visibility state of the Save to IOC button.
	 */
	private void updateSaveToIocButtonVisibility()
	{
		btn_save_to_ioc.setVisible(getSaveValueCommand().isEnabled());
	}

	/**
	 * Creates a save value command parameterized for saving the currently
	 * displayed value.
	 * 
	 * @return the parameterized command.
	 * @throws NotDefinedException
	 *             if one of the parameter ids is undefined (this should never
	 *             happen).
	 */
	private ParameterizedCommand createParameterizedSaveValueCommand()
			throws NotDefinedException
	{
		Command saveValueCommand = getSaveValueCommand();
		IParameter pvParamter = saveValueCommand.getParameter(PV_PARAMETER_ID);
		Parameterization pvParameterization = new Parameterization(
				pvParamter, pv.getName());
		IParameter valueParameter = saveValueCommand.getParameter(VALUE_PARAMETER_ID);
		Parameterization valueParameterization = new Parameterization(
				valueParameter, value.getValueString());
		ParameterizedCommand cmd =
			new ParameterizedCommand(saveValueCommand,
					new Parameterization[] { pvParameterization, valueParameterization });
		return cmd;
	}

	/**
	 * Returns the save value command.
	 * 
	 * @return the save value command.
	 */
	private Command getSaveValueCommand()
	{
		ICommandService commandService =
			(ICommandService) getSite().getService(ICommandService.class);
		return commandService.getCommand(SAVE_VALUE_COMMAND_ID);
	}

	/** Show or hide the meter */
    protected void showMeter(final boolean show)
    {
        if (show)
        {   // Meter about to become visible
            // Attach bottom box to bottom of screen,
            // and meter stretches between top and bottom box.
            FormData fd = new FormData();
            fd.left = new FormAttachment(0, 0);
            fd.right = new FormAttachment(100, 0);
            fd.bottom = new FormAttachment(100, 0);
            bottom_box.setLayoutData(fd);
        }
        else
        {   // Meter about to be hidden.
            // Attach bottom box to top box.
            FormData fd = new FormData();
            fd.left = new FormAttachment(0, 0);
            fd.top = new FormAttachment(top_box);
            fd.right = new FormAttachment(100, 0);
            bottom_box.setLayoutData(fd);
        }
        meter.setVisible(show);
        meter.getShell().layout(true, true);
    }

    /** Add context menu.
     *  Basically empty, only contains MB_ADDITIONS to allow object contribs.
     *  <p>
     *  TODO: This doesn't work on all platforms.
     *  On Windows, the combo box already comes with a default context menu
     *  for cut/copy/paste/select all/...
     *  Sometimes you see the CSS context menu on right-click,
     *  and sometimes you don't.
     */
    private void makeContextMenu()
    {
        MenuManager manager = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        manager.addMenuListener(new IMenuListener()
        {
            public void menuAboutToShow(IMenuManager manager)
            {
                manager.add(new Separator(
                                IWorkbenchActionConstants.MB_ADDITIONS));
            }
        });
        Control control = cbo_name.getControl();
        //Control control = pv_label;
        Menu menu = manager.createContextMenu(control);
        control.setMenu(menu);
        getSite().registerContextMenu(manager, cbo_name);
    }

    /** Update the PV name that is probed.
     *  <p>
     *  Opens a new channel, closing any old one first
     *  @param name
     */
    @SuppressWarnings("nls")
    public boolean setPVName(String pv_name)
    {
        Plugin.getLogger().debug("setPVName(" + pv_name+ ")");

        // Close a previous channel
        disposeChannel();

        // Reset rest of GUI
        lbl_value.setText("");
        lbl_time.setText("");
        value.reset();
        meter.setEnabled(false);
        new_channel = true;
        
        // Check the name
        if (pv_name == null || pv_name.equals(""))
        {
            cbo_name.getCombo().setText("");
            updateStatus(Messages.S_Waiting);
            return false;
        }
        
        name_helper.addEntry(pv_name);
        cbo_name.setSelection(
            new StructuredSelection(
                        CentralItemFactory.createProcessVariable(pv_name)));
        // Update displayed name, unless it's already current
        if (! (cbo_name.getCombo().getText().equals(pv_name)))
            cbo_name.getCombo().setText(pv_name);

        // Create a new channel
        try
        {
            updateStatus(Messages.S_Searching);
            pv = PVFactory.createPV(pv_name);
            pv.addListener(this);
            pv.start();
        }
        catch (Exception ex)
        {
            Plugin.getLogger().error(Messages.S_CreateError, ex);
            updateStatus(Messages.S_CreateError + ex.getMessage());
            return false;
        }
        return true;
    }

    // PVListener
    public void pvDisconnected(PV pv)
    {
        updateStatus(Messages.S_Disconnected);
    }

    // PVListener
    public void pvValueUpdate(PV pv)
    {
        Plugin.getLogger().debug("Probe pvValueUpdate: " + pv.getName()); //$NON-NLS-1$
        // We might receive events after the view is already disposed....
        if (lbl_value.isDisposed())
            return;
        try
        {
            value.update(pv.getValue());
            // Perform update in GUI thread.
            Display.getDefault().asyncExec(update_value);
        }
        catch (Exception e)
        {
            Plugin.getLogger().error("pvValueUpdate error", e); //$NON-NLS-1$
            updateStatus(e.getMessage());
        }
    }

    /** Closes a channel and releases resource */
    private void disposeChannel()
    {
        if (pv != null)
        {
            Plugin.getLogger().debug("Probe: disposeChannel " + pv.getName()); //$NON-NLS-1$
            pv.removeListener(this);
            pv.stop();
            pv = null;
        }
    }

    /** Updates the status bar with given string.
     *  <p>
     *  Thread safe.
     */
    private void updateStatus(final String text)
    {
        if (text != null)
        {   // Make it run in the SWT UI thread
            Display.getDefault().asyncExec(new Runnable()
            {
                public void run()
                {
                    lbl_status.setText(text);
                }
            });
        }
    }

    /**
     * Info button selection handler
     * @param ev
     */
    private void showInfo()
    {
        final String nl = "\n"; //$NON-NLS-1$

        final StringBuffer info = new StringBuffer();
        if (pv == null)
        {
            info.append(Messages.S_NotConnected + nl);
        }
        else
        {
            info.append(nl + Messages.S_ChannelInfo + "  " + pv.getName() + nl); //$NON-NLS-1$
            if (pv.isConnected())
                info.append(Messages.S_STATEConn + nl);
            else
                info.append(Messages.S_STATEDisconn + nl);
            final IValue value = pv.getValue();
            if (value != null)
            {
                final IMetaData meta = value.getMetaData();
                if (meta != null)
                    info.append(meta.toString());
            }
        }
        if (info.length() == 0)
            info.append(Messages.S_NoInfo);
        MessageBox box =
            new MessageBox(lbl_value.getShell(), SWT.ICON_INFORMATION);
        box.setText(Messages.S_Info);
        box.setMessage(info.toString());
        box.open();
    }

    /** Interactively adjust the PV's value. */
    private void adjustValue(final String new_value)
    {
        try
        {
            if (pv == null)
            {
                updateStatus(Messages.S_NoChannel);
                return;
            }
            if (!pv.isConnected())
            {
                updateStatus(Messages.S_NotConnected);
                return;
            }
            pv.setValue(new_value);
        }
        catch (Throwable ex)
        {
            Plugin.getLogger().error(Messages.S_AdjustFailed, ex);
            updateStatus(Messages.S_AdjustFailed + ex.getMessage());
        }
    }
}
