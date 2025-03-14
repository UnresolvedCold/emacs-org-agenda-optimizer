package codes.shubham.emacsscheduler.orgparse;

import codes.shubham.emacsscheduler.config.ApplicationProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AgendaTodoProviderTest {

    @Autowired
    ApplicationProperties applicationProperties;
    @Autowired
    AgendaTodoProvider agendaTodoProvider;

    @Test
    void getTodos() {
        System.out.println(applicationProperties.getOrgDirectoryList());
        System.out.println(agendaTodoProvider.getTodos());
    }
}