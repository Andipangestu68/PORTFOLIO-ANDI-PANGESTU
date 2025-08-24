import java.io.*;
import java.util.*;


public class Main {
    // ArrayList untuk menyimpan daftar karyawan
    private static List<Staff> staffList = new ArrayList<>();

    public static void main(String[] args) {
        // Load data karyawan dari file saat program dimulai
        loadKaryawan();
        Scanner scanner = new Scanner(System.in);
        int pilihan;

        // Menu utama
        do {
            System.out.println("MENU");
            System.out.println("1. Input Data Karyawan");
            System.out.println("2. Edit Data Karyawan");
            System.out.println("3. Absensi Karyawan");
            System.out.println("4. Hitung Total Gaji Karyawan");
            System.out.println("5. Tampilkan Laporan per Karyawan");
            System.out.println("6. Keluar");
            System.out.print("Pilih menu: ");
            pilihan = scanner.nextInt();
            scanner.nextLine(); // Membersihkan newline dari input

            // Proses pilihan menu
            switch (pilihan) {
                case 1:
                    inputDataKaryawan(scanner); // Tambah data karyawan
                    break;
                case 2:
                    editDataKaryawan(scanner); // Edit data karyawan
                    break;
                case 3:
                    absensiKaryawan(scanner); // Proses absensi karyawan
                    break;
                case 4:
                    hitungTotalGajiKaryawan(); // Hitung dan simpan total gaji semua karyawan
                    break;
                case 5:
                    tampilkanLaporanKaryawan(scanner); // Tampilkan laporan per karyawan
                    break;
                case 6:
                    System.out.println("Keluar...");
                    break;
                default:
                    System.out.println("Pilihan tidak valid.");
            }
        } while (pilihan != 6); // Keluar jika pilihan 6

        scanner.close(); // Tutup scanner setelah selesai
    }

    // Method untuk memuat data karyawan dari file Karyawan.txt
    private static void loadKaryawan() {
        try (BufferedReader reader = new BufferedReader(new FileReader("Karyawan.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Pisahkan data per karyawan berdasarkan koma
                String[] data = line.split(",");
                int id = Integer.parseInt(data[0]);
                String nama = data[1];
                int gajiPokok = Integer.parseInt(data[2]);
                int jmlAbsensi = Integer.parseInt(data[3]);
                int jmlCuti = Integer.parseInt(data[4]);
                // Tambahkan objek Staff ke dalam ArrayList
                staffList.add(new Staff(id, nama, gajiPokok, jmlAbsensi, jmlCuti));
            }
        } catch (IOException e) {
            System.out.println("Gagal membaca file Karyawan.txt: " + e.getMessage());
        }
    }

    // Method untuk menambah data karyawan
    private static void inputDataKaryawan(Scanner scanner) {
        System.out.print("Masukkan ID: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Membersihkan newline dari input
        System.out.print("Masukkan Nama: ");
        String nama = scanner.nextLine();
        System.out.print("Masukkan Gaji Pokok: ");
        int gajiPokok = scanner.nextInt();
        System.out.print("Masukkan Jumlah Absensi: ");
        int jmlAbsensi = scanner.nextInt();
        System.out.print("Masukkan Jumlah Cuti: ");
        int jmlCuti = scanner.nextInt();

        // Tambahkan objek Staff baru ke ArrayList
        staffList.add(new Staff(id, nama, gajiPokok, jmlAbsensi, jmlCuti));
        System.out.println("Data karyawan berhasil ditambahkan.");
    }

    // Method untuk mengedit nama karyawan berdasarkan ID atau nama
    private static void editDataKaryawan(Scanner scanner) {
        System.out.print("Masukkan ID atau Nama Karyawan: ");
        String input = scanner.nextLine();
        // Cari karyawan berdasarkan ID atau nama
        for (Staff staff : staffList) {
            if (String.valueOf(staff.getId()).equals(input) || staff.getNama().equalsIgnoreCase(input)) {
                System.out.print("Masukkan Nama baru: ");
                String namaBaru = scanner.nextLine();
                staff.setNama(namaBaru); // Update nama
                System.out.println("Nama berhasil diubah.");
                return;
            }
        }
        System.out.println("Karyawan tidak ditemukan.");
    }

    // Method untuk menambah absensi karyawan berdasarkan ID
    private static void absensiKaryawan(Scanner scanner) {
        System.out.print("Masukkan ID: ");
        int id = scanner.nextInt();
        // Cari karyawan berdasarkan ID
        for (Staff staff : staffList) {
            if (staff.getId() == id) {
                staff.tambahAbsensi(); // Tambah absensi
                System.out.println("Absensi berhasil ditambahkan.");
                return;
            }
        }
        System.out.println("Karyawan tidak ditemukan.");
    }

    // Method untuk menghitung total gaji seluruh karyawan dan menulisnya ke file
    private static void hitungTotalGajiKaryawan() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("All_Karyawan.txt"))) {
            // Loop untuk menghitung total gaji tiap karyawan
            for (Staff staff : staffList) {
                staff.hitungTotalGaji(); // Hitung total gaji
                writer.write(staff.toString()); // Tulis data ke file
                writer.newLine();
            }
            System.out.println("Total gaji telah dihitung dan disimpan ke All_Karyawan.txt");
        } catch (IOException e) {
            System.out.println("Gagal menulis ke file All_Karyawan.txt: " + e.getMessage());
        }
    }

    // Method untuk menampilkan laporan karyawan berdasarkan ID atau nama
    private static void tampilkanLaporanKaryawan(Scanner scanner) {
        System.out.print("Masukkan ID atau Nama Karyawan: ");
        String input = scanner.nextLine();
        // Cari karyawan berdasarkan ID atau nama
        for (Staff staff : staffList) {
            if (String.valueOf(staff.getId()).equals(input) || staff.getNama().equalsIgnoreCase(input)) {
                // Tampilkan informasi karyawan
                System.out.println("ID: " + staff.getId());
                System.out.println("Nama: " + staff.getNama());
                System.out.println("Absensi: " + staff.getJmlAbsensi());
                System.out.println("Cuti: " + staff.getJmlAbsensi());
                System.out.println("Total Gaji: " + staff.getTotalGaji());
                return;
            }
        }
        System.out.println("Karyawan tidak ditemukan.");
    }
}