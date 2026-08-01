package com.fraudmodel.deepnetts;

import deepnetts.net.FeedForwardNetwork;
import deepnetts.net.layers.AbstractLayer;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.loss.LossType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CreditCardFraudPredictor {

  private static final Path MODEL_PATH = Path.of("saved_model/fraud_model.dnet");

  private static final Path PREPROCESSING_CONFIG_PATH =
      Path.of("saved_model/preprocessing_config.json");

  private static final float PREDICTION_THRESHOLD = 0.50f;

  private final FeedForwardNetwork neuralNetwork;
  private final float[] maximumValues;

  public CreditCardFraudPredictor() throws Exception {

    validateSavedFiles();

    FeedForwardNetwork loadedNetwork =
        FeedForwardNetwork.load(MODEL_PATH.toString(), FeedForwardNetwork.class);

    neuralNetwork = createNetwork();

    copyLearnedParameters(loadedNetwork, neuralNetwork);

    maximumValues = loadMaximumValues();

    System.out.println("DeepNetts model i preprocessing su uspešno učitani.");
  }

  public PredictionResult predict(
      double distanceFromHome,
      double distanceFromLastTransaction,
      double ratioToMedianPurchasePrice,
      double repeatRetailer,
      double usedChip,
      double usedPinNumber,
      double onlineOrder,
      double amount) {

    float[] scaledInput = {
      scale(distanceFromHome, 0),
      scale(distanceFromLastTransaction, 1),
      scale(ratioToMedianPurchasePrice, 2),
      scale(repeatRetailer, 3),
      scale(usedChip, 4),
      scale(usedPinNumber, 5),
      scale(onlineOrder, 6),
      scale(amount, 7)
    };

    float[] output = neuralNetwork.predict(scaledInput);

    if (output.length != 1) {
      throw new IllegalStateException("Model nije vratio tačno jedan izlaz.");
    }

    float fraudProbability = output[0];

    boolean fraud = fraudProbability >= PREDICTION_THRESHOLD;

    return new PredictionResult(fraudProbability, fraud);
  }

  private float scale(double originalValue, int featureIndex) {

    if (!Double.isFinite(originalValue)) {
      throw new IllegalArgumentException("Ulazna vrednost mora biti konačan broj.");
    }

    float maximumValue = maximumValues[featureIndex];

    if (maximumValue <= 0.0f) {
      throw new IllegalStateException("Maksimalna vrednost mora biti veća od nule.");
    }

    return (float) (originalValue / maximumValue);
  }

  private static float[] loadMaximumValues() throws Exception {

    String json = Files.readString(PREPROCESSING_CONFIG_PATH);

    return new float[] {
      readJsonNumber(json, "distanceFromHome"),
      readJsonNumber(json, "distanceFromLastTransaction"),
      readJsonNumber(json, "ratioToMedianPurchasePrice"),
      readJsonNumber(json, "repeatRetailer"),
      readJsonNumber(json, "usedChip"),
      readJsonNumber(json, "usedPinNumber"),
      readJsonNumber(json, "onlineOrder"),
      readJsonNumber(json, "amount")
    };
  }

  private static float readJsonNumber(String json, String propertyName) {

    Pattern pattern =
        Pattern.compile(
            "\""
                + Pattern.quote(propertyName)
                + "\"\\s*:\\s*"
                + "([-+]?[0-9]*\\.?[0-9]+"
                + "(?:[eE][-+]?[0-9]+)?)");

    Matcher matcher = pattern.matcher(json);

    if (!matcher.find()) {
      throw new IllegalStateException("Nedostaje vrednost za: " + propertyName);
    }

    return Float.parseFloat(matcher.group(1));
  }

  private static FeedForwardNetwork createNetwork() {

    return FeedForwardNetwork.builder()
        .addInputLayer(8)
        .addFullyConnectedLayer(128, ActivationType.RELU)
        .addFullyConnectedLayer(64, ActivationType.RELU)
        .addOutputLayer(1, ActivationType.SIGMOID)
        .lossFunction(LossType.CROSS_ENTROPY)
        .build();
  }

  private static void copyLearnedParameters(
      FeedForwardNetwork sourceNetwork, FeedForwardNetwork targetNetwork) {

    if (sourceNetwork.getLayers().size() != targetNetwork.getLayers().size()) {

      throw new IllegalStateException("Sačuvani model nema očekivanu arhitekturu.");
    }

    for (int i = 1; i < sourceNetwork.getLayers().size(); i++) {

      AbstractLayer sourceLayer = sourceNetwork.getLayerAt(i);

      AbstractLayer targetLayer = targetNetwork.getLayerAt(i);

      targetLayer.setWeights(sourceLayer.getWeights());

      targetLayer.setBiases(sourceLayer.getBiases());
    }
  }

  private static void validateSavedFiles() {

    if (!Files.exists(MODEL_PATH)) {
      throw new IllegalStateException("Model ne postoji: " + MODEL_PATH);
    }

    if (!Files.exists(PREPROCESSING_CONFIG_PATH)) {
      throw new IllegalStateException(
          "Preprocessing config ne postoji: " + PREPROCESSING_CONFIG_PATH);
    }
  }

  public record PredictionResult(float fraudProbability, boolean fraud) {}

  public static void main(String[] args) throws Exception {

    CreditCardFraudPredictor predictor = new CreditCardFraudPredictor();

    PredictionResult result = predictor.predict(10.0, 2.0, 1.5, 1.0, 1.0, 0.0, 1.0, 150.0);

    System.out.println("Fraud probability: " + result.fraudProbability());

    System.out.println("Fraud: " + result.fraud());
  }
}
