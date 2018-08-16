package org.csstudio.trends.databrowser2.propsheet;

import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.Model;
import org.csstudio.trends.databrowser2.model.ModelItem;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Display;

public class ChangeColourTraceAction extends ModelItemTraceAction
{

    public ChangeColourTraceAction(final Model model, final ModelItem model_item)
    {
        super(Messages.ChangeColour, model, model_item);
    }

    @Override
    public void run()
    {
        ColorDialog dlg = new ColorDialog(Display.getCurrent().getActiveShell());
        dlg.setRGB(model_item.getColor());
        RGB rgb = dlg.open();
        if (rgb != null)
            model_item.setColor(rgb);
    }


}
