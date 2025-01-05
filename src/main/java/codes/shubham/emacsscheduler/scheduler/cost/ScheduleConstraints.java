package codes.shubham.emacsscheduler.scheduler.cost;

import ai.timefold.solver.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import codes.shubham.emacsscheduler.scheduler.domain.Priority;
import codes.shubham.emacsscheduler.scheduler.pojo.ItemType;
import codes.shubham.emacsscheduler.scheduler.domain.TodoItem;
import org.joda.time.DateTime;

import java.time.LocalTime;
import java.util.*;

import static ai.timefold.solver.core.api.score.stream.Joiners.overlapping;

public class ScheduleConstraints implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        List<Constraint> constraints = new ArrayList<>();
        return constraints.toArray(new Constraint[0]);
    }
}
