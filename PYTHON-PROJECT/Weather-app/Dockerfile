# Gunakan Python image sebagai base image
FROM python:3.12-slim

# Set direktori kerja
WORKDIR /app

# Salin file requirements dan aplikasi ke container
COPY requirements.txt .
COPY . .

# Install dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Expose port Flask (default 5000)
EXPOSE 5000

# Jalankan aplikasi Flask
CMD ["python", "WeatherApp.py"]
