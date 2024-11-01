package codes.shubham.emacsscheduler.orgparse;

import com.orgzly.org.OrgProperties;
import com.orgzly.org.datetime.OrgRange;
import com.orgzly.org.parser.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

public class Agenda {

    //singleton
    private static Agenda instance = null;
    private Agenda() {
    }
    public static Agenda getInstance() {
        if (instance == null) {
            synchronized (Agenda.class) {
                if (instance == null) {
                    instance = new Agenda();
                }
            }
        }
        return instance;
    }

    public static void main(String[] args) {
        List<Todo> todos = Agenda.getInstance().getTodayTodoFromOrgDirectory("D:\\Shared\\org\\roam\\journal\\");
        for (Todo todo: todos) {
            System.out.println(todo);
        }
    }

    public List<Todo> getTodayTodoFromOrgDirectory(String dir) {
        try {
            List<Todo> todos = new ArrayList<>();
            File directory = new File(dir);
            File[] files = directory.listFiles();
            if (files == null) {
                return todos;
            }

            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".org")) {
                    todos.addAll(getTodayTodoFromFile(file.getAbsolutePath()));
                }
            }

            return todos;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Todo> getTodayTodoFromFile(String filename) {
        try {

            List<Todo> todos = new ArrayList<>();

            // Load the Org file
            String orgFileData = readFileToString(filename);

            Set<String> todoKeywords = new HashSet<>(Set.of("TODO","LRN", "BLOG","FIN","ASK","WAIT"));
            Set<String> doneKeywords = new HashSet<>(Set.of("DONE","CANCELLED"));

            OrgParser parser = new OrgParser.Builder()
                    .setTodoKeywords(todoKeywords)
                    .setDoneKeywords(doneKeywords)
                    .setInput(orgFileData)
                    .build();

            var parsed = parser.parse();
            Calendar today = new GregorianCalendar();
            Map<Integer, List<String>> levelTags = new HashMap<>();
            for (OrgNodeInList head: parsed.getHeadsInList()) {
                Integer level = head.getLevel();
                computeChildTagAsPerParent(head, levelTags, level);

                OrgRange scheduled = head.getHead().getScheduled();
                if (scheduled == null
                        || !(isScheduledBeforeToday(today, scheduled.getStartTime().getCalendar())
                                || isScheduledToday(today, scheduled.getStartTime().getCalendar()))) {
                    continue;
                }

                String state = head.getHead().getState();
                if (state==null || !todoKeywords.contains(state)) {
                    continue;
                }

                String priority = head.getHead().getPriority();
                String title = head.getHead().getTitle();
                List<String> tags = levelTags.get(level);
                boolean hasScheduledTime = scheduled.getStartTime().hasTime();
                Date scheduledTime = scheduled.getStartTime().getCalendar().getTime();
                LocalTime scheduledLocalTime = null;
                if (hasScheduledTime) {
                    scheduledLocalTime = Instant.ofEpochMilli(scheduledTime.getTime())
                            .atZone(ZoneId.systemDefault())
                            .toLocalTime();
                }

                int effort = 60; // mins

                if (scheduled.getStartTime().getEndCalendar()!=null) {
                    Date endTime = scheduled.getStartTime().getEndCalendar().getTime();
                    LocalTime endTimeLocalTime = Instant.ofEpochMilli(endTime.getTime())
                            .atZone(ZoneId.systemDefault())
                            .toLocalTime();
                    effort = (int) (endTimeLocalTime.toSecondOfDay() - scheduledLocalTime.toSecondOfDay()) / 60;
                } else {
                    // effort can be in minutes or hours or just number (hours)
                    // 15m or 1h or 1
                    OrgProperties properties = head.getHead().getProperties();
                    String effortString = properties.getOrDefault("Effort", "1h");
                    if (effortString.endsWith("m")) {
                        effort = Integer.parseInt(effortString.substring(0, effortString.length() - 1));
                    } else if (effortString.endsWith("h")) {
                        effort = Integer.parseInt(effortString.substring(0, effortString.length() - 1)) * 60;
                    } else {
                        effort = Integer.parseInt(effortString) * 60;
                    }
                }

                if (!isScheduledToday(today, scheduled.getStartTime().getCalendar())) {
                    scheduledLocalTime = null;
                }

                LocalTime deadlineTime = null;
                if (head.getHead().getDeadline()!=null){
                    Date deadline = head.getHead().getDeadline().getStartTime().getCalendar().getTime();
                    deadlineTime = Instant.ofEpochMilli(deadline.getTime())
                            .atZone(ZoneId.systemDefault())
                            .toLocalTime();
                }

                todos.add(new Todo(state, priority, title, tags, effort, scheduledLocalTime, deadlineTime));
            }
            return todos;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void computeChildTagAsPerParent(OrgNodeInList head, Map<Integer, List<String>> levelTags, Integer level) {
        if (level == 1) {
            levelTags.clear();
        }
        List<Integer> levelsToRemove = new ArrayList<>();
        for (Map.Entry<Integer, List<String>> e: levelTags.entrySet()) {
            if (e.getKey() > level) {
                levelsToRemove.add(e.getKey());
            }
        }
        for (Integer l: levelsToRemove) {
            levelTags.remove(l);
        }
        List<String> tags = head.getHead().getTags();
        levelTags.put(level, tags);
        if (level > 1) {
            List<String> parentTags = levelTags.get(level - 1);
            if (parentTags != null) {
                tags.addAll(parentTags);
            }
        }
    }

    private String readFileToString(String filePath) throws IOException {
        // huge file
        byte[] encoded = Files.readAllBytes(Paths.get(filePath));
        return new String(encoded);
    }

    private boolean isScheduledBeforeToday(Calendar cal1, Calendar cal2) {
        if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) return cal1.get(Calendar.DAY_OF_YEAR) > cal2.get(Calendar.DAY_OF_YEAR);
        return cal1.get(Calendar.YEAR) > cal2.get(Calendar.YEAR);
    }

    private boolean isScheduledToday(Calendar cal1, Calendar cal2) {
        return  (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) &&
                (cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }
}
