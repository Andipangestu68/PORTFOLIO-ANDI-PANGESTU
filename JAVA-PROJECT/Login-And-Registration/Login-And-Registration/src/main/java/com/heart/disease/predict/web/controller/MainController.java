package com.heart.disease.predict.web.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/v1")
public class MainController {

    @GetMapping("/login")
    public String getLoginPage(Model model) {
        return "login";
    }

    @GetMapping("/index")
    public String getHomePage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Logged in as: " + authentication.getName());
        System.out.println("Roles: " + authentication.getAuthorities());

        // Memproses authorities untuk menghilangkan prefix 'ROLE_' dengan Collectors.toList()
        List<String> roles = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                .collect(Collectors.toList()); // Menggunakan Collectors.toList() untuk Java 11

        // Menambahkan username dan roles ke model
        model.addAttribute("username", authentication.getName());
        model.addAttribute("roles", roles);

        return "index";
    }
}