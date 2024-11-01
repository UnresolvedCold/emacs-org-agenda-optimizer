package codes.shubham.emacsscheduler.scheduler.dto;

import lombok.Data;

import java.time.LocalTime;

@Data
public class SchedulesOutput {
    private String title;
    private LocalTime startTime;
    private LocalTime endTime;
    private int duration;
    private boolean isPinned;
    private String itemType;
    private String priority;
}
