package fr.idm.myWebsite;

import javax.validation.constraints.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class User {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserService service = SpringUtils.ctx.getBean(UserService.class);

    public UserService getService() {
        return service;
    }

    // @Size(min = 2, max = 30)
    // @Pattern(regexp = "[A-Za-z]*", message = "must contain only letters")
    private String username;

    @Size(min = 2, max = 30)
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String name) {
        this.username = name;
        if (service.checkSuspicious(this.username))
            log.info("User " + this.username + " was suspicious");
    }

    @Override
    public String toString() {
        return "Person(Name: " + this.username + ")";
    }

}
