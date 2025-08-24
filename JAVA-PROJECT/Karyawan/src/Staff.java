import java.io.*;
import java.util.*;

class Staff {
    // Deklarasi variabel untuk menyimpan informasi karyawan
    private int id;
    private String nama;
    private int gajiPokok;
    private int jmlAbsensi;
    private int jmlCuti;
    private int totalGaji;

    // Constructor untuk inisialisasi data karyawan
    public Staff(int id, String nama, int gajiPokok, int jmlAbsensi, int jmlCuti) {
        this.id = id;
        this.nama = nama;
        this.gajiPokok = gajiPokok;
        this.jmlAbsensi = jmlAbsensi;
        this.jmlCuti = jmlCuti;
        this.totalGaji = 0; // Inisialisasi total gaji
    }

    // Getter untuk ID karyawan
    public int getId() {
        return id;
    }

    // Getter untuk nama karyawan
    public String getNama() {
        return nama;
    }

    // Setter untuk mengubah nama karyawan
    public void setNama(String nama) {
        this.nama = nama;
    }

    // Getter untuk jumlah absensi karyawan
    public int getJmlAbsensi() {
        return jmlAbsensi;
    }

    // Method untuk menambah jumlah absensi (absensi maksimal 22)
    public void tambahAbsensi() {
        if (jmlAbsensi + jmlCuti < 22) {
            jmlAbsensi++;
        }
    }

    // Method untuk menghitung total gaji berdasarkan absensi, tunjangan, dan gaji pokok
    public void hitungTotalGaji() {
        int gajiHarian = gajiPokok / 22; // Gaji per hari
        int tunjanganMakan = jmlAbsensi * 10000; // Tunjangan makan Rp 10.000/hari
        int tunjanganTransport = jmlAbsensi * 20000; // Tunjangan transport Rp 20.000/hari
        this.totalGaji = (gajiHarian * jmlAbsensi) + tunjanganMakan + tunjanganTransport; // Total gaji
    }

    // Getter untuk total gaji karyawan
    public int getTotalGaji() {
        return totalGaji;
    }

    // Override toString untuk format output saat menulis ke file
    @Override
    public String toString() {
        return id + "," + nama + "," + totalGaji;
    }
}