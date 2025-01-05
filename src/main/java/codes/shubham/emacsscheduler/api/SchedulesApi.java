package codes.shubham.emacsscheduler.api;

import ai.timefold.solver.core.api.solver.SolverManager;
import codes.shubham.emacsscheduler.orgparse.AgendaTodoProvider;
import codes.shubham.emacsscheduler.scheduler.domain.Schedule;
import codes.shubham.emacsscheduler.scheduler.domain.TodoItem;
import codes.shubham.emacsscheduler.scheduler.dto.SchedulesOutput;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class SchedulesApi {
    @Autowired
    AgendaTodoProvider agendaTodoProvider;

    @Autowired
    private SolverManager<Schedule, String> solverManager; // schedule and id of item

    @Value("${ideal.day.time.block.duration}")
    private int idealDayTimeBlockDuration;

    @GetMapping("/")
    public List<SchedulesOutput> schedule() throws ExecutionException, InterruptedException {

        List<TodoItem> tasksToSchedule = agendaTodoProvider.getAllTodosFromOrgFilesAndDirectories();
        int totalTaskDuration = tasksToSchedule.stream().mapToInt(TodoItem::getDuration).sum();
        List<DateTime> timeBlocks = divideDayInTimeBlocks(totalTaskDuration);

        Schedule problem = new Schedule(tasksToSchedule,  timeBlocks);

        Schedule schedule = solverManager.solve("problem", problem).getFinalBestSolution();

        System.out.println(schedule);
        List<SchedulesOutput> schedulesOutput = new ArrayList<>();
        for (TodoItem todoItem : schedule.getTodoItems()) {
            SchedulesOutput out = new SchedulesOutput();
            out.setTitle(todoItem.getName());
            out.setStartTime(todoItem.getStartTime());
            out.setEndTime(todoItem.getEndTime());
            out.setDuration((int) todoItem.getDuration());
            out.setPinned(todoItem.isPinned());
            out.setItemType(todoItem.getItemType().toString());
            out.setPriority(todoItem.getPriority().toString());
            schedulesOutput.add(out);
        }

        schedulesOutput.sort((o1, o2) -> {
            if (o1.getStartTime()==null && o2.getStartTime()==null) return 0;
            if (o1.getStartTime()==null) return 1;
            if (o2.getStartTime()==null) return -1;

            return o1.getStartTime().compareTo(o2.getStartTime());
        });

        return schedulesOutput;
    }

    private List<DateTime> divideDayInTimeBlocks(int totalTaskDuration) {
        List<DateTime> timeBlocks = new ArrayList<>();
        DateTime dayEndTime = DateTime.now().withTimeAtStartOfDay().plusDays(1);
        DateTime currentTime = DateTime.now();
        // Adjust minutes to the nearest ideal time block
        currentTime = currentTime.plusMinutes(idealDayTimeBlockDuration - (currentTime.getMinuteOfHour() % idealDayTimeBlockDuration));

        int totalDayDuration = dayEndTime.getMinuteOfDay() - currentTime.getMinuteOfDay();
        totalDayDuration = Math.max(totalDayDuration, totalTaskDuration);

        int numIterations = totalDayDuration / idealDayTimeBlockDuration;
        for (int i = 0; i < numIterations; i++) {
            timeBlocks.add(currentTime.plusMinutes(i * idealDayTimeBlockDuration));
        }

        return timeBlocks;
    }
}
