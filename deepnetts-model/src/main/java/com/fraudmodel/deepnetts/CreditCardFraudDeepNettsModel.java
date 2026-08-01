package com.fraudmodel.deepnetts;

import deepnetts.data.DataSets;
import deepnetts.data.MLDataItem;
import deepnetts.data.norm.MaxScaler;
import deepnetts.net.FeedForwardNetwork;
import deepnetts.net.layers.AbstractLayer;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.loss.LossType;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.visrec.ml.data.DataSet;
import javax.visrec.ml.eval.EvaluationMetrics;

public final class CreditCardFraudDeepNettsModel {

  private static final boolean TRAIN_MODEL = true;

  private static final Path SAVED_MODEL_DIRECTORY = Path.of("saved_model");

  private static final Path MODEL_PATH = SAVED_MODEL_DIRECTORY.resolve("fraud_model.dnet");

  private static final Path PREPROCESSING_CONFIG_PATH =
      SAVED_MODEL_DIRECTORY.resolve("preprocessing_config.json");

  private static final int NUMBER_OF_INPUTS = 8;
  private static final int NUMBER_OF_OUTPUTS = 1;

  private CreditCardFraudDeepNettsModel() {}

  public static void main(String[] args) {

    try {

      Files.createDirectories(SAVED_MODEL_DIRECTORY);

      if (!Files.exists(DatasetGenerator.OUTPUT_DATASET)) {

        System.out.println("Dataset sa amount kolonom ne postoji.");

        System.out.println("Generišem novi dataset...");

        DatasetGenerator.generateDataset(
            DatasetGenerator.INPUT_DATASET, DatasetGenerator.OUTPUT_DATASET);
      }

      System.out.println("Učitavanje dataseta...");

      DataSet<MLDataItem> dataSet =
          (DataSet<MLDataItem>)
              DataSets.readCsv(
                  DatasetGenerator.OUTPUT_DATASET.toString(),
                  NUMBER_OF_INPUTS,
                  NUMBER_OF_OUTPUTS,
                  true);

      DataSet<MLDataItem>[] split = dataSet.split(0.8f);

      DataSet<MLDataItem> trainSet = split[0];

      DataSet<MLDataItem> testSet = split[1];

      MaxScaler maxScaler = DataSets.scaleToMax(trainSet);

      maxScaler.apply(testSet);

      Map<String, Double> maximumValues = extractMaximumValues(maxScaler);

      FeedForwardNetwork neuralNetwork;

      if (TRAIN_MODEL) {

        neuralNetwork = createNewNetwork();

        neuralNetwork.getTrainer().setLearningRate(0.001f);

        neuralNetwork.getTrainer().setStopEpochs(30);

        neuralNetwork.getTrainer().setBatchSize(64);

        System.out.println("Početak treniranja...");

        neuralNetwork.train(trainSet);

        neuralNetwork.save(MODEL_PATH.toString());

        savePreprocessingConfig(maximumValues);

        System.out.println("Model uspešno sačuvan u: " + MODEL_PATH);

        System.out.println("Preprocessing uspešno sačuvan u: " + PREPROCESSING_CONFIG_PATH);

      } else {

        validateSavedFiles();

        System.out.println("Učitavanje sačuvanog modela...");

        FeedForwardNetwork loadedNetwork =
            FeedForwardNetwork.load(MODEL_PATH.toString(), FeedForwardNetwork.class);

        neuralNetwork = createNewNetwork();

        copyLearnedParameters(loadedNetwork, neuralNetwork);

        System.out.println("Model i naučene težine " + "uspešno učitani.");
      }

      System.out.println("Evaluacija modela...");

      EvaluationMetrics metrics = neuralNetwork.test(testSet);

      System.out.println(metrics);

    } catch (Exception exception) {

      System.err.println("Greška prilikom rada sa modelom:");

      exception.printStackTrace();
    }
  }

  private static FeedForwardNetwork createNewNetwork() {

    return FeedForwardNetwork.builder()
        .addInputLayer(NUMBER_OF_INPUTS)
        .addFullyConnectedLayer(128, ActivationType.RELU)
        .addFullyConnectedLayer(64, ActivationType.RELU)
        .addOutputLayer(NUMBER_OF_OUTPUTS, ActivationType.SIGMOID)
        .lossFunction(LossType.CROSS_ENTROPY)
        .build();
  }

  private static Map<String, Double> extractMaximumValues(MaxScaler maxScaler) {

    float[] maximumInputs = maxScaler.getMaxInputs().getValues();

    if (maximumInputs.length != NUMBER_OF_INPUTS) {

      throw new IllegalStateException(
          "Scaler nema očekivanih " + NUMBER_OF_INPUTS + " maksimalnih vrednosti.");
    }

    Map<String, Double> maximumValues = new LinkedHashMap<>();

    maximumValues.put("distanceFromHome", (double) maximumInputs[0]);

    maximumValues.put("distanceFromLastTransaction", (double) maximumInputs[1]);

    maximumValues.put("ratioToMedianPurchasePrice", (double) maximumInputs[2]);

    maximumValues.put("repeatRetailer", (double) maximumInputs[3]);

    maximumValues.put("usedChip", (double) maximumInputs[4]);

    maximumValues.put("usedPinNumber", (double) maximumInputs[5]);

    maximumValues.put("onlineOrder", (double) maximumInputs[6]);

    maximumValues.put("amount", (double) maximumInputs[7]);

    validateMaximumValues(maximumValues);

    return maximumValues;
  }

  private static void validateMaximumValues(Map<String, Double> maximumValues) {

    for (Map.Entry<String, Double> entry : maximumValues.entrySet()) {

      if (entry.getValue() <= 0.0) {

        throw new IllegalStateException(
            "Maksimalna vrednost za " + entry.getKey() + " mora biti veća od 0.");
      }
    }
  }

  private static void savePreprocessingConfig(Map<String, Double> maximumValues) throws Exception {

    String json =
        """
        {
          "scalingMethod": "SCALE_TO_MAX",
          "formula": "scaledValue = originalValue / maximumValue",
          "featureOrder": [
            "distanceFromHome",
            "distanceFromLastTransaction",
            "ratioToMedianPurchasePrice",
            "repeatRetailer",
            "usedChip",
            "usedPinNumber",
            "onlineOrder",
            "amount"
          ],
          "maximumValues": {
            "distanceFromHome": %s,
            "distanceFromLastTransaction": %s,
            "ratioToMedianPurchasePrice": %s,
            "repeatRetailer": %s,
            "usedChip": %s,
            "usedPinNumber": %s,
            "onlineOrder": %s,
            "amount": %s
          }
        }
        """
            .formatted(
                maximumValues.get("distanceFromHome"),
                maximumValues.get("distanceFromLastTransaction"),
                maximumValues.get("ratioToMedianPurchasePrice"),
                maximumValues.get("repeatRetailer"),
                maximumValues.get("usedChip"),
                maximumValues.get("usedPinNumber"),
                maximumValues.get("onlineOrder"),
                maximumValues.get("amount"));

    Files.writeString(PREPROCESSING_CONFIG_PATH, json, StandardCharsets.UTF_8);
  }

  private static void validateSavedFiles() {

    if (!Files.exists(MODEL_PATH)) {

      throw new IllegalStateException("Sačuvani model ne postoji: " + MODEL_PATH);
    }

    if (!Files.exists(PREPROCESSING_CONFIG_PATH)) {

      throw new IllegalStateException(
          "Preprocessing config ne postoji: " + PREPROCESSING_CONFIG_PATH);
    }
  }

  private static void copyLearnedParameters(
      FeedForwardNetwork sourceNetwork, FeedForwardNetwork targetNetwork) {

    if (sourceNetwork.getLayers().size() != targetNetwork.getLayers().size()) {

      throw new IllegalStateException("Sačuvani model nema istu arhitekturu " + "kao nova mreža.");
    }

    for (int i = 1; i < sourceNetwork.getLayers().size(); i++) {

      AbstractLayer sourceLayer = sourceNetwork.getLayerAt(i);

      AbstractLayer targetLayer = targetNetwork.getLayerAt(i);

      targetLayer.setWeights(sourceLayer.getWeights());

      targetLayer.setBiases(sourceLayer.getBiases());
    }
  }
}
