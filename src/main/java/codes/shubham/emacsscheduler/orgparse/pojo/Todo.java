package codes.shubham.emacsscheduler.orgparse.pojo;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class Todo {
    String state;
    String priority;
    String title;
    List<String> tags;
    Integer effort;
    DateTime scheduledTime;
    DateTime deadlineTime;

    public Todo() {
    }

    public Todo(String state, String priority, String title, List<String> tags, Integer effort, DateTime scheduledTime, DateTime deadlineTime) {
        this.state = state;
        this.priority = priority;
        this.title = title;
        this.tags = tags;
        this.effort = effort;
        this.scheduledTime = scheduledTime;
        this.deadlineTime = deadlineTime;
    }

    public String toString() {
        return "{" +
                "state:" + state +
                ", priority:" + priority +
                ", title:" + title +
                ", tags:" + tags +
                ", effort:" + effort +
                ", scheduledTime:" + scheduledTime +
                '}';
    }
}
