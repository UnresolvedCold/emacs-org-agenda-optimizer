package codes.shubham.emacsscheduler.scheduler;

import lombok.Data;

import java.time.LocalTime;

@Data
public class Output {
    private String title;
    private LocalTime startTime;
    private LocalTime endTime;
    private int duration;
    private boolean isPinned;
    private String itemType;
}
