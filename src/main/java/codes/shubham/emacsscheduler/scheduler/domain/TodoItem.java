package codes.shubham.emacsscheduler.scheduler.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.entity.PlanningPin;
import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import ai.timefold.solver.core.api.domain.variable.PlanningVariable;
import codes.shubham.emacsscheduler.scheduler.pojo.ItemType;
import lombok.Data;
import org.joda.time.DateTime;

@PlanningEntity
@Data
public class TodoItem {
    @PlanningId
    String name;
    Integer duration;
    ItemType itemType;

    @PlanningPin
    boolean isPinned;
    DateTime deadline;

    @PlanningVariable()
    DateTime startTime;

    Priority priority;

    public TodoItem() {}

    public TodoItem(String name, Integer duration, ItemType itemType, boolean isPinned, Priority priority) {
        this.name = name;
        this.duration = duration;
        this.itemType = itemType;
        this.isPinned = isPinned;
        this.priority = priority;

        // initialize with day end
        this.deadline = new DateTime().withTimeAtStartOfDay().plusDays(1);
    }

    public TodoItem(String name, Integer duration, ItemType itemType, boolean isPinned, Priority priority, DateTime deadline) {
        this(name, duration, itemType, isPinned, priority);
        this.deadline = deadline;
    }

    public static boolean isPriorityConflict(TodoItem item1, TodoItem item2) {
        if (item1.getStartTime().isAfter(item2.getStartTime())) {
            return Priority.compare(item1.getPriority(), item2.getPriority()) > 0;
        }
        return Priority.compare(item1.getPriority(), item2.getPriority()) < 0;
    }

    public DateTime getEndTime() {
        if (startTime == null) return null;

        // Don't let the end time exceed tonight
        if (startTime.isAfter(startTime.plusMinutes(duration))) {
            return DateTime.now().withTimeAtStartOfDay().plusDays(1);
        }
        return startTime.plusMinutes(duration);
    }

    public DateTime getEndTimeWithBuffer() {
        if (startTime == null) return null;
        if (getEndTime().getMillis() - getStartTime().getMillis() >= 60 * 60 * 1000) {
            return getEndTime().plusMinutes(15);
        }

        return getEndTime().plusMinutes(5);
    }

    public String toString() {
        return "{" +
                "name:" + name +
                ", duration:" + duration +
                ", startTime:" + startTime +
                '}';
    }
}
