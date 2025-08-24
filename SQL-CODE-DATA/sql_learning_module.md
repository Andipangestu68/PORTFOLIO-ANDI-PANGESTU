# Modul Pembelajaran SQL untuk Data Professional
*Panduan Lengkap SQL untuk Data Analyst, Data Scientist, dan Data Engineer*

## Daftar Isi
1. [SQL Dasar](#sql-dasar)
2. [Query untuk Data Analyst](#query-untuk-data-analyst)
3. [Query untuk Data Scientist](#query-untuk-data-scientist)
4. [Query untuk Data Engineer](#query-untuk-data-engineer)
5. [Optimasi dan Best Practices](#optimasi-dan-best-practices)

---

## SQL Dasar

### 1. SELECT - Mengambil Data
```sql
-- Mengambil semua kolom
SELECT * FROM tabel1;

-- Mengambil kolom tertentu
SELECT kolom1, kolom2, kolom3 FROM tabel1;

-- Menggunakan alias untuk nama kolom
SELECT 
    kolom1 AS nama_kolom1,
    kolom2 AS nama_kolom2
FROM tabel1;
```

### 2. WHERE - Filter Data
```sql
-- Filter dengan kondisi tunggal
SELECT * FROM tabel1 WHERE kolom1 = 'nilai_tertentu';

-- Filter dengan beberapa kondisi
SELECT * FROM tabel1 
WHERE kolom1 = 'nilai1' 
  AND kolom2 > 100 
  OR kolom3 IS NOT NULL;

-- Filter menggunakan IN
SELECT * FROM tabel1 WHERE kolom1 IN ('nilai1', 'nilai2', 'nilai3');

-- Filter menggunakan LIKE untuk pencarian pattern
SELECT * FROM tabel1 WHERE kolom1 LIKE '%kata%';
SELECT * FROM tabel1 WHERE kolom1 LIKE 'awalan%';
SELECT * FROM tabel1 WHERE kolom1 LIKE '%akhiran';
```

### 3. ORDER BY - Mengurutkan Data
```sql
-- Mengurutkan ascending (default)
SELECT * FROM tabel1 ORDER BY kolom1;

-- Mengurutkan descending
SELECT * FROM tabel1 ORDER BY kolom1 DESC;

-- Mengurutkan berdasarkan beberapa kolom
SELECT * FROM tabel1 ORDER BY kolom1 ASC, kolom2 DESC;
```

### 4. GROUP BY - Mengelompokkan Data
```sql
-- Mengelompokkan dan menghitung
SELECT kolom1, COUNT(*) AS jumlah
FROM tabel1 
GROUP BY kolom1;

-- Mengelompokkan dengan kondisi HAVING
SELECT kolom1, COUNT(*) AS jumlah
FROM tabel1 
GROUP BY kolom1
HAVING COUNT(*) > 5;
```

---

## Query untuk Data Analyst

### 1. Analisis Deskriptif
```sql
-- Statistik dasar untuk satu kolom numerik
SELECT 
    COUNT(*) AS total_record,
    MIN(kolom_numerik) AS nilai_minimum,
    MAX(kolom_numerik) AS nilai_maksimum,
    AVG(kolom_numerik) AS rata_rata,
    SUM(kolom_numerik) AS total_nilai,
    STDDEV(kolom_numerik) AS standar_deviasi
FROM tabel1;

-- Distribusi data kategorikal
SELECT 
    kolom_kategori,
    COUNT(*) AS frekuensi,
    ROUND(COUNT(*) * 100.0 / SUM(COUNT(*)) OVER(), 2) AS persentase
FROM tabel1
GROUP BY kolom_kategori
ORDER BY frekuensi DESC;
```

### 2. Analisis Trend dan Time Series
```sql
-- Trend penjualan bulanan
SELECT 
    DATE_FORMAT(kolom_tanggal, '%Y-%m') AS bulan,
    SUM(kolom_nilai) AS total_per_bulan,
    COUNT(*) AS jumlah_transaksi,
    AVG(kolom_nilai) AS rata_rata_per_transaksi
FROM tabel1
WHERE kolom_tanggal >= '2023-01-01'
GROUP BY DATE_FORMAT(kolom_tanggal, '%Y-%m')
ORDER BY bulan;

-- Perbandingan Year-over-Year
SELECT 
    YEAR(kolom_tanggal) AS tahun,
    MONTH(kolom_tanggal) AS bulan,
    SUM(kolom_nilai) AS total_bulanan,
    LAG(SUM(kolom_nilai), 12) OVER (ORDER BY YEAR(kolom_tanggal), MONTH(kolom_tanggal)) AS total_tahun_lalu,
    ROUND(
        (SUM(kolom_nilai) - LAG(SUM(kolom_nilai), 12) OVER (ORDER BY YEAR(kolom_tanggal), MONTH(kolom_tanggal))) 
        * 100.0 / LAG(SUM(kolom_nilai), 12) OVER (ORDER BY YEAR(kolom_tanggal), MONTH(kolom_tanggal)), 2
    ) AS pertumbuhan_persen
FROM tabel1
GROUP BY YEAR(kolom_tanggal), MONTH(kolom_tanggal)
ORDER BY tahun, bulan;
```

### 3. Cohort Analysis
```sql
-- Analisis kohort sederhana
WITH cohort_data AS (
    SELECT 
        id_customer,
        DATE_FORMAT(MIN(kolom_tanggal), '%Y-%m') AS cohort_month,
        DATE_FORMAT(kolom_tanggal, '%Y-%m') AS transaction_month
    FROM tabel1
    GROUP BY id_customer, DATE_FORMAT(kolom_tanggal, '%Y-%m')
)
SELECT 
    cohort_month,
    transaction_month,
    COUNT(DISTINCT id_customer) AS jumlah_customer_aktif
FROM cohort_data
GROUP BY cohort_month, transaction_month
ORDER BY cohort_month, transaction_month;
```

### 4. RFM Analysis (Recency, Frequency, Monetary)
```sql
-- Analisis RFM untuk segmentasi customer
WITH rfm_data AS (
    SELECT 
        id_customer,
        DATEDIFF(CURRENT_DATE, MAX(kolom_tanggal)) AS recency,
        COUNT(*) AS frequency,
        SUM(kolom_nilai) AS monetary
    FROM tabel1
    GROUP BY id_customer
),
rfm_scores AS (
    SELECT *,
        NTILE(5) OVER (ORDER BY recency DESC) AS r_score,
        NTILE(5) OVER (ORDER BY frequency) AS f_score,
        NTILE(5) OVER (ORDER BY monetary) AS m_score
    FROM rfm_data
)
SELECT 
    CONCAT(r_score, f_score, m_score) AS rfm_score,
    COUNT(*) AS jumlah_customer,
    AVG(recency) AS rata_rata_recency,
    AVG(frequency) AS rata_rata_frequency,
    AVG(monetary) AS rata_rata_monetary
FROM rfm_scores
GROUP BY CONCAT(r_score, f_score, m_score)
ORDER BY rfm_score;
```

### 5. Window Functions untuk Ranking
```sql
-- Ranking dan percentile
SELECT 
    kolom1,
    kolom_nilai,
    RANK() OVER (ORDER BY kolom_nilai DESC) AS ranking,
    DENSE_RANK() OVER (ORDER BY kolom_nilai DESC) AS dense_ranking,
    ROW_NUMBER() OVER (ORDER BY kolom_nilai DESC) AS row_number,
    PERCENT_RANK() OVER (ORDER BY kolom_nilai) AS percentile_rank,
    NTILE(10) OVER (ORDER BY kolom_nilai) AS decile
FROM tabel1;

-- Running total
SELECT 
    kolom_tanggal,
    kolom_nilai,
    SUM(kolom_nilai) OVER (ORDER BY kolom_tanggal ROWS UNBOUNDED PRECEDING) AS running_total,
    AVG(kolom_nilai) OVER (ORDER BY kolom_tanggal ROWS BETWEEN 6 PRECEDING AND CURRENT ROW) AS moving_average_7_days
FROM tabel1
ORDER BY kolom_tanggal;
```

---

## Query untuk Data Scientist

### 1. Data Preparation dan Cleaning
```sql
-- Deteksi missing values
SELECT 
    'kolom1' AS nama_kolom,
    COUNT(*) AS total_records,
    COUNT(kolom1) AS non_null_count,
    COUNT(*) - COUNT(kolom1) AS null_count,
    ROUND((COUNT(*) - COUNT(kolom1)) * 100.0 / COUNT(*), 2) AS null_percentage
FROM tabel1
UNION ALL
SELECT 
    'kolom2' AS nama_kolom,
    COUNT(*) AS total_records,
    COUNT(kolom2) AS non_null_count,
    COUNT(*) - COUNT(kolom2) AS null_count,
    ROUND((COUNT(*) - COUNT(kolom2)) * 100.0 / COUNT(*), 2) AS null_percentage
FROM tabel1;

-- Deteksi outliers menggunakan IQR
WITH quartiles AS (
    SELECT 
        PERCENTILE_CONT(0.25) WITHIN GROUP (ORDER BY kolom_numerik) AS q1,
        PERCENTILE_CONT(0.75) WITHIN GROUP (ORDER BY kolom_numerik) AS q3,
        PERCENTILE_CONT(0.75) WITHIN GROUP (ORDER BY kolom_numerik) - 
        PERCENTILE_CONT(0.25) WITHIN GROUP (ORDER BY kolom_numerik) AS iqr
    FROM tabel1
)
SELECT 
    t1.*,
    CASE 
        WHEN t1.kolom_numerik < (q.q1 - 1.5 * q.iqr) OR 
             t1.kolom_numerik > (q.q3 + 1.5 * q.iqr) 
        THEN 'Outlier' 
        ELSE 'Normal' 
    END AS outlier_flag
FROM tabel1 t1
CROSS JOIN quartiles q;
```

### 2. Feature Engineering
```sql
-- Binning numerical variables
SELECT *,
    CASE 
        WHEN kolom_numerik < 25 THEN 'Low'
        WHEN kolom_numerik BETWEEN 25 AND 75 THEN 'Medium'
        ELSE 'High'
    END AS kolom_numerik_binned,
    
    -- Log transformation
    LN(kolom_numerik + 1) AS kolom_numerik_log,
    
    -- Normalization (z-score)
    (kolom_numerik - AVG(kolom_numerik) OVER()) / STDDEV(kolom_numerik) OVER() AS kolom_numerik_zscore,
    
    -- Min-Max scaling
    (kolom_numerik - MIN(kolom_numerik) OVER()) / 
    (MAX(kolom_numerik) OVER() - MIN(kolom_numerik) OVER()) AS kolom_numerik_minmax
FROM tabel1;

-- Time-based features
SELECT *,
    EXTRACT(YEAR FROM kolom_tanggal) AS tahun,
    EXTRACT(MONTH FROM kolom_tanggal) AS bulan,
    EXTRACT(DAY FROM kolom_tanggal) AS hari,
    EXTRACT(DOW FROM kolom_tanggal) AS hari_dalam_minggu,
    EXTRACT(WEEK FROM kolom_tanggal) AS minggu_dalam_tahun,
    CASE 
        WHEN EXTRACT(DOW FROM kolom_tanggal) IN (0, 6) THEN 'Weekend'
        ELSE 'Weekday'
    END AS weekend_flag
FROM tabel1;
```

### 3. Correlation Analysis
```sql
-- Korelasi sederhana antara dua variabel
WITH stats AS (
    SELECT 
        AVG(kolom1) AS mean_x,
        AVG(kolom2) AS mean_y,
        STDDEV(kolom1) AS std_x,
        STDDEV(kolom2) AS std_y,
        COUNT(*) AS n
    FROM tabel1
)
SELECT 
    SUM((kolom1 - mean_x) * (kolom2 - mean_y)) / ((n - 1) * std_x * std_y) AS correlation
FROM tabel1
CROSS JOIN stats;
```

### 4. Sampling Techniques
```sql
-- Random sampling
SELECT * FROM tabel1 
ORDER BY RANDOM() 
LIMIT 1000;

-- Stratified sampling
WITH stratified_sample AS (
    SELECT *,
        ROW_NUMBER() OVER (PARTITION BY kolom_kategori ORDER BY RANDOM()) AS rn
    FROM tabel1
)
SELECT * FROM stratified_sample
WHERE rn <= 100; -- 100 samples per kategori

-- Train-test split
SELECT *,
    CASE 
        WHEN RANDOM() < 0.8 THEN 'train'
        ELSE 'test'
    END AS dataset_split
FROM tabel1;
```

---

## Query untuk Data Engineer

### 1. Data Quality Checks
```sql
-- Comprehensive data quality report
WITH quality_checks AS (
    SELECT 
        'tabel1' AS table_name,
        'kolom1' AS column_name,
        'string' AS data_type,
        COUNT(*) AS total_records,
        COUNT(kolom1) AS non_null_records,
        COUNT(DISTINCT kolom1) AS distinct_values,
        MIN(LENGTH(kolom1)) AS min_length,
        MAX(LENGTH(kolom1)) AS max_length
    FROM tabel1
    
    UNION ALL
    
    SELECT 
        'tabel1' AS table_name,
        'kolom2' AS column_name,
        'numeric' AS data_type,
        COUNT(*) AS total_records,
        COUNT(kolom2) AS non_null_records,
        COUNT(DISTINCT kolom2) AS distinct_values,
        NULL AS min_length,
        NULL AS max_length
    FROM tabel1
)
SELECT *,
    ROUND((non_null_records * 100.0 / total_records), 2) AS completeness_pct,
    ROUND((distinct_values * 100.0 / total_records), 2) AS uniqueness_pct
FROM quality_checks;

-- Duplicate detection
SELECT 
    kolom1, kolom2, kolom3,
    COUNT(*) AS duplicate_count
FROM tabel1
GROUP BY kolom1, kolom2, kolom3
HAVING COUNT(*) > 1
ORDER BY duplicate_count DESC;
```

### 2. ETL Operations
```sql
-- Upsert operation (Insert or Update)
MERGE INTO tabel_target AS target
USING tabel_source AS source
ON target.id = source.id
WHEN MATCHED THEN
    UPDATE SET 
        kolom1 = source.kolom1,
        kolom2 = source.kolom2,
        updated_at = CURRENT_TIMESTAMP
WHEN NOT MATCHED THEN
    INSERT (id, kolom1, kolom2, created_at, updated_at)
    VALUES (source.id, source.kolom1, source.kolom2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Slowly Changing Dimension (SCD) Type 2
INSERT INTO dim_tabel (
    business_key,
    kolom1,
    kolom2,
    valid_from,
    valid_to,
    is_current
)
SELECT 
    business_key,
    kolom1,
    kolom2,
    CURRENT_DATE AS valid_from,
    '9999-12-31' AS valid_to,
    1 AS is_current
FROM staging_tabel s
WHERE NOT EXISTS (
    SELECT 1 FROM dim_tabel d 
    WHERE d.business_key = s.business_key 
    AND d.is_current = 1
    AND d.kolom1 = s.kolom1 
    AND d.kolom2 = s.kolom2
);
```

### 3. Performance Monitoring
```sql
-- Table size and row count monitoring
SELECT 
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS size,
    pg_stat_get_tuples_returned(c.oid) AS row_count,
    pg_stat_get_tuples_inserted(c.oid) AS inserts,
    pg_stat_get_tuples_updated(c.oid) AS updates,
    pg_stat_get_tuples_deleted(c.oid) AS deletes
FROM pg_tables t
JOIN pg_class c ON c.relname = t.tablename
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;

-- Query performance analysis
SELECT 
    query,
    calls,
    total_time,
    mean_time,
    min_time,
    max_time,
    stddev_time
FROM pg_stat_statements
WHERE query LIKE '%tabel1%'
ORDER BY total_time DESC;
```

### 4. Data Lineage dan Audit
```sql
-- Change data capture pattern
CREATE TABLE audit_tabel1 (
    audit_id SERIAL PRIMARY KEY,
    operation_type VARCHAR(10), -- INSERT, UPDATE, DELETE
    record_id INT,
    old_values JSONB,
    new_values JSONB,
    changed_by VARCHAR(100),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Trigger untuk audit trail
CREATE OR REPLACE FUNCTION audit_trigger_function()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        INSERT INTO audit_tabel1 (operation_type, record_id, new_values, changed_by)
        VALUES ('INSERT', NEW.id, row_to_json(NEW), current_user);
        RETURN NEW;
    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO audit_tabel1 (operation_type, record_id, old_values, new_values, changed_by)
        VALUES ('UPDATE', NEW.id, row_to_json(OLD), row_to_json(NEW), current_user);
        RETURN NEW;
    ELSIF TG_OP = 'DELETE' THEN
        INSERT INTO audit_tabel1 (operation_type, record_id, old_values, changed_by)
        VALUES ('DELETE', OLD.id, row_to_json(OLD), current_user);
        RETURN OLD;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;
```

### 5. Partitioning dan Sharding
```sql
-- Time-based partitioning
CREATE TABLE tabel1_partitioned (
    id SERIAL,
    kolom_tanggal DATE NOT NULL,
    kolom1 VARCHAR(100),
    kolom2 INTEGER
) PARTITION BY RANGE (kolom_tanggal);

-- Create monthly partitions
CREATE TABLE tabel1_y2024m01 PARTITION OF tabel1_partitioned
FOR VALUES FROM ('2024-01-01') TO ('2024-02-01');

CREATE TABLE tabel1_y2024m02 PARTITION OF tabel1_partitioned
FOR VALUES FROM ('2024-02-01') TO ('2024-03-01');

-- Hash-based partitioning
CREATE TABLE tabel2_partitioned (
    id SERIAL,
    kolom1 VARCHAR(100),
    kolom2 INTEGER
) PARTITION BY HASH (id);

CREATE TABLE tabel2_p0 PARTITION OF tabel2_partitioned
FOR VALUES WITH (MODULUS 4, REMAINDER 0);

CREATE TABLE tabel2_p1 PARTITION OF tabel2_partitioned
FOR VALUES WITH (MODULUS 4, REMAINDER 1);
```

---

## Optimasi dan Best Practices

### 1. Index Optimization
```sql
-- Composite index untuk query yang sering digunakan
CREATE INDEX idx_tabel1_kolom1_kolom2 ON tabel1 (kolom1, kolom2);

-- Partial index dengan kondisi
CREATE INDEX idx_tabel1_active_records ON tabel1 (kolom_tanggal)
WHERE status = 'active';

-- Index untuk text search
CREATE INDEX idx_tabel1_text_search ON tabel1 
USING gin(to_tsvector('english', kolom_text));

-- Analyze index usage
SELECT 
    schemaname,
    tablename,
    indexname,
    idx_scan AS index_scans,
    idx_tup_read AS tuples_read,
    idx_tup_fetch AS tuples_fetched
FROM pg_stat_user_indexes
ORDER BY idx_scan DESC;
```

### 2. Query Optimization Techniques
```sql
-- Menggunakan EXPLAIN ANALYZE untuk performance analysis
EXPLAIN ANALYZE
SELECT t1.kolom1, t2.kolom2
FROM tabel1 t1
JOIN tabel2 t2 ON t1.id = t2.foreign_key
WHERE t1.kolom3 > 100;

-- Optimized subquery dengan EXISTS
SELECT * FROM tabel1 t1
WHERE EXISTS (
    SELECT 1 FROM tabel2 t2 
    WHERE t2.foreign_key = t1.id AND t2.status = 'active'
);

-- Window function optimization
SELECT 
    kolom1,
    kolom2,
    -- More efficient than correlated subquery
    SUM(kolom2) OVER (PARTITION BY kolom1) AS total_per_group,
    ROW_NUMBER() OVER (PARTITION BY kolom1 ORDER BY kolom2 DESC) AS rank_in_group
FROM tabel1;
```

### 3. Memory dan Storage Optimization
```sql
-- Vacuum dan analyze untuk maintenance
VACUUM ANALYZE tabel1;

-- Reindex untuk fragmentation
REINDEX TABLE tabel1;

-- Compression dengan columnar storage (jika tersedia)
CREATE TABLE tabel1_compressed (
    LIKE tabel1 INCLUDING ALL
) WITH (
    fillfactor = 100,
    compression = 'lz4'
);
```

### 4. Monitoring Queries
```sql
-- Long running queries
SELECT 
    pid,
    now() - pg_stat_activity.query_start AS duration,
    query 
FROM pg_stat_activity 
WHERE (now() - pg_stat_activity.query_start) > interval '5 minutes'
AND state = 'active';

-- Lock monitoring
SELECT 
    blocked_locks.pid AS blocked_pid,
    blocked_activity.usename AS blocked_user,
    blocking_locks.pid AS blocking_pid,
    blocking_activity.usename AS blocking_user,
    blocked_activity.query AS blocked_statement,
    blocking_activity.query AS blocking_statement
FROM pg_catalog.pg_locks blocked_locks
JOIN pg_catalog.pg_stat_activity blocked_activity ON blocked_activity.pid = blocked_locks.pid
JOIN pg_catalog.pg_locks blocking_locks ON blocking_locks.locktype = blocked_locks.locktype
JOIN pg_catalog.pg_stat_activity blocking_activity ON blocking_activity.pid = blocking_locks.pid
WHERE NOT blocked_locks.granted;
```

---

## Kesimpulan

Modul ini mencakup query SQL yang dibutuhkan untuk berbagai peran data professional:

- **Data Analyst**: Fokus pada analisis deskriptif, trend analysis, dan business intelligence
- **Data Scientist**: Emphasis pada data preparation, feature engineering, dan statistical analysis
- **Data Engineer**: Concentration pada ETL processes, data quality, dan system optimization

### Tips Penggunaan:
1. **Mulai dari dasar** - Pastikan memahami SQL fundamental sebelum ke advanced topics
2. **Practice with real data** - Gunakan dataset nyata untuk latihan
3. **Understand your data** - Selalu eksplorasi data sebelum menulis query kompleks
4. **Performance matters** - Selalu pertimbangkan performance impact dari query yang ditulis
5. **Documentation** - Dokumentasikan query kompleks untuk maintainability

### Next Steps:
- Pelajari database-specific functions (PostgreSQL, MySQL, SQL Server, dll.)
- Eksplorasi advanced topics seperti recursive CTEs, arrays, dan JSON operations
- Integrasikan dengan tools lain seperti Python, R, atau BI tools
- Pelajari tentang distributed systems dan big data technologies (Spark SQL, etc.)