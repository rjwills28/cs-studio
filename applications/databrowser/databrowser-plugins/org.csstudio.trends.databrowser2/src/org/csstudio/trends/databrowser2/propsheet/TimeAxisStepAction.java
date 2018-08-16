package org.csstudio.trends.databrowser2.propsheet;

import java.time.temporal.ChronoUnit;

import org.csstudio.trends.databrowser2.model.Model;
import org.eclipse.jface.action.Action;

public class TimeAxisStepAction extends Action
{
    final Model model;
    final TimeModificationType type;
    final ChronoUnit unit;
    final int increment;

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
            final ChronoUnit unit,
            final Model model)
    {
        super("Custom ...");
        this.model = model;
        this.type = type;
        this.unit = unit;
        this.increment = 0;
    }

    @Override
    public void run()
    {
        if (this.increment == 0) {

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
