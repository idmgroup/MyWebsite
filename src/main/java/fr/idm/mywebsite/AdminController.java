package fr.idm.mywebsite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Controller

public class AdminController extends WebMvcConfigurerAdapter {

    private static final String ADMINHOME = "admin/home";

    @Autowired
    UserService service;

    @RequestMapping(value = "/admin/", params = { "setadmin" }, method = RequestMethod.GET)
    public String setAdmin(@RequestParam(value = "setadmin") String username, Model model) {
        if (service.userExists(username)) {
            UserDetails ud = service.loadUserByUsername(username);
            service.setAdmin(ud.getUsername());
            model.addAttribute("username", ud.getUsername());
            model.addAttribute("password", ud.getPassword());
        } else {
            model.addAttribute("error", "Are you kidding me ?");
        }
        return ADMINHOME;
    }

    @RequestMapping(value = "/admin/**", method = RequestMethod.GET)
    public String showHome() {
        return ADMINHOME;
    }

    @RequestMapping(value = "/admin/", params = { "u" }, method = RequestMethod.POST)
    public String showHome(@RequestParam(value = "u") String username, Model model) {
        if (service.userExists(username)) {
            UserDetails ud = service.loadUserByUsername(username);
            model.addAttribute("username", ud.getUsername());
            model.addAttribute("password", ud.getPassword());
            model.addAttribute("role", ud.getAuthorities());
        } else {
            model.addAttribute("error", "This search return no result.");
        }
        return ADMINHOME;
    }

}
