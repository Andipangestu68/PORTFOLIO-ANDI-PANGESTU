package com.example.heart.model;



import jakarta.persistence.*;

@Entity
@Table(name = "patient_data")
public class PatientsData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int age;
    private int sex;
    private int cp;
    private int trestbps;
    private int chol;
    private int fbs;
    private int restecg;
    private int thalach;
    private int exang;
    private float oldpeak;
    private int slope;
    private int ca;
    private int thal;
    private String risk;
    private float atRiskProbability;
    private float noRiskProbability;

    // Getters and Setters


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getCp() {
        return cp;
    }

    public void setCp(int cp) {
        this.cp = cp;
    }

    public int getTrestbps() {
        return trestbps;
    }

    public void setTrestbps(int trestbps) {
        this.trestbps = trestbps;
    }

    public int getChol() {
        return chol;
    }

    public void setChol(int chol) {
        this.chol = chol;
    }

    public int getFbs() {
        return fbs;
    }

    public void setFbs(int fbs) {
        this.fbs = fbs;
    }

    public int getRestecg() {
        return restecg;
    }

    public void setRestecg(int restecg) {
        this.restecg = restecg;
    }

    public int getThalach() {
        return thalach;
    }

    public void setThalach(int thalach) {
        this.thalach = thalach;
    }

    public int getExang() {
        return exang;
    }

    public void setExang(int exang) {
        this.exang = exang;
    }

    public float getOldpeak() {
        return oldpeak;
    }

    public void setOldpeak(float oldpeak) {
        this.oldpeak = oldpeak;
    }

    public int getSlope() {
        return slope;
    }

    public void setSlope(int slope) {
        this.slope = slope;
    }

    public int getCa() {
        return ca;
    }

    public void setCa(int ca) {
        this.ca = ca;
    }

    public int getThal() {
        return thal;
    }

    public void setThal(int thal) {
        this.thal = thal;
    }

    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }

    public float getAtRiskProbability() {
        return atRiskProbability;
    }

    public void setAtRiskProbability(float atRiskProbability) {
        this.atRiskProbability = atRiskProbability;
    }

    public float getNoRiskProbability() {
        return noRiskProbability;
    }

    public void setNoRiskProbability(float noRiskProbability) {
        this.noRiskProbability = noRiskProbability;
    }


    // (Generate using your IDE for brevity)
}
