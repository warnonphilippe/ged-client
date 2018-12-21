package be.phw.gedclient.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Gedclient.
 * <p>
 * Properties are configured in the application.yml file.
 * See {@link io.github.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {
    private String gedUser;
    private String gedPasword;

    public ApplicationProperties() {
    }

    /**
     * @return the gedPasword
     */
    public String getGedPasword() {
        return gedPasword;
    }

    /**
     * @param gedPasword the gedPasword to set
     */
    public void setGedPasword(String gedPasword) {
        this.gedPasword = gedPasword;
    }

    /**
     * @return the gedUser
     */
    public String getGedUser() {
        return gedUser;
    }

    /**
     * @param gedUser the gedUser to set
     */
    public void setGedUser(String gedUser) {
        this.gedUser = gedUser;
    }



}
