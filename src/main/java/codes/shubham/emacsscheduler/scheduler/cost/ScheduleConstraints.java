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

    public static DateTime dayStartTime = DateTime.now()
            .withTimeAtStartOfDay().withHourOfDay(7).withMinuteOfHour(0).withSecondOfMinute(0);
    public static DateTime dayEndTime = DateTime.now()
            .withTimeAtStartOfDay().withHourOfDay(22).withMinuteOfHour(0).withSecondOfMinute(0);
    public static DateTime workStartTime = DateTime.now()
            .withTimeAtStartOfDay().withHourOfDay(11).withMinuteOfHour(0).withSecondOfMinute(0);
    public static DateTime workEndTime = DateTime.now()
            .withTimeAtStartOfDay().withHourOfDay(19).withMinuteOfHour(0).withSecondOfMinute(0);
    public static DateTime currentTime = DateTime.now();

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        List<Constraint> constraints = new ArrayList<>();
        constraints.add(overlappingTime(constraintFactory));
        constraints.add(generateSchedulesWithinDayActiveHours(constraintFactory));
        constraints.add(createNewSchedulesAfterCurrentTime(constraintFactory));

        constraints.add(preferBreaksBetweenTasks(constraintFactory));
        constraints.add(scheduleTaskBeforeDeadline(constraintFactory));
        constraints.add(preferHighPriorityTasksFirst(constraintFactory));
        constraints.add(reduceTheGapBetweenTasks(constraintFactory));

        // if not holidays
        Set<Integer> workingDays = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        if (workingDays.contains(DateTime.now().getDayOfWeek())) {
            constraints.add(preferWorkItemsInWorkHours(constraintFactory));
            constraints.add(preferPersonalItemsInNonWorkingHours(constraintFactory));
        }

        return constraints.toArray(new Constraint[0]);
    }

    private Constraint reduceTheGapBetweenTasks(ConstraintFactory constraintFactory) {
        return constraintFactory.forEachUniquePair(TodoItem.class)
                .filter((item1, item2) -> !item1.isPinned() && !item2.isPinned())
                .filter((item1, item2) -> item1.getEndTimeWithBuffer().isBefore(item2.getStartTime()))
                .penalize(HardMediumSoftScore.ONE_SOFT)
                .asConstraint("reduce the gap between tasks");
    }

    private Constraint preferHighPriorityTasksFirst(ConstraintFactory constraintFactory) {
        // for each unique pair
        // compare the start times
        // If high priority task is starting after low priority task -> penalize
        return constraintFactory.forEachUniquePair(TodoItem.class)
                .filter((item1, item2) -> !item1.isPinned() && !item2.isPinned())
                .filter((item1, item2) -> Priority.compare(item1.getPriority(), item2.getPriority()) != 0)
                .filter((item1, item2) -> TodoItem.isPriorityConflict(item1, item2))
                .penalize(HardMediumSoftScore.ONE_MEDIUM)
                .asConstraint("prefer high priority tasks first");
    }

    private Constraint scheduleTaskBeforeDeadline(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(TodoItem.class)
                .filter(item -> item.getDeadline().isBefore(item.getEndTime()))
                .filter(item -> !item.isPinned())
                .penalize(HardMediumSoftScore.ONE_SOFT)
                .asConstraint("schedule task before deadline");
    }

    // break of 15 mins between tasks
    private Constraint preferBreaksBetweenTasks(ConstraintFactory constraintFactory) {
        return constraintFactory.forEachUniquePair(TodoItem.class,
                        overlapping(TodoItem::getStartTime, TodoItem::getEndTimeWithBuffer))
                .filter((item1, item2) -> item1.getStartTime().isBefore(item2.getEndTimeWithBuffer()))
                .penalize(HardMediumSoftScore.ONE_SOFT)
                .asConstraint("prefer breaks between tasks");
    }

    private Constraint createNewSchedulesAfterCurrentTime(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(TodoItem.class)
                .filter(item -> item.getStartTime().isBefore(currentTime))
                .filter(item -> !item.isPinned())
                .penalize(HardMediumSoftScore.ONE_HARD)
                .asConstraint("create new schedules after current time");
    }

    private Constraint generateSchedulesWithinDayActiveHours(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(TodoItem.class)
                .filter(item -> item.getStartTime().isBefore(dayStartTime)
                        || item.getEndTime().isAfter(dayEndTime))
                .filter(item -> !item.isPinned())
                .penalize(HardMediumSoftScore.ONE_SOFT)
                .asConstraint("generate schedules within day active hours");
    }

    private Constraint preferPersonalItemsInNonWorkingHours(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(TodoItem.class)
                .filter(item -> item.getItemType() == ItemType.PERSONAL)
                .filter(item ->
                        item.getStartTime().isAfter(workStartTime)
                        && item.getEndTime().isBefore(workEndTime))
                .filter(item -> !item.isPinned())
                .penalize(HardMediumSoftScore.ONE_SOFT)
                .asConstraint("prefer personal items in non-working hours");
    }

    private Constraint preferWorkItemsInWorkHours(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(TodoItem.class)
                .filter(item -> item.getItemType() == ItemType.WORK)
                .filter(item ->
                        item.getStartTime().isBefore(workStartTime)
                        || item.getEndTime().isAfter(workEndTime))
                .filter(item -> !item.isPinned())
                .penalize(HardMediumSoftScore.ONE_SOFT)
                .asConstraint("prefer work items in work hours");
    }

    private Constraint overlappingTime(ConstraintFactory constraintFactory) {
        return constraintFactory.forEachUniquePair(TodoItem.class,
                        overlapping(TodoItem::getStartTime, TodoItem::getEndTime))
                .penalize(HardMediumSoftScore.ONE_HARD)
                .asConstraint("overlapping time");
    }
}
