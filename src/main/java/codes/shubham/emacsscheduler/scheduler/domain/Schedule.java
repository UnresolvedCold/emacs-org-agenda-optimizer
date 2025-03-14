package codes.shubham.emacsscheduler.scheduler.domain;

import ai.timefold.solver.core.api.domain.solution.PlanningEntityCollectionProperty;
import ai.timefold.solver.core.api.domain.solution.PlanningScore;
import ai.timefold.solver.core.api.domain.solution.PlanningSolution;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider;
import ai.timefold.solver.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import lombok.Getter;
import org.joda.time.DateTime;

@Getter
@PlanningSolution
public class Schedule {
    @PlanningEntityCollectionProperty
    List<TodoItem> todoItems;

    @ValueRangeProvider
    @JsonIgnore
    List<DateTime> timeBlocks;

    @PlanningScore
    HardMediumSoftScore score;

    public Schedule() {}

    public Schedule(List<TodoItem> todoItems, List<DateTime> timeBlocks) {
        this.todoItems = todoItems;
        this.timeBlocks = timeBlocks;
    }

    public void setTodoItems(List<TodoItem> todoItems) {
        this.todoItems = todoItems;
    }


    public String toString() {
        return "{" +
                "todoItems:" + todoItems +
                '}';
    }
}
