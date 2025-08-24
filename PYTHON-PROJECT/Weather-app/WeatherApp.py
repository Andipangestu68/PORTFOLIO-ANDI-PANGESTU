from flask import Flask, request, render_template, jsonify
import threading
import requests
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from io import BytesIO
import base64
from sklearn.model_selection import train_test_split
import xgboost as xgb

# Inisialisasi Flask
app = Flask(__name__)

# Fungsi untuk mengambil data cuaca dari OpenWeather API
def get_weather_data(api_key, city):
    url = "http://api.openweathermap.org/data/2.5/forecast"
    params = {"q": city, "appid": api_key, "units": "metric"}
    response = requests.get(url, params=params)
    if response.status_code == 200:
        return response.json()
    else:
        raise Exception(f"Error fetching data: {response.status_code}")

# Fungsi untuk mengubah data cuaca ke DataFrame
def parse_weather_data(weather_data):
    df = pd.json_normalize(weather_data, record_path=['list'])
    df['dt'] = pd.to_datetime(df['dt'], unit='s')
    df.set_index('dt', inplace=True)
    df = df[['main.temp_max', 'main.humidity', 'wind.speed']]
    df.columns = ['Temp_Max', 'Humidity', 'Wind_Speed']
    return df

# Route untuk halaman utama
@app.route('/')
def index():
    cities = ['Jakarta', 'Surabaya', 'Bandung', 'Yogyakarta', 'Medan']  # Daftar kota untuk combo box
    return render_template('index.html', cities=cities)

# Route untuk memproses data cuaca dan melakukan peramalan
@app.route('/forecast', methods=['POST'])
def forecast():
    # Ambil input dari pengguna
    api_key = request.form['api_key']
    city = request.form['city']
    days = int(request.form['days'])

    try:
        # Ambil dan parse data cuaca
        weather_data = get_weather_data(api_key, city)
        data = parse_weather_data(weather_data)

        # Siapkan data untuk model
        data['Hour'] = data.index.hour
        data['Day'] = data.index.dayofweek
        data['Month'] = data.index.month
        X = data[['Hour', 'Day', 'Month']]
        y_temp = data['Temp_Max']
        y_humidity = data['Humidity']
        y_wind_speed = data['Wind_Speed']

        # Split data untuk masing-masing target
        X_train, X_test, y_temp_train, y_temp_test = train_test_split(X, y_temp, test_size=0.2, random_state=42)
        X_train, X_test, y_humidity_train, y_humidity_test = train_test_split(X, y_humidity, test_size=0.2, random_state=42)
        X_train, X_test, y_wind_speed_train, y_wind_speed_test = train_test_split(X, y_wind_speed, test_size=0.2, random_state=42)

        # Model untuk suhu (Temp_Max)
        dtrain_temp = xgb.DMatrix(X_train, label=y_temp_train)
        dtest_temp = xgb.DMatrix(X_test, label=y_temp_test)
        params_temp = {'objective': 'reg:squarederror', 'colsample_bytree': 0.3, 'learning_rate': 0.1, 'max_depth': 5, 'alpha': 10}
        model_temp = xgb.train(params_temp, dtrain_temp, num_boost_round=100)

        # Model untuk kelembapan (Humidity)
        dtrain_humidity = xgb.DMatrix(X_train, label=y_humidity_train)
        dtest_humidity = xgb.DMatrix(X_test, label=y_humidity_test)
        params_humidity = {'objective': 'reg:squarederror', 'colsample_bytree': 0.3, 'learning_rate': 0.1, 'max_depth': 5, 'alpha': 10}
        model_humidity = xgb.train(params_humidity, dtrain_humidity, num_boost_round=100)

        # Model untuk kecepatan angin (Wind_Speed)
        dtrain_wind_speed = xgb.DMatrix(X_train, label=y_wind_speed_train)
        dtest_wind_speed = xgb.DMatrix(X_test, label=y_wind_speed_test)
        params_wind_speed = {'objective': 'reg:squarederror', 'colsample_bytree': 0.3, 'learning_rate': 0.1, 'max_depth': 5, 'alpha': 10}
        model_wind_speed = xgb.train(params_wind_speed, dtrain_wind_speed, num_boost_round=100)

        # Peramalan masa depan
        future_hours = np.array([[i % 24, (i // 24) % 7, (i // 24) % 12 + 1] for i in range(len(data), len(data) + days * 24)])
        future_df = pd.DataFrame(future_hours, columns=['Hour', 'Day', 'Month'])
        future_dmatrix = xgb.DMatrix(future_df)

        future_predictions_temp = model_temp.predict(future_dmatrix)
        future_predictions_humidity = model_humidity.predict(future_dmatrix)
        future_predictions_wind_speed = model_wind_speed.predict(future_dmatrix)

        # Format hasil peramalan
        future_dates = pd.date_range(start=data.index[-1] + pd.DateOffset(hours=1), periods=days * 24, freq='H')
        forecast_df_temp = pd.DataFrame({'Forecast_Temp_Max': future_predictions_temp}, index=future_dates)
        forecast_df_humidity = pd.DataFrame({'Forecast_Humidity': future_predictions_humidity}, index=future_dates)
        forecast_df_wind_speed = pd.DataFrame({'Forecast_Wind_Speed': future_predictions_wind_speed}, index=future_dates)

        # Plot hasil peramalan
        fig, axs = plt.subplots(3, 1, figsize=(10, 15))

        # Plot suhu maksimum
        axs[0].plot(data['Temp_Max'], label='Suhu Maksimum Aktual', color='blue', linewidth=1.5)
        axs[0].plot(forecast_df_temp, label='Peramalan Suhu Maksimum', color='red', linestyle='--', linewidth=1.5)
        axs[0].set_title(f'Peramalan Suhu Maksimum di {city}', fontsize=14)
        axs[0].set_xlabel('Tanggal', fontsize=12)
        axs[0].set_ylabel('Suhu Maksimum (°C)', fontsize=12)
        axs[0].legend()
        axs[0].grid(True, which='both', linestyle='--', linewidth=0.5)

        # Plot kelembapan
        axs[1].plot(data['Humidity'], label='Kelembapan Aktual', color='green', linewidth=1.5)
        axs[1].plot(forecast_df_humidity, label='Peramalan Kelembapan', color='orange', linestyle='--', linewidth=1.5)
        axs[1].set_title(f'Peramalan Kelembapan di {city}', fontsize=14)
        axs[1].set_xlabel('Tanggal', fontsize=12)
        axs[1].set_ylabel('Kelembapan (%)', fontsize=12)
        axs[1].legend()
        axs[1].grid(True, which='both', linestyle='--', linewidth=0.5)

        # Plot kecepatan angin
        axs[2].plot(data['Wind_Speed'], label='Kecepatan Angin Aktual', color='purple', linewidth=1.5)
        axs[2].plot(forecast_df_wind_speed, label='Peramalan Kecepatan Angin', color='brown', linestyle='--', linewidth=1.5)
        axs[2].set_title(f'Peramalan Kecepatan Angin di {city}', fontsize=14)
        axs[2].set_xlabel('Tanggal', fontsize=12)
        axs[2].set_ylabel('Kecepatan Angin (m/s)', fontsize=12)
        axs[2].legend()
        axs[2].grid(True, which='both', linestyle='--', linewidth=0.5)

        # Sesuaikan layout agar tidak ada elemen yang terpotong
        plt.tight_layout()

        # Simpan plot ke buffer
        buffer = BytesIO()
        plt.savefig(buffer, format='png')
        buffer.seek(0)
        image_base64 = base64.b64encode(buffer.getvalue()).decode('utf-8')
        buffer.close()

        return render_template('forecast.html', city=city, days=days, image_base64=image_base64)


    except Exception as e:
        return jsonify({'error': str(e)})

# menjalankan Flask di thread
def run_app():
    app.run(debug=True, use_reloader=False)  # Menonaktifkan reloader untuk menghindari error di notebook

# Menjalankan Flask dalam thread
threading.Thread(target=run_app).start()