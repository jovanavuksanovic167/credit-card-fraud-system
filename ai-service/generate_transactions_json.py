import json
import os
import joblib

import numpy as np
import pandas as pd
from tensorflow import keras


DATASET_PATH = "data/card_transdata.csv"

MODEL_PATH = "saved_model/fraud_detection_model.keras"
SCALER_PATH = "saved_model/scaler.pkl"
CONFIG_PATH = "saved_model/model_config.json"

OUTPUT_PATH = "../verification-optimization-frontend/public/transactions.json"

TARGET_COLUMN = "fraud"

BINARY_FEATURES = [
    "repeatRetailer",
    "usedChip",
    "usedPinNumber",
    "onlineOrder"
]


def load_dataset():
    data = pd.read_csv(DATASET_PATH)

    data = data.rename(columns={
        "distance_from_home": "distanceFromHome",
        "distance_from_last_transaction": "distanceFromLastTransaction",
        "ratio_to_median_purchase_price": "ratioToMedianPurchasePrice",
        "repeat_retailer": "repeatRetailer",
        "used_chip": "usedChip",
        "used_pin_number": "usedPinNumber",
        "online_order": "onlineOrder"
    })

    required_columns = [
        "distanceFromHome",
        "distanceFromLastTransaction",
        "ratioToMedianPurchasePrice",
        "repeatRetailer",
        "usedChip",
        "usedPinNumber",
        "onlineOrder",
        "fraud"
    ]

    data = data[required_columns].copy()

    for column in BINARY_FEATURES:
        data[column] = data[column].astype(int)

    data[TARGET_COLUMN] = data[TARGET_COLUMN].astype(int)

    return data


def generate_amount(row):
    base_amount = np.random.uniform(10, 300)
    amount = base_amount * row["ratioToMedianPurchasePrice"]

    if row[TARGET_COLUMN] == 1:
        amount *= np.random.uniform(1.5, 3.5)

    return round(float(amount), 2)


def add_amount_column(data):
    np.random.seed(42)

    data = data.copy()
    data["amount"] = data.apply(generate_amount, axis=1)

    return data


def add_abandon_column(data):
    np.random.seed(42)

    data = data.copy()
    data["isAbandon"] = 0

    abandon_indices = data.sample(frac=0.25, random_state=42).index
    data.loc[abandon_indices, "isAbandon"] = 1

    return data


def load_model_resources():
    model = keras.models.load_model(MODEL_PATH)
    scaler = joblib.load(SCALER_PATH)

    with open(CONFIG_PATH, "r") as file:
        config = json.load(file)

    return model, scaler, config


def preprocess_for_prediction(data, scaler, config):
    feature_columns = config["featureColumns"]
    numeric_features = config["numericFeatures"]
    uses_log_transform = config.get("usesLogTransform", True)

    X = data[feature_columns].copy()

    if uses_log_transform:
        X[numeric_features] = np.log1p(X[numeric_features])

    X[numeric_features] = scaler.transform(X[numeric_features])

    return X


def take_stratified_sample(data, sample_size=500000):
    fraud_rate = data[TARGET_COLUMN].mean()

    fraud_count = round(sample_size * fraud_rate)
    regular_count = sample_size - fraud_count

    regular_data = data[data[TARGET_COLUMN] == 0]
    fraud_data = data[data[TARGET_COLUMN] == 1]

    regular_sample = regular_data.sample(
        n=regular_count,
        random_state=42
    )

    fraud_sample = fraud_data.sample(
        n=fraud_count,
        random_state=42
    )

    sampled_data = pd.concat(
        [regular_sample, fraud_sample],
        ignore_index=True
    )

    sampled_data = sampled_data.sample(
        frac=1,
        random_state=42
    ).reset_index(drop=True)

    return sampled_data


def generate_json():
    print("Učitavanje dataseta...")
    data = load_dataset()

    print("Uzimanje stratifikovanog uzorka od 100.000 transakcija...")
    data = take_stratified_sample(data, sample_size=500000)

    print("Dodavanje amount kolone...")
    data = add_amount_column(data)

    print("Dodavanje isAbandon kolone...")
    data = add_abandon_column(data)

    print("Učitavanje modela, scalera i konfiguracije...")
    model, scaler, config = load_model_resources()

    print("Preprocesiranje podataka...")
    X_processed = preprocess_for_prediction(data, scaler, config)

    print("Računanje fraud probability vrednosti...")
    probabilities = model.predict(X_processed, verbose=1).flatten()

    data["fraudProbability"] = probabilities

    output_columns = [
        "amount",
        "distanceFromHome",
        "distanceFromLastTransaction",
        "ratioToMedianPurchasePrice",
        "repeatRetailer",
        "usedChip",
        "usedPinNumber",
        "onlineOrder",
        "fraud",
        "isAbandon",
        "fraudProbability"
    ]

    output_data = data[output_columns].copy()

    output_data["amount"] = output_data["amount"].round(2)
    output_data["fraudProbability"] = output_data["fraudProbability"].round(6)

    os.makedirs(os.path.dirname(OUTPUT_PATH), exist_ok=True)

    print(f"Čuvanje JSON fajla u: {OUTPUT_PATH}")

    output_data.to_json(
        OUTPUT_PATH,
        orient="records",
        force_ascii=False,
        indent=2
    )

    print("Gotovo.")
    print(f"Broj transakcija: {len(output_data)}")
    print(f"Abandon rate: {output_data['isAbandon'].mean() * 100:.2f}%")
    print(f"Fraud rate: {output_data['fraud'].mean() * 100:.2f}%")


if __name__ == "__main__":
    generate_json()