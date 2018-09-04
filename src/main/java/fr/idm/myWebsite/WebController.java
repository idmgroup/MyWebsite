package fr.idm.myWebsite;

import java.sql.SQLException;

import javax.validation.Valid;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Controller
public class WebController extends WebMvcConfigurerAdapter {

    @Autowired
    UserService service;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("home");
        registry.addViewController("/profil").setViewName("profil");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/CSRF").setViewName("CSRF");
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String showForm(User person) {
        return "form";
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String showUsers(Model model) throws SQLException {

        /*
         * Field usersMapField = ReflectionUtils.findField(InMemoryUserDetailsManager.class, "users");
         * ReflectionUtils.makeAccessible(usersMapField); Map<String, UserDetails> map = (Map<String, UserDetails>)
         * ReflectionUtils.getField(usersMapField, inMemoryUserDetailsManager); for (Map.Entry<String, UserDetails> user
         * : map.entrySet()) { System.out.println(user.getKey() + "/" + user.getValue().getUsername()); }
         */
        model.addAttribute("users", service.getUsers());

        return "users";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String checkPersonInfo(@Valid User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "form";
        }
        // No validation error. Check if user exist
        if (service.checkExist(user.getUsername())) {
            bindingResult.rejectValue("username", "error.user", "An account already exists for this username.");
            return "form";
        }

        // put a secure password, because user are too dumb
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()";
        String pwd = RandomStringUtils.random(15, characters);
        user.setPassword(pwd);

        service.registerUser(user);
        model.addAttribute("registered", true);
        model.addAttribute("password", pwd);
        return "form";
    }
}
