package org.csstudio.trends.databrowser2.propsheet;

import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.Model;
import org.csstudio.trends.databrowser2.model.ModelItem;

public class HideTraceAction extends ModelItemTraceAction
{

    public HideTraceAction(final Model model, final ModelItem model_item)
    {
        super(Messages.HideTrace, model, model_item);
    }

    @Override
    public void run()
    {
        model_item.setVisible(false);
    }


}
