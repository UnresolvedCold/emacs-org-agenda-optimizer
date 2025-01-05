package codes.shubham.emacsscheduler;

import ai.timefold.solver.core.api.solver.SolverManager;
import codes.shubham.emacsscheduler.orgparse.AgendaTodoProvider;
import codes.shubham.emacsscheduler.scheduler.domain.Schedule;
import codes.shubham.emacsscheduler.scheduler.domain.TodoItem;
import codes.shubham.emacsscheduler.scheduler.dto.SchedulesOutput;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
public class EmacsScheduleGenerator {
	public static void main(String[] args) throws ExecutionException, InterruptedException {
		SpringApplication.run(EmacsScheduleGenerator.class, args);
	}
}