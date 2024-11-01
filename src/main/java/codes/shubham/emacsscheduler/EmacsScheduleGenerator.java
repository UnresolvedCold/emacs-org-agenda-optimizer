package codes.shubham.emacsscheduler;

import ai.timefold.solver.core.api.solver.SolverManager;
import codes.shubham.emacsscheduler.orgparse.AgendaTodoProvider;
import codes.shubham.emacsscheduler.scheduler.dto.Output;
import codes.shubham.emacsscheduler.scheduler.domain.Schedule;
import codes.shubham.emacsscheduler.scheduler.domain.TodoItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
@RestController
public class EmacsScheduleGenerator {

	public static void main(String[] args) throws ExecutionException, InterruptedException {
		SpringApplication.run(EmacsScheduleGenerator.class, args);
	}

	@Autowired
	private SolverManager<Schedule, String> solverManager; // schedule and id of item

	@Value("${ideal.day.time.block.duration}")
	private int idealDayTimeBlockDuration;

	@GetMapping("/")
	public List<Output> schedule() throws ExecutionException, InterruptedException {

		List<LocalTime> timeBlocks = divideDayInTimeBlocks();

		List<TodoItem> tasksToSchedule = AgendaTodoProvider.getInstance().getTodos();

		Schedule problem = new Schedule(tasksToSchedule,  timeBlocks);

		Schedule schedule = solverManager.solve("problem", problem).getFinalBestSolution();
		System.out.println(schedule);
		List<Output> output = new ArrayList<>();
		for (TodoItem todoItem: schedule.getTodoItems()) {
			Output out = new Output();
			out.setTitle(todoItem.getName());
			out.setStartTime(todoItem.getStartTime());
			out.setEndTime(todoItem.getEndTime());
			out.setDuration((int) todoItem.getDuration().toMinutes());
			out.setPinned(todoItem.isPinned());
			out.setItemType(todoItem.getItemType().toString());
			output.add(out);
		}

		output.sort((o1, o2) -> o1.getStartTime().compareTo(o2.getStartTime()));

		return output;
	}

	private List<LocalTime> divideDayInTimeBlocks() {
		List<LocalTime> timeBlocks = new ArrayList<>();
		LocalTime dayStartTime = LocalTime.of(0, 0, 0);
		LocalTime dayEndTime = LocalTime.of(23, 59, 59);

		int numIterations = (int) Duration.between(dayStartTime, dayEndTime).toMinutes() / idealDayTimeBlockDuration;
		for (int i = 0; i < numIterations; i++) {
			timeBlocks.add(dayStartTime.plusMinutes((long) i * idealDayTimeBlockDuration));
		}

		return timeBlocks;
	}
}