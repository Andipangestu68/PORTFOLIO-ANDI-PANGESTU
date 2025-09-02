# Panduan Pembelajaran SQL untuk Data Professional

## Daftar Isi
1. [Query Dasar SQL](#1-query-dasar-sql)
2. [Analisis Data Penjualan](#2-analisis-data-penjualan)
3. [Analisis Perilaku Pengguna](#3-analisis-perilaku-pengguna)
4. [Pembersihan Data](#4-pembersihan-data)
5. [Transformasi Data](#5-transformasi-data)
6. [Penggabungan Data (JOIN)](#6-penggabungan-data-join)
7. [Subquery untuk Analisis Mendalam](#7-subquery-untuk-analisis-mendalam)
8. [Fungsi Agregasi Lanjutan](#8-fungsi-agregasi-lanjutan)
9. [Window Function](#9-window-function)
10. [Optimasi Query](#10-optimasi-query)

---

## 1. Query Dasar SQL

### 1.1 SELECT Sederhana

```sql
-- Menampilkan semua data dari tabel
SELECT * FROM tabel1;

-- Menampilkan kolom tertentu
SELECT kolom1, kolom2, kolom3 
FROM tabel1;

-- Menampilkan data dengan alias
SELECT 
    kolom1 AS nama_produk,
    kolom2 AS harga,
    kolom3 AS kategori
FROM tabel1;
```

**Penjelasan:** Query dasar untuk menampilkan data. Dalam pekerjaan sehari-hari, ini digunakan untuk eksplorasi awal data dan memahami struktur tabel.

### 1.2 Filtering dengan WHERE

```sql
-- Filter berdasarkan kondisi tunggal
SELECT kolom1, kolom2 
FROM tabel1 
WHERE kolom3 > 100000;

-- Filter dengan multiple kondisi
SELECT kolom1, kolom2, kolom3 
FROM tabel1 
WHERE kolom3 > 50000 
    AND kolom4 = 'Elektronik' 
    AND kolom5 IS NOT NULL;

-- Filter dengan pattern matching
SELECT * 
FROM tabel1 
WHERE kolom1 LIKE 'Laptop%';
```

**Penjelasan:** Filtering essential untuk data analyst dalam menyeleksi data spesifik, seperti produk dengan harga tertentu atau kategori khusus.

### 1.3 Sorting dan Limiting

```sql
-- Mengurutkan data
SELECT kolom1, kolom2, kolom3 
FROM tabel1 
ORDER BY kolom3 DESC;

-- Limit hasil query
SELECT kolom1, kolom2 
FROM tabel1 
ORDER BY kolom3 DESC 
LIMIT 10;

-- Pagination dengan OFFSET
SELECT kolom1, kolom2 
FROM tabel1 
ORDER BY kolom1 
LIMIT 20 OFFSET 40;
```

**Penjelasan:** Sorting dan limiting sangat penting untuk mendapatkan top performers, ranking produk, atau implementasi pagination dalam dashboard.

---

## 2. Analisis Data Penjualan

### 2.1 Total Penjualan per Bulan

```sql
-- Total penjualan per bulan
SELECT 
    DATE_FORMAT(kolom_tanggal, '%Y-%m') AS bulan,
    SUM(kolom_total) AS total_penjualan,
    COUNT(*) AS jumlah_transaksi,
    AVG(kolom_total) AS rata_rata_transaksi
FROM tabel_transaksi 
WHERE kolom_tanggal >= '2024-01-01'
GROUP BY DATE_FORMAT(kolom_tanggal, '%Y-%m')
ORDER BY bulan;
```

**Penjelasan:** Query untuk analisis trend penjualan bulanan. Data Engineer menggunakan ini untuk membuat pipeline ETL, sementara Data Analyst menggunakannya untuk reporting bulanan.

### 2.2 Produk Terlaris

```sql
-- Top 10 produk dengan penjualan tertinggi
SELECT 
    kolom_produk,
    SUM(kolom_quantity) AS total_terjual,
    SUM(kolom_total) AS total_revenue,
    COUNT(DISTINCT kolom_customer_id) AS unique_customers
FROM tabel_transaksi 
WHERE kolom_tanggal >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
GROUP BY kolom_produk
ORDER BY total_revenue DESC
LIMIT 10;
```

**Penjelasan:** Identifikasi best-selling products crucial untuk inventory management dan strategic planning. Data Scientist menggunakan ini untuk recommendation system.

### 2.3 Analisis Seasonality

```sql
-- Penjualan per kuartal dan day of week
SELECT 
    QUARTER(kolom_tanggal) AS kuartal,
    DAYOFWEEK(kolom_tanggal) AS hari_dalam_minggu,
    CASE DAYOFWEEK(kolom_tanggal)
        WHEN 1 THEN 'Minggu'
        WHEN 2 THEN 'Senin'
        WHEN 3 THEN 'Selasa'
        WHEN 4 THEN 'Rabu'
        WHEN 5 THEN 'Kamis'
        WHEN 6 THEN 'Jumat'
        WHEN 7 THEN 'Sabtu'
    END AS nama_hari,
    AVG(kolom_total) AS rata_rata_penjualan,
    COUNT(*) AS jumlah_transaksi
FROM tabel_transaksi 
GROUP BY kuartal, hari_dalam_minggu, nama_hari
ORDER BY kuartal, hari_dalam_minggu;
```

**Penjelasan:** Analisis pola seasonal untuk forecasting dan resource planning. Data Scientist menggunakan hasil ini sebagai feature engineering untuk model prediksi.

---

## 3. Analisis Perilaku Pengguna

### 3.1 User Aktif Harian (DAU)

```sql
-- Daily Active Users
SELECT 
    DATE(kolom_timestamp) AS tanggal,
    COUNT(DISTINCT kolom_user_id) AS daily_active_users,
    COUNT(*) AS total_aktivitas
FROM tabel_aktivitas 
WHERE kolom_timestamp >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
GROUP BY DATE(kolom_timestamp)
ORDER BY tanggal;
```

**Penjelasan:** Metrics DAU essential untuk product analytics. Data Analyst menggunakan ini untuk monitoring user engagement dan growth metrics.

### 3.2 User Retention Analysis

```sql
-- Cohort retention analysis (simplified)
WITH first_visit AS (
    SELECT 
        kolom_user_id,
        MIN(DATE(kolom_timestamp)) AS first_visit_date
    FROM tabel_aktivitas 
    GROUP BY kolom_user_id
),
user_activity AS (
    SELECT 
        a.kolom_user_id,
        DATE(a.kolom_timestamp) AS activity_date,
        f.first_visit_date,
        DATEDIFF(DATE(a.kolom_timestamp), f.first_visit_date) AS days_since_first_visit
    FROM tabel_aktivitas a
    JOIN first_visit f ON a.kolom_user_id = f.kolom_user_id
)
SELECT 
    first_visit_date,
    COUNT(DISTINCT CASE WHEN days_since_first_visit = 0 THEN kolom_user_id END) AS day_0,
    COUNT(DISTINCT CASE WHEN days_since_first_visit = 1 THEN kolom_user_id END) AS day_1,
    COUNT(DISTINCT CASE WHEN days_since_first_visit = 7 THEN kolom_user_id END) AS day_7,
    COUNT(DISTINCT CASE WHEN days_since_first_visit = 30 THEN kolom_user_id END) AS day_30
FROM user_activity
GROUP BY first_visit_date
ORDER BY first_visit_date;
```

**Penjelasan:** Cohort analysis untuk mengukur user retention. Data Scientist menggunakan ini untuk churn prediction model dan customer lifecycle analysis.

### 3.3 Segmentasi Pelanggan

```sql
-- RFM Analysis (Recency, Frequency, Monetary)
WITH customer_metrics AS (
    SELECT 
        kolom_customer_id,
        DATEDIFF(CURDATE(), MAX(kolom_tanggal)) AS recency,
        COUNT(*) AS frequency,
        SUM(kolom_total) AS monetary
    FROM tabel_transaksi 
    WHERE kolom_tanggal >= DATE_SUB(CURDATE(), INTERVAL 365 DAY)
    GROUP BY kolom_customer_id
),
rfm_scores AS (
    SELECT 
        kolom_customer_id,
        recency,
        frequency,
        monetary,
        NTILE(5) OVER (ORDER BY recency DESC) AS r_score,
        NTILE(5) OVER (ORDER BY frequency ASC) AS f_score,
        NTILE(5) OVER (ORDER BY monetary ASC) AS m_score
    FROM customer_metrics
)
SELECT 
    kolom_customer_id,
    recency,
    frequency,
    monetary,
    CASE 
        WHEN r_score >= 4 AND f_score >= 4 THEN 'Champions'
        WHEN r_score >= 3 AND f_score >= 3 THEN 'Loyal Customers'
        WHEN r_score >= 3 AND f_score <= 2 THEN 'Potential Loyalists'
        WHEN r_score <= 2 AND f_score >= 4 THEN 'At Risk'
        ELSE 'Others'
    END AS customer_segment
FROM rfm_scores
ORDER BY monetary DESC;
```

**Penjelasan:** RFM segmentation untuk customer classification. Marketing teams menggunakan ini untuk targeted campaigns, while Data Scientists menggunakannya untuk personalization algorithms.

---

## 4. Pembersihan Data

### 4.1 Menghapus Duplikat

```sql
-- Identifikasi duplikat
SELECT 
    kolom1, kolom2, kolom3,
    COUNT(*) as jumlah_duplikat
FROM tabel1 
GROUP BY kolom1, kolom2, kolom3
HAVING COUNT(*) > 1;

-- Menghapus duplikat (keep first occurrence)
DELETE t1 FROM tabel1 t1
INNER JOIN (
    SELECT 
        MIN(id) as min_id,
        kolom1, kolom2
    FROM tabel1 
    GROUP BY kolom1, kolom2
    HAVING COUNT(*) > 1
) t2 
WHERE t1.kolom1 = t2.kolom1 
    AND t1.kolom2 = t2.kolom2 
    AND t1.id > t2.min_id;
```

**Penjelasan:** Data deduplication crucial dalam data cleaning pipeline. Data Engineers implementasi ini dalam ETL processes untuk ensure data quality.

### 4.2 Menangani Nilai NULL

```sql
-- Identifikasi missing values
SELECT 
    COUNT(*) AS total_rows,
    COUNT(kolom1) AS non_null_kolom1,
    COUNT(kolom2) AS non_null_kolom2,
    COUNT(kolom3) AS non_null_kolom3,
    (COUNT(*) - COUNT(kolom1)) AS missing_kolom1,
    ROUND((COUNT(*) - COUNT(kolom1)) * 100.0 / COUNT(*), 2) AS missing_kolom1_pct
FROM tabel1;

-- Replace NULL dengan default values
UPDATE tabel1 
SET 
    kolom1 = COALESCE(kolom1, 'Unknown'),
    kolom2 = COALESCE(kolom2, 0),
    kolom3 = COALESCE(kolom3, (SELECT AVG(kolom3) FROM tabel1 WHERE kolom3 IS NOT NULL));

-- Forward fill untuk time series data
SELECT 
    kolom_tanggal,
    kolom_value,
    COALESCE(kolom_value, 
        LAG(kolom_value) OVER (ORDER BY kolom_tanggal)) AS kolom_value_filled
FROM tabel_timeseries 
ORDER BY kolom_tanggal;
```

**Penjelasan:** Handling missing values essential dalam data preprocessing. Data Scientists perlu clean data sebelum model training, sementara analysts perlu complete datasets untuk accurate reporting.

### 4.3 Outlier Detection

```sql
-- Detect outliers using IQR method
WITH quartiles AS (
    SELECT 
        PERCENTILE_CONT(0.25) WITHIN GROUP (ORDER BY kolom_numeric) AS Q1,
        PERCENTILE_CONT(0.75) WITHIN GROUP (ORDER BY kolom_numeric) AS Q3
    FROM tabel1
),
outlier_bounds AS (
    SELECT 
        Q1,
        Q3,
        Q3 - Q1 AS IQR,
        Q1 - 1.5 * (Q3 - Q1) AS lower_bound,
        Q3 + 1.5 * (Q3 - Q1) AS upper_bound
    FROM quartiles
)
SELECT 
    t.*,
    CASE 
        WHEN t.kolom_numeric < o.lower_bound OR t.kolom_numeric > o.upper_bound 
        THEN 'Outlier' 
        ELSE 'Normal' 
    END AS outlier_flag
FROM tabel1 t
CROSS JOIN outlier_bounds o
ORDER BY t.kolom_numeric;
```

**Penjelasan:** Outlier detection penting untuk data quality assurance. Data Scientists menggunakan ini untuk feature engineering, sementara analysts menggunakannya untuk anomaly reporting.

---

## 5. Transformasi Data

### 5.1 Membuat Kolom Baru

```sql
-- Membuat calculated fields
SELECT 
    kolom1,
    kolom2,
    kolom3,
    -- Mathematical calculations
    kolom2 * kolom3 AS total_value,
    kolom2 * 0.1 AS tax_amount,
    kolom2 + (kolom2 * 0.1) AS total_with_tax,
    
    -- Date calculations
    YEAR(kolom_tanggal) AS tahun,
    MONTH(kolom_tanggal) AS bulan,
    DAYOFWEEK(kolom_tanggal) AS hari_minggu,
    DATEDIFF(CURDATE(), kolom_tanggal) AS days_ago,
    
    -- String manipulations
    UPPER(kolom_text) AS kolom_text_upper,
    SUBSTRING(kolom_text, 1, 3) AS kolom_text_prefix,
    CONCAT(kolom1, ' - ', kolom2) AS combined_field,
    
    -- Conditional logic
    CASE 
        WHEN kolom2 > 1000000 THEN 'High'
        WHEN kolom2 > 500000 THEN 'Medium'
        ELSE 'Low'
    END AS price_category
FROM tabel1;
```

**Penjelasan:** Feature engineering fundamental untuk analytics dan machine learning. Data Engineers menggunakan ini dalam transformation layers, Data Scientists untuk model features.

### 5.2 Konversi Tipe Data

```sql
-- Data type conversions
SELECT 
    kolom1,
    -- String to numeric
    CAST(kolom_string_number AS DECIMAL(10,2)) AS numeric_value,
    
    -- Date conversions
    STR_TO_DATE(kolom_date_string, '%Y-%m-%d') AS proper_date,
    DATE_FORMAT(kolom_timestamp, '%Y-%m-%d') AS formatted_date,
    
    -- Boolean conversions
    CASE 
        WHEN kolom_status = 'active' THEN 1 
        ELSE 0 
    END AS is_active,
    
    -- JSON parsing (MySQL 5.7+)
    JSON_EXTRACT(kolom_json, '$.name') AS extracted_name,
    JSON_EXTRACT(kolom_json, '$.age') AS extracted_age
FROM tabel1;
```

**Penjelasan:** Data type conversion essential dalam ETL pipelines. Data Engineers perlu ensure proper data types untuk storage optimization dan query performance.

### 5.3 Normalisasi Data

```sql
-- Min-Max normalization
WITH stats AS (
    SELECT 
        MIN(kolom_numeric) AS min_val,
        MAX(kolom_numeric) AS max_val,
        AVG(kolom_numeric) AS avg_val,
        STDDEV(kolom_numeric) AS std_val
    FROM tabel1
)
SELECT 
    t.kolom1,
    t.kolom_numeric,
    -- Min-Max normalization (0-1 scale)
    (t.kolom_numeric - s.min_val) / (s.max_val - s.min_val) AS normalized_minmax,
    
    -- Z-score normalization
    (t.kolom_numeric - s.avg_val) / s.std_val AS normalized_zscore,
    
    -- Decimal scaling
    t.kolom_numeric / POWER(10, LENGTH(CAST(s.max_val AS CHAR))) AS normalized_decimal
FROM tabel1 t
CROSS JOIN stats s;
```

**Penjelasan:** Data normalization penting untuk machine learning model preparation. Data Scientists menggunakan ini untuk feature scaling sebelum training algorithms seperti neural networks atau clustering.

---

## 6. Penggabungan Data (JOIN)

### 6.1 Basic JOINs

```sql
-- INNER JOIN: Hanya data yang ada di kedua tabel
SELECT 
    t1.kolom1,
    t1.kolom2,
    t2.kolom3,
    t2.kolom4
FROM tabel_transaksi t1
INNER JOIN tabel_pelanggan t2 
    ON t1.customer_id = t2.customer_id;

-- LEFT JOIN: Semua data dari tabel kiri
SELECT 
    p.customer_id,
    p.nama_pelanggan,
    t.kolom_total,
    t.kolom_tanggal
FROM tabel_pelanggan p
LEFT JOIN tabel_transaksi t 
    ON p.customer_id = t.customer_id;

-- RIGHT JOIN: Semua data dari tabel kanan
SELECT 
    t.transaction_id,
    t.kolom_total,
    p.nama_pelanggan,
    p.email
FROM tabel_transaksi t
RIGHT JOIN tabel_pelanggan p 
    ON t.customer_id = p.customer_id;
```

**Penjelasan:** JOIN operations fundamental untuk combining related data. Data Analysts menggunakan ini untuk comprehensive reporting, Data Engineers untuk building dimensional models.

### 6.2 Complex JOINs

```sql
-- Multiple JOINs dengan aggregation
SELECT 
    p.customer_id,
    p.nama_pelanggan,
    p.kota,
    COUNT(t.transaction_id) AS total_transaksi,
    SUM(t.kolom_total) AS total_pembelian,
    AVG(t.kolom_total) AS rata_rata_transaksi,
    MAX(t.kolom_tanggal) AS transaksi_terakhir,
    COUNT(DISTINCT pr.product_id) AS unique_products_bought
FROM tabel_pelanggan p
LEFT JOIN tabel_transaksi t ON p.customer_id = t.customer_id
LEFT JOIN tabel_product_transaction pr ON t.transaction_id = pr.transaction_id
WHERE p.status = 'active'
GROUP BY p.customer_id, p.nama_pelanggan, p.kota
HAVING total_pembelian > 100000
ORDER BY total_pembelian DESC;
```

**Penjelasan:** Complex joins dengan aggregation untuk customer analytics. Business intelligence teams menggunakan ini untuk customer profiling dan segmentation analysis.

### 6.3 Self JOIN

```sql
-- Self JOIN untuk hierarchical data
SELECT 
    e1.employee_id,
    e1.nama_employee,
    e1.manager_id,
    e2.nama_employee AS nama_manager,
    e1.salary,
    e2.salary AS manager_salary
FROM tabel_employee e1
LEFT JOIN tabel_employee e2 ON e1.manager_id = e2.employee_id
ORDER BY e1.employee_id;

-- Self JOIN untuk finding pairs
SELECT 
    t1.kolom1 AS item1,
    t2.kolom1 AS item2,
    t1.kolom2 AS price1,
    t2.kolom2 AS price2
FROM tabel1 t1
JOIN tabel1 t2 ON t1.kolom3 = t2.kolom3 
    AND t1.id < t2.id  -- Avoid duplicate pairs
WHERE t1.kolom2 + t2.kolom2 < 1000;
```

**Penjelasan:** Self joins berguna untuk hierarchical data analysis dan finding relationships within same dataset. HR analytics menggunakan ini untuk organizational structure analysis.

---

## 7. Subquery untuk Analisis Mendalam

### 7.1 Correlated Subquery

```sql
-- Find customers dengan pembelian di atas rata-rata
SELECT 
    customer_id,
    nama_pelanggan,
    (SELECT SUM(kolom_total) 
     FROM tabel_transaksi t 
     WHERE t.customer_id = p.customer_id) AS total_pembelian
FROM tabel_pelanggan p
WHERE (SELECT SUM(kolom_total) 
       FROM tabel_transaksi t 
       WHERE t.customer_id = p.customer_id) > 
      (SELECT AVG(customer_total) 
       FROM (SELECT SUM(kolom_total) AS customer_total 
             FROM tabel_transaksi 
             GROUP BY customer_id) AS avg_calc);
```

**Penjelasan:** Correlated subqueries powerful untuk row-by-row comparisons. Data Analysts menggunakan ini untuk identifying above-average performers atau anomaly detection.

### 7.2 EXISTS dan NOT EXISTS

```sql
-- Pelanggan yang pernah membeli produk elektronik
SELECT DISTINCT 
    p.customer_id,
    p.nama_pelanggan
FROM tabel_pelanggan p
WHERE EXISTS (
    SELECT 1 
    FROM tabel_transaksi t
    JOIN tabel_produk pr ON t.product_id = pr.product_id
    WHERE t.customer_id = p.customer_id 
        AND pr.kategori = 'Elektronik'
);

-- Pelanggan yang tidak pernah membeli dalam 90 hari terakhir
SELECT 
    customer_id,
    nama_pelanggan,
    email
FROM tabel_pelanggan p
WHERE NOT EXISTS (
    SELECT 1 
    FROM tabel_transaksi t
    WHERE t.customer_id = p.customer_id 
        AND t.kolom_tanggal >= DATE_SUB(CURDATE(), INTERVAL 90 DAY)
)
AND p.status = 'active';
```

**Penjelasan:** EXISTS clauses efficient untuk checking relationships tanpa actual data retrieval. Marketing teams menggunakan ini untuk customer targeting dan churn identification.

### 7.3 Advanced Subqueries

```sql
-- Ranking dengan subquery
SELECT 
    kolom1,
    kolom2,
    kolom3,
    (SELECT COUNT(*) 
     FROM tabel1 t2 
     WHERE t2.kolom3 > t1.kolom3) + 1 AS ranking
FROM tabel1 t1
ORDER BY kolom3 DESC;

-- Moving averages dengan subquery
SELECT 
    kolom_tanggal,
    kolom_value,
    (SELECT AVG(kolom_value) 
     FROM tabel_timeseries t2 
     WHERE t2.kolom_tanggal BETWEEN 
         DATE_SUB(t1.kolom_tanggal, INTERVAL 6 DAY) 
         AND t1.kolom_tanggal) AS moving_avg_7day
FROM tabel_timeseries t1
ORDER BY kolom_tanggal;
```

**Penjelasan:** Advanced subqueries untuk complex analytics seperti ranking dan time series analysis. Data Scientists menggunakan ini untuk feature engineering dalam predictive models.

---

## 8. Fungsi Agregasi Lanjutan

### 8.1 GROUP BY dengan Multiple Dimensions

```sql
-- Multi-dimensional analysis
SELECT 
    kolom_kategori,
    kolom_region,
    YEAR(kolom_tanggal) AS tahun,
    MONTH(kolom_tanggal) AS bulan,
    COUNT(*) AS jumlah_transaksi,
    SUM(kolom_total) AS total_penjualan,
    AVG(kolom_total) AS rata_rata_transaksi,
    MIN(kolom_total) AS min_transaksi,
    MAX(kolom_total) AS max_transaksi,
    STDDEV(kolom_total) AS std_dev_transaksi
FROM tabel_transaksi
WHERE kolom_tanggal >= '2024-01-01'
GROUP BY kolom_kategori, kolom_region, tahun, bulan
ORDER BY kolom_kategori, kolom_region, tahun, bulan;
```

**Penjelasan:** Multi-dimensional grouping untuk comprehensive business analysis. BI teams menggunakan ini untuk creating OLAP cubes dan dashboard reporting.

### 8.2 HAVING untuk Advanced Filtering

```sql
-- Advanced filtering pada aggregated data
SELECT 
    customer_id,
    COUNT(*) AS total_orders,
    SUM(kolom_total) AS total_spent,
    AVG(kolom_total) AS avg_order_value,
    MAX(kolom_tanggal) AS last_order_date
FROM tabel_transaksi
WHERE kolom_tanggal >= DATE_SUB(CURDATE(), INTERVAL 365 DAY)
GROUP BY customer_id
HAVING COUNT(*) >= 5  -- Minimum 5 orders
    AND SUM(kolom_total) > 500000  -- Total spending > 500k
    AND AVG(kolom_total) > 50000  -- Average order > 50k
    AND MAX(kolom_tanggal) >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)  -- Recent activity
ORDER BY total_spent DESC;
```

**Penjelasan:** HAVING clauses essential untuk filtering aggregated results. Customer analytics teams menggunakan ini untuk high-value customer identification.

### 8.3 Statistical Functions

```sql
-- Advanced statistical analysis
SELECT 
    kolom_kategori,
    COUNT(*) AS sample_size,
    AVG(kolom_harga) AS mean_price,
    STDDEV(kolom_harga) AS std_deviation,
    VARIANCE(kolom_harga) AS variance,
    MIN(kolom_harga) AS min_price,
    MAX(kolom_harga) AS max_price,
    
    -- Percentiles
    PERCENTILE_CONT(0.25) WITHIN GROUP (ORDER BY kolom_harga) AS Q1,
    PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY kolom_harga) AS median,
    PERCENTILE_CONT(0.75) WITHIN GROUP (ORDER BY kolom_harga) AS Q3,
    PERCENTILE_CONT(0.9) WITHIN GROUP (ORDER BY kolom_harga) AS P90,
    PERCENTILE_CONT(0.95) WITHIN GROUP (ORDER BY kolom_harga) AS P95
FROM tabel_produk
GROUP BY kolom_kategori
HAVING COUNT(*) >= 10  -- Minimum sample size
ORDER BY mean_price DESC;
```

**Penjelasan:** Statistical functions crucial untuk data science analysis. Analysts menggunakan ini untuk distribution analysis dan outlier detection dalam pricing strategies.

---

## 9. Window Function

### 9.1 ROW_NUMBER dan RANK

```sql
-- Ranking products dalam setiap kategori
SELECT 
    kolom_kategori,
    kolom_produk,
    kolom_harga,
    kolom_penjualan,
    
    -- Different ranking methods
    ROW_NUMBER() OVER (PARTITION BY kolom_kategori ORDER BY kolom_penjualan DESC) AS row_num,
    RANK() OVER (PARTITION BY kolom_kategori ORDER BY kolom_penjualan DESC) AS rank_dense,
    DENSE_RANK() OVER (PARTITION BY kolom_kategori ORDER BY kolom_penjualan DESC) AS dense_rank,
    
    -- Percentage ranking
    PERCENT_RANK() OVER (PARTITION BY kolom_kategori ORDER BY kolom_penjualan) AS percent_rank,
    NTILE(4) OVER (PARTITION BY kolom_kategori ORDER BY kolom_penjualan DESC) AS quartile
FROM tabel_produk
WHERE kolom_status = 'active'
ORDER BY kolom_kategori, row_num;
```

**Penjelasan:** Window functions untuk ranking analysis. Product managers menggunakan ini untuk identifying top performers per category dan competitive positioning.

### 9.2 LAG dan LEAD untuk Time Series

```sql
-- Time series analysis dengan LAG/LEAD
SELECT 
    kolom_tanggal,
    kolom_metric,
    
    -- Previous values
    LAG(kolom_metric, 1) OVER (ORDER BY kolom_tanggal) AS prev_day,
    LAG(kolom_metric, 7) OVER (ORDER BY kolom_tanggal) AS prev_week,
    LAG(kolom_metric, 30) OVER (ORDER BY kolom_tanggal) AS prev_month,
    
    -- Next values
    LEAD(kolom_metric, 1) OVER (ORDER BY kolom_tanggal) AS next_day,
    
    -- Growth calculations
    kolom_metric - LAG(kolom_metric, 1) OVER (ORDER BY kolom_tanggal) AS day_over_day_change,
    ROUND(
        (kolom_metric - LAG(kolom_metric, 1) OVER (ORDER BY kolom_tanggal)) * 100.0 / 
        LAG(kolom_metric, 1) OVER (ORDER BY kolom_tanggal), 2
    ) AS day_over_day_pct_change,
    
    -- Week over week growth
    ROUND(
        (kolom_metric - LAG(kolom_metric, 7) OVER (ORDER BY kolom_tanggal)) * 100.0 / 
        LAG(kolom_metric, 7) OVER (ORDER BY kolom_tanggal), 2
    ) AS week_over_week_pct_change
FROM tabel_metrics
WHERE kolom_tanggal >= DATE_SUB(CURDATE(), INTERVAL 90 DAY)
ORDER BY kolom_tanggal;
```

**Penjelasan:** LAG/LEAD functions essential untuk time series analysis dan growth calculations. Finance teams menggunakan ini untuk trend analysis dan performance monitoring.

### 9.3 Running Totals dan Moving Averages

```sql
-- Running calculations
SELECT 
    kolom_tanggal,
    kolom_revenue,
    
    -- Running totals
    SUM(kolom_revenue) OVER (
        ORDER BY kolom_tanggal 
        ROWS UNBOUNDED PRECEDING
    ) AS running_total,
    
    -- Running averages
    AVG(kolom_revenue) OVER (
        ORDER BY kolom_tanggal 
        ROWS UNBOUNDED PRECEDING
    ) AS running_avg,
    
    -- Moving averages (7-day window)
    AVG(kolom_revenue) OVER (
        ORDER BY kolom_tanggal 
        ROWS BETWEEN 6 PRECEDING AND CURRENT ROW
    ) AS moving_avg_7day,
    
    -- Moving averages (30-day window)
    AVG(kolom_revenue) OVER (
        ORDER BY kolom_tanggal 
        ROWS BETWEEN 29 PRECEDING AND CURRENT ROW
    ) AS moving_avg_30day,
    
    -- Rolling sum (7-day window)
    SUM(kolom_revenue) OVER (
        ORDER BY kolom_tanggal 
        ROWS BETWEEN 6 PRECEDING AND CURRENT ROW
    ) AS rolling_sum_7day,
    
    -- Min and Max in moving window
    MIN(kolom_revenue) OVER (
        ORDER BY kolom_tanggal 
        ROWS BETWEEN 6 PRECEDING AND CURRENT ROW
    ) AS min_7day,
    MAX(kolom_revenue) OVER (
        ORDER BY kolom_tanggal 
        ROWS BETWEEN 6 PRECEDING AND CURRENT ROW
    ) AS max_7day
FROM tabel_daily_revenue
ORDER BY kolom_tanggal;
```

**Penjelasan:** Running totals dan moving averages fundamental untuk financial reporting dan trend analysis. Data Scientists menggunakan ini untuk smoothing time series data dan feature engineering untuk forecasting models.

### 9.4 First Value dan Last Value

```sql
-- Cohort analysis dengan window functions
SELECT 
    customer_id,
    kolom_tanggal,
    kolom_total,
    
    -- First transaction info
    FIRST_VALUE(kolom_tanggal) OVER (
        PARTITION BY customer_id 
        ORDER BY kolom_tanggal
    ) AS first_transaction_date,
    FIRST_VALUE(kolom_total) OVER (
        PARTITION BY customer_id 
        ORDER BY kolom_tanggal
    ) AS first_transaction_amount,
    
    -- Last transaction info
    LAST_VALUE(kolom_tanggal) OVER (
        PARTITION BY customer_id 
        ORDER BY kolom_tanggal
        ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING
    ) AS last_transaction_date,
    
    -- Customer lifetime calculations
    DATEDIFF(
        LAST_VALUE(kolom_tanggal) OVER (
            PARTITION BY customer_id 
            ORDER BY kolom_tanggal
            ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING
        ),
        FIRST_VALUE(kolom_tanggal) OVER (
            PARTITION BY customer_id 
            ORDER BY kolom_tanggal
        )
    ) AS customer_lifetime_days,
    
    -- Transaction sequence
    ROW_NUMBER() OVER (
        PARTITION BY customer_id 
        ORDER BY kolom_tanggal
    ) AS transaction_sequence
FROM tabel_transaksi
ORDER BY customer_id, kolom_tanggal;
```

**Penjelasan:** FIRST_VALUE/LAST_VALUE functions powerful untuk cohort analysis dan customer lifecycle calculations. CRM teams menggunakan ini untuk customer journey analysis dan retention strategies.

---

## 10. Optimasi Query

### 10.1 Index Optimization

```sql
-- Membuat index untuk optimasi query
CREATE INDEX idx_customer_date ON tabel_transaksi(customer_id, kolom_tanggal);
CREATE INDEX idx_product_category ON tabel_produk(kolom_kategori, kolom_status);
CREATE INDEX idx_composite ON tabel_transaksi(kolom_tanggal, kolom_status, customer_id);

-- Analyze query performance
EXPLAIN SELECT 
    t.customer_id,
    SUM(t.kolom_total) AS total_spent
FROM tabel_transaksi t
WHERE t.kolom_tanggal >= '2024-01-01'
    AND t.kolom_status = 'completed'
GROUP BY t.customer_id;

-- Query dengan hint untuk force index usage
SELECT /*+ USE_INDEX(tabel_transaksi, idx_customer_date) */
    customer_id,
    COUNT(*) AS transaction_count
FROM tabel_transaksi
WHERE kolom_tanggal >= '2024-01-01'
GROUP BY customer_id;
```

**Penjelasan:** Index optimization crucial untuk query performance. Database Engineers menggunakan ini untuk ensuring scalable data warehouse performance dan reducing query execution time.

### 10.2 Query Optimization Techniques

```sql
-- Efficient filtering (push predicates down)
WITH filtered_data AS (
    SELECT customer_id, kolom_total, kolom_tanggal
    FROM tabel_transaksi
    WHERE kolom_tanggal >= '2024-01-01'
        AND kolom_status = 'completed'
        AND kolom_total > 0
)
SELECT 
    customer_id,
    COUNT(*) AS transaction_count,
    SUM(kolom_total) AS total_spent
FROM filtered_data
GROUP BY customer_id
HAVING COUNT(*) >= 3;

-- Avoid SELECT * in subqueries
SELECT 
    p.customer_id,
    p.nama_pelanggan,
    customer_stats.total_spent
FROM tabel_pelanggan p
JOIN (
    SELECT 
        customer_id,
        SUM(kolom_total) AS total_spent
    FROM tabel_transaksi
    WHERE kolom_tanggal >= '2024-01-01'
    GROUP BY customer_id
) customer_stats ON p.customer_id = customer_stats.customer_id;

-- Use EXISTS instead of IN for large datasets
SELECT customer_id, nama_pelanggan
FROM tabel_pelanggan p
WHERE EXISTS (
    SELECT 1 
    FROM tabel_transaksi t
    WHERE t.customer_id = p.customer_id
        AND t.kolom_tanggal >= '2024-01-01'
);
```

**Penjelasan:** Query optimization techniques untuk improving performance pada large datasets. Data Engineers implementasi best practices ini dalam production ETL pipelines untuk efficient data processing.

### 10.3 Partitioning dan Materialized Views

```sql
-- Table partitioning example (syntax varies by database)
CREATE TABLE tabel_transaksi_partitioned (
    transaction_id INT AUTO_INCREMENT,
    customer_id INT,
    kolom_total DECIMAL(10,2),
    kolom_tanggal DATE,
    PRIMARY KEY (transaction_id, kolom_tanggal)
) PARTITION BY RANGE (YEAR(kolom_tanggal)) (
    PARTITION p2023 VALUES LESS THAN (2024),
    PARTITION p2024 VALUES LESS THAN (2025),
    PARTITION p2025 VALUES LESS THAN (2026)
);

-- Materialized view untuk pre-computed aggregations
CREATE MATERIALIZED VIEW mv_monthly_sales AS
SELECT 
    DATE_FORMAT(kolom_tanggal, '%Y-%m') AS month_year,
    COUNT(*) AS transaction_count,
    SUM(kolom_total) AS total_revenue,
    AVG(kolom_total) AS avg_transaction,
    COUNT(DISTINCT customer_id) AS unique_customers
FROM tabel_transaksi
WHERE kolom_status = 'completed'
GROUP BY DATE_FORMAT(kolom_tanggal, '%Y-%m');

-- Query menggunakan materialized view
SELECT 
    month_year,
    total_revenue,
    LAG(total_revenue, 1) OVER (ORDER BY month_year) AS prev_month_revenue,
    ROUND(
        (total_revenue - LAG(total_revenue, 1) OVER (ORDER BY month_year)) * 100.0 /
        LAG(total_revenue, 1) OVER (ORDER BY month_year), 2
    ) AS month_over_month_growth
FROM mv_monthly_sales
ORDER BY month_year;
```

**Penjelasan:** Partitioning dan materialized views advanced techniques untuk enterprise-level data warehousing. Data Architects menggunakan ini untuk designing scalable data infrastructure.

---

## 11. Advanced Analytics Patterns

### 11.1 Customer Lifetime Value (CLV)

```sql
-- CLV calculation dengan cohort analysis
WITH customer_metrics AS (
    SELECT 
        customer_id,
        MIN(kolom_tanggal) AS first_purchase_date,
        MAX(kolom_tanggal) AS last_purchase_date,
        COUNT(*) AS total_orders,
        SUM(kolom_total) AS total_spent,
        AVG(kolom_total) AS avg_order_value,
        DATEDIFF(MAX(kolom_tanggal), MIN(kolom_tanggal)) AS customer_lifespan_days
    FROM tabel_transaksi
    WHERE kolom_status = 'completed'
    GROUP BY customer_id
),
clv_calculation AS (
    SELECT 
        customer_id,
        total_orders,
        total_spent,
        avg_order_value,
        customer_lifespan_days,
        CASE 
            WHEN customer_lifespan_days > 0 
            THEN total_orders / (customer_lifespan_days / 365.0)
            ELSE total_orders
        END AS purchase_frequency_yearly,
        -- Simple CLV = AOV * Purchase Frequency * Customer Lifespan
        avg_order_value * 
        (CASE 
            WHEN customer_lifespan_days > 0 
            THEN total_orders / (customer_lifespan_days / 365.0)
            ELSE total_orders
        END) * 
        (customer_lifespan_days / 365.0) AS estimated_clv
    FROM customer_metrics
)
SELECT 
    customer_id,
    total_spent AS historical_value,
    estimated_clv,
    CASE 
        WHEN estimated_clv > 1000000 THEN 'High Value'
        WHEN estimated_clv > 500000 THEN 'Medium Value'
        ELSE 'Low Value'
    END AS customer_value_segment
FROM clv_calculation
WHERE customer_lifespan_days >= 30  -- Exclude very new customers
ORDER BY estimated_clv DESC;
```

**Penjelasan:** CLV calculation essential untuk customer-centric business strategy. Marketing teams menggunakan ini untuk budget allocation dan customer acquisition cost optimization.

### 11.2 Market Basket Analysis

```sql
-- Market basket analysis untuk cross-selling
WITH transaction_products AS (
    SELECT 
        t.transaction_id,
        t.customer_id,
        tp.product_id,
        p.nama_produk,
        p.kolom_kategori
    FROM tabel_transaksi t
    JOIN tabel_transaction_products tp ON t.transaction_id = tp.transaction_id
    JOIN tabel_produk p ON tp.product_id = p.product_id
    WHERE t.kolom_tanggal >= DATE_SUB(CURDATE(), INTERVAL 90 DAY)
),
product_pairs AS (
    SELECT 
        tp1.product_id AS product_a,
        tp1.nama_produk AS nama_product_a,
        tp2.product_id AS product_b,
        tp2.nama_produk AS nama_product_b,
        COUNT(DISTINCT tp1.transaction_id) AS co_occurrence_count
    FROM transaction_products tp1
    JOIN transaction_products tp2 
        ON tp1.transaction_id = tp2.transaction_id
        AND tp1.product_id < tp2.product_id  -- Avoid duplicate pairs
    GROUP BY tp1.product_id, tp1.nama_produk, tp2.product_id, tp2.nama_produk
    HAVING COUNT(DISTINCT tp1.transaction_id) >= 5  -- Minimum co-occurrence
)
SELECT 
    product_a,
    nama_product_a,
    product_b,
    nama_product_b,
    co_occurrence_count,
    -- Calculate lift (simplified)
    ROUND(co_occurrence_count * 100.0 / 
        (SELECT COUNT(DISTINCT transaction_id) FROM transaction_products), 2) 
    AS co_occurrence_percentage
FROM product_pairs
ORDER BY co_occurrence_count DESC
LIMIT 20;
```

**Penjelasan:** Market basket analysis untuk identifying product associations. E-commerce teams menggunakan ini untuk recommendation systems dan cross-selling strategies.

### 11.3 Anomaly Detection

```sql
-- Statistical anomaly detection
WITH daily_metrics AS (
    SELECT 
        DATE(kolom_timestamp) AS tanggal,
        COUNT(*) AS daily_count,
        SUM(kolom_value) AS daily_sum,
        AVG(kolom_value) AS daily_avg
    FROM tabel_metrics
    WHERE kolom_timestamp >= DATE_SUB(CURDATE(), INTERVAL 90 DAY)
    GROUP BY DATE(kolom_timestamp)
),
stats AS (
    SELECT 
        AVG(daily_count) AS avg_count,
        STDDEV(daily_count) AS std_count,
        AVG(daily_sum) AS avg_sum,
        STDDEV(daily_sum) AS std_sum,
        AVG(daily_avg) AS avg_avg,
        STDDEV(daily_avg) AS std_avg
    FROM daily_metrics
)
SELECT 
    dm.tanggal,
    dm.daily_count,
    dm.daily_sum,
    dm.daily_avg,
    -- Z-score calculations
    (dm.daily_count - s.avg_count) / s.std_count AS z_score_count,
    (dm.daily_sum - s.avg_sum) / s.std_sum AS z_score_sum,
    -- Anomaly flags (threshold: |z-score| > 2)
    CASE 
        WHEN ABS((dm.daily_count - s.avg_count) / s.std_count) > 2 THEN 'Anomaly'
        ELSE 'Normal'
    END AS count_anomaly,
    CASE 
        WHEN ABS((dm.daily_sum - s.avg_sum) / s.std_sum) > 2 THEN 'Anomaly'
        ELSE 'Normal'
    END AS sum_anomaly
FROM daily_metrics dm
CROSS JOIN stats s
ORDER BY dm.tanggal DESC;
```

**Penjelasan:** Anomaly detection crucial untuk data quality monitoring dan business intelligence. Operations teams menggunakan ini untuk early warning systems dan fraud detection.

---

## 12. Data Engineering Patterns

### 12.1 Slowly Changing Dimensions (SCD Type 2)

```sql
-- SCD Type 2 implementation untuk tracking historical changes
CREATE TABLE dim_customer_scd (
    surrogate_key INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    nama_pelanggan VARCHAR(100),
    email VARCHAR(100),
    kota VARCHAR(50),
    status VARCHAR(20),
    effective_date DATE,
    expiry_date DATE,
    is_current BOOLEAN DEFAULT TRUE,
    created_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert new record untuk customer changes
INSERT INTO dim_customer_scd (
    customer_id, nama_pelanggan, email, kota, status, 
    effective_date, expiry_date, is_current
)
SELECT 
    customer_id,
    nama_pelanggan,
    email,
    kota,
    status,
    CURDATE() AS effective_date,
    '9999-12-31' AS expiry_date,
    TRUE AS is_current
FROM tabel_pelanggan_staging
WHERE customer_id NOT IN (
    SELECT customer_id 
    FROM dim_customer_scd 
    WHERE is_current = TRUE
);

-- Update existing records when changes detected
UPDATE dim_customer_scd 
SET 
    expiry_date = DATE_SUB(CURDATE(), INTERVAL 1 DAY),
    is_current = FALSE
WHERE customer_id IN (
    SELECT s.customer_id
    FROM tabel_pelanggan_staging s
    JOIN dim_customer_scd d ON s.customer_id = d.customer_id
    WHERE d.is_current = TRUE
        AND (s.nama_pelanggan != d.nama_pelanggan 
            OR s.email != d.email 
            OR s.kota != d.kota 
            OR s.status != d.status)
);
```

**Penjelasan:** SCD Type 2 essential untuk data warehousing dan tracking historical changes. Data Engineers menggunakan ini untuk maintaining data lineage dan historical reporting.

### 12.2 ETL Error Handling

```sql
-- ETL process dengan error handling dan logging
CREATE TABLE etl_log (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    process_name VARCHAR(100),
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    status VARCHAR(20),
    records_processed INT,
    records_error INT,
    error_message TEXT,
    created_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ETL process dengan exception handling
DELIMITER //
CREATE PROCEDURE sp_process_daily_sales()
BEGIN
    DECLARE v_error_count INT DEFAULT 0;
    DECLARE v_processed_count INT DEFAULT 0;
    DECLARE v_start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        INSERT INTO etl_log (
            process_name, start_time, end_time, status, 
            records_processed, records_error, error_message
        ) VALUES (
            'daily_sales_processing', 
            v_start_time, 
            CURRENT_TIMESTAMP, 
            'FAILED', 
            v_processed_count,
            v_error_count,
            'SQL Exception occurred during processing'
        );
    END;

    START TRANSACTION;
    
    -- Insert successful records
    INSERT INTO tabel_sales_clean (
        transaction_id, customer_id, product_id, 
        kolom_total, kolom_tanggal, status
    )
    SELECT 
        transaction_id, customer_id, product_id,
        kolom_total, kolom_tanggal, 'processed'
    FROM tabel_sales_raw
    WHERE kolom_total > 0 
        AND customer_id IS NOT NULL 
        AND kolom_tanggal IS NOT NULL;
    
    SET v_processed_count = ROW_COUNT();
    
    -- Log error records
    INSERT INTO tabel_sales_errors (
        transaction_id, customer_id, product_id,
        kolom_total, kolom_tanggal, error_reason
    )
    SELECT 
        transaction_id, customer_id, product_id,
        kolom_total, kolom_tanggal,
        CASE 
            WHEN kolom_total <= 0 THEN 'Invalid amount'
            WHEN customer_id IS NULL THEN 'Missing customer'
            WHEN kolom_tanggal IS NULL THEN 'Missing date'
        END as error_reason
    FROM tabel_sales_raw
    WHERE kolom_total <= 0 
        OR customer_id IS NULL 
        OR kolom_tanggal IS NULL;
    
    SET v_error_count = ROW_COUNT();
    
    -- Success logging
    INSERT INTO etl_log (
        process_name, start_time, end_time, status, 
        records_processed, records_error
    ) VALUES (
        'daily_sales_processing', 
        v_start_time, 
        CURRENT_TIMESTAMP, 
        'SUCCESS', 
        v_processed_count,
        v_error_count
    );
    
    COMMIT;
END //
DELIMITER ;
```

**Penjelasan:** ETL error handling critical untuk production data pipelines. Data Engineers menggunakan ini untuk ensuring data quality dan maintaining audit trails dalam enterprise environments.

---

## Kesimpulan

Dokumen ini mencakup query SQL dari level dasar hingga advanced yang sering digunakan dalam pekerjaan sehari-hari sebagai:

### **Data Analyst:**
- Basic queries untuk exploration dan reporting
- Aggregation functions untuk business metrics
- JOIN operations untuk comprehensive analysis
- Window functions untuk trend analysis

### **Data Scientist:**  
- Data cleaning dan transformation untuk model preparation
- Statistical functions untuk exploratory data analysis
- Advanced analytics patterns seperti CLV dan cohort analysis
- Feature engineering dengan window functions

### **Data Engineer:**
- ETL patterns dengan error handling
- Performance optimization dengan indexing
- Data warehouse patterns (SCD, partitioning)
- Pipeline monitoring dan logging

### **Tips Praktis:**
1. **Selalu gunakan EXPLAIN** untuk analyze query performance
2. **Implementasikan proper indexing** pada kolom yang sering di-filter
3. **Gunakan CTEs (Common Table Expressions)** untuk readability
4. **Monitor dan log ETL processes** untuk production environments
5. **Test queries dengan sample data** sebelum production deployment

### **Best Practices:**
- Konsistensi dalam naming conventions
- Dokumentasi query untuk maintainability  
- Version control untuk query scripts
- Regular performance monitoring
- Data quality validation di setiap stage

Dokumen ini dapat dijadikan reference guide untuk daily work dan continuous learning dalam bidang data analytics dan engineering.