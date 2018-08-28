package org.csstudio.trends.databrowser2.propsheet;

import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.Model;
import org.csstudio.trends.databrowser2.model.ModelItem;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

public class WaveformIndexTraceAction extends ModelItemTraceAction
{

    public WaveformIndexTraceAction(final Model model, final ModelItem model_item)
    {
        super(Messages.ChangeWaveformIndex, model, model_item);
    }

    @Override
    public void run()
    {
        InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(),"Set Waveform Index","Index", Integer.toString(this.model_item.getWaveformIndex()),
                new IInputValidator() {
                    @Override
                    public String isValid(String input) {
                        try {
                            Integer.parseInt(input);
                            return null;
                        } catch (NumberFormatException e) {
                            return "Waveform Index must be an integer";
                        }
                    }
        });
        if (dlg.open() == Window.OK)
            model_item.setWaveformIndex(Integer.parseInt(dlg.getValue()));
    }

}
