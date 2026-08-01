package com.example.deepnettsaiservice.predictor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import deepnetts.net.FeedForwardNetwork;
import deepnetts.net.layers.AbstractLayer;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.loss.LossType;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public final class CreditCardFraudPredictor {

  private static final String MODEL_RESOURCE = "models/fraud_model.dnet";

  private static final String PREPROCESSING_RESOURCE = "models/preprocessing_config.json";

  private static final float PREDICTION_THRESHOLD = 0.50f;

  private static final int NUMBER_OF_INPUTS = 8;

  private static final int NUMBER_OF_OUTPUTS = 1;

  private final FeedForwardNetwork neuralNetwork;
  private final float[] maximumValues;

  public CreditCardFraudPredictor(ObjectMapper objectMapper) throws Exception {

    neuralNetwork = loadNetwork();

    maximumValues = loadMaximumValues(objectMapper);

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

    if (output == null || output.length != NUMBER_OF_OUTPUTS) {

      throw new IllegalStateException("Model nije vratio očekivani broj izlaza.");
    }

    float fraudProbability = output[0];

    boolean fraud = fraudProbability >= PREDICTION_THRESHOLD;

    return new PredictionResult(fraudProbability, fraud);
  }

  private FeedForwardNetwork loadNetwork() throws Exception {

    ClassPathResource modelResource = new ClassPathResource(MODEL_RESOURCE);

    if (!modelResource.exists()) {
      throw new IllegalStateException("Model ne postoji u resources: " + MODEL_RESOURCE);
    }

    Path temporaryModelFile = Files.createTempFile("fraud-model-", ".dnet");

    temporaryModelFile.toFile().deleteOnExit();

    try (InputStream inputStream = modelResource.getInputStream()) {

      Files.copy(inputStream, temporaryModelFile, StandardCopyOption.REPLACE_EXISTING);
    }

    FeedForwardNetwork loadedNetwork =
        FeedForwardNetwork.load(temporaryModelFile.toString(), FeedForwardNetwork.class);

    FeedForwardNetwork targetNetwork = createNetwork();

    copyLearnedParameters(loadedNetwork, targetNetwork);

    return targetNetwork;
  }

  private float[] loadMaximumValues(ObjectMapper objectMapper) throws Exception {

    ClassPathResource configResource = new ClassPathResource(PREPROCESSING_RESOURCE);

    if (!configResource.exists()) {
      throw new IllegalStateException(
          "Preprocessing config ne postoji u resources: " + PREPROCESSING_RESOURCE);
    }

    PreprocessingConfig config;

    try (InputStream inputStream = configResource.getInputStream()) {

      config = objectMapper.readValue(inputStream, PreprocessingConfig.class);
    }

    if (config.maximumValues() == null) {
      throw new IllegalStateException("Preprocessing config nema maximumValues.");
    }

    Map<String, Double> values = config.maximumValues();

    float[] loadedMaximumValues = {
      getMaximumValue(values, "distanceFromHome"),
      getMaximumValue(values, "distanceFromLastTransaction"),
      getMaximumValue(values, "ratioToMedianPurchasePrice"),
      getMaximumValue(values, "repeatRetailer"),
      getMaximumValue(values, "usedChip"),
      getMaximumValue(values, "usedPinNumber"),
      getMaximumValue(values, "onlineOrder"),
      getMaximumValue(values, "amount")
    };

    if (loadedMaximumValues.length != NUMBER_OF_INPUTS) {

      throw new IllegalStateException("Nije učitan očekivani broj maksimuma.");
    }

    return loadedMaximumValues;
  }

  private float getMaximumValue(Map<String, Double> values, String featureName) {

    Double value = values.get(featureName);

    if (value == null) {
      throw new IllegalStateException("Nedostaje maksimalna vrednost za: " + featureName);
    }

    if (!Double.isFinite(value) || value <= 0.0) {

      throw new IllegalStateException("Neispravna maksimalna vrednost za: " + featureName);
    }

    return value.floatValue();
  }

  private float scale(double originalValue, int featureIndex) {

    if (!Double.isFinite(originalValue)) {
      throw new IllegalArgumentException("Ulazna vrednost mora biti konačan broj.");
    }

    if (featureIndex < 0 || featureIndex >= maximumValues.length) {

      throw new IllegalArgumentException("Neispravan indeks karakteristike: " + featureIndex);
    }

    float maximumValue = maximumValues[featureIndex];

    return (float) (originalValue / maximumValue);
  }

  private FeedForwardNetwork createNetwork() {

    return FeedForwardNetwork.builder()
        .addInputLayer(NUMBER_OF_INPUTS)
        .addFullyConnectedLayer(128, ActivationType.RELU)
        .addFullyConnectedLayer(64, ActivationType.RELU)
        .addOutputLayer(NUMBER_OF_OUTPUTS, ActivationType.SIGMOID)
        .lossFunction(LossType.CROSS_ENTROPY)
        .build();
  }

  private void copyLearnedParameters(
      FeedForwardNetwork sourceNetwork, FeedForwardNetwork targetNetwork) {

    if (sourceNetwork.getLayers().size() != targetNetwork.getLayers().size()) {

      throw new IllegalStateException("Sačuvani model nema očekivanu arhitekturu.");
    }

    for (int layerIndex = 1; layerIndex < sourceNetwork.getLayers().size(); layerIndex++) {

      AbstractLayer sourceLayer = sourceNetwork.getLayerAt(layerIndex);

      AbstractLayer targetLayer = targetNetwork.getLayerAt(layerIndex);

      targetLayer.setWeights(sourceLayer.getWeights());

      targetLayer.setBiases(sourceLayer.getBiases());
    }
  }

  public record PredictionResult(float fraudProbability, boolean fraud) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  private record PreprocessingConfig(Map<String, Double> maximumValues) {}
}
