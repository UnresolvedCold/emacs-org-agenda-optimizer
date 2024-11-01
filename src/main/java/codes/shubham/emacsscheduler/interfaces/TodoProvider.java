package codes.shubham.emacsscheduler.interfaces;

import codes.shubham.emacsscheduler.scheduler.domain.TodoItem;

import java.util.List;

public interface TodoProvider {
    public List<TodoItem> getTodos();
}
