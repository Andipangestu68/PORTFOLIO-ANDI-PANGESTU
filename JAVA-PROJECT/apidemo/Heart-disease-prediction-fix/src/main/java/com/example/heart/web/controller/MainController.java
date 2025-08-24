//package com.example.heart.web.controller;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//@Controller
//@RequestMapping("api/v1/")
//public class MainController {
//
//    @GetMapping("/login")
//    public String getLoginPage(Model model) {
//        return "login";
//    }
//
//    @GetMapping
//    public String getHomePage() {
//        return "index";
//    }
//}

package com.example.heart.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/v1")
public class MainController {

    @GetMapping("/login")
    public String getLoginPage(Model model) {
        return "login";
    }

    @GetMapping("/index")
    public String getHomePage() {
        return "index";
    }

    @GetMapping("/patients")
    public String getPatientsPage() {
        return "patients";
    }

    @GetMapping("/forms")
    public String getFormsPage() {
        return "forms";
    }

}

