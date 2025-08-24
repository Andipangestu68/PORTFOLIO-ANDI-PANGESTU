package com.example.heart.repo;

import com.example.heart.model.PatientsData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientsRepository extends JpaRepository<PatientsData, Long> {

    // Metode untuk mencari pasien berdasarkan nama (case-insensitive)
    List<PatientsData> findByNameContainingIgnoreCase(String name);
}
