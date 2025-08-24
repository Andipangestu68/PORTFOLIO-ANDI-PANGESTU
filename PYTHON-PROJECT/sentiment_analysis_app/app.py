from flask import Flask, render_template, request, jsonify
import pandas as pd
import numpy as np
import re
import string
from sklearn.model_selection import train_test_split
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.ensemble import RandomForestClassifier
from sklearn.svm import SVC
from sklearn.metrics import classification_report, accuracy_score
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Embedding, LSTM, Dense, Dropout, Bidirectional
from tensorflow.keras.preprocessing.text import Tokenizer
from tensorflow.keras.preprocessing.sequence import pad_sequences
import nltk
from nltk.corpus import stopwords
from nltk.tokenize import word_tokenize

# Inisialisasi Flask app
app = Flask(__name__)

# Pastikan Anda telah mengunduh stopwords NLTK terlebih dahulu
nltk.download('stopwords')
nltk.download('punkt')

# Load dataset dan model
dataset_path = "Indonlu_Sentiment.xlsx"
try:
    data = pd.read_excel(dataset_path)
except FileNotFoundError:
    print(f"File {dataset_path} tidak ditemukan. Pastikan path ke file sudah benar.")
    exit(1)

# Mapping label ke angka
label_mapping = {'positive': 0, 'negative': 1, 'neutral': 2}
data['Label'] = data['Label'].map(label_mapping)

# Preprocessing teks
def preprocess_text(text):
    text = text.lower()
    text = re.sub(r'http[s]?://\S+', '', text)
    text = re.sub(r'\d+', '', text)
    text = text.translate(str.maketrans('', '', string.punctuation))
    words = word_tokenize(text)
    stop_words = set(stopwords.words('indonesian'))
    words = [word for word in words if word not in stop_words]
    return ' '.join(words)

data['text_cleaned'] = data['Tweet'].apply(preprocess_text)

# Split dataset
X = data['text_cleaned'].values
y = data['Label'].values
X_train, X_temp, y_train, y_temp = train_test_split(X, y, test_size=0.4, random_state=42)
X_val, X_test, y_val, y_test = train_test_split(X_temp, y_temp, test_size=0.5, random_state=42)

# Tokenisasi dan padding
tokenizer = Tokenizer(num_words=10000, oov_token='<OOV>')
tokenizer.fit_on_texts(X_train)
max_len = 100
X_train_seq = tokenizer.texts_to_sequences(X_train)
X_val_seq = tokenizer.texts_to_sequences(X_val)
X_test_seq = tokenizer.texts_to_sequences(X_test)
X_train_pad = pad_sequences(X_train_seq, maxlen=max_len, padding='post')
X_val_pad = pad_sequences(X_val_seq, maxlen=max_len, padding='post')
X_test_pad = pad_sequences(X_test_seq, maxlen=max_len, padding='post')

# Model Bidirectional LSTM
model = Sequential([
    Embedding(10000, 128, input_length=max_len),
    Bidirectional(LSTM(64, return_sequences=True)),
    Dropout(0.5),
    LSTM(64),
    Dense(64, activation='relu'),
    Dropout(0.5),
    Dense(3, activation='softmax')
])
model.compile(optimizer='adam', loss='sparse_categorical_crossentropy', metrics=['accuracy'])

# Training model LSTM
model.fit(X_train_pad, y_train, validation_data=(X_val_pad, y_val), epochs=5, batch_size=32)

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/predict', methods=['POST'])
def predict():
    input_text = request.form.get('text')
    processed_text = preprocess_text(input_text)
    input_seq = tokenizer.texts_to_sequences([processed_text])
    input_pad = pad_sequences(input_seq, maxlen=max_len, padding='post')
    prediction = model.predict(input_pad)
    predicted_class = np.argmax(prediction)
    label_mapping_reverse = {0: 'positive', 1: 'negative', 2: 'neutral'}
    predicted_label = label_mapping_reverse[predicted_class]
    return jsonify({
        'input_text': input_text,
        'predicted_label': predicted_label
    })

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)
