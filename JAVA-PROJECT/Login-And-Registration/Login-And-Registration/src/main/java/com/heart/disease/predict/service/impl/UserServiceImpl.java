package com.heart.disease.predict.service.impl;

import com.heart.disease.predict.model.Users;
import com.heart.disease.predict.model.Role;
import com.heart.disease.predict.repository.UserRepository;
import com.heart.disease.predict.service.UserService;
import com.heart.disease.predict.web.dto.UserRegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Users save(UserRegistrationDto registrationDto) {
        // Mengecek apakah ini adalah user pertama di sistem (jika jumlah user = 0)
        boolean isFirstUser = userRepository.count() == 0;

        // Menetapkan role, jika ini user pertama maka ROLE_ADMIN, jika tidak maka ROLE_USER
        Role role = isFirstUser ? new Role("ROLE_ADMIN") : new Role("ROLE_USER");

        // Membuat list roles untuk user baru
        List<Role> roles = Collections.singletonList(role);

        // Membuat objek Users baru dengan data dari DTO dan role yang ditetapkan
        Users users = new Users(
                registrationDto.getFirstName(),
                registrationDto.getLastName(),
                registrationDto.getEmail(),
                passwordEncoder.encode(registrationDto.getPassword()), // Mengenkripsi password
                roles // Menetapkan roles ke user
        );

        // Menyimpan user baru ke dalam database
        return userRepository.save(users);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Mencari user berdasarkan email (digunakan sebagai username)
        Users user = userRepository.findByEmail(username);

        // Jika user tidak ditemukan, lemparkan exception
        if (user == null) {
            throw new UsernameNotFoundException("Pengguna tidak ditemukan dengan email: " + username);
        }

        // Mengembalikan user sebagai UserDetails untuk digunakan oleh Spring Security
        return new User(
                user.getEmail(),
                user.getPassword(),
                mapRolesToAuthorities(user.getRoles())
        );
    }

    private List<GrantedAuthority> mapRolesToAuthorities(List<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
}
