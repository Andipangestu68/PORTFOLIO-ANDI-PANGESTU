package com.heart.disease.predict.service;

import com.heart.disease.predict.model.Users;
import com.heart.disease.predict.web.dto.UserRegistrationDto;

public interface UserService {
    // Method untuk menyimpan pengguna baru
    Users save(UserRegistrationDto registrationDto);
}
