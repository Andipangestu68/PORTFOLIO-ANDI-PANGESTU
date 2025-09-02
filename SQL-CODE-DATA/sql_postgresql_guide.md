**Penjelasan**: RFM Analysis adalah framework standard untuk customer segmentation. Data Scientist menggunakan segmentasi ini sebagai input untuk personalization algorithm dan targeted marketing campaign. NTILE function membagi data menjadi percentile yang equal.

### 9.3 User Journey Analysis

```sql
-- Analisis customer journey dan funnel conversion
WITH user_funnel AS (
    SELECT 
        id_pelanggan,
        -- Apakah user pernah login
        MAX(CASE WHEN jenis_aktivitas = 'login' THEN 1 ELSE 0 END) as has_login,
        
        -- Apakah user pernah view product
        MAX(CASE WHEN jenis_aktivitas = 'view_product' THEN 1 ELSE 0 END) as has_viewed,
        
        -- Apakah user pernah purchase
        MAX(CASE WHEN jenis_aktivitas = 'purchase' THEN 1 ELSE 0 END) as has_purchased,
        
        -- Total durasi sesi
        SUM(durasi_sesi) as total_session_duration
    FROM tabel_aktivitas
    GROUP BY id_pelanggan                 -- 5. Group by minimal necessary columns
    HAVING COUNT(*) > 1                   -- 6. Filter aggregated data dengan HAVING
)
SELECT 
    p.nama,
    ad.order_count,
    ad.total_revenue,
    ROUND(ad.total_revenue / ad.order_count, 2) as avg_order_value
FROM aggregated_data ad
INNER JOIN tabel_pelanggan p ON ad.id_pelanggan = p.id_pelanggan  -- 7. JOIN setelah aggregation
ORDER BY ad.total_revenue DESC           -- 8. Sort di akhir
LIMIT 100;                              -- 9. Limit hasil untuk performance
)
SELECT 
    'Total Users' as stage,
    COUNT(*) as users,
    100.0 as conversion_rate
FROM user_funnel

UNION ALL

SELECT 
    'Logged In',
    SUM(has_login),
    ROUND(SUM(has_login) * 100.0 / COUNT(*), 2)
FROM user_funnel

UNION ALL

SELECT 
    'Viewed Products',
    SUM(has_viewed),
    ROUND(SUM(has_viewed) * 100.0 / COUNT(*), 2)
FROM user_funnel

UNION ALL

SELECT 
    'Made Purchase',
    SUM(has_purchased),
    ROUND(SUM(has_purchased) * 100.0 / COUNT(*), 2)
FROM user_funnel;

-- Session analysis per user
SELECT 
    id_pelanggan,
    COUNT(*) as total_sessions,                    -- Total sesi
    AVG(durasi_sesi) as avg_session_duration,      -- Rata-rata durasi sesi
    MAX(durasi_sesi) as max_session_duration,      -- Sesi terlama
    
    -- Kategorisasi engagement level
    CASE 
        WHEN AVG(durasi_sesi) > 60 THEN 'High Engagement'
        WHEN AVG(durasi_sesi) > 30 THEN 'Medium Engagement'
        ELSE 'Low Engagement'
    END as engagement_level
FROM tabel_aktivitas
GROUP BY id_pelanggan
HAVING COUNT(*) >= 3                              -- Minimal 3 sesi
ORDER BY avg_session_duration DESC;
```

**Penjelasan**: Funnel analysis membantu identify bottleneck dalam customer journey. Data Analyst menggunakan ini untuk optimizing conversion rate dan understanding user behavior pattern.

---

## 10. Query Lanjutan untuk Data Science

### 10.1 Statistical Analysis

```sql
-- Statistical summary untuk analisis data
SELECT 
    kategori,
    COUNT(*) as sample_size,                       -- Ukuran sampel
    AVG(harga) as mean_price,                      -- Mean
    PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY harga) as median_price,  -- Median
    STDDEV(harga) as std_deviation,                -- Standard deviation
    VAR_POP(harga) as variance,                    -- Variance
    MIN(harga) as min_price,
    MAX(harga) as max_price,
    
    -- Quartiles
    PERCENTILE_CONT(0.25) WITHIN GROUP (ORDER BY harga) as q1,
    PERCENTILE_CONT(0.75) WITHIN GROUP (ORDER BY harga) as q3,
    
    -- Detect outliers using IQR method
    PERCENTILE_CONT(0.75) WITHIN GROUP (ORDER BY harga) - 
    PERCENTILE_CONT(0.25) WITHIN GROUP (ORDER BY harga) as iqr
FROM tabel_produk
GROUP BY kategori
ORDER BY mean_price DESC;

-- Identifikasi outliers
WITH stats AS (
    SELECT 
        id_produk,
        nama_produk,
        harga,
        PERCENTILE_CONT(0.25) WITHIN GROUP (ORDER BY harga) OVER () as q1,
        PERCENTILE_CONT(0.75) WITHIN GROUP (ORDER BY harga) OVER () as q3
    FROM tabel_produk
),
outlier_bounds AS (
    SELECT 
        *,
        q1 - 1.5 * (q3 - q1) as lower_bound,       -- Batas bawah outlier
        q3 + 1.5 * (q3 - q1) as upper_bound        -- Batas atas outlier
    FROM stats
)
SELECT 
    nama_produk,
    harga,
    CASE 
        WHEN harga < lower_bound THEN 'Outlier Rendah'
        WHEN harga > upper_bound THEN 'Outlier Tinggi'
        ELSE 'Normal'
    END as outlier_status
FROM outlier_bounds
WHERE harga < lower_bound OR harga > upper_bound;   -- Hanya tampilkan outlier
```

**Penjelasan**: Statistical analysis essential untuk data exploration dan feature engineering. Data Scientist menggunakan percentile dan IQR untuk outlier detection sebelum training ML models.

### 10.2 Time Series Analysis

```sql
-- Decomposing time series: trend, seasonality
WITH daily_sales AS (
    SELECT 
        DATE(tanggal_transaksi) as tanggal,
        SUM(total_harga) as daily_revenue,
        COUNT(*) as daily_transactions
    FROM tabel_transaksi
    WHERE status = 'completed'
      AND tanggal_transaksi >= CURRENT_DATE - INTERVAL '1 year'
    GROUP BY DATE(tanggal_transaksi)
),
sales_with_moving_avg AS (
    SELECT 
        tanggal,
        daily_revenue,
        daily_transactions,
        
        -- Moving average 7 hari (trend)
        AVG(daily_revenue) OVER (
            ORDER BY tanggal 
            ROWS BETWEEN 6 PRECEDING AND CURRENT ROW
        ) as ma_7_day,
        
        -- Moving average 30 hari (trend jangka panjang)
        AVG(daily_revenue) OVER (
            ORDER BY tanggal 
            ROWS BETWEEN 29 PRECEDING AND CURRENT ROW
        ) as ma_30_day,
        
        -- Lag untuk growth calculation
        LAG(daily_revenue, 7) OVER (ORDER BY tanggal) as revenue_7_days_ago
    FROM daily_sales
)
SELECT 
    tanggal,
    daily_revenue,
    ma_7_day,
    ma_30_day,
    
    -- Week-over-week growth
    ROUND(
        (daily_revenue - revenue_7_days_ago) * 100.0 / 
        NULLIF(revenue_7_days_ago, 0), 2
    ) as wow_growth_pct,
    
    -- Seasonality indicator (day of week effect)
    EXTRACT(DOW FROM tanggal) as day_of_week,      -- 0=Sunday, 6=Saturday
    TO_CHAR(tanggal, 'Day') as day_name,
    
    -- Detrended value (actual vs trend)
    daily_revenue - ma_30_day as detrended_value
FROM sales_with_moving_avg
WHERE tanggal >= CURRENT_DATE - INTERVAL '3 months'  -- 3 bulan terakhir
ORDER BY tanggal;
```

**Penjelasan**: Time series decomposition penting untuk forecasting dan understanding business cycle. Data Scientist menggunakan moving averages untuk noise reduction dan trend identification.

### 10.3 Customer Lifetime Value (CLV)

```sql
-- Menghitung Customer Lifetime Value
WITH customer_metrics AS (
    SELECT 
        p.id_pelanggan,
        p.nama,
        p.tanggal_daftar,
        
        -- Recency
        CURRENT_DATE - MAX(DATE(t.tanggal_transaksi)) as days_since_last_purchase,
        
        -- Frequency
        COUNT(t.id_transaksi) as total_orders,
        
        -- Monetary
        SUM(t.total_harga) as total_revenue,
        AVG(t.total_harga) as avg_order_value,
        
        -- Customer age
        CURRENT_DATE - p.tanggal_daftar as customer_age_days,
        
        -- Purchase frequency (orders per day)
        COUNT(t.id_transaksi)::FLOAT / 
        NULLIF(CURRENT_DATE - p.tanggal_daftar, 0) as purchase_frequency_per_day
    FROM tabel_pelanggan p
    LEFT JOIN tabel_transaksi t ON p.id_pelanggan = t.id_pelanggan
        AND t.status = 'completed'
    GROUP BY p.id_pelanggan, p.nama, p.tanggal_daftar
),
clv_calculation AS (
    SELECT 
        *,
        -- Predicted lifespan (inverse of churn rate estimation)
        CASE 
            WHEN days_since_last_purchase <= 30 THEN 365  -- Active customer: 1 year
            WHEN days_since_last_purchase <= 90 THEN 180  -- Moderately active: 6 months
            ELSE 90                                        -- At risk: 3 months
        END as predicted_lifespan_days,
        
        -- CLV calculation: AOV * Purchase Frequency * Predicted Lifespan
        avg_order_value * purchase_frequency_per_day * 
        CASE 
            WHEN days_since_last_purchase <= 30 THEN 365
            WHEN days_since_last_purchase <= 90 THEN 180
            ELSE 90
        END as estimated_clv
    FROM customer_metrics
    WHERE total_orders > 0                         -- Hanya customer yang pernah beli
)
SELECT 
    nama,
    total_orders,
    total_revenue,
    ROUND(avg_order_value, 2) as avg_order_value,
    days_since_last_purchase,
    ROUND(estimated_clv, 2) as estimated_clv,
    
    -- CLV Segmentation
    CASE 
        WHEN estimated_clv > 1000000 THEN 'High Value'
        WHEN estimated_clv > 500000 THEN 'Medium Value'
        ELSE 'Low Value'
    END as clv_segment
FROM clv_calculation
ORDER BY estimated_clv DESC
LIMIT 20;
```

**Penjelasan**: CLV calculation fundamental untuk marketing budget allocation dan customer acquisition strategy. Data Scientist menggunakan ini untuk building predictive models dan optimizing customer portfolio.

---

## 11. Advanced Analytics Patterns

### 11.1 Churn Prediction Data Preparation

```sql
-- Feature engineering untuk churn prediction
WITH customer_features AS (
    SELECT 
        p.id_pelanggan,
        p.kategori_pelanggan,
        
        -- Demographic features
        EXTRACT(DAY FROM CURRENT_DATE - p.tanggal_daftar) as tenure_days,
        
        -- Behavioral features dari transaksi
        COUNT(t.id_transaksi) as total_transactions,
        COALESCE(SUM(t.total_harga), 0) as total_spent,
        COALESCE(AVG(t.total_harga), 0) as avg_transaction_value,
        
        -- Recent activity features
        MAX(t.tanggal_transaksi) as last_transaction_date,
        CURRENT_DATE - MAX(DATE(t.tanggal_transaksi)) as days_since_last_transaction,
        
        -- Transaction frequency features
        COUNT(CASE WHEN t.tanggal_transaksi >= CURRENT_DATE - INTERVAL '30 days' 
              THEN 1 END) as transactions_last_30_days,
        COUNT(CASE WHEN t.tanggal_transaksi >= CURRENT_DATE - INTERVAL '90 days' 
              THEN 1 END) as transactions_last_90_days,
        
        -- Variety of products purchased
        COUNT(DISTINCT t.id_produk) as unique_products_purchased
    FROM tabel_pelanggan p
    LEFT JOIN tabel_transaksi t ON p.id_pelanggan = t.id_pelanggan
        AND t.status = 'completed'
    GROUP BY p.id_pelanggan, p.kategori_pelanggan, p.tanggal_daftar
),
engagement_features AS (
    SELECT 
        a.id_pelanggan,
        COUNT(*) as total_sessions,
        AVG(a.durasi_sesi) as avg_session_duration,
        MAX(a.tanggal_aktivitas) as last_activity_date,
        COUNT(CASE WHEN a.jenis_aktivitas = 'login' THEN 1 END) as login_count,
        COUNT(CASE WHEN a.jenis_aktivitas = 'view_product' THEN 1 END) as product_view_count
    FROM tabel_aktivitas a
    GROUP BY a.id_pelanggan
)
SELECT 
    cf.*,
    COALESCE(ef.total_sessions, 0) as total_sessions,
    COALESCE(ef.avg_session_duration, 0) as avg_session_duration,
    COALESCE(ef.login_count, 0) as login_count,
    COALESCE(ef.product_view_count, 0) as product_view_count,
    
    -- Churn label (business rule: no activity in 90 days = churned)
    CASE 
        WHEN GREATEST(
            COALESCE(days_since_last_transaction, 999),
            CURRENT_DATE - COALESCE(ef.last_activity_date, '1900-01-01')
        ) > 90 THEN 1 
        ELSE 0 
    END as is_churned,
    
    -- Risk score calculation
    CASE 
        WHEN days_since_last_transaction > 60 THEN 3
        WHEN days_since_last_transaction > 30 THEN 2
        WHEN days_since_last_transaction > 14 THEN 1
        ELSE 0
    END as churn_risk_score
FROM customer_features cf
LEFT JOIN engagement_features ef ON cf.id_pelanggan = ef.id_pelanggan
ORDER BY churn_risk_score DESC, total_spent DESC;
```

**Penjelasan**: Feature engineering untuk churn prediction adalah use case nyata Data Scientist. Query ini menghasilkan dataset siap pakai untuk machine learning model dengan features yang sudah terbukti predictive power-nya.

### 11.2 Market Basket Analysis

```sql
-- Analisis produk yang sering dibeli bersamaan
WITH transaction_products AS (
    SELECT 
        t1.id_transaksi,
        t1.id_pelanggan,
        t1.id_produk as produk_a,
        t2.id_produk as produk_b,
        p1.nama_produk as nama_produk_a,
        p2.nama_produk as nama_produk_b
    FROM tabel_transaksi t1
    INNER JOIN tabel_transaksi t2 
        ON t1.id_pelanggan = t2.id_pelanggan       -- Same customer
        AND t1.id_transaksi = t2.id_transaksi      -- Same transaction
        AND t1.id_produk < t2.id_produk            -- Avoid duplicate pairs
    INNER JOIN tabel_produk p1 ON t1.id_produk = p1.id_produk
    INNER JOIN tabel_produk p2 ON t2.id_produk = p2.id_produk
    WHERE t1.status = 'completed'
),
product_associations AS (
    SELECT 
        produk_a,
        produk_b,
        nama_produk_a,
        nama_produk_b,
        COUNT(*) as co_occurrence_count,           -- Frekuensi dibeli bersamaan
        
        -- Support: proporsi transaksi yang mengandung kedua produk
        COUNT(*) * 100.0 / (
            SELECT COUNT(DISTINCT id_transaksi) 
            FROM tabel_transaksi 
            WHERE status = 'completed'
        ) as support_percentage
    FROM transaction_products
    GROUP BY produk_a, produk_b, nama_produk_a, nama_produk_b
    HAVING COUNT(*) >= 5                          -- Minimal 5x co-occurrence
)
SELECT 
    nama_produk_a,
    nama_produk_b,
    co_occurrence_count,
    ROUND(support_percentage, 3) as support_pct,
    
    -- Confidence: P(B|A) = Support(A,B) / Support(A)
    ROUND(
        co_occurrence_count * 100.0 / (
            SELECT COUNT(*) 
            FROM tabel_transaksi 
            WHERE id_produk = pa.produk_a AND status = 'completed'
        ), 2
    ) as confidence_a_to_b
FROM product_associations pa
ORDER BY co_occurrence_count DESC, confidence_a_to_b DESC
LIMIT 15;
```

**Penjelasan**: Market basket analysis digunakan untuk recommendation system dan cross-selling strategy. Data Scientist menggunakan support dan confidence metrics untuk measuring association strength antar produk.

---

## 12. Data Quality dan Monitoring

### 12.1 Data Quality Checks

```sql
-- Comprehensive data quality assessment
WITH quality_checks AS (
    -- Check 1: Missing values
    SELECT 
        'tabel_pelanggan' as table_name,
        'completeness' as check_type,
        ROUND(
            (COUNT(*) - COUNT(nama)) * 100.0 / COUNT(*), 2
        ) as missing_percentage,
        COUNT(*) - COUNT(nama) as missing_count
    FROM tabel_pelanggan
    
    UNION ALL
    
    -- Check 2: Duplicate emails
    SELECT 
        'tabel_pelanggan',
        'uniqueness',
        ROUND(
            (COUNT(*) - COUNT(DISTINCT email)) * 100.0 / COUNT(*), 2
        ),
        COUNT(*) - COUNT(DISTINCT email)
    FROM tabel_pelanggan
    
    UNION ALL
    
    -- Check 3: Invalid email format
    SELECT 
        'tabel_pelanggan',
        'validity',
        ROUND(
            COUNT(CASE WHEN email NOT LIKE '%@%.%' THEN 1 END) * 100.0 / COUNT(*), 2
        ),
        COUNT(CASE WHEN email NOT LIKE '%@%.%' THEN 1 END)
    FROM tabel_pelanggan
    WHERE email IS NOT NULL
    
    UNION ALL
    
    -- Check 4: Negative prices
    SELECT 
        'tabel_produk',
        'validity',
        ROUND(COUNT(CASE WHEN harga < 0 THEN 1 END) * 100.0 / COUNT(*), 2),
        COUNT(CASE WHEN harga < 0 THEN 1 END)
    FROM tabel_produk
    
    UNION ALL
    
    -- Check 5: Future transactions
    SELECT 
        'tabel_transaksi',
        'validity',
        ROUND(
            COUNT(CASE WHEN tanggal_transaksi > CURRENT_TIMESTAMP THEN 1 END) * 100.0 / COUNT(*), 2
        ),
        COUNT(CASE WHEN tanggal_transaksi > CURRENT_TIMESTAMP THEN 1 END)
    FROM tabel_transaksi
)
SELECT 
    table_name,
    check_type,
    missing_percentage as issue_percentage,
    missing_count as issue_count,
    CASE 
        WHEN missing_percentage > 10 THEN 'CRITICAL'
        WHEN missing_percentage > 5 THEN 'WARNING'
        ELSE 'OK'
    END as status
FROM quality_checks
ORDER BY issue_percentage DESC;
```

**Penjelasan**: Data quality monitoring essential dalam production environment. Data Engineer menggunakan automated checks ini dalam ETL pipeline untuk ensuring data reliability sebelum analysis atau model training.

### 11.2 Performance Monitoring Queries

```sql
-- Monitor performa database dan identifikasi slow queries
SELECT 
    schemaname,
    tablename,
    attname as column_name,
    n_distinct as distinct_values,          -- Jumlah nilai unik
    correlation,                            -- Korelasi dengan physical storage
    most_common_vals as top_values,         -- Nilai yang paling sering muncul
    most_common_freqs as value_frequencies  -- Frekuensi nilai tersebut
FROM pg_stats 
WHERE schemaname = 'public'                -- Schema yang digunakan
  AND tablename IN ('tabel_pelanggan', 'tabel_produk', 'tabel_transaksi')
ORDER BY tablename, attname;

-- Analisis ukuran tabel dan indeks
SELECT 
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) as total_size,
    pg_size_pretty(pg_relation_size(schemaname||'.'||tablename)) as table_size,
    pg_size_pretty(
        pg_total_relation_size(schemaname||'.'||tablename) - 
        pg_relation_size(schemaname||'.'||tablename)
    ) as index_size
FROM pg_tables 
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;
```

**Penjelasan**: Database monitoring crucial untuk maintaining performance dalam production. Data Engineer menggunakan pg_stats untuk understanding data distribution dan optimizing query performance.

---

## 13. Optimasi Query dan Indexing

### 13.1 Membuat Indeks untuk Optimasi

```sql
-- Indeks untuk kolom yang sering digunakan dalam WHERE dan JOIN
CREATE INDEX idx_pelanggan_email ON tabel_pelanggan(email);          -- Unique lookups
CREATE INDEX idx_pelanggan_kota ON tabel_pelanggan(kota);            -- Geographic filtering
CREATE INDEX idx_transaksi_pelanggan ON tabel_transaksi(id_pelanggan); -- JOIN performance
CREATE INDEX idx_transaksi_tanggal ON tabel_transaksi(tanggal_transaksi); -- Date filtering
CREATE INDEX idx_transaksi_status ON tabel_transaksi(status);        -- Status filtering

-- Composite index untuk query kompleks
CREATE INDEX idx_transaksi_composite 
ON tabel_transaksi(id_pelanggan, tanggal_transaksi, status);         -- Multi-column index

-- Partial index untuk kondisi spesifik
CREATE INDEX idx_transaksi_completed 
ON tabel_transaksi(tanggal_transaksi) 
WHERE status = 'completed';              -- Hanya indeks untuk transaksi completed

-- Indeks untuk text search
CREATE INDEX idx_produk_nama_gin 
ON tabel_produk USING gin(to_tsvector('indonesian', nama_produk));   -- Full text search
```

**Penjelasan**: Proper indexing dapat meningkatkan query performance 10-100x. Data Engineer harus memahami trade-off antara query speed dan storage/update overhead. Composite index efektif untuk multi-column filtering.

### 13.2 Query Optimization Techniques

```sql
-- Query optimization: menggunakan EXISTS instead of IN untuk large datasets
SELECT p.nama, p.email
FROM tabel_pelanggan p
WHERE EXISTS (                            -- EXISTS lebih efisien dari IN
    SELECT 1 
    FROM tabel_transaksi t 
    WHERE t.id_pelanggan = p.id_pelanggan 
      AND t.status = 'completed'
      AND t.tanggal_transaksi >= '2024-01-01'
);

-- Menggunakan UNION ALL instead of UNION jika tidak perlu distinct
SELECT id_pelanggan, 'active' as status
FROM tabel_pelanggan 
WHERE tanggal_daftar >= CURRENT_DATE - INTERVAL '1 year'

UNION ALL                                 -- Lebih cepat karena tidak check duplicate

SELECT id_pelanggan, 'inactive' as status
FROM tabel_pelanggan 
WHERE tanggal_daftar < CURRENT_DATE - INTERVAL '1 year';

-- Optimized aggregation dengan filter pushdown
SELECT 
    kota,
    COUNT(*) as total_pelanggan,
    COUNT(*) FILTER (WHERE kategori_pelanggan = 'Premium') as premium_count,
    COUNT(*) FILTER (WHERE kategori_pelanggan = 'Regular') as regular_count,
    COUNT(*) FILTER (WHERE kategori_pelanggan = 'Basic') as basic_count
FROM tabel_pelanggan
WHERE tanggal_daftar >= '2023-01-01'      -- Filter di WHERE, bukan di subquery
GROUP BY kota
ORDER BY total_pelanggan DESC;
```

**Penjelasan**: Query optimization techniques penting ketika dealing dengan large datasets (millions of rows). Data Engineer menggunakan ini untuk maintaining response time dalam production systems.

---

## 14. Advanced Reporting Queries

### 14.1 Monthly Business Report

```sql
-- Laporan bulanan comprehensive
WITH monthly_metrics AS (
    SELECT 
        DATE_TRUNC('month', t.tanggal_transaksi) as bulan,
        
        -- Revenue metrics
        SUM(t.total_harga) as total_revenue,
        COUNT(t.id_transaksi) as total_transactions,
        COUNT(DISTINCT t.id_pelanggan) as unique_customers,
        AVG(t.total_harga) as avg_transaction_value,
        
        -- Customer metrics
        COUNT(DISTINCT CASE 
            WHEN p.tanggal_daftar >= DATE_TRUNC('month', t.tanggal_transaksi)
            THEN t.id_pelanggan 
        END) as new_customers,
        
        -- Product metrics
        COUNT(DISTINCT t.id_produk) as unique_products_sold,
        SUM(t.jumlah) as total_quantity_sold
    FROM tabel_transaksi t
    INNER JOIN tabel_pelanggan p ON t.id_pelanggan = p.id_pelanggan
    WHERE t.status = 'completed'
      AND t.tanggal_transaksi >= '2024-01-01'
    GROUP BY DATE_TRUNC('month', t.tanggal_transaksi)
)
SELECT 
    bulan,
    TO_CHAR(bulan, 'Month YYYY') as bulan_format,
    
    -- Current month metrics
    total_revenue,
    total_transactions,
    unique_customers,
    new_customers,
    ROUND(avg_transaction_value, 2) as avg_transaction_value,
    
    -- Month-over-month comparison
    LAG(total_revenue) OVER (ORDER BY bulan) as prev_month_revenue,
    ROUND(
        (total_revenue - LAG(total_revenue) OVER (ORDER BY bulan)) * 100.0 / 
        NULLIF(LAG(total_revenue) OVER (ORDER BY bulan), 0), 2
    ) as revenue_growth_pct,
    
    -- Customer retention
    ROUND(
        (unique_customers - new_customers) * 100.0 / 
        NULLIF(LAG(unique_customers) OVER (ORDER BY bulan), 0), 2
    ) as customer_retention_rate
FROM monthly_metrics
ORDER BY bulan;
```

**Penjelasan**: Monthly business report template yang siap pakai untuk executive dashboard. Data Analyst menggunakan pattern ini untuk automated reporting dan KPI tracking.

### 14.2 Cohort Retention Analysis

```sql
-- Detailed cohort retention analysis
WITH user_cohorts AS (
    SELECT 
        p.id_pelanggan,
        DATE_TRUNC('month', p.tanggal_daftar) as cohort_month,
        DATE_TRUNC('month', t.tanggal_transaksi) as transaction_month,
        
        -- Period number (0 = acquisition month, 1 = first month after, etc.)
        EXTRACT(
            MONTH FROM AGE(
                DATE_TRUNC('month', t.tanggal_transaksi),
                DATE_TRUNC('month', p.tanggal_daftar)
            )
        ) as period_number
    FROM tabel_pelanggan p
    INNER JOIN tabel_transaksi t ON p.id_pelanggan = t.id_pelanggan
    WHERE t.status = 'completed'
),
cohort_table AS (
    SELECT 
        cohort_month,
        period_number,
        COUNT(DISTINCT id_pelanggan) as customers
    FROM user_cohorts
    GROUP BY cohort_month, period_number
),
cohort_sizes AS (
    SELECT 
        cohort_month,
        customers as cohort_size
    FROM cohort_table
    WHERE period_number = 0               -- Ukuran cohort di bulan acquisition
)
SELECT 
    ct.cohort_month,
    cs.cohort_size,
    ct.period_number,
    ct.customers,
    ROUND(ct.customers * 100.0 / cs.cohort_size, 2) as retention_rate
FROM cohort_table ct
INNER JOIN cohort_sizes cs ON ct.cohort_month = cs.cohort_month
WHERE ct.period_number <= 12              -- Analisis 12 bulan pertama
ORDER BY ct.cohort_month, ct.period_number;
```

**Penjelasan**: Cohort retention analysis adalah gold standard untuk measuring product stickiness dan user engagement over time. Data Scientist menggunakan ini untuk understanding user lifecycle dan optimizing onboarding process.

---

## 15. Real-time Analytics Patterns

### 15.1 Real-time Dashboard Queries

```sql
-- Dashboard real-time: metrics 24 jam terakhir
SELECT 
    'Revenue 24h' as metric,
    ROUND(SUM(total_harga), 2) as value,
    'IDR' as unit
FROM tabel_transaksi
WHERE status = 'completed'
  AND tanggal_transaksi >= CURRENT_TIMESTAMP - INTERVAL '24 hours'

UNION ALL

SELECT 
    'Transactions 24h',
    COUNT(*)::NUMERIC,
    'count'
FROM tabel_transaksi
WHERE status = 'completed'
  AND tanggal_transaksi >= CURRENT_TIMESTAMP - INTERVAL '24 hours'

UNION ALL

SELECT 
    'Active Users 24h',
    COUNT(DISTINCT id_pelanggan)::NUMERIC,
    'users'
FROM tabel_aktivitas
WHERE tanggal_aktivitas >= CURRENT_DATE - INTERVAL '1 day'

UNION ALL

SELECT 
    'Avg Order Value 24h',
    ROUND(AVG(total_harga), 2),
    'IDR'
FROM tabel_transaksi
WHERE status = 'completed'
  AND tanggal_transaksi >= CURRENT_TIMESTAMP - INTERVAL '24 hours';

-- Hourly performance untuk hari ini
SELECT 
    EXTRACT(HOUR FROM tanggal_transaksi) as jam,
    COUNT(*) as jumlah_transaksi,              -- Transaksi per jam
    SUM(total_harga) as revenue_per_jam,       -- Revenue per jam
    COUNT(DISTINCT id_pelanggan) as unique_customers_per_jam,
    ROUND(AVG(total_harga), 2) as avg_order_value_per_jam
FROM tabel_transaksi
WHERE DATE(tanggal_transaksi) = CURRENT_DATE   -- Hari ini saja
  AND status = 'completed'
GROUP BY EXTRACT(HOUR FROM tanggal_transaksi)
ORDER BY jam;
```

**Penjelasan**: Real-time dashboard queries harus efficient dan fast. Data Engineer menggunakan materialized views atau caching untuk queries ini karena dijalankan setiap beberapa menit dalam production dashboard.

### 15.2 Anomaly Detection

```sql
-- Deteksi anomali dalam penjualan harian
WITH daily_stats AS (
    SELECT 
        DATE(tanggal_transaksi) as tanggal,
        SUM(total_harga) as daily_revenue,
        COUNT(*) as daily_transactions
    FROM tabel_transaksi
    WHERE status = 'completed'
      AND tanggal_transaksi >= CURRENT_DATE - INTERVAL '90 days'
    GROUP BY DATE(tanggal_transaksi)
),
statistical_bounds AS (
    SELECT 
        AVG(daily_revenue) as mean_revenue,        -- Rata-rata revenue
        STDDEV(daily_revenue) as stddev_revenue,   -- Standard deviation
        AVG(daily_transactions) as mean_transactions,
        STDDEV(daily_transactions) as stddev_transactions
    FROM daily_stats
)
SELECT 
    ds.tanggal,
    ds.daily_revenue,
    ds.daily_transactions,
    sb.mean_revenue,
    
    -- Z-score untuk revenue
    ROUND(
        (ds.daily_revenue - sb.mean_revenue) / NULLIF(sb.stddev_revenue, 0), 2
    ) as revenue_z_score,
    
    -- Z-score untuk transactions
    ROUND(
        (ds.daily_transactions - sb.mean_transactions) / NULLIF(sb.stddev_transactions, 0), 2
    ) as transaction_z_score,
    
    -- Anomaly detection (|z-score| > 2 = anomaly)
    CASE 
        WHEN ABS((ds.daily_revenue - sb.mean_revenue) / NULLIF(sb.stddev_revenue, 0)) > 2
        THEN 'Revenue Anomaly'
        WHEN ABS((ds.daily_transactions - sb.mean_transactions) / NULLIF(sb.stddev_transactions, 0)) > 2
        THEN 'Transaction Anomaly'
        ELSE 'Normal'
    END as anomaly_status
FROM daily_stats ds
CROSS JOIN statistical_bounds sb
WHERE ABS((ds.daily_revenue - sb.mean_revenue) / NULLIF(sb.stddev_revenue, 0)) > 1.5
   OR ABS((ds.daily_transactions - sb.mean_transactions) / NULLIF(sb.stddev_transactions, 0)) > 1.5
ORDER BY ds.tanggal DESC;
```

**Penjelasan**: Anomaly detection menggunakan statistical methods (z-score) untuk identifying unusual patterns. Data Scientist menggunakan ini untuk fraud detection dan business intelligence alerting systems.

---

## 16. Advanced Date-Time Operations

### 16.1 Time-based Segmentation

```sql
-- Segmentasi waktu untuk analisis perilaku belanja
SELECT 
    CASE 
        WHEN EXTRACT(HOUR FROM tanggal_transaksi) BETWEEN 6 AND 11 THEN 'Pagi (06-11)'
        WHEN EXTRACT(HOUR FROM tanggal_transaksi) BETWEEN 12 AND 17 THEN 'Siang (12-17)'
        WHEN EXTRACT(HOUR FROM tanggal_transaksi) BETWEEN 18 AND 23 THEN 'Malam (18-23)'
        ELSE 'Dini Hari (00-05)'
    END as periode_waktu,
    
    CASE 
        WHEN EXTRACT(DOW FROM tanggal_transaksi) IN (0, 6) THEN 'Weekend'
        ELSE 'Weekday'
    END as jenis_hari,
    
    COUNT(*) as jumlah_transaksi,
    SUM(total_harga) as total_revenue,
    AVG(total_harga) as avg_transaction_value,
    COUNT(DISTINCT id_pelanggan) as unique_customers
FROM tabel_transaksi
WHERE status = 'completed'
  AND tanggal_transaksi >= CURRENT_DATE - INTERVAL '3 months'
GROUP BY 
    CASE 
        WHEN EXTRACT(HOUR FROM tanggal_transaksi) BETWEEN 6 AND 11 THEN 'Pagi (06-11)'
        WHEN EXTRACT(HOUR FROM tanggal_transaksi) BETWEEN 12 AND 17 THEN 'Siang (12-17)'
        WHEN EXTRACT(HOUR FROM tanggal_transaksi) BETWEEN 18 AND 23 THEN 'Malam (18-23)'
        ELSE 'Dini Hari (00-05)'
    END,
    CASE 
        WHEN EXTRACT(DOW FROM tanggal_transaksi) IN (0, 6) THEN 'Weekend'
        ELSE 'Weekday'
    END
ORDER BY total_revenue DESC;

-- Seasonal analysis
SELECT 
    EXTRACT(QUARTER FROM tanggal_transaksi) as quarter,
    EXTRACT(MONTH FROM tanggal_transaksi) as bulan,
    TO_CHAR(tanggal_transaksi, 'Month') as nama_bulan,
    
    COUNT(*) as transaksi,
    SUM(total_harga) as revenue,
    
    -- Year-over-year comparison
    LAG(SUM(total_harga)) OVER (
        PARTITION BY EXTRACT(MONTH FROM tanggal_transaksi)
        ORDER BY EXTRACT(YEAR FROM tanggal_transaksi)
    ) as revenue_tahun_sebelumnya,
    
    ROUND(
        (SUM(total_harga) - LAG(SUM(total_harga)) OVER (
            PARTITION BY EXTRACT(MONTH FROM tanggal_transaksi)
            ORDER BY EXTRACT(YEAR FROM tanggal_transaksi)
        )) * 100.0 / NULLIF(LAG(SUM(total_harga)) OVER (
            PARTITION BY EXTRACT(MONTH FROM tanggal_transaksi)
            ORDER BY EXTRACT(YEAR FROM tanggal_transaksi)
        ), 0), 2
    ) as yoy_growth_pct
FROM tabel_transaksi
WHERE status = 'completed'
GROUP BY 
    EXTRACT(QUARTER FROM tanggal_transaksi),
    EXTRACT(MONTH FROM tanggal_transaksi),
    EXTRACT(YEAR FROM tanggal_transaksi)
ORDER BY 
    EXTRACT(YEAR FROM tanggal_transaksi),
    EXTRACT(MONTH FROM tanggal_transaksi);
```

**Penjelasan**: Temporal analysis membantu identify seasonal patterns dan optimal timing untuk marketing campaigns. Data Analyst menggunakan year-over-year comparison untuk understanding business growth trajectory.

---

## 17. Complex Analytical Queries

### 17.1 Customer Segmentation dengan Multiple Criteria

```sql
-- Advanced customer segmentation menggunakan multiple behavioral metrics
WITH customer_behavior AS (
    SELECT 
        p.id_pelanggan,
        p.nama,
        p.kategori_pelanggan,
        p.kota,
        
        -- Transaction behavior
        COUNT(t.id_transaksi) as total_orders,
        SUM(t.total_harga) as total_spent,
        AVG(t.total_harga) as avg_order_value,
        MAX(t.tanggal_transaksi) as last_purchase_date,
        MIN(t.tanggal_transaksi) as first_purchase_date,
        
        -- Diversity metrics
        COUNT(DISTINCT t.id_produk) as unique_products_bought,
        COUNT(DISTINCT prod.kategori) as unique_categories_bought,
        
        -- Engagement metrics
        COALESCE(AVG(a.durasi_sesi), 0) as avg_session_duration,
        COALESCE(COUNT(a.id_aktivitas), 0) as total_activities
    FROM tabel_pelanggan p
    LEFT JOIN tabel_transaksi t ON p.id_pelanggan = t.id_pelanggan 
        AND t.status = 'completed'
    LEFT JOIN tabel_produk prod ON t.id_produk = prod.id_produk
    LEFT JOIN tabel_aktivitas a ON p.id_pelanggan = a.id_pelanggan
    GROUP BY p.id_pelanggan, p.nama, p.kategori_pelanggan, p.kota
),
segmentation AS (
    SELECT 
        *,
        -- Recency scoring
        CASE 
            WHEN last_purchase_date >= CURRENT_DATE - INTERVAL '30 days' THEN 4
            WHEN last_purchase_date >= CURRENT_DATE - INTERVAL '90 days' THEN 3
            WHEN last_purchase_date >= CURRENT_DATE - INTERVAL '180 days' THEN 2
            WHEN last_purchase_date IS NOT NULL THEN 1
            ELSE 0
        END as recency_score,
        
        -- Frequency scoring (quintiles)
        NTILE(5) OVER (ORDER BY total_orders) as frequency_score,
        
        -- Monetary scoring (quintiles)
        NTILE(5) OVER (ORDER BY total_spent) as monetary_score,
        
        -- Engagement scoring
        CASE 
            WHEN avg_session_duration > 60 AND total_activities > 50 THEN 4
            WHEN avg_session_duration > 30 AND total_activities > 20 THEN 3
            WHEN avg_session_duration > 15 AND total_activities > 10 THEN 2
            WHEN total_activities > 0 THEN 1
            ELSE 0
        END as engagement_score
    FROM customer_behavior
)
SELECT 
    nama,
    kota,
    total_orders,
    total_spent,
    recency_score,
    frequency_score,
    monetary_score,
    engagement_score,
    
    -- Final segmentation logic
    CASE 
        WHEN recency_score >= 3 AND frequency_score >= 4 AND monetary_score >= 4 
        THEN 'VIP Champions'
        WHEN recency_score >= 3 AND frequency_score >= 3 AND monetary_score >= 3 
        THEN 'Loyal Customers'
        WHEN recency_score >= 3 AND frequency_score <= 2 AND monetary_score >= 3
        THEN 'Big Spenders'
        WHEN recency_score >= 3 AND frequency_score <= 2 
        THEN 'New Customers'
        WHEN recency_score = 2 AND frequency_score >= 3 
        THEN 'At Risk Loyal'
        WHEN recency_score = 2 
        THEN 'At Risk'
        WHEN recency_score <= 1 AND frequency_score >= 3 
        THEN 'Cannot Lose Them'
        WHEN recency_score <= 1 
        THEN 'Lost Customers'
        ELSE 'Others'
    END as customer_segment,
    
    -- Priority score untuk marketing action
    (recency_score + frequency_score + monetary_score + engagement_score) as priority_score
FROM segmentation
ORDER BY priority_score DESC, total_spent DESC;
```

**Penjelasan**: Multi-dimensional customer segmentation memberikan actionable insights untuk marketing team. Data Scientist menggunakan scoring system ini sebagai input untuk personalization engine dan campaign targeting.

### 15.2 Predictive Analytics Foundation

```sql
-- Feature extraction untuk predictive modeling
WITH feature_extraction AS (
    SELECT 
        p.id_pelanggan,
        
        -- Demographics
        EXTRACT(DAY FROM CURRENT_DATE - p.tanggal_daftar) as tenure_days,
        p.kategori_pelanggan,
        p.kota,
        
        -- Purchase behavior features
        COUNT(t.id_transaksi) as lifetime_orders,
        COALESCE(SUM(t.total_harga), 0) as lifetime_value,
        COALESCE(AVG(t.total_harga), 0) as avg_order_value,
        COALESCE(STDDEV(t.total_harga), 0) as order_value_std,
        
        -- Recency features
        COALESCE(CURRENT_DATE - MAX(DATE(t.tanggal_transaksi)), 999) as days_since_last_order,
        
        -- Frequency features (orders per month)
        COUNT(t.id_transaksi) * 30.0 / 
        NULLIF(EXTRACT(DAY FROM CURRENT_DATE - p.tanggal_daftar), 0) as orders_per_month,
        
        -- Product diversity
        COUNT(DISTINCT t.id_produk) as unique_products,
        COUNT(DISTINCT prod.kategori) as unique_categories,
        
        -- Recent activity (last 30 days)
        COUNT(CASE WHEN t.tanggal_transaksi >= CURRENT_DATE - INTERVAL '30 days' 
              THEN 1 END) as orders_last_30_days,
        SUM(CASE WHEN t.tanggal_transaksi >= CURRENT_DATE - INTERVAL '30 days' 
            THEN t.total_harga ELSE 0 END) as revenue_last_30_days,
        
        -- Engagement features
        COALESCE(COUNT(a.id_aktivitas), 0) as total_sessions,
        COALESCE(AVG(a.durasi_sesi), 0) as avg_session_duration
    FROM tabel_pelanggan p
    LEFT JOIN tabel_transaksi t ON p.id_pelanggan = t.id_pelanggan 
        AND t.status = 'completed'
    LEFT JOIN tabel_produk prod ON t.id_produk = prod.id_produk
    LEFT JOIN tabel_aktivitas a ON p.id_pelanggan = a.id_pelanggan
    GROUP BY p.id_pelanggan, p.tanggal_daftar, p.kategori_pelanggan, p.kota
)
SELECT 
    *,
    -- Churn probability indicator
    CASE 
        WHEN days_since_last_order > 90 AND orders_per_month < 0.5 THEN 'High Risk'
        WHEN days_since_last_order > 60 AND orders_per_month < 1.0 THEN 'Medium Risk'
        WHEN days_since_last_order > 30 THEN 'Low Risk'
        ELSE 'Active'
    END as churn_risk_category,
    
    -- CLV prediction input
    avg_order_value * orders_per_month * 12 as predicted_annual_value
FROM feature_extraction
ORDER BY predicted_annual_value DESC;
```

**Penjelasan**: Feature engineering adalah foundation untuk machine learning pipeline. Data Scientist menggunakan features ini untuk training churn prediction, CLV prediction, dan recommendation models.

---

## 18. ETL dan Data Pipeline Patterns

### 18.1 Incremental Data Loading

```sql
-- Pattern untuk incremental ETL (hanya load data baru)
-- Asumsi: ada tabel staging untuk data baru
CREATE TABLE staging_transaksi (LIKE tabel_transaksi);

-- Insert hanya data yang belum ada
INSERT INTO tabel_transaksi (
    id_pelanggan, 
    id_produk, 
    tanggal_transaksi, 
    jumlah, 
    total_harga, 
    status
)
SELECT 
    s.id_pelanggan,
    s.id_produk,
    s.tanggal_transaksi,
    s.jumlah,
    s.total_harga,
    s.status
FROM staging_transaksi s
LEFT JOIN tabel_transaksi t 
    ON s.id_pelanggan = t.id_pelanggan 
    AND s.tanggal_transaksi = t.tanggal_transaksi
    AND s.id_produk = t.id_produk
WHERE t.id_transaksi IS NULL              -- Hanya insert yang belum ada
  AND s.tanggal_transaksi >= CURRENT_DATE - INTERVAL '7 days';  -- Safety window

-- Update existing records jika ada perubahan
UPDATE tabel_transaksi t
SET 
    status = s.status,
    total_harga = s.total_harga,
    jumlah = s.jumlah
FROM staging_transaksi s
WHERE t.id_pelanggan = s.id_pelanggan
  AND t.tanggal_transaksi = s.tanggal_transaksi
  AND t.id_produk = s.id_produk
  AND (t.status != s.status OR t.total_harga != s.total_harga);  -- Hanya update jika ada perubahan
```

**Penjelasan**: Incremental loading pattern essential untuk efficient ETL dalam production. Data Engineer menggunakan pattern ini untuk minimizing processing time dan avoiding full table reload.

### 18.2 Data Validation dan Quality Gates

```sql
-- Automated data validation untuk ETL pipeline
DO $
DECLARE
    validation_errors TEXT[] := ARRAY[]::TEXT[];  -- Array untuk menyimpan error
    error_count INTEGER;
    total_records INTEGER;
BEGIN
    -- Validation 1: Check for NULL in critical fields
    SELECT COUNT(*) INTO error_count
    FROM tabel_transaksi 
    WHERE id_pelanggan IS NULL OR id_produk IS NULL OR total_harga IS NULL;
    
    IF error_count > 0 THEN
        validation_errors := array_append(validation_errors, 
            'Found ' || error_count || ' records with NULL in critical fields');
    END IF;
    
    -- Validation 2: Check for negative values
    SELECT COUNT(*) INTO error_count
    FROM tabel_transaksi 
    WHERE total_harga < 0 OR jumlah < 0;
    
    IF error_count > 0 THEN
        validation_errors := array_append(validation_errors, 
            'Found ' || error_count || ' records with negative values');
    END IF;
    
    -- Validation 3: Check for future dates
    SELECT COUNT(*) INTO error_count
    FROM tabel_transaksi 
    WHERE tanggal_transaksi > CURRENT_TIMESTAMP;
    
    IF error_count > 0 THEN
        validation_errors := array_append(validation_errors, 
            'Found ' || error_count || ' records with future dates');
    END IF;
    
    -- Validation 4: Check referential integrity
    SELECT COUNT(*) INTO error_count
    FROM tabel_transaksi t
    LEFT JOIN tabel_pelanggan p ON t.id_pelanggan = p.id_pelanggan
    WHERE p.id_pelanggan IS NULL;
    
    IF error_count > 0 THEN
        validation_errors := array_append(validation_errors, 
            'Found ' || error_count || ' orphaned transaction records');
    END IF;
    
    -- Output validation results
    IF array_length(validation_errors, 1) > 0 THEN
        RAISE NOTICE 'DATA VALIDATION FAILED:';
        FOR i IN 1..array_length(validation_errors, 1) LOOP
            RAISE NOTICE '- %', validation_errors[i];
        END LOOP;
    ELSE
        SELECT COUNT(*) INTO total_records FROM tabel_transaksi;
        RAISE NOTICE 'DATA VALIDATION PASSED - % records processed', total_records;
    END IF;
END $;

-- Query untuk monitoring data quality over time
SELECT 
    DATE(created_at) as tanggal,            -- Asumsi ada kolom created_at
    COUNT(*) as total_records,
    COUNT(*) - COUNT(id_pelanggan) as missing_customer_id,
    COUNT(*) - COUNT(total_harga) as missing_amount,
    COUNT(CASE WHEN total_harga < 0 THEN 1 END) as negative_amounts,
    ROUND(
        (COUNT(*) - COUNT(id_pelanggan)) * 100.0 / COUNT(*), 2
    ) as data_quality_score
FROM tabel_transaksi
WHERE created_at >= CURRENT_DATE - INTERVAL '30 days'
GROUP BY DATE(created_at)
ORDER BY tanggal DESC;
```

**Penjelasan**: Data validation critical dalam automated ETL pipeline. Data Engineer menggunakan procedural code (DO blocks) untuk implementing business rules dan data quality gates sebelum data masuk production.

---

## 19. Performance Optimization untuk Big Data

### 19.1 Partitioning Strategy

```sql
-- Membuat partitioned table untuk large datasets
CREATE TABLE tabel_transaksi_partitioned (
    id_transaksi SERIAL,
    id_pelanggan INTEGER,
    id_produk INTEGER,
    tanggal_transaksi TIMESTAMP,
    jumlah INTEGER,
    total_harga DECIMAL(10,2),
    status VARCHAR(20)
) PARTITION BY RANGE (tanggal_transaksi);    -- Partition berdasarkan tanggal

-- Membuat partisi bulanan
CREATE TABLE tabel_transaksi_2024_01 PARTITION OF tabel_transaksi_partitioned
    FOR VALUES FROM ('2024-01-01') TO ('2024-02-01');

CREATE TABLE tabel_transaksi_2024_02 PARTITION OF tabel_transaksi_partitioned
    FOR VALUES FROM ('2024-02-01') TO ('2024-03-01');

-- Query yang memanfaatkan partition pruning
SELECT 
    COUNT(*) as transaksi_januari,
    SUM(total_harga) as revenue_januari
FROM tabel_transaksi_partitioned
WHERE tanggal_transaksi >= '2024-01-01'    -- PostgreSQL akan query hanya partisi yang relevan
  AND tanggal_transaksi < '2024-02-01'
  AND status = 'completed';

-- Maintenance: drop old partitions
-- DROP TABLE tabel_transaksi_2023_01;    -- Hapus data lama jika tidak diperlukan
```

**Penjelasan**: Table partitioning essential untuk handling big data (10M+ rows). Data Engineer menggunakan date-based partitioning untuk improving query performance dan enabling efficient data archival.

### 19.2 Materialized Views untuk Fast Analytics

```sql
-- Membuat materialized view untuk metrics yang sering diquery
CREATE MATERIALIZED VIEW mv_customer_summary AS
SELECT 
    p.id_pelanggan,
    p.nama,
    p.kategori_pelanggan,
    p.kota,
    COUNT(t.id_transaksi) as total_orders,
    COALESCE(SUM(t.total_harga), 0) as total_spent,
    COALESCE(AVG(t.total_harga), 0) as avg_order_value,
    MAX(t.tanggal_transaksi) as last_purchase_date,
    CURRENT_DATE - MAX(DATE(t.tanggal_transaksi)) as days_since_last_purchase
FROM tabel_pelanggan p
LEFT JOIN tabel_transaksi t ON p.id_pelanggan = t.id_pelanggan
    AND t.status = 'completed'
GROUP BY p.id_pelanggan, p.nama, p.kategori_pelanggan, p.kota;

-- Membuat index pada materialized view
CREATE INDEX idx_mv_customer_kota ON mv_customer_summary(kota);
CREATE INDEX idx_mv_customer_total_spent ON mv_customer_summary(total_spent);

-- Query yang menggunakan materialized view (jauh lebih cepat)
SELECT 
    kota,
    COUNT(*) as jumlah_customer,
    AVG(total_spent) as avg_customer_value,
    SUM(total_spent) as total_city_revenue
FROM mv_customer_summary
WHERE total_orders > 0
GROUP BY kota
ORDER BY total_city_revenue DESC;

-- Refresh materialized view (biasanya dijadwalkan)
REFRESH MATERIALIZED VIEW mv_customer_summary;

-- Atau concurrent refresh (tidak block readers)
REFRESH MATERIALIZED VIEW CONCURRENTLY mv_customer_summary;
```

**Penjelasan**: Materialized views menyimpan hasil query yang sudah di-compute, dramatically improving dashboard loading time. Data Engineer menggunakan ini untuk complex aggregations yang expensive untuk compute real-time.

---

## 20. Troubleshooting dan Best Practices

### 20.1 Query Performance Analysis

```sql
-- Menggunakan EXPLAIN untuk analisis query performance
EXPLAIN (ANALYZE, BUFFERS, FORMAT JSON)
SELECT 
    p.nama,
    COUNT(t.id_transaksi) as total_orders,
    SUM(t.total_harga) as total_spent
FROM tabel_pelanggan p
INNER JOIN tabel_transaksi t ON p.id_pelanggan = t.id_pelanggan
WHERE t.tanggal_transaksi >= '2024-01-01'
  AND t.status = 'completed'
  AND p.kota = 'Jakarta'
GROUP BY p.id_pelanggan, p.nama
ORDER BY total_spent DESC;

-- Query untuk monitoring slow queries
SELECT 
    query,
    calls,
    total_time,
    mean_time,
    rows,
    100.0 * shared_blks_hit / nullif(shared_blks_hit + shared_blks_read, 0) as hit_percent
FROM pg_stat_statements 
WHERE mean_time > 1000                     -- Queries yang rata-rata > 1 detik
ORDER BY mean_time DESC
LIMIT 10;

-- Check index usage
SELECT 
    schemaname,
    tablename,
    indexname,
    idx_scan as index_scans,              -- Berapa kali index digunakan
    idx_tup_read as tuples_read,          -- Berapa baris dibaca via index
    idx_tup_fetch as tuples_fetched       -- Berapa baris di-fetch
FROM pg_stat_user_indexes
WHERE idx_scan < 100                      -- Index yang jarang digunakan
ORDER BY idx_scan;
```

**Penjelasan**: Performance monitoring dan tuning critical dalam production systems. Data Engineer menggunakan EXPLAIN ANALYZE untuk understanding query execution plans dan pg_stat_statements untuk identifying bottlenecks.

### 20.2 Common SQL Pitfalls dan Solutions

```sql
-- Pitfall 1: N+1 Query Problem - Solusi dengan Window Function
-- SALAH: Query terpisah untuk setiap pelanggan
-- SELECT nama, (SELECT COUNT(*) FROM tabel_transaksi WHERE id_pelanggan = p.id_pelanggan) FROM tabel_pelanggan p;

-- BENAR: Satu query dengan JOIN
SELECT 
    p.nama,
    COUNT(t.id_transaksi) as jumlah_transaksi
FROM tabel_pelanggan p
LEFT JOIN tabel_transaksi t ON p.id_pelanggan = t.id_pelanggan
GROUP BY p.id_pelanggan, p.nama;

-- Pitfall 2: Implicit Cartesian Product - Solusi dengan Proper JOIN
-- SALAH: FROM multiple tables tanpa proper JOIN condition
-- SELECT * FROM tabel_pelanggan, tabel_transaksi WHERE kategori_pelanggan = 'Premium';

-- BENAR: Explicit JOIN dengan proper conditions
SELECT p.nama, t.total_harga
FROM tabel_pelanggan p
INNER JOIN tabel_transaksi t ON p.id_pelanggan = t.id_pelanggan
WHERE p.kategori_pelanggan = 'Premium'
  AND t.status = 'completed';

-- Pitfall 3: Inefficient WHERE dengan Functions - Solusi dengan Index
-- SALAH: Function di WHERE clause (tidak bisa pakai index)
-- SELECT * FROM tabel_transaksi WHERE EXTRACT(YEAR FROM tanggal_transaksi) = 2024;

-- BENAR: Range condition yang bisa pakai index
SELECT * 
FROM tabel_transaksi 
WHERE tanggal_transaksi >= '2024-01-01'   -- Bisa menggunakan index
  AND tanggal_transaksi < '2025-01-01';

-- Pitfall 4: SELECT * dalam production queries
-- SALAH: Mengambil semua kolom
-- SELECT * FROM tabel_transaksi WHERE status = 'completed';

-- BENAR: Hanya ambil kolom yang diperlukan
SELECT 
    id_transaksi, 
    id_pelanggan, 
    total_harga, 
    tanggal_transaksi
FROM tabel_transaksi 
WHERE status = 'completed';
```

**Penjelasan**: Menghindari common pitfalls essential untuk writing efficient SQL dalam production. Data Engineer harus aware terhadap performance implications dari setiap query pattern yang digunakan.

---

## 21. Kesimpulan dan Best Practices

### 21.1 Checklist untuk Query yang Baik

```sql
-- Template query yang optimal
WITH 
-- 1. Gunakan CTE untuk readability
base_data AS (
    SELECT                                -- 2. Select hanya kolom yang diperlukan
        t.id_pelanggan,
        t.tanggal_transaksi,
        t.total_harga
    FROM tabel_transaksi t
    WHERE t.status = 'completed'          -- 3. Filter sedini mungkin
      AND t.tanggal_transaksi >= '2024-01-01'  -- 4. Gunakan index-friendly conditions
),
aggregated_data AS (
    SELECT 
        id_pelanggan,
        COUNT(*) as order_count,
        SUM(total_harga) as total_revenue
    FROM base_data
    # Panduan SQL PostgreSQL untuk Data Professional
## Dari Dasar hingga Menengah-Lanjut

---

## Daftar Isi
1. [Setup Data Sample](#setup-data-sample)
2. [Query Dasar](#query-dasar)
3. [Fungsi Agregasi dan Grouping](#fungsi-agregasi-dan-grouping)
4. [JOIN untuk Penggabungan Data](#join-untuk-penggabungan-data)
5. [Subquery untuk Analisis Mendalam](#subquery-untuk-analisis-mendalam)
6. [Window Functions](#window-functions)
7. [Pembersihan dan Transformasi Data](#pembersihan-dan-transformasi-data)
8. [Analisis Data Penjualan](#analisis-data-penjualan)
9. [Analisis Perilaku Pengguna](#analisis-perilaku-pengguna)
10. [Optimasi Query](#optimasi-query)

---

## 1. Setup Data Sample

Sebelum memulai, mari buat struktur tabel sederhana yang akan digunakan sepanjang pembelajaran:

```sql
-- Membuat tabel pelanggan
CREATE TABLE tabel_pelanggan (
    id_pelanggan SERIAL PRIMARY KEY,    -- ID unik pelanggan
    nama VARCHAR(100),                  -- Nama pelanggan
    email VARCHAR(100),                 -- Email pelanggan
    tanggal_daftar DATE,               -- Tanggal pendaftaran
    kota VARCHAR(50),                  -- Kota asal
    kategori_pelanggan VARCHAR(20)     -- Kategori: Premium, Regular, Basic
);

-- Membuat tabel produk
CREATE TABLE tabel_produk (
    id_produk SERIAL PRIMARY KEY,      -- ID unik produk
    nama_produk VARCHAR(100),          -- Nama produk
    kategori VARCHAR(50),              -- Kategori produk
    harga DECIMAL(10,2),               -- Harga produk
    stok INTEGER                       -- Jumlah stok
);

-- Membuat tabel transaksi
CREATE TABLE tabel_transaksi (
    id_transaksi SERIAL PRIMARY KEY,   -- ID unik transaksi
    id_pelanggan INTEGER,              -- Foreign key ke tabel_pelanggan
    id_produk INTEGER,                 -- Foreign key ke tabel_produk
    tanggal_transaksi TIMESTAMP,      -- Waktu transaksi
    jumlah INTEGER,                    -- Jumlah item dibeli
    total_harga DECIMAL(10,2),         -- Total nilai transaksi
    status VARCHAR(20)                 -- Status: completed, pending, cancelled
);

-- Membuat tabel log aktivitas pengguna
CREATE TABLE tabel_aktivitas (
    id_aktivitas SERIAL PRIMARY KEY,   -- ID unik aktivitas
    id_pelanggan INTEGER,              -- Foreign key ke tabel_pelanggan
    tanggal_aktivitas DATE,           -- Tanggal aktivitas
    jenis_aktivitas VARCHAR(50),      -- Jenis: login, view_product, purchase
    durasi_sesi INTEGER               -- Durasi sesi dalam menit
);
```

**Penjelasan**: Setup ini menciptakan struktur data yang mirip dengan sistem e-commerce nyata. Dalam pekerjaan sehari-hari, Data Professional sering bekerja dengan struktur serupa untuk menganalisis perilaku pelanggan, penjualan, dan performa bisnis.

---

## 2. Query Dasar

### 2.1 SELECT Sederhana

```sql
-- Menampilkan semua data pelanggan
SELECT * FROM tabel_pelanggan;

-- Menampilkan kolom tertentu
SELECT nama, email, kota 
FROM tabel_pelanggan;

-- Menampilkan data dengan kondisi
SELECT nama, email 
FROM tabel_pelanggan 
WHERE kota = 'Jakarta'           -- Filter berdasarkan kota
  AND kategori_pelanggan = 'Premium';  -- Dan kategori premium
```

**Penjelasan**: Query dasar ini digunakan daily untuk eksplorasi data awal. Data Analyst menggunakan ini untuk memahami struktur data dan melakukan filtering sederhana sebelum analisis lebih mendalam.

### 2.2 Pengurutan dan Pembatasan Data

```sql
-- Mengurutkan data berdasarkan tanggal daftar (terbaru ke terlama)
SELECT nama, tanggal_daftar, kategori_pelanggan
FROM tabel_pelanggan
ORDER BY tanggal_daftar DESC;     -- Urutkan descending (terbaru dulu)

-- Mengambil 10 pelanggan teratas
SELECT nama, tanggal_daftar
FROM tabel_pelanggan
ORDER BY tanggal_daftar DESC
LIMIT 10;                         -- Batasi hasil hanya 10 baris

-- Menggunakan OFFSET untuk pagination
SELECT nama, email
FROM tabel_pelanggan
ORDER BY nama
LIMIT 10 OFFSET 20;              -- Skip 20 baris pertama, ambil 10 berikutnya
```

**Penjelasan**: Pengurutan dan pembatasan data sangat penting dalam dashboard dan laporan. Data Engineer menggunakan LIMIT/OFFSET untuk implementasi pagination di aplikasi web.

---

## 3. Fungsi Agregasi dan Grouping

### 3.1 Fungsi Agregasi Dasar

```sql
-- Statistik dasar pelanggan
SELECT 
    COUNT(*) as total_pelanggan,           -- Hitung total pelanggan
    COUNT(DISTINCT kota) as total_kota,    -- Hitung kota unik
    MIN(tanggal_daftar) as pendaftaran_pertama,  -- Tanggal daftar tertua
    MAX(tanggal_daftar) as pendaftaran_terakhir  -- Tanggal daftar terbaru
FROM tabel_pelanggan;

-- Statistik produk
SELECT 
    COUNT(*) as total_produk,              -- Total produk
    AVG(harga) as rata_rata_harga,         -- Rata-rata harga
    SUM(stok) as total_stok,               -- Total stok semua produk
    MAX(harga) as harga_tertinggi,         -- Harga tertinggi
    MIN(harga) as harga_terendah           -- Harga terendah
FROM tabel_produk;
```

**Penjelasan**: Fungsi agregasi adalah tools utama Data Analyst untuk membuat summary statistics. Digunakan untuk dashboard KPI, laporan bulanan, dan analisis performa bisnis.

### 3.2 GROUP BY untuk Analisis Kategori

```sql
-- Analisis pelanggan per kota
SELECT 
    kota,
    COUNT(*) as jumlah_pelanggan,          -- Jumlah pelanggan per kota
    COUNT(*) * 100.0 / SUM(COUNT(*)) OVER() as persentase  -- Persentase per kota
FROM tabel_pelanggan
GROUP BY kota                              -- Kelompokkan berdasarkan kota
ORDER BY jumlah_pelanggan DESC;            -- Urutkan dari terbanyak

-- Analisis produk per kategori
SELECT 
    kategori,
    COUNT(*) as jumlah_produk,             -- Jumlah produk per kategori
    AVG(harga) as rata_rata_harga,         -- Rata-rata harga per kategori
    SUM(stok) as total_stok_kategori       -- Total stok per kategori
FROM tabel_produk
GROUP BY kategori
HAVING AVG(harga) > 50000;                -- Filter group yang rata-rata harga > 50k
```

**Penjelasan**: GROUP BY dengan HAVING sangat penting untuk segmentasi data. Data Scientist menggunakan ini untuk customer segmentation dan analisis cohort. HAVING berbeda dengan WHERE karena HAVING filter hasil agregasi.

---

## 4. JOIN untuk Penggabungan Data

### 4.1 INNER JOIN - Data yang Ada di Kedua Tabel

```sql
-- Gabungkan data transaksi dengan pelanggan
SELECT 
    t.id_transaksi,                        -- ID transaksi
    p.nama as nama_pelanggan,              -- Nama pelanggan
    p.kota,                                -- Kota pelanggan
    t.tanggal_transaksi,                   -- Waktu transaksi
    t.total_harga                          -- Nilai transaksi
FROM tabel_transaksi t                     -- Alias 't' untuk tabel transaksi
INNER JOIN tabel_pelanggan p               -- Alias 'p' untuk tabel pelanggan
    ON t.id_pelanggan = p.id_pelanggan     -- Kondisi join
WHERE t.status = 'completed'               -- Filter transaksi selesai
ORDER BY t.tanggal_transaksi DESC;

-- JOIN tiga tabel: transaksi, pelanggan, dan produk
SELECT 
    t.id_transaksi,
    pel.nama as nama_pelanggan,            -- Nama pelanggan
    prod.nama_produk,                      -- Nama produk
    t.jumlah,                              -- Quantity
    t.total_harga,                         -- Total transaksi
    t.tanggal_transaksi
FROM tabel_transaksi t
INNER JOIN tabel_pelanggan pel ON t.id_pelanggan = pel.id_pelanggan
INNER JOIN tabel_produk prod ON t.id_produk = prod.id_produk
WHERE t.tanggal_transaksi >= '2024-01-01';  -- Filter tahun 2024
```

**Penjelasan**: INNER JOIN adalah jantung analisis relational data. Data Engineer menggunakan ini untuk ETL pipeline, sementara Data Analyst menggunakan untuk menggabungkan data dari berbagai sumber untuk reporting.

### 4.2 LEFT JOIN - Termasuk Data yang Tidak Match

```sql
-- Tampilkan semua pelanggan beserta total transaksi mereka (termasuk yang belum transaksi)
SELECT 
    p.nama,
    p.kategori_pelanggan,
    COUNT(t.id_transaksi) as jumlah_transaksi,  -- Akan 0 jika tidak ada transaksi
    COALESCE(SUM(t.total_harga), 0) as total_pembelian  -- Ganti NULL dengan 0
FROM tabel_pelanggan p                         -- Tabel utama (semua data diambil)
LEFT JOIN tabel_transaksi t                    -- Tabel kedua (boleh tidak match)
    ON p.id_pelanggan = t.id_pelanggan 
    AND t.status = 'completed'                 -- Tambahan kondisi di JOIN
GROUP BY p.id_pelanggan, p.nama, p.kategori_pelanggan
ORDER BY total_pembelian DESC;
```

**Penjelasan**: LEFT JOIN penting untuk analisis customer lifetime value dan identifikasi pelanggan tidak aktif. Data Scientist menggunakan ini untuk feature engineering dalam model churn prediction.

---

## 5. Subquery untuk Analisis Mendalam

### 5.1 Subquery di WHERE

```sql
-- Cari pelanggan yang melakukan transaksi di atas rata-rata
SELECT 
    nama,
    kota,
    kategori_pelanggan
FROM tabel_pelanggan
WHERE id_pelanggan IN (                    -- Subquery untuk filter
    SELECT id_pelanggan 
    FROM tabel_transaksi 
    WHERE total_harga > (                  -- Nested subquery
        SELECT AVG(total_harga)            -- Hitung rata-rata transaksi
        FROM tabel_transaksi 
        WHERE status = 'completed'
    )
);

-- Produk yang tidak pernah terjual
SELECT 
    nama_produk,
    kategori,
    harga
FROM tabel_produk
WHERE id_produk NOT IN (                   -- NOT IN untuk exclusion
    SELECT DISTINCT id_produk 
    FROM tabel_transaksi 
    WHERE id_produk IS NOT NULL           -- Handle NULL values
);
```

**Penjelasan**: Subquery memungkinkan analisis bertingkat. Data Analyst menggunakan ini untuk identifikasi outlier dan segmentasi pelanggan berdasarkan perilaku pembelian.

### 5.2 Subquery di SELECT (Scalar Subquery)

```sql
-- Tambahkan informasi tambahan ke setiap pelanggan
SELECT 
    nama,
    kota,
    (SELECT COUNT(*) 
     FROM tabel_transaksi t 
     WHERE t.id_pelanggan = p.id_pelanggan 
       AND status = 'completed') as total_transaksi,  -- Subquery untuk hitung transaksi
    
    (SELECT COALESCE(SUM(total_harga), 0) 
     FROM tabel_transaksi t 
     WHERE t.id_pelanggan = p.id_pelanggan 
       AND status = 'completed') as total_pembelian   -- Subquery untuk total pembelian
FROM tabel_pelanggan p
ORDER BY total_pembelian DESC;
```

**Penjelasan**: Scalar subquery berguna untuk menambahkan calculated fields. Data Engineer menggunakan pattern ini dalam view creation untuk dashboard yang perlu real-time calculation.

---

## 6. Window Functions

### 6.1 ROW_NUMBER dan RANK

```sql
-- Ranking produk berdasarkan harga dalam setiap kategori
SELECT 
    nama_produk,
    kategori,
    harga,
    ROW_NUMBER() OVER (
        PARTITION BY kategori 
        ORDER BY harga DESC
    ) as rank_harga_per_kategori,          -- Ranking berurutan tanpa tie
    
    RANK() OVER (
        PARTITION BY kategori 
        ORDER BY harga DESC
    ) as rank_dengan_tie                   -- Ranking dengan tie (sama dapat rank sama)
FROM tabel_produk
ORDER BY kategori, harga DESC;

-- Top 3 produk termahal per kategori
SELECT *
FROM (
    SELECT 
        nama_produk,
        kategori,
        harga,
        ROW_NUMBER() OVER (
            PARTITION BY kategori 
            ORDER BY harga DESC
        ) as rank_harga
    FROM tabel_produk
) ranked
WHERE rank_harga <= 3;                    -- Filter hanya top 3
```

**Penjelasan**: Window functions dengan PARTITION BY memungkinkan ranking dalam group. Data Scientist menggunakan ini untuk feature engineering, seperti ranking produk dalam kategori untuk recommendation system.

### 6.2 LAG dan LEAD untuk Analisis Tren

```sql
-- Analisis tren penjualan bulanan
WITH penjualan_bulanan AS (
    SELECT 
        DATE_TRUNC('month', tanggal_transaksi) as bulan,  -- Truncate ke bulan
        SUM(total_harga) as total_penjualan               -- Total per bulan
    FROM tabel_transaksi
    WHERE status = 'completed'
    GROUP BY DATE_TRUNC('month', tanggal_transaksi)
)
SELECT 
    bulan,
    total_penjualan,
    LAG(total_penjualan) OVER (
        ORDER BY bulan
    ) as penjualan_bulan_sebelumnya,       -- Nilai bulan sebelumnya
    
    total_penjualan - LAG(total_penjualan) OVER (
        ORDER BY bulan
    ) as selisih_penjualan,                -- Selisih dengan bulan sebelumnya
    
    ROUND(
        (total_penjualan - LAG(total_penjualan) OVER (ORDER BY bulan)) * 100.0 
        / LAG(total_penjualan) OVER (ORDER BY bulan), 2
    ) as persentase_pertumbuhan            -- Persentase pertumbuhan
FROM penjualan_bulanan
ORDER BY bulan;
```

**Penjelasan**: LAG/LEAD essential untuk time series analysis. Data Scientist menggunakan ini untuk menghitung growth rate, seasonality analysis, dan trend detection dalam forecasting model.

### 6.3 Running Totals dengan SUM OVER

```sql
-- Running total penjualan harian
SELECT 
    DATE(tanggal_transaksi) as tanggal,
    SUM(total_harga) as penjualan_harian,         -- Penjualan per hari
    SUM(SUM(total_harga)) OVER (
        ORDER BY DATE(tanggal_transaksi)
    ) as running_total                            -- Kumulatif penjualan
FROM tabel_transaksi
WHERE status = 'completed'
  AND tanggal_transaksi >= '2024-01-01'
GROUP BY DATE(tanggal_transaksi)
ORDER BY tanggal;

-- Persentase kontribusi setiap transaksi
SELECT 
    id_transaksi,
    total_harga,
    SUM(total_harga) OVER () as total_keseluruhan,  -- Total semua transaksi
    ROUND(
        total_harga * 100.0 / SUM(total_harga) OVER (), 2
    ) as persentase_kontribusi                      -- Persentase kontribusi
FROM tabel_transaksi
WHERE status = 'completed'
ORDER BY total_harga DESC;
```

**Penjelasan**: Running totals penting untuk cash flow analysis dan cumulative metrics. Data Analyst menggunakan ini untuk tracking progress terhadap target bulanan/tahunan.

---

## 7. Pembersihan dan Transformasi Data

### 7.1 Menangani Duplicate Data

```sql
-- Identifikasi duplikat berdasarkan email
SELECT 
    email,
    COUNT(*) as jumlah_duplikat
FROM tabel_pelanggan
GROUP BY email
HAVING COUNT(*) > 1;                      -- Hanya tampilkan yang duplikat

-- Hapus duplikat, simpan yang ID terkecil
DELETE FROM tabel_pelanggan
WHERE id_pelanggan NOT IN (               -- Hapus yang bukan ID terkecil
    SELECT MIN(id_pelanggan)              -- Pilih ID terkecil per email
    FROM tabel_pelanggan
    GROUP BY email
);

-- Alternatif: Menggunakan window function untuk identifikasi duplikat
SELECT 
    *,
    ROW_NUMBER() OVER (
        PARTITION BY email 
        ORDER BY id_pelanggan
    ) as row_num                          -- Nomor urut per email
FROM tabel_pelanggan
WHERE email IS NOT NULL;                  -- Hanya email yang tidak NULL
```

**Penjelasan**: Data cleaning adalah 80% pekerjaan Data Scientist. Duplikasi data dapat merusak analisis dan model ML. Pattern ini standard dalam ETL pipeline.

### 7.2 Menangani Missing Values

```sql
-- Identifikasi missing values
SELECT 
    'tabel_pelanggan' as tabel,
    'nama' as kolom,
    COUNT(*) as total_baris,
    COUNT(nama) as baris_terisi,           -- COUNT mengabaikan NULL
    COUNT(*) - COUNT(nama) as missing_values,
    ROUND(
        (COUNT(*) - COUNT(nama)) * 100.0 / COUNT(*), 2
    ) as persentase_missing
FROM tabel_pelanggan

UNION ALL

SELECT 
    'tabel_pelanggan',
    'email',
    COUNT(*),
    COUNT(email),
    COUNT(*) - COUNT(email),
    ROUND((COUNT(*) - COUNT(email)) * 100.0 / COUNT(*), 2)
FROM tabel_pelanggan;

-- Mengisi missing values
UPDATE tabel_pelanggan 
SET kota = 'Unknown'                      -- Isi dengan default value
WHERE kota IS NULL;

-- Mengisi dengan nilai yang paling sering muncul (mode)
UPDATE tabel_pelanggan 
SET kategori_pelanggan = (
    SELECT kategori_pelanggan
    FROM tabel_pelanggan
    WHERE kategori_pelanggan IS NOT NULL
    GROUP BY kategori_pelanggan
    ORDER BY COUNT(*) DESC
    LIMIT 1                               -- Ambil yang paling sering muncul
)
WHERE kategori_pelanggan IS NULL;
```

**Penjelasan**: Missing value handling critical dalam data preparation. Data Engineer perlu memastikan data quality sebelum data masuk ke data warehouse atau model ML.

### 7.3 Transformasi Tipe Data dan Format

```sql
-- Konversi dan transformasi data
SELECT 
    nama,
    UPPER(nama) as nama_uppercase,         -- Konversi ke huruf besar
    LOWER(email) as email_lowercase,       -- Konversi ke huruf kecil
    
    -- Ekstrak bagian dari tanggal
    EXTRACT(YEAR FROM tanggal_daftar) as tahun_daftar,
    EXTRACT(MONTH FROM tanggal_daftar) as bulan_daftar,
    
    -- Kategorisasi berdasarkan lama menjadi member
    CASE 
        WHEN CURRENT_DATE - tanggal_daftar > INTERVAL '1 year' 
        THEN 'Member Lama'
        WHEN CURRENT_DATE - tanggal_daftar > INTERVAL '6 months' 
        THEN 'Member Menengah'
        ELSE 'Member Baru'
    END as kategori_member,                -- Conditional logic
    
    -- Membuat kolom boolean
    CASE 
        WHEN kategori_pelanggan = 'Premium' THEN TRUE 
        ELSE FALSE 
    END as is_premium                      -- Boolean flag
FROM tabel_pelanggan;

-- Membuat kolom derived baru
ALTER TABLE tabel_pelanggan 
ADD COLUMN umur_member INTEGER;

UPDATE tabel_pelanggan 
SET umur_member = EXTRACT(
    DAY FROM CURRENT_DATE - tanggal_daftar
);                                        -- Hitung umur member dalam hari
```

**Penjelasan**: Data transformation essential untuk feature engineering. Data Scientist menggunakan CASE WHEN untuk creating categorical features dan binning continuous variables.

---

## 8. Analisis Data Penjualan

### 8.1 Analisis Penjualan per Periode

```sql
-- Penjualan per bulan dengan target
WITH target_bulanan AS (
    SELECT 50000000 as target           -- Asumsi target 50 juta per bulan
),
penjualan_bulanan AS (
    SELECT 
        DATE_TRUNC('month', tanggal_transaksi) as bulan,
        SUM(total_harga) as total_penjualan,
        COUNT(*) as jumlah_transaksi,
        COUNT(DISTINCT id_pelanggan) as unique_customers
    FROM tabel_transaksi
    WHERE status = 'completed'
    GROUP BY DATE_TRUNC('month', tanggal_transaksi)
)
SELECT 
    bulan,
    total_penjualan,
    target,
    total_penjualan - target as selisih_target,      -- Gap dengan target
    ROUND(total_penjualan * 100.0 / target, 2) as persentase_pencapaian,
    jumlah_transaksi,
    unique_customers,
    ROUND(total_penjualan / jumlah_transaksi, 2) as avg_transaction_value
FROM penjualan_bulanan
CROSS JOIN target_bulanan                            -- Gabung dengan target
ORDER BY bulan;
```

**Penjelasan**: Analisis penjualan vs target adalah core KPI dashboard. Data Analyst menggunakan CTE (Common Table Expression) untuk membuat query yang readable dan maintainable.

### 8.2 Analisis Produk Terlaris

```sql
-- Top 10 produk terlaris berdasarkan quantity dan revenue
SELECT 
    p.nama_produk,
    p.kategori,
    p.harga,
    SUM(t.jumlah) as total_quantity_terjual,        -- Total quantity
    SUM(t.total_harga) as total_revenue,            -- Total revenue
    COUNT(*) as frequency_pembelian,                -- Frequency pembelian
    
    -- Ranking berdasarkan revenue
    RANK() OVER (ORDER BY SUM(t.total_harga) DESC) as rank_revenue,
    
    -- Ranking berdasarkan quantity
    RANK() OVER (ORDER BY SUM(t.jumlah) DESC) as rank_quantity
FROM tabel_produk p
INNER JOIN tabel_transaksi t ON p.id_produk = t.id_produk
WHERE t.status = 'completed'
  AND t.tanggal_transaksi >= CURRENT_DATE - INTERVAL '6 months'  -- 6 bulan terakhir
GROUP BY p.id_produk, p.nama_produk, p.kategori, p.harga
ORDER BY total_revenue DESC
LIMIT 10;
```

**Penjelasan**: Product performance analysis crucial untuk inventory management dan marketing strategy. Data Analyst menggunakan multiple ranking untuk understanding product performance dari berbagai perspektif.

### 8.3 Cohort Analysis Sederhana

```sql
-- Analisis cohort berdasarkan bulan pendaftaran
WITH first_purchase AS (
    SELECT 
        t.id_pelanggan,
        MIN(DATE_TRUNC('month', t.tanggal_transaksi)) as first_purchase_month,
        DATE_TRUNC('month', p.tanggal_daftar) as cohort_month
    FROM tabel_transaksi t
    INNER JOIN tabel_pelanggan p ON t.id_pelanggan = p.id_pelanggan
    WHERE t.status = 'completed'
    GROUP BY t.id_pelanggan, DATE_TRUNC('month', p.tanggal_daftar)
),
cohort_data AS (
    SELECT 
        cohort_month,
        first_purchase_month,
        COUNT(DISTINCT id_pelanggan) as customers,
        -- Hitung selisih bulan antara cohort dan first purchase
        EXTRACT(MONTH FROM AGE(first_purchase_month, cohort_month)) as month_number
    FROM first_purchase
    GROUP BY cohort_month, first_purchase_month
)
SELECT 
    cohort_month,
    month_number,
    customers,
    -- Persentase retention
    ROUND(
        customers * 100.0 / FIRST_VALUE(customers) OVER (
            PARTITION BY cohort_month 
            ORDER BY month_number
        ), 2
    ) as retention_rate
FROM cohort_data
ORDER BY cohort_month, month_number;
```

**Penjelasan**: Cohort analysis fundamental untuk understanding customer retention. Data Scientist menggunakan ini untuk measuring product-market fit dan optimizing customer acquisition strategy.

---

## 9. Analisis Perilaku Pengguna

### 9.1 Daily Active Users (DAU) dan Retention

```sql
-- Hitung Daily Active Users
SELECT 
    tanggal_aktivitas,
    COUNT(DISTINCT id_pelanggan) as dau,           -- Daily Active Users
    
    -- Moving average 7 hari
    AVG(COUNT(DISTINCT id_pelanggan)) OVER (
        ORDER BY tanggal_aktivitas 
        ROWS BETWEEN 6 PRECEDING AND CURRENT ROW   -- Window 7 hari
    ) as dau_7day_avg
FROM tabel_aktivitas
WHERE jenis_aktivitas IN ('login', 'purchase', 'view_product')
GROUP BY tanggal_aktivitas
ORDER BY tanggal_aktivitas;

-- User retention rate
WITH user_activity AS (
    SELECT 
        id_pelanggan,
        tanggal_aktivitas,
        LAG(tanggal_aktivitas) OVER (
            PARTITION BY id_pelanggan 
            ORDER BY tanggal_aktivitas
        ) as previous_activity_date
    FROM tabel_aktivitas
    WHERE jenis_aktivitas = 'login'
)
SELECT 
    tanggal_aktivitas,
    COUNT(DISTINCT id_pelanggan) as total_users,
    COUNT(DISTINCT CASE 
        WHEN previous_activity_date = tanggal_aktivitas - INTERVAL '1 day' 
        THEN id_pelanggan 
    END) as returning_users,                       -- User yang kembali hari berikutnya
    
    ROUND(
        COUNT(DISTINCT CASE 
            WHEN previous_activity_date = tanggal_aktivitas - INTERVAL '1 day' 
            THEN id_pelanggan 
        END) * 100.0 / COUNT(DISTINCT id_pelanggan), 2
    ) as day_1_retention_rate
FROM user_activity
GROUP BY tanggal_aktivitas
ORDER BY tanggal_aktivitas;
```

**Penjelasan**: DAU dan retention metrics adalah KPI utama untuk product manager dan growth team. Data Analyst menggunakan moving average untuk smoothing daily fluctuation.

### 9.2 Segmentasi Pelanggan berdasarkan RFM

```sql
-- RFM Analysis (Recency, Frequency, Monetary)
WITH rfm_data AS (
    SELECT 
        p.id_pelanggan,
        p.nama,
        
        -- Recency: Hari sejak transaksi terakhir
        CURRENT_DATE - MAX(DATE(t.tanggal_transaksi)) as recency_days,
        
        -- Frequency: Jumlah transaksi
        COUNT(t.id_transaksi) as frequency,
        
        -- Monetary: Total nilai transaksi
        SUM(t.total_harga) as monetary_value
    FROM tabel_pelanggan p
    INNER JOIN tabel_transaksi t ON p.id_pelanggan = t.id_pelanggan
    WHERE t.status = 'completed'
    GROUP BY p.id_pelanggan, p.nama
),
rfm_scores AS (
    SELECT 
        *,
        -- Scoring Recency (1-5, dimana 5 = paling recent)
        CASE 
            WHEN recency_days <= 30 THEN 5
            WHEN recency_days <= 60 THEN 4
            WHEN recency_days <= 90 THEN 3
            WHEN recency_days <= 180 THEN 2
            ELSE 1
        END as recency_score,
        
        -- Scoring Frequency menggunakan quintile
        NTILE(5) OVER (ORDER BY frequency) as frequency_score,
        
        -- Scoring Monetary menggunakan quintile
        NTILE(5) OVER (ORDER BY monetary_value) as monetary_score
    FROM rfm_data
)
SELECT 
    id_pelanggan,
    nama,
    recency_days,
    frequency,
    monetary_value,
    recency_score,
    frequency_score,
    monetary_score,
    
    -- Kombinasi RFM score
    CONCAT(recency_score, frequency_score, monetary_score) as rfm_combined,
    
    -- Segmentasi pelanggan
    CASE 
        WHEN recency_score >= 4 AND frequency_score >= 4 AND monetary_score >= 4 
        THEN 'Champions'                   -- Pelanggan terbaik
        WHEN recency_score >= 3 AND frequency_score >= 3 AND monetary_score >= 3 
        THEN 'Loyal Customers'             -- Pelanggan loyal
        WHEN recency_score >= 4 AND frequency_score <= 2 
        THEN 'New Customers'               -- Pelanggan baru
        WHEN recency_score <= 2 AND frequency_score >= 3 
        THEN 'At Risk'                     -- Berisiko churn
        WHEN recency_score <= 2 AND frequency_score <= 2 
        THEN 'Lost Customers'              -- Pelanggan hilang
        ELSE 'Regular Customers'           -- Pelanggan reguler
    END as customer_segment
FROM rfm_scores
ORDER BY monetary_value DESC;