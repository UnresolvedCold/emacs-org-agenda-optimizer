package codes.shubham.emacsscheduler.scheduler.domain;

import ai.timefold.solver.core.api.domain.solution.PlanningEntityCollectionProperty;
import ai.timefold.solver.core.api.domain.solution.PlanningScore;
import ai.timefold.solver.core.api.domain.solution.PlanningSolution;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider;
import ai.timefold.solver.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalTime;
import java.util.List;

@PlanningSolution
public class Schedule {
    @PlanningEntityCollectionProperty
    List<TodoItem> todoItems;

    @ValueRangeProvider
    @JsonIgnore
    List<LocalTime> timeBlocks;

    @PlanningScore
    HardMediumSoftScore score;

    public Schedule() {}

    public Schedule(List<TodoItem> todoItems, List<LocalTime> timeBlocks) {
        this.todoItems = todoItems;
        this.timeBlocks = timeBlocks;
    }

    public HardMediumSoftScore getScore() {
        return score;
    }

    public List<TodoItem> getTodoItems() {
        return todoItems;
    }

    public void setTodoItems(List<TodoItem> todoItems) {
        this.todoItems = todoItems;
    }

    public List<LocalTime> getTimeBlocks() {
        return timeBlocks;
    }


    public String toString() {
        return "{" +
                "todoItems:" + todoItems +
                '}';
    }
}
