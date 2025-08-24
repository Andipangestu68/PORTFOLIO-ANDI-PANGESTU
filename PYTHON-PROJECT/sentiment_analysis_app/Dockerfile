# Base image with Python 3
FROM python:3.11-slim

# Set the working directory in the container
WORKDIR /app

# Copy the current directory contents into the container at /app
COPY . .

# Copy the dataset into the container
COPY datasets/Indonlu_Sentiment.xlsx /datasets/Indonlu_Sentiment.xlsx

# Install system dependencies (e.g., for handling Excel files)
RUN apt-get update && apt-get install -y \
    libffi-dev \
    libssl-dev \
    build-essential \
    && rm -rf /var/lib/apt/lists/*

# Install Python dependencies
RUN pip install --no-cache-dir \
    pandas \
    numpy \
    scikit-learn \
    tensorflow \
    Flask \
    nltk \
    openpyxl


# Expose the port for the Flask app
EXPOSE 5000

# Run the Flask app
CMD ["python", "app.py"]
