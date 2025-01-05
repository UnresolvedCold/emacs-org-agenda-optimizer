package codes.shubham.emacsscheduler.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "emacs.org")
public class ApplicationProperties {
    @Value("${emacs.org.files}")
    private List<String> orgFiles;
    @Value("${emacs.org.directories}")
    private List<String> orgDirectoryList;
}
