package com.example.heart.service;

import com.example.heart.model.PatientsData;

import java.util.List;

public interface SearchService {
    List<PatientsData> getAllPatients();
    List<PatientsData> searchPatientsByName(String name);
}
