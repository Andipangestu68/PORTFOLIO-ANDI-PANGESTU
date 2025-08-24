package com.example.heart.repo;

import com.example.heart.model.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<MyUser, Long> {
    MyUser findByEmail(String email);
}
