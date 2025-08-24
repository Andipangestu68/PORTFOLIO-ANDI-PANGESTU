package com.example.heart.service;

import com.example.heart.model.PatientsData;
import com.example.heart.repository.PatientsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class PatientService {



    @Autowired
    private PatientsRepository patientRepository;

    public PatientsData savePatientData(PatientsData patientData) {
        // Kirim data ke API Flask
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "http://localhost:5000/predict";

        Map<String, Object> response = restTemplate.postForObject(apiUrl, patientData, Map.class);

        // Ambil hasil prediksi dari response API
        Map<String, Double> probability = (Map<String, Double>) response.get("probability");
        patientData.setRisk((String) response.get("risk"));
        patientData.setAtRiskProbability(probability.get("at_risk").floatValue());
        patientData.setNoRiskProbability(probability.get("no_risk").floatValue());

        // Simpan ke database
        return patientRepository.save(patientData);
    }
}