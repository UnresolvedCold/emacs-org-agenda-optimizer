package codes.shubham.learnrabbitmq;

import codes.shubham.learnrabbitmq.dto.Data;
import codes.shubham.learnrabbitmq.publisher.RabbitMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LearnrabbitmqApplication {

	private static RabbitMQProducer rabbitMQProducer;

	public LearnrabbitmqApplication(RabbitMQProducer rabbitMQProducer) {
		this.rabbitMQProducer = rabbitMQProducer;
	}

	public static void main(String[] args) {
		SpringApplication.run(LearnrabbitmqApplication.class, args);
		Data data = new Data(1, "Shubham");
		rabbitMQProducer.sendMessage(data);
	}
}