package com.example.heart.service;

import com.example.heart.model.MyUser;

import java.util.Optional;

public interface ProfileService {
    Optional<MyUser> getProfileByEmail(String email);
    MyUser registerProfile(MyUser myUser);
}
