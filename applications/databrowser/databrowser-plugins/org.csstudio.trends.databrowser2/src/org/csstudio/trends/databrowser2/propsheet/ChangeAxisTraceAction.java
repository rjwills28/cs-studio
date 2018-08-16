package org.csstudio.trends.databrowser2.propsheet;

import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.AxisConfig;
import org.csstudio.trends.databrowser2.model.Model;
import org.csstudio.trends.databrowser2.model.ModelItem;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.ListDialog;

public class ChangeAxisTraceAction extends ModelItemTraceAction
{

    public ChangeAxisTraceAction(final Model model, final ModelItem model_item)
    {
        super(Messages.ChangeAxis, model, model_item);
    }

    @Override
    public void run()
    {
        ListDialog dlg = new ListDialog(Display.getCurrent().getActiveShell());
        dlg.setInput(model.getAxes());
        dlg.setBlockOnOpen(true);
        dlg.setContentProvider(new ArrayContentProvider());
        dlg.setLabelProvider(new LabelProvider());
        dlg.setInitialSelections(new AxisConfig[] {model_item.getAxis()});
        dlg.setMessage(Messages.ChangeAxis);
        dlg.setTitle(model_item.getDisplayName());
        if (dlg.open() == Window.OK && dlg.getResult().length == 1)
            model_item.setAxis((AxisConfig)dlg.getResult()[0]);
    }


}
