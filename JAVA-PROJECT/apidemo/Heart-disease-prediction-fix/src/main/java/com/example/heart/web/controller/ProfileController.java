package com.example.heart.web.controller;

import com.example.heart.model.MyUser;
import com.example.heart.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping("/index")
    public String index(Model model, Authentication authentication) {
        // Mendapatkan email dari pengguna yang login
        String email = authentication.getName();
        Optional<MyUser> profileOpt = profileService.getProfileByEmail(email);

        // Menambahkan data profile ke Model jika pengguna ditemukan
        if (profileOpt.isPresent()) {
            MyUser myUser = profileOpt.get();
            model.addAttribute("fullName", myUser.getFullName());
            model.addAttribute("email", myUser.getEmail());
        } else {
            System.out.println("Profile not found for email: " + email);
        }

        return "index"; // Mengarahkan ke template index.html
    }
}
