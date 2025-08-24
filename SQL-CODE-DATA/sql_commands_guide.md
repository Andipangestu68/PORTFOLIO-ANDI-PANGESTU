# Panduan Perintah SQL Berdasarkan Jenis Bahasa SQL

SQL (Structured Query Language) dibagi menjadi beberapa kategori berdasarkan fungsinya. Setiap kategori memiliki perintah-perintah khusus yang digunakan untuk tujuan tertentu.

## 1. DDL (Data Definition Language)

DDL digunakan untuk mendefinisikan struktur database dan skema. Perintah-perintah ini mengubah struktur database.

### CREATE
Digunakan untuk membuat objek database baru seperti tabel, database, indeks, atau view.

```sql
-- Membuat database
CREATE DATABASE nama_database;

-- Membuat tabel
CREATE TABLE karyawan (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nama VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE,
    gaji DECIMAL(10,2),
    tanggal_masuk DATE
);

-- Membuat indeks
CREATE INDEX idx_nama ON karyawan(nama);
```

### ALTER
Digunakan untuk mengubah struktur objek database yang sudah ada.

```sql
-- Menambah kolom baru
ALTER TABLE karyawan ADD COLUMN departemen VARCHAR(30);

-- Mengubah tipe data kolom
ALTER TABLE karyawan MODIFY COLUMN gaji DECIMAL(12,2);

-- Menghapus kolom
ALTER TABLE karyawan DROP COLUMN email;

-- Menambah constraint
ALTER TABLE karyawan ADD CONSTRAINT chk_gaji CHECK (gaji > 0);
```

### DROP
Digunakan untuk menghapus objek database.

```sql
-- Menghapus tabel
DROP TABLE karyawan;

-- Menghapus database
DROP DATABASE nama_database;

-- Menghapus indeks
DROP INDEX idx_nama ON karyawan;
```

### TRUNCATE
Digunakan untuk menghapus semua data dalam tabel dengan cepat, tetapi mempertahankan struktur tabel.

```sql
TRUNCATE TABLE karyawan;
```

## 2. DML (Data Manipulation Language)

DML digunakan untuk memanipulasi data dalam tabel yang sudah ada.

### SELECT
Digunakan untuk mengambil data dari tabel.

```sql
-- Select dasar
SELECT * FROM karyawan;

-- Select dengan kondisi
SELECT nama, gaji FROM karyawan WHERE gaji > 5000000;

-- Select dengan join
SELECT k.nama, d.nama_departemen 
FROM karyawan k 
JOIN departemen d ON k.departemen_id = d.id;

-- Select dengan aggregate function
SELECT COUNT(*), AVG(gaji) FROM karyawan;

-- Select dengan GROUP BY
SELECT departemen, COUNT(*), AVG(gaji) 
FROM karyawan 
GROUP BY departemen;

-- Select dengan ORDER BY
SELECT * FROM karyawan ORDER BY gaji DESC;
```

### INSERT
Digunakan untuk menambahkan data baru ke dalam tabel.

```sql
-- Insert single record
INSERT INTO karyawan (nama, email, gaji, tanggal_masuk) 
VALUES ('John Doe', 'john@email.com', 7500000, '2024-01-15');

-- Insert multiple records
INSERT INTO karyawan (nama, email, gaji, tanggal_masuk) VALUES
('Jane Smith', 'jane@email.com', 8000000, '2024-01-20'),
('Bob Wilson', 'bob@email.com', 6500000, '2024-02-01');

-- Insert dari query lain
INSERT INTO karyawan_backup 
SELECT * FROM karyawan WHERE tanggal_masuk < '2024-01-01';
```

### UPDATE
Digunakan untuk mengubah data yang sudah ada dalam tabel.

```sql
-- Update dengan kondisi
UPDATE karyawan 
SET gaji = gaji * 1.1 
WHERE departemen = 'IT';

-- Update multiple columns
UPDATE karyawan 
SET gaji = 8500000, departemen = 'Senior IT' 
WHERE id = 1;

-- Update dengan join
UPDATE karyawan k 
JOIN departemen d ON k.departemen_id = d.id 
SET k.gaji = k.gaji * 1.05 
WHERE d.nama_departemen = 'Sales';
```

### DELETE
Digunakan untuk menghapus data dari tabel.

```sql
-- Delete dengan kondisi
DELETE FROM karyawan WHERE gaji < 3000000;

-- Delete dengan join
DELETE k FROM karyawan k 
JOIN departemen d ON k.departemen_id = d.id 
WHERE d.nama_departemen = 'Marketing';

-- Delete semua data (hati-hati!)
DELETE FROM karyawan;
```

## 3. DCL (Data Control Language)

DCL digunakan untuk mengontrol akses dan hak istimewa pengguna terhadap database.

### GRANT
Memberikan hak akses kepada pengguna.

```sql
-- Memberikan hak SELECT pada tabel tertentu
GRANT SELECT ON karyawan TO 'user1'@'localhost';

-- Memberikan multiple privileges
GRANT SELECT, INSERT, UPDATE ON database_hr.* TO 'hr_user'@'%';

-- Memberikan semua hak pada database
GRANT ALL PRIVILEGES ON database_hr.* TO 'admin_user'@'localhost';

-- Memberikan hak untuk membuat user baru
GRANT CREATE USER ON *.* TO 'admin_user'@'localhost';
```

### REVOKE
Mencabut hak akses dari pengguna.

```sql
-- Mencabut hak SELECT
REVOKE SELECT ON karyawan FROM 'user1'@'localhost';

-- Mencabut multiple privileges
REVOKE INSERT, UPDATE ON database_hr.* FROM 'hr_user'@'%';

-- Mencabut semua hak
REVOKE ALL PRIVILEGES ON database_hr.* FROM 'admin_user'@'localhost';
```

## 4. TCL (Transaction Control Language)

TCL digunakan untuk mengontrol transaksi dalam database.

### START TRANSACTION / BEGIN
Memulai transaksi baru.

```sql
START TRANSACTION;
-- atau
BEGIN;
```

### COMMIT
Menyimpan semua perubahan dalam transaksi ke database secara permanen.

```sql
START TRANSACTION;
UPDATE karyawan SET gaji = gaji * 1.1 WHERE departemen = 'IT';
INSERT INTO log_perubahan (aksi, waktu) VALUES ('Kenaikan gaji IT', NOW());
COMMIT;
```

### ROLLBACK
Membatalkan semua perubahan dalam transaksi dan kembali ke keadaan sebelumnya.

```sql
START TRANSACTION;
DELETE FROM karyawan WHERE id = 1;
-- Ups, salah hapus!
ROLLBACK;
```

### SAVEPOINT
Membuat titik simpan dalam transaksi untuk rollback parsial.

```sql
START TRANSACTION;
UPDATE karyawan SET gaji = gaji * 1.1;
SAVEPOINT after_salary_update;

DELETE FROM karyawan WHERE gaji < 3000000;
-- Ternyata tidak jadi hapus
ROLLBACK TO SAVEPOINT after_salary_update;

COMMIT;
```

### SET TRANSACTION
Mengatur karakteristik transaksi.

```sql
-- Mengatur isolation level
SET TRANSACTION ISOLATION LEVEL READ COMMITTED;

-- Mengatur sebagai read-only
SET TRANSACTION READ ONLY;
```

## 5. Perintah SQL Tambahan (Utility Commands)

### SHOW
Menampilkan informasi tentang database dan objek-objeknya.

```sql
SHOW DATABASES;
SHOW TABLES;
SHOW COLUMNS FROM karyawan;
SHOW INDEX FROM karyawan;
SHOW CREATE TABLE karyawan;
```

### DESCRIBE / DESC
Menampilkan struktur tabel.

```sql
DESCRIBE karyawan;
-- atau
DESC karyawan;
```

### USE
Menggunakan database tertentu.

```sql
USE database_hr;
```

## Tips dan Best Practices

### 1. Selalu gunakan WHERE clause saat UPDATE/DELETE
```sql
-- BAIK
UPDATE karyawan SET gaji = gaji * 1.1 WHERE departemen = 'IT';

-- HINDARI (akan update semua record!)
UPDATE karyawan SET gaji = gaji * 1.1;
```

### 2. Gunakan transaksi untuk operasi kompleks
```sql
START TRANSACTION;
UPDATE karyawan SET gaji = gaji * 1.1 WHERE departemen = 'IT';
INSERT INTO log_perubahan (aksi, waktu) VALUES ('Kenaikan gaji IT', NOW());
COMMIT;
```

### 3. Buat backup sebelum operasi besar
```sql
CREATE TABLE karyawan_backup AS SELECT * FROM karyawan;
```

### 4. Gunakan LIMIT untuk testing
```sql
-- Test dulu dengan LIMIT
SELECT * FROM karyawan WHERE gaji > 5000000 LIMIT 5;
```

### 5. Selalu validate constraint
```sql
ALTER TABLE karyawan ADD CONSTRAINT chk_gaji CHECK (gaji > 0);
ALTER TABLE karyawan ADD CONSTRAINT chk_email UNIQUE (email);
```

## Kesimpulan

Setiap jenis perintah SQL memiliki tujuan spesifik:
- **DDL**: Mengelola struktur database
- **DML**: Mengelola data dalam tabel  
- **DCL**: Mengelola akses dan keamanan
- **TCL**: Mengelola transaksi

Pemahaman yang baik tentang klasifikasi ini akan membantu Anda menggunakan SQL dengan lebih efektif dan terstruktur.