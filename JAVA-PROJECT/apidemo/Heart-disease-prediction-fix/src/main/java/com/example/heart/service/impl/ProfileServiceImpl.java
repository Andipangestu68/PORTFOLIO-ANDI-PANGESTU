package com.example.heart.service.impl;

import com.example.heart.model.MyUser;
import com.example.heart.repo.ProfileRepository;
import com.example.heart.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Optional<MyUser> getProfileByEmail(String email) {
        return Optional.ofNullable(profileRepository.findByEmail(email));
    }

    @Override
    public MyUser registerProfile(MyUser myUser) {
        myUser.setPassword(passwordEncoder.encode(myUser.getPassword())); // Enkripsi password
        return profileRepository.save(myUser);
    }
}
