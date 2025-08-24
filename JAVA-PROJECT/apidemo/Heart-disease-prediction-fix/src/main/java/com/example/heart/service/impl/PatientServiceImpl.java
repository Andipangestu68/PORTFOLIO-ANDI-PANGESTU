package com.example.heart.service.impl;

import com.example.heart.model.PatientsData;
import com.example.heart.repo.PatientsRepository;
import com.example.heart.service.PatientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class PatientServiceImpl implements PatientService {

    private static final Logger logger = LoggerFactory.getLogger(PatientServiceImpl.class);

    @Autowired
    private PatientsRepository patientRepository;

    @Override
    public PatientsData savePatientData(PatientsData patientData) {
        // URL API Flask
        String apiUrl = "http://localhost:5000/predict";
        RestTemplate restTemplate = new RestTemplate();

        try {
            // Kirim data pasien ke API Flask dan dapatkan respons
            Map<String, Object> response = restTemplate.postForObject(apiUrl, patientData, Map.class);
            logger.info("Received response from Flask API: {}", response);

            // Cek struktur respons dan ambil data prediksi
            if (response != null && response.containsKey("probability") && response.containsKey("risk")) {
                Map<String, Double> probability = (Map<String, Double>) response.get("probability");
                patientData.setRisk((String) response.get("risk"));
                patientData.setAtRiskProbability(probability.get("at_risk").floatValue());
                patientData.setNoRiskProbability(probability.get("no_risk").floatValue());
            } else {
                logger.error("Invalid response structure from API.");
                throw new RuntimeException("Invalid response from prediction API.");
            }

            // Simpan hasil prediksi ke database
            return patientRepository.save(patientData);

        } catch (HttpClientErrorException e) {
            logger.error("Error communicating with prediction API: {}", e.getMessage());
            throw new RuntimeException("Error communicating with prediction API: " + e.getMessage());
        } catch (Exception e) {
            logger.error("An error occurred while processing patient data: {}", e.getMessage());
            throw new RuntimeException("An error occurred while processing patient data: " + e.getMessage());
        }
    }
}
