# Import library yang dibutuhkan
import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from sklearn.metrics import accuracy_score, classification_report, confusion_matrix
import joblib
from xgboost import XGBClassifier

# 1. Load Dataset dari CSV
# Pastikan path sesuai dengan lokasi dataset Anda
df = pd.read_csv('datasets/heart_disease_data.csv')

# 2. Eksplorasi Data
print("Baris pertama dataset:")
print(df.head())
print("\nInfo dataset:")
print(df.info())

# 3. Preprocessing Data
# Isi missing values (jika ada) dengan mean
df.fillna(df.mean(), inplace=True)

# Pisahkan fitur (X) dan target (y)
X = df.drop('num', axis=1)  # 'num' adalah kolom target
y = df['num'].apply(lambda x: 1 if x > 0 else 0)  # Konversi target jadi biner (0 = no risk, 1 = at risk)

# 4. Bagi Dataset menjadi Data Latih dan Uji
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.3, random_state=42)

# 5. Standarisasi Fitur
scaler = StandardScaler()
X_train_scaled = scaler.fit_transform(X_train)
X_test_scaled = scaler.transform(X_test)

# 6. Inisialisasi dan Latih Model XGBoost
model = XGBClassifier(use_label_encoder=False, eval_metric='logloss', random_state=42)
model.fit(X_train_scaled, y_train)

# 7. Evaluasi Model
y_pred = model.predict(X_test_scaled)
accuracy = accuracy_score(y_test, y_pred)
print(f"\nAkurasi Model: {accuracy * 100:.2f}%")
print("\nConfusion Matrix:")
print(confusion_matrix(y_test, y_pred))
print("\nClassification Report:")
print(classification_report(y_test, y_pred))

# 8. Simpan Model dan Scaler ke File .pkl
joblib.dump(model, 'model/heart_xgb_model.pkl')
joblib.dump(scaler, 'model/scaler.pkl')

print("\nModel dan scaler berhasil disimpan!")
