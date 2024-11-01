package codes.shubham.emacsscheduler.scheduler;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.entity.PlanningPin;
import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import ai.timefold.solver.core.api.domain.variable.PlanningVariable;

import java.time.Duration;
import java.time.LocalTime;

@PlanningEntity
public class TodoItem {
    @PlanningId
    String name;
    Duration duration;
    ItemType itemType;

    @PlanningPin
    boolean isPinned;
    LocalTime deadline;

    @PlanningVariable
    LocalTime startTime;

    public LocalTime getEndTime() {
        return startTime.plus(duration);
    }

    public LocalTime getEndTimeWithBuffer() {
        return startTime.plus(duration).plusMinutes(15);
    }

    public TodoItem() {}

    public TodoItem(String name, Duration duration, ItemType itemType, boolean isPinned) {
        this.name = name;
        this.duration = duration;
        this.itemType = itemType;
        this.isPinned = isPinned;
        this.deadline = LocalTime.of(23, 59, 59);
    }

    public TodoItem(String name, Duration duration, ItemType itemType, boolean isPinned, LocalTime deadline) {
        this(name, duration, itemType, isPinned);
        this.deadline = deadline;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean isPinned) {
        this.isPinned = isPinned;
    }

    public LocalTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalTime deadline) {
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
