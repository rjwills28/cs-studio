package org.csstudio.trends.databrowser2.propsheet;

import org.csstudio.trends.databrowser2.model.Model;
import org.csstudio.trends.databrowser2.model.ModelItem;
import org.eclipse.jface.action.Action;

public abstract class ModelItemTraceAction extends Action
{

    protected ModelItem model_item;
    protected Model model;

    public ModelItemTraceAction(
            final String text,
            final Model model,
            final ModelItem model_item)
    {
        super(text);
        this.model = model;
        this.model_item = model_item;
    }

}