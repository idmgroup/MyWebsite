package fr.idm.mywebsite;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Controller

public class XSSController extends WebMvcConfigurerAdapter {

    @RequestMapping(value = "/XSS", method = RequestMethod.GET)
    public String showXSS() {
        return "XSS";
    }

    @RequestMapping(value = "/XSS", params = { "u" }, method = RequestMethod.POST)
    public String showXSS(@RequestParam(value = "u") String userInput, Model model) {
        model.addAttribute("userInput", userInput);
        return "XSS";
    }

}
