package codes.shubham.emacsscheduler.scheduler.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.entity.PlanningPin;
import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import ai.timefold.solver.core.api.domain.variable.PlanningVariable;
import codes.shubham.emacsscheduler.scheduler.pojo.ItemType;
import lombok.Data;

import java.time.Duration;
import java.time.LocalTime;

@PlanningEntity
@Data
public class TodoItem {
    @PlanningId
    String name;
    Duration duration;
    ItemType itemType;

    @PlanningPin
    boolean isPinned;
    LocalTime deadline;

    @PlanningVariable()
    LocalTime startTime;

    Priority priority;

    public LocalTime getEndTime() {
        if (startTime == null) return null;

        // Don't let the end time exceed tonight
        if (startTime.isAfter(startTime.plus(duration))) {
            return LocalTime.of(23,59,59);
        }
        return startTime.plus(duration);
    }

    public LocalTime getEndTimeWithBuffer() {
        if (startTime == null) return null;
        return getEndTime().plusMinutes(15);
    }

    public TodoItem() {}

    public TodoItem(String name, Duration duration, ItemType itemType, boolean isPinned, Priority priority) {
        this.name = name;
        this.duration = duration;
        this.itemType = itemType;
        this.isPinned = isPinned;
        this.priority = priority;
        this.deadline = LocalTime.of(23, 59, 59);
    }

    public TodoItem(String name, Duration duration, ItemType itemType, boolean isPinned, Priority priority, LocalTime deadline) {
        this(name, duration, itemType, isPinned, priority);
        this.deadline = deadline;
    }


    public String toString() {
        return "{" +
                "name:" + name +
                ", duration:" + duration +
                ", startTime:" + startTime +
                '}';
    }
}
