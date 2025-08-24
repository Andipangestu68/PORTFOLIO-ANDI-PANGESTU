package com.example.heart.service.impl;

import com.example.heart.model.PatientsData;
import com.example.heart.repo.PatientsRepository;
import com.example.heart.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

    private final PatientsRepository patientRepository;

    @Autowired
    public SearchServiceImpl(PatientsRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public List<PatientsData> getAllPatients() {
        return patientRepository.findAll();
    }

    @Override
    public List<PatientsData> searchPatientsByName(String name) {
        return patientRepository.findByNameContainingIgnoreCase(name);
    }
}
