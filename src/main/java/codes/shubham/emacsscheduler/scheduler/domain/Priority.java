package codes.shubham.emacsscheduler.scheduler.domain;

public enum Priority {
  LOW,
  MEDIUM,
  HIGH;

  public static int compare(Priority priority1, Priority priority2) {
    if (priority1 == LOW && priority2 == LOW) return 0;
    if (priority1 == LOW) return -1;

    if (priority1 == MEDIUM && priority2 == MEDIUM) return 0;
    if (priority1 == MEDIUM) return priority2 == LOW ? 1 : -1;

    if (priority1 == HIGH && priority2 == HIGH) return 0;
    if (priority1 == HIGH) return 1;

    return 0;
  }
}
