# 📘 README
## Setup Environment Project Cyberbullying Analysis (IndoBERT)

Dokumen ini berisi **langkah-langkah lengkap dan terstruktur** untuk menyiapkan *virtual environment (venv)*, menginstal dependensi dari `requirements.txt`, serta menjalankan proyek **Deteksi dan Klasifikasi Cyberbullying pada Komentar TikTok Bahasa Indonesia menggunakan IndoBERT dan Analisis Fairness**.

---

## 1. Prasyarat Sistem
Pastikan sistem Anda telah memenuhi prasyarat berikut:
- Sistem Operasi: **Windows 10 / 11**
- Python: **Python 3.12.x (disarankan 3.12.7)**
- Git (opsional, jika clone repository)
- Koneksi internet stabil

Cek versi Python:
```bash
python --version
```

---

## 2. Struktur Direktori Project
Pastikan struktur folder project sebagai berikut:
```text
project cyberbullyng analys/
│
├── data/
│   ├── raw/
│   └── processed/
├── models/
├── notebooks/
├── results/
│   ├── figures/
│   └── metrics/
├── requirements.txt
├── README.md
└── venv/
```

---

## 3. Membuat Virtual Environment (venv)
Masuk ke direktori project (perhatikan spasi pada path):
```bash
cd "D:\A kuliah guys\project cyberbullyng analys"
```

Buat virtual environment:
```bash
python -m venv venv
```

Jika berhasil, folder `venv/` akan terbentuk.

---

## 4. Mengaktifkan Virtual Environment

### Command Prompt (CMD):
```bash
venv\Scripts\activate
```

### PowerShell:
```powershell
venv\Scripts\Activate.ps1
```

Jika berhasil, prompt akan berubah menjadi:
```text
(venv) D:\A kuliah guys\project cyberbullyng analys>
```

---

## 5. Verifikasi Python dari venv
Pastikan Python yang digunakan berasal dari venv:
```bash
where python
```
Output teratas **harus**:
```text
...\project cyberbullyng analys\venv\Scripts\python.exe
```

Jika masih muncul Anaconda atau Python lain di atas, hentikan proses dan aktifkan ulang venv.

---

## 6. Upgrade pip
Langkah ini penting untuk menghindari error dependensi:
```bash
python -m pip install --upgrade pip
```

---

## 7. Instalasi Library dari requirements.txt
Pastikan file `requirements.txt` sudah tersedia di root project.

Install semua dependensi:
```bash
pip install -r requirements.txt
```

⏳ Proses ini dapat memakan waktu beberapa menit.

---

## 8. Instalasi Jupyter Notebook & Kernel
Agar venv dapat digunakan di Jupyter Notebook:

```bash
pip install jupyter ipykernel
```

Daftarkan kernel:
```bash
python -m ipykernel install --user --name cyberbullying-venv --display-name "Python (cyberbullying-venv)"
```

---

## 9. Menjalankan Jupyter Notebook
Jalankan Jupyter:
```bash
jupyter notebook
```

Di browser:
- Pilih **Kernel → Change Kernel**
- Pilih **Python (cyberbullying-venv)**

---

## 10. Penggunaan di VS Code (Opsional)
1. Buka folder project di VS Code
2. Tekan `Ctrl + Shift + P`
3. Pilih **Python: Select Interpreter**
4. Pilih **Python (cyberbullying-venv)**

---

## 11. Troubleshooting Umum

### ❌ Kernel Jupyter Crash
- Pastikan RAM cukup (minimal 8 GB disarankan)
- Kurangi `BATCH_SIZE` saat training
- Pastikan tidak ada konflik Anaconda

### ❌ Error install matplotlib
- Pastikan Python 3.12
- Gunakan pip versi terbaru

---

## 12. Catatan Penting
- Jangan mencampur **Anaconda** dan **venv**
- Selalu aktifkan `(venv)` sebelum install library
- Gunakan versi library yang kompatibel dengan Python 3.12

---

## 13. Tujuan Akhir Setup
Setelah semua langkah selesai, environment siap digunakan untuk:
- Training model IndoBERT
- Evaluasi performa (Accuracy, F1, ROC-AUC)
- Analisis fairness (Demographic Parity & Equal Opportunity)
- Penulisan artikel jurnal SINTA 3

---

✅ **Environment siap digunakan. Selamat melakukan penelitian!**

