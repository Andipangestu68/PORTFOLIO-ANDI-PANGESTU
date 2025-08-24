//package com.example.heart.web.controller;
//
//
//import com.example.heart.model.PatientsData;
//import com.example.heart.repo.PatientsRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.*;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/heart")
//public class HeartPredictionController {
//
//    private final String FLASK_API_URL = "http://127.0.0.1:5000/predict";
//
//    @Autowired
//    private PatientsRepository patientRepository;
//    @PostMapping("/predict")
//    public ResponseEntity<Map<String, Object>> predictHeartRisk(@RequestBody PatientsData inputData) {
//        Map<String, Object> responseMap = new HashMap<>();
//
//        try {
//            // Siapkan RestTemplate untuk mengirim data ke Flask API
//            RestTemplate restTemplate = new RestTemplate();
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//
//            // Konversi input data menjadi Map
//            Map<String, Object> requestData = new HashMap<>();
//            requestData.put("name", inputData.getName());
//            requestData.put("age", inputData.getAge());
//            requestData.put("sex", inputData.getSex());
//            requestData.put("cp", inputData.getCp());
//            requestData.put("trestbps", inputData.getTrestbps());
//            requestData.put("chol", inputData.getChol());
//            requestData.put("fbs", inputData.getFbs());
//            requestData.put("restecg", inputData.getRestecg());
//            requestData.put("thalach", inputData.getThalach());
//            requestData.put("exang", inputData.getExang());
//            requestData.put("oldpeak", inputData.getOldpeak());
//            requestData.put("slope", inputData.getSlope());
//            requestData.put("ca", inputData.getCa());
//            requestData.put("thal", inputData.getThal());
//
//            // Kirim data ke Flask API
//            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestData, headers);
//            ResponseEntity<Map> response = restTemplate.postForEntity(FLASK_API_URL, request, Map.class);
//
//            // Ambil respons dari Flask API
//            Map<String, Object> responseBody = response.getBody();
//            if (responseBody != null) {
//                Map<String, Double> probability = (Map<String, Double>) responseBody.get("probability");
//                inputData.setRisk((String) responseBody.get("risk"));
//                inputData.setAtRiskProbability(probability.get("at_risk").floatValue());
//                inputData.setNoRiskProbability(probability.get("no_risk").floatValue());
//            }
//
//            // Simpan ke database
//            patientRepository.save(inputData);
//
//            // Siapkan respons JSON
//            responseMap.put("message", "Prediction and data saved successfully!");
//            responseMap.put("data", responseBody);
//
//            return ResponseEntity.ok(responseMap);
//
//        } catch (Exception e) {
//            responseMap.put("error", "Error: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
//        }
//    }
//
//    @GetMapping("/patients")
//    public ResponseEntity<List<PatientsData>> getAllPatients() {
//        try {
//            List<PatientsData> patients = patientRepository.findAll();
//            return ResponseEntity.ok(patients);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }
//
//
//
//}

package com.example.heart.web.controller;

import com.example.heart.model.PatientsData;
import com.example.heart.repo.PatientsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/heart")
public class HeartPredictionController {

    private static final String FLASK_API_URL = "http://127.0.0.1:5000/predict";

    @Autowired
    private PatientsRepository patientRepository;

    @PostMapping("/predict")
    public ResponseEntity<Map<String, Object>> predictHeartRisk(@RequestBody PatientsData inputData) {
        Map<String, Object> responseMap = new HashMap<>();
        try {
            // Persiapkan RestTemplate untuk komunikasi ke Flask API
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Konversi objek inputData ke format JSON Map yang diperlukan
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("name", inputData.getName());
            requestData.put("age", inputData.getAge());
            requestData.put("sex", inputData.getSex());
            requestData.put("cp", inputData.getCp());
            requestData.put("trestbps", inputData.getTrestbps());
            requestData.put("chol", inputData.getChol());
            requestData.put("fbs", inputData.getFbs());
            requestData.put("restecg", inputData.getRestecg());
            requestData.put("thalach", inputData.getThalach());
            requestData.put("exang", inputData.getExang());
            requestData.put("oldpeak", inputData.getOldpeak());
            requestData.put("slope", inputData.getSlope());
            requestData.put("ca", inputData.getCa());
            requestData.put("thal", inputData.getThal());

            // Kirim data ke Flask API dan tangani response
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestData, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(FLASK_API_URL, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                Map<String, Double> probability = (Map<String, Double>) responseBody.get("probability");

                // Update data pasien berdasarkan respons Flask
                inputData.setRisk((String) responseBody.get("risk"));
                inputData.setAtRiskProbability(probability != null ? probability.get("at_risk").floatValue() : 0.0f);
                inputData.setNoRiskProbability(probability != null ? probability.get("no_risk").floatValue() : 0.0f);

                // Simpan ke database
                patientRepository.save(inputData);

                // Persiapkan respons untuk API
                responseMap.put("message", "Prediction and data saved successfully!");
                responseMap.put("data", responseBody);
                return ResponseEntity.ok(responseMap);
            } else {
                responseMap.put("error", "Flask API response was invalid or returned an error.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
            }
        } catch (Exception e) {
            responseMap.put("error", "Exception occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
        }
    }

    @GetMapping("/patients")
    public ResponseEntity<List<PatientsData>> getAllPatients() {
        try {
            List<PatientsData> patients = patientRepository.findAll();
            return ResponseEntity.ok(patients);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
