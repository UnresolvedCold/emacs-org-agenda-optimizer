package codes.shubham.emacsscheduler;

import ai.timefold.solver.core.api.solver.SolverManager;
import codes.shubham.emacsscheduler.learnrabbitmq.dto.Data;
import codes.shubham.emacsscheduler.learnrabbitmq.publisher.RabbitMQProducer;
import codes.shubham.emacsscheduler.orgparse.Agenda;
import codes.shubham.emacsscheduler.orgparse.Todo;
import codes.shubham.emacsscheduler.scheduler.ItemType;
import codes.shubham.emacsscheduler.scheduler.Output;
import codes.shubham.emacsscheduler.scheduler.Schedule;
import codes.shubham.emacsscheduler.scheduler.TodoItem;
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
public class LearnrabbitmqApplication {

	private static RabbitMQProducer rabbitMQProducer;

	@Value("${ideal.work.start.time}")
	public static LocalTime idealWorkStartTime;

	@Value("${ideal.work.end.time}")
	public static LocalTime idealWorkEndTime;

	public LearnrabbitmqApplication(RabbitMQProducer rabbitMQProducer) {
		this.rabbitMQProducer = rabbitMQProducer;
	}

	public static void main(String[] args) throws ExecutionException, InterruptedException {
		SpringApplication.run(LearnrabbitmqApplication.class, args);
	}
	public void rabbitMQProducer() {
		Data data = new Data(1, "Shubham");
		rabbitMQProducer.sendMessage(data);
	}

	@Autowired
	private SolverManager<Schedule, String> solverManager; // schedule and id of item

	@Value("${ideal.day.time.block.duration}")
	private int idealDayTimeBlockDuration;

	@GetMapping("/")
	public List<Output> schedule() throws ExecutionException, InterruptedException {

		List<LocalTime> timeBlocks = getDayTimeBlocks();

		List<TodoItem> tasksToSchedule = getTodosFromEmacsOrgDirectories();

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

	private List<TodoItem> getTodosFromEmacsOrgDirectories() {
		List<Todo> todos1 = Agenda.getInstance().getTodayTodoFromOrgDirectory("D:\\Shared\\org\\roam\\journal\\");
		List<Todo> todos2 = Agenda.getInstance().getTodayTodoFromFile("D:\\Shared\\org\\agenda-per.org");
		List<Todo> todos3 = Agenda.getInstance().getTodayTodoFromFile("D:\\Shared\\org\\agenda-fin.org");
		List<Todo> todos = new ArrayList<>();
		todos.addAll(todos1);
		todos.addAll(todos2);
		todos.addAll(todos3);

		List<TodoItem> todoItems = new ArrayList<>();
		for (Todo todo: todos) {
			ItemType itemType = ItemType.PERSONAL;
			if (todo.getTags().contains("company")) itemType = ItemType.WORK;

			boolean isPinned = false;
			if (todo.getScheduledTime() != null) {
				isPinned = true;
			}

			TodoItem todoItem = new TodoItem(todo.getTitle(),
					Duration.ofMinutes(todo.getEffort()),
					itemType, isPinned);

			if (todoItem.isPinned()) {
				todoItem.setStartTime(todo.getScheduledTime());
			}

			if (todo.getDeadlineTime() != null) {
				todoItem.setDeadline(todo.getDeadlineTime());
			}

			todoItems.add(todoItem);
		}
		return todoItems;
	}

	private List<LocalTime> getDayTimeBlocks() {
		List<LocalTime> timeBlocks = new ArrayList<>();
		LocalTime dayStartTime = LocalTime.of(0, 0, 0);
		LocalTime dayEndTime = LocalTime.of(23, 59, 59);

		int numitr = (int) Duration.between(dayStartTime, dayEndTime).toMinutes() / idealDayTimeBlockDuration;
		for (int i = 0; i < numitr; i++) {
			timeBlocks.add(dayStartTime.plusMinutes((long) i * idealDayTimeBlockDuration));
		}

		return timeBlocks;
	}
}