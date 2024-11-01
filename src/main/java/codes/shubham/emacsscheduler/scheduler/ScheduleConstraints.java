package codes.shubham.emacsscheduler.scheduler;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import org.joda.time.DateTime;

import java.time.LocalTime;
import java.util.*;

import static ai.timefold.solver.core.api.score.stream.Joiners.overlapping;

public class ScheduleConstraints implements ConstraintProvider {

    public static LocalTime dayStartTime = LocalTime.of(9, 0, 0);
    public static LocalTime dayEndTime = LocalTime.of(22, 0, 0);
    public static LocalTime workStartTime = LocalTime.of(11, 0, 0);
    public static LocalTime workEndTime = LocalTime.of(19, 0, 0);
    public static LocalTime currentTime = LocalTime.now();

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        List<Constraint> constraints = new ArrayList<>();
        constraints.add(overlappingTime(constraintFactory));
        constraints.add(scheduleWorkHours(constraintFactory));
        constraints.add(createSchedulesAfterCurrentTime(constraintFactory));

        constraints.add(preferBreaksBetweenTasks(constraintFactory));
        constraints.add(scheduleTaskBeforeDeadline(constraintFactory));
        constraints.add(startTimeShouldNotBeAfterEndTime(constraintFactory));

        // evenly spread tasks throughout the day
//        constraints.add(evenlySpreadTasks(constraintFactory));

        // if not holidays
        Set<Integer> workingDays = new HashSet<>(Arrays.asList(1, 2, 3, 4));
        if (workingDays.contains(DateTime.now().getDayOfWeek())) {
            constraints.add(preferWorkItemsInWorkHours(constraintFactory));
            constraints.add(preferPersonalItemsInNonWorkingHours(constraintFactory));
        }

        return constraints.toArray(new Constraint[0]);
    }

    private Constraint evenlySpreadTasks(ConstraintFactory constraintFactory) {
        return constraintFactory.forEachUniquePair(TodoItem.class)
                .reward(HardSoftScore.ONE_SOFT, // Adjust score based on preference
                        (item1, item2) -> {
                            return Math.abs(item2.getStartTime().toSecondOfDay() - item1.getEndTime().toSecondOfDay());
                        })
                .asConstraint("evenly spread tasks");
    }

    private Constraint startTimeShouldNotBeAfterEndTime(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(TodoItem.class)
                .filter(item -> item.getStartTime().isAfter(item.getEndTime()))
                .filter(item -> !item.isPinned())
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("start time should not be after end time");
    }

    private Constraint scheduleTaskBeforeDeadline(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(TodoItem.class)
                .filter(item -> item.getDeadline().isBefore(item.getEndTime()))
                .filter(item -> !item.isPinned())
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("schedule task before deadline");
    }

    // break of 15 mins between tasks
    private Constraint preferBreaksBetweenTasks(ConstraintFactory constraintFactory) {
        return constraintFactory.forEachUniquePair(TodoItem.class,
                        overlapping(TodoItem::getStartTime, TodoItem::getEndTimeWithBuffer))
                .filter((item1, item2) -> item1.getStartTime().isBefore(item2.getEndTimeWithBuffer()))
                .penalize(HardSoftScore.ONE_SOFT)
                .asConstraint("prefer breaks between tasks");
    }

    private Constraint createSchedulesAfterCurrentTime(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(TodoItem.class)
                .filter(item -> item.getStartTime().isBefore(currentTime))
                .filter(item -> !item.isPinned())
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("create schedules after current time");
    }

    private Constraint scheduleWorkHours(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(TodoItem.class)
                .filter(item -> item.getStartTime().isBefore(dayStartTime)
                        || item.getEndTime().isAfter(dayEndTime))
                .filter(item -> !item.isPinned())
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("schedule work hours");
    }

    private Constraint preferPersonalItemsInNonWorkingHours(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(TodoItem.class)
                .filter(item -> item.getItemType() == ItemType.PERSONAL)
                .filter(item ->
                        item.getStartTime().isAfter(workStartTime)
                        && item.getEndTime().isBefore(workEndTime))
                .filter(item -> !item.isPinned())
                .penalize(HardSoftScore.ONE_SOFT)
                .asConstraint("prefer personal items in non-working hours");
    }

    private Constraint preferWorkItemsInWorkHours(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(TodoItem.class)
                .filter(item -> item.getItemType() == ItemType.WORK)
                .filter(item ->
                        item.getStartTime().isBefore(workStartTime)
                        || item.getEndTime().isAfter(workEndTime))
                .filter(item -> !item.isPinned())
                .penalize(HardSoftScore.ONE_SOFT)
                .asConstraint("prefer work items in work hours");
    }

    private Constraint overlappingTime(ConstraintFactory constraintFactory) {
        return constraintFactory.forEachUniquePair(TodoItem.class,
                        overlapping(TodoItem::getStartTime, TodoItem::getEndTime))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("overlapping time");
    }
}
