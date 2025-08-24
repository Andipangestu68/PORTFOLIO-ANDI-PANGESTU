package org.example.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nip;

    @Column(nullable = false)
    private String nama;

    @Column(nullable = false)
    private String jabatan;

    @Column(nullable = false)
    private String alamat;

    @Column(nullable = false)
    private String email;

    @Column(name = "no_telpon", nullable = false)
    private String noTelpon;

    // Constructors
    public Employee() {}

    public Employee(String nip, String nama, String jabatan, String alamat, String email, String noTelpon) {
        this.nip = nip;
        this.nama = nama;
        this.jabatan = jabatan;
        this.alamat = alamat;
        this.email = email;
        this.noTelpon = noTelpon;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNip() { return nip; }
    public void setNip(String nip) { this.nip = nip; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getJabatan() { return jabatan; }
    public void setJabatan(String jabatan) { this.jabatan = jabatan; }

    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNoTelpon() { return noTelpon; }
    public void setNoTelpon(String noTelpon) { this.noTelpon = noTelpon; }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", nip='" + nip + '\'' +
                ", nama='" + nama + '\'' +
                ", jabatan='" + jabatan + '\'' +
                ", alamat='" + alamat + '\'' +
                ", email='" + email + '\'' +
                ", noTelpon='" + noTelpon + '\'' +
                '}';
    }
}
