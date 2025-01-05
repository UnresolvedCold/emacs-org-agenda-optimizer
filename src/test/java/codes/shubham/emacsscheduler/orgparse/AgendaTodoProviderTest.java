package codes.shubham.emacsscheduler.orgparse;

import codes.shubham.emacsscheduler.config.ApplicationProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AgendaTodoProviderTest {

    @Autowired
    ApplicationProperties applicationProperties;
    @Autowired
    AgendaTodoProvider agendaTodoProvider;

    @Test
    void getAllTodosFromOrgFilesAndDirectories() {
        System.out.println(applicationProperties.getOrgDirectoryList());
        System.out.println(agendaTodoProvider.getAllTodosFromOrgFilesAndDirectories());
    }
}