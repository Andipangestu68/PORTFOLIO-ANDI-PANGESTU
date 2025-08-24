# Import library yang diperlukan
import pandas as pd
from flask import Flask, request, jsonify
import joblib

# Inisialisasi aplikasi Flask
app = Flask(__name__)

# Load model dan scaler
model = joblib.load('model/heart_xgb_model.pkl')
scaler = joblib.load('model/scaler.pkl')

# Endpoint untuk prediksi
@app.route('/predict', methods=['POST'])
def predict():
    try:
        # Dapatkan data JSON dari request
        data = request.get_json()

        # Pastikan format input sesuai
        features = ['age', 'sex', 'cp', 'trestbps', 'chol', 'fbs',
                    'restecg', 'thalach', 'exang', 'oldpeak', 'slope', 'ca', 'thal']
        input_data = pd.DataFrame([data], columns=features)

        # Standarisasi input menggunakan scaler
        input_scaled = scaler.transform(input_data)

        # Prediksi menggunakan model
        prediction = model.predict(input_scaled)[0]
        probabilities = model.predict_proba(input_scaled)[0]

        # Tampilkan probabilitas dan risiko
        response = {
            "probability": {
                "no_risk": float(probabilities[0]),
                "at_risk": float(probabilities[1])
            },
            "risk": "at risk" if prediction == 1 else "no risk"
        }
        return jsonify(response)

    except Exception as e:
        return jsonify({"error": str(e)}), 500

# Jalankan aplikasi Flask
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
