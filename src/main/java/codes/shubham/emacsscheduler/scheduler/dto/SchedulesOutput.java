package codes.shubham.emacsscheduler.scheduler.dto;

import com.fasterxml.jackson.annotation.JacksonAnnotation;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.joda.deser.DateTimeDeserializer;
import lombok.Data;
import org.joda.time.DateTime;

import java.time.LocalTime;

@Data
public class SchedulesOutput {
    private String title;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMM dd, yyyy HH:mm", timezone = "Asia/Kolkata")
    private DateTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMM dd, yyyy HH:mm", timezone = "Asia/Kolkata")
    private DateTime endTime;
    private int duration;
    private boolean isPinned;
    private String itemType;
    private String priority;
}
