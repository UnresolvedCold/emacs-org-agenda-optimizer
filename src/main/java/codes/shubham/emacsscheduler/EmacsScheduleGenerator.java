package codes.shubham.emacsscheduler;

import java.util.concurrent.ExecutionException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EmacsScheduleGenerator {
	public static void main(String[] args) throws ExecutionException, InterruptedException {
		SpringApplication.run(EmacsScheduleGenerator.class, args);
	}
}