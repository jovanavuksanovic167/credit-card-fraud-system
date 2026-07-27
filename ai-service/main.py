from contextlib import asynccontextmanager
from threading import Lock
from typing import List

import json
import os
import joblib

import pandas as pd
import numpy as np

from fastapi import FastAPI, Query
from pydantic import BaseModel

from apscheduler.schedulers.background import BackgroundScheduler

from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from sklearn.metrics import (
    accuracy_score,
    precision_score,
    recall_score,
    f1_score,
    roc_auc_score,
    average_precision_score,
    confusion_matrix
)
from sklearn.utils.class_weight import compute_class_weight

from tensorflow import keras
from tensorflow.keras import layers


DATASET_PATH = "data/card_transdata.csv"

MODEL_PATH = "saved_model/fraud_detection_model.keras"
SCALER_PATH = "saved_model/scaler.pkl"
CONFIG_PATH = "saved_model/model_config.json"

REQUIRED_COLUMNS = [
    "distanceFromHome",
    "distanceFromLastTransaction",
    "ratioToMedianPurchasePrice",
    "repeatRetailer",
    "usedChip",
    "usedPinNumber",
    "onlineOrder",
    "fraud"
]

NUMERIC_FEATURES = [
    "amount",
    "distanceFromHome",
    "distanceFromLastTransaction",
    "ratioToMedianPurchasePrice"
]

BINARY_FEATURES = [
    "repeatRetailer",
    "usedChip",
    "usedPinNumber",
    "onlineOrder"
]

FEATURE_COLUMNS = NUMERIC_FEATURES + BINARY_FEATURES
TARGET_COLUMN = "fraud"

model_lock = Lock()
model = None
scaler = None
model_config = None

scheduler = BackgroundScheduler()


class TransactionResponse(BaseModel):
    amount: float
    distanceFromHome: float
    distanceFromLastTransaction: float
    ratioToMedianPurchasePrice: float
    repeatRetailer: int
    usedChip: int
    usedPinNumber: int
    onlineOrder: int
    fraud: int
    fraudProbability: float
    aiPrediction: int


class DailyProcessingResponse(BaseModel):
    generatedTransactions: int
    trainingStatus: str
    accuracy: float
    precision: float
    recall: float
    f1Score: float
    rocAuc: float
    prAuc: float
    trueNegatives: int
    falsePositives: int
    falseNegatives: int
    truePositives: int
    
    transactions: List[TransactionResponse]


class RetrainingResponse(BaseModel):
    status: str
    generatedTransactions: int
    epochsTrained: int
    accuracy: float
    precision: float
    recall: float
    f1Score: float
    rocAuc: float
    prAuc: float
    trueNegatives: int
    falsePositives: int
    falseNegatives: int
    truePositives: int


def load_trained_resources():
    global model, scaler, model_config

    if not os.path.exists(MODEL_PATH):
        raise FileNotFoundError(f"Nije pronađen model: {MODEL_PATH}")

    if not os.path.exists(SCALER_PATH):
        raise FileNotFoundError(f"Nije pronađen scaler: {SCALER_PATH}")

    if not os.path.exists(CONFIG_PATH):
        raise FileNotFoundError(f"Nije pronađen config: {CONFIG_PATH}")

    loaded_model = keras.models.load_model(MODEL_PATH)
    loaded_scaler = joblib.load(SCALER_PATH)

    with open(CONFIG_PATH, "r") as file:
        loaded_config = json.load(file)

    with model_lock:
        model = loaded_model
        scaler = loaded_scaler
        model_config = loaded_config

    print("Model, scaler i config su uspešno učitani.")


@asynccontextmanager
async def lifespan(app: FastAPI):
    load_trained_resources()

    scheduler.add_job(
        func=retrain_model,
        trigger="interval",
        days=7,
        id="weekly_model_retraining",
        replace_existing=True
    )

    scheduler.start()
    print("Scheduler je pokrenut. Model će se automatski retrenirati na svakih 7 dana.")

    yield

    scheduler.shutdown()
    print("Scheduler je zaustavljen.")


app = FastAPI(
    title="Credit Card Fraud AI Service",
    lifespan=lifespan
)


@app.get("/status")
def status():
    threshold = None
    model_name = None

    if model_config is not None:
        threshold = model_config.get("threshold")
        model_name = model_config.get("modelName")

    return {
        "service": "Credit Card Fraud AI Service",
        "status": "RUNNING",
        "modelLoaded": model is not None,
        "modelName": model_name,
        "threshold": threshold
    }


def load_dataset() -> pd.DataFrame:
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

    missing_columns = [
        column for column in REQUIRED_COLUMNS
        if column not in data.columns
    ]

    if missing_columns:
        raise ValueError(f"Dataset nema potrebne kolone: {missing_columns}")

    data = data[REQUIRED_COLUMNS].copy()

    for column in BINARY_FEATURES:
        data[column] = data[column].astype(int)

    data[TARGET_COLUMN] = data[TARGET_COLUMN].astype(int)

    return data


@app.get("/dataset-info")
def dataset_info():
    data = load_dataset()

    return {
        "rows": len(data),
        "columns": list(data.columns),
        "fraudRate": float(data[TARGET_COLUMN].mean())
    }


@app.get("/dataset-analysis")
def dataset_analysis():
    data = load_dataset()

    total_transactions = len(data)

    fraud_counts = data[TARGET_COLUMN].value_counts().to_dict()
    fraud_percentages = (data[TARGET_COLUMN].value_counts(normalize=True) * 100).to_dict()

    regular_count = int(fraud_counts.get(0, 0))
    fraud_count = int(fraud_counts.get(1, 0))

    regular_percentage = float(fraud_percentages.get(0, 0))
    fraud_percentage = float(fraud_percentages.get(1, 0))

    binary_distribution = {}

    for column in BINARY_FEATURES:
        binary_distribution[column] = {
            "overallMean": float(data[column].mean()),
            "regularMean": float(data[data[TARGET_COLUMN] == 0][column].mean()),
            "fraudMean": float(data[data[TARGET_COLUMN] == 1][column].mean())
        }

    numeric_columns = [
        "distanceFromHome",
        "distanceFromLastTransaction",
        "ratioToMedianPurchasePrice"
    ]

    numeric_statistics = {}

    for column in numeric_columns:
        numeric_statistics[column] = {
            "overall": {
                "mean": float(data[column].mean()),
                "median": float(data[column].median()),
                "min": float(data[column].min()),
                "max": float(data[column].max())
            },
            "regular": {
                "mean": float(data[data[TARGET_COLUMN] == 0][column].mean()),
                "median": float(data[data[TARGET_COLUMN] == 0][column].median())
            },
            "fraud": {
                "mean": float(data[data[TARGET_COLUMN] == 1][column].mean()),
                "median": float(data[data[TARGET_COLUMN] == 1][column].median())
            }
        }

    return {
        "totalTransactions": total_transactions,
        "regularTransactions": regular_count,
        "fraudTransactions": fraud_count,
        "regularPercentage": regular_percentage,
        "fraudPercentage": fraud_percentage,
        "binaryDistribution": binary_distribution,
        "numericStatistics": numeric_statistics
    }


def generate_amount(row: dict) -> float:
    base_amount = np.random.uniform(10, 300)
    amount = base_amount * row["ratioToMedianPurchasePrice"]

    if row[TARGET_COLUMN] == 1:
        amount *= np.random.uniform(1.5, 3.5)

    return round(float(amount), 2)


def generate_synthetic_transactions(count: int) -> pd.DataFrame:
    dataset = load_dataset()

    fraud_rate = dataset[TARGET_COLUMN].mean()

    regular_data = dataset[dataset[TARGET_COLUMN] == 0]
    fraud_data = dataset[dataset[TARGET_COLUMN] == 1]

    rows = []

    for _ in range(count):
        is_fraud = int(np.random.random() < fraud_rate)

        reference_data = fraud_data if is_fraud == 1 else regular_data

        template = reference_data.sample(n=1).iloc[0].copy()

        row = {
            "distanceFromHome": float(template["distanceFromHome"]),
            "distanceFromLastTransaction": float(template["distanceFromLastTransaction"]),
            "ratioToMedianPurchasePrice": float(template["ratioToMedianPurchasePrice"]),
            "repeatRetailer": int(template["repeatRetailer"]),
            "usedChip": int(template["usedChip"]),
            "usedPinNumber": int(template["usedPinNumber"]),
            "onlineOrder": int(template["onlineOrder"]),
            TARGET_COLUMN: is_fraud
        }

        numeric_columns_without_amount = [
            "distanceFromHome",
            "distanceFromLastTransaction",
            "ratioToMedianPurchasePrice"
        ]

        for column in numeric_columns_without_amount:
            noise = np.random.normal(loc=1.0, scale=0.10)
            generated_value = row[column] * noise

            lower_bound = reference_data[column].quantile(0.001)
            upper_bound = reference_data[column].quantile(0.999)

            row[column] = float(np.clip(generated_value, lower_bound, upper_bound))

        row["amount"] = generate_amount(row)

        rows.append(row)

    synthetic_data = pd.DataFrame(rows)

    for column in BINARY_FEATURES:
        synthetic_data[column] = synthetic_data[column].astype(int)

    synthetic_data[TARGET_COLUMN] = synthetic_data[TARGET_COLUMN].astype(int)

    return synthetic_data


@app.get("/synthetic-sample")
def synthetic_sample():
    data = generate_synthetic_transactions(100)
    return data.to_dict(orient="records")


def preprocess_for_prediction(data: pd.DataFrame) -> pd.DataFrame:
    if scaler is None or model_config is None:
        raise RuntimeError("Model resources nisu učitani.")

    feature_columns = model_config["featureColumns"]
    numeric_features = model_config["numericFeatures"]
    uses_log_transform = model_config.get("usesLogTransform", True)

    X = data[feature_columns].copy()

    if uses_log_transform:
        X[numeric_features] = np.log1p(X[numeric_features])

    X[numeric_features] = scaler.transform(X[numeric_features])

    return X


def calculate_metrics(y_true, probabilities, threshold: float):
    predictions = (probabilities >= threshold).astype(int)

    tn, fp, fn, tp = confusion_matrix(
        y_true,
        predictions,
        labels=[0, 1]
    ).ravel()

    return {
        "accuracy": float(accuracy_score(y_true, predictions)),
        "precision": float(precision_score(y_true, predictions, zero_division=0)),
        "recall": float(recall_score(y_true, predictions, zero_division=0)),
        "f1Score": float(f1_score(y_true, predictions, zero_division=0)),
        "rocAuc": float(roc_auc_score(y_true, probabilities)),
        "prAuc": float(average_precision_score(y_true, probabilities)),
        "trueNegatives": int(tn),
        "falsePositives": int(fp),
        "falseNegatives": int(fn),
        "truePositives": int(tp)
    }





@app.post("/process-daily-transactions", response_model=DailyProcessingResponse)
def process_daily_transactions(count: int = Query(default=1000, ge=100, le=10000)):
    data = generate_synthetic_transactions(count)

    X_processed = preprocess_for_prediction(data)

    with model_lock:
        probabilities = model.predict(X_processed, verbose=0).flatten()
        threshold = float(model_config["threshold"])

    y_true = data[TARGET_COLUMN].copy()

    metrics = calculate_metrics(
        y_true=y_true,
        probabilities=probabilities,
        threshold=threshold
    )

    

    transactions = []

    for index, row in data.iterrows():
        probability = float(probabilities[index])
        ai_prediction = 1 if probability >= threshold else 0

        if ai_prediction == 0:
            continue

        transactions.append(TransactionResponse(
            amount=float(row["amount"]),
            distanceFromHome=float(row["distanceFromHome"]),
            distanceFromLastTransaction=float(row["distanceFromLastTransaction"]),
            ratioToMedianPurchasePrice=float(row["ratioToMedianPurchasePrice"]),
            repeatRetailer=int(row["repeatRetailer"]),
            usedChip=int(row["usedChip"]),
            usedPinNumber=int(row["usedPinNumber"]),
            onlineOrder=int(row["onlineOrder"]),
            fraud=int(row[TARGET_COLUMN]),
            fraudProbability=probability,
            aiPrediction=ai_prediction
        ))

    return DailyProcessingResponse(
        generatedTransactions=count,
        trainingStatus="PREDICTION_SUCCESS",
        accuracy=metrics["accuracy"],
        precision=metrics["precision"],
        recall=metrics["recall"],
        f1Score=metrics["f1Score"],
        rocAuc=metrics["rocAuc"],
        prAuc=metrics["prAuc"],
        trueNegatives=metrics["trueNegatives"],
        falsePositives=metrics["falsePositives"],
        falseNegatives=metrics["falseNegatives"],
        truePositives=metrics["truePositives"],
        
        transactions=transactions
    )


def build_improved_model(input_dim: int) -> keras.Sequential:
    new_model = keras.Sequential([
        layers.Input(shape=(input_dim,)),

        layers.Dense(128, activation="relu"),
        layers.BatchNormalization(),
        layers.Dropout(0.3),

        layers.Dense(64, activation="relu"),
        layers.BatchNormalization(),
        layers.Dropout(0.25),

        layers.Dense(32, activation="relu"),
        layers.BatchNormalization(),
        layers.Dropout(0.2),

        layers.Dense(1, activation="sigmoid")
    ])

    new_model.compile(
        optimizer=keras.optimizers.Adam(learning_rate=0.001),
        loss="binary_crossentropy",
        metrics=[
            keras.metrics.Precision(name="precision"),
            keras.metrics.Recall(name="recall"),
            keras.metrics.AUC(name="roc_auc"),
            keras.metrics.AUC(name="pr_auc", curve="PR")
        ]
    )

    return new_model


def prepare_train_test_data_for_retraining(data: pd.DataFrame):
    X = data[FEATURE_COLUMNS].copy()
    y = data[TARGET_COLUMN].copy()

    X_train, X_test, y_train, y_test = train_test_split(
        X,
        y,
        test_size=0.2,
        random_state=42,
        stratify=y
    )

    new_scaler = StandardScaler()

    X_train_processed = X_train.copy()
    X_test_processed = X_test.copy()

    X_train_processed[NUMERIC_FEATURES] = np.log1p(
        X_train_processed[NUMERIC_FEATURES]
    )

    X_test_processed[NUMERIC_FEATURES] = np.log1p(
        X_test_processed[NUMERIC_FEATURES]
    )

    X_train_processed[NUMERIC_FEATURES] = new_scaler.fit_transform(
        X_train_processed[NUMERIC_FEATURES]
    )

    X_test_processed[NUMERIC_FEATURES] = new_scaler.transform(
        X_test_processed[NUMERIC_FEATURES]
    )

    return X_train_processed, X_test_processed, y_train, y_test, new_scaler


def calculate_class_weights(y_train: pd.Series) -> dict:
    class_weights = compute_class_weight(
        class_weight="balanced",
        classes=np.unique(y_train),
        y=y_train
    )

    class_weight_dict = {
        int(class_label): float(weight)
        for class_label, weight in zip(np.unique(y_train), class_weights)
    }

    if 1 in class_weight_dict:
        class_weight_dict[1] = class_weight_dict[1] * 1.2

    return class_weight_dict


def save_retrained_resources(new_model, new_scaler):
    os.makedirs("saved_model", exist_ok=True)

    new_model.save(MODEL_PATH)
    joblib.dump(new_scaler, SCALER_PATH)

    new_config = {
        "numericFeatures": NUMERIC_FEATURES,
        "binaryFeatures": BINARY_FEATURES,
        "featureColumns": FEATURE_COLUMNS,
        "threshold": 0.80,
        "modelName": "Improved model with Batch Normalization",
        "usesLogTransform": True
    }

    with open(CONFIG_PATH, "w") as file:
        json.dump(new_config, file, indent=4)


def retrain_model():
    print("Pokrenuto automatsko retreniranje modela...")

    retrain_count = 10000
    data = generate_synthetic_transactions(retrain_count)

    X_train, X_test, y_train, y_test, new_scaler = prepare_train_test_data_for_retraining(data)

    class_weight_dict = calculate_class_weights(y_train)

    new_model = build_improved_model(input_dim=len(FEATURE_COLUMNS))

    early_stopping = keras.callbacks.EarlyStopping(
        monitor="val_pr_auc",
        mode="max",
        patience=5,
        restore_best_weights=True
    )

    history = new_model.fit(
        X_train,
        y_train,
        validation_split=0.2,
        epochs=30,
        batch_size=64,
        class_weight=class_weight_dict,
        callbacks=[early_stopping],
        verbose=1
    )

    probabilities = new_model.predict(X_test, verbose=0).flatten()

    metrics = calculate_metrics(
        y_true=y_test,
        probabilities=probabilities,
        threshold=0.80
    )

    save_retrained_resources(new_model, new_scaler)
    load_trained_resources()

    epochs_trained = len(history.history["loss"])

    print(f"Retreniranje završeno. Model je treniran {epochs_trained} epoha.")
    print(f"PR-AUC: {metrics['prAuc']:.4f}, ROC-AUC: {metrics['rocAuc']:.4f}")

    return {
        "status": "RETRAINING_SUCCESS",
        "generatedTransactions": retrain_count,
        "epochsTrained": epochs_trained,
        **metrics
    }


@app.post("/retrain-model", response_model=RetrainingResponse)
def retrain_model_endpoint():
    result = retrain_model()

    return RetrainingResponse(
        status=result["status"],
        generatedTransactions=result["generatedTransactions"],
        epochsTrained=result["epochsTrained"],
        accuracy=result["accuracy"],
        precision=result["precision"],
        recall=result["recall"],
        f1Score=result["f1Score"],
        rocAuc=result["rocAuc"],
        prAuc=result["prAuc"],
        trueNegatives=result["trueNegatives"],
        falsePositives=result["falsePositives"],
        falseNegatives=result["falseNegatives"],
        truePositives=result["truePositives"]
    )