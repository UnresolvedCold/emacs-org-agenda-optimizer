package codes.shubham.emacsscheduler.scheduler.cost;

import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import java.util.*;

public class ScheduleConstraints implements ConstraintProvider {

  @Override
  public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
    List<Constraint> constraints = new ArrayList<>();
    return constraints.toArray(new Constraint[0]);
  }
}
