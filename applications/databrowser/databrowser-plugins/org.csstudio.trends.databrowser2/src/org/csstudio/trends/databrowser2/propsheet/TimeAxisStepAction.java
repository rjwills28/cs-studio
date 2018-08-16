package org.csstudio.trends.databrowser2.propsheet;

import java.time.temporal.ChronoUnit;

import org.csstudio.trends.databrowser2.model.Model;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

public class TimeAxisStepAction extends Action
{
    final Model model;
    final TimeModificationType type;
    ChronoUnit unit;
    int increment;

    public enum TimeModificationType {
        EndTime,
        StartTime,
        Step;
        @Override public String toString() {
            return super.toString().replace("Time", " Time");
        }
    }

    public TimeAxisStepAction(final TimeModificationType type,
            final ChronoUnit unit,
            final int increment,
            final Model model)
    {
        super(Integer.toString(increment) + " " + unit.toString());
        this.model = model;
        this.type = type;
        this.unit = unit;
        this.increment = increment;
    }

    public TimeAxisStepAction(final TimeModificationType type,
            final Model model)
    {
        super(type.toString() + " ...");
        this.model = model;
        this.type = type;
        this.unit = ChronoUnit.DAYS;
        this.increment = 0;
    }

    @Override
    public void run()
    {
        if (this.increment == 0) {
            InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(),"Increment time","+/-N(s/m/h/d)", "+1d",
                    new IInputValidator() {
                        @Override
                        public String isValid(String input) {
                            if (input.matches("[-,+]?[0-9]+[s,m,h,d]"))
                                return null;
                            else
                                return "Invalid step specification";
                        }
            });
            if (dlg.open() == Window.OK) {
                String value = dlg.getValue();
                if (value.contains("s"))
                    this.unit = ChronoUnit.SECONDS;
                else if (value.contains("m"))
                    this.unit = ChronoUnit.MINUTES;
                else if (value.contains("h"))
                    this.unit = ChronoUnit.HOURS;
                else {
                    this.unit = ChronoUnit.DAYS;
                }
                this.increment = Integer.parseInt(value.replace("s","").replace("m","").replace("h","").replace("d",""));
            }
            else
                return;
        }
        if (type == TimeModificationType.StartTime)
            model.setTimerange(model.getStartTime().plus(increment, unit), model.getEndTime());
        else if (type == TimeModificationType.EndTime) {
            model.enableScrolling(false);
            model.setTimerange(model.getStartTime(), model.getEndTime().plus(increment, unit));
        }
        else {
            model.enableScrolling(false);
            model.setTimerange(model.getStartTime().plus(increment, unit), model.getEndTime().plus(increment, unit));
        }
    }
}
