package com.example.heart.repository;

import com.example.heart.model.PatientsData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientsRepository extends JpaRepository<PatientsData, Long> {
}
