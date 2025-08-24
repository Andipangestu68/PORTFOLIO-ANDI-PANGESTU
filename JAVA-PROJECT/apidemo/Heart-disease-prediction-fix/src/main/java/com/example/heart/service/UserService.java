package com.example.heart.service;

import com.example.heart.model.MyUser;
import com.example.heart.web.dto.MyUserRegistrationDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    MyUser findById(Long id);

    List<MyUser> findAll();

    MyUser save(MyUserRegistrationDto registrationDto);

    void delete(MyUser user);

    void deleteById(Long id);

}
