package com.example.deepnettsaiservice.service;

import com.example.deepnettsaiservice.dto.TransactionDto;
import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class DatasetService {

  private static final double NOISE_PERCENTAGE = 0.05;

  private final Resource datasetResource;

  private final List<DatasetRow> regularTransactions = new ArrayList<>();

  private final List<DatasetRow> fraudTransactions = new ArrayList<>();

  private final Random random = new Random();

  private double fraudRate;

  private FeatureBounds featureBounds;

  public DatasetService(
      @Value("${fraud.dataset.path:file:../datasets/card_transdata_with_amount.csv}")
          Resource datasetResource) {
    this.datasetResource = datasetResource;
  }

  @PostConstruct
  public void loadDataset() throws Exception {

    if (!datasetResource.exists()) {
      throw new IllegalStateException("Dataset ne postoji: " + datasetResource.getDescription());
    }

    regularTransactions.clear();
    fraudTransactions.clear();

    List<DatasetRow> allRows = new ArrayList<>();

    try (BufferedReader reader =
        new BufferedReader(
            new InputStreamReader(datasetResource.getInputStream(), StandardCharsets.UTF_8))) {

      String headerLine = reader.readLine();

      if (headerLine == null || headerLine.isBlank()) {

        throw new IllegalStateException("Dataset je prazan.");
      }

      String[] headers = splitCsvLine(removeBom(headerLine));

      Map<String, Integer> columnIndexes = createColumnIndexMap(headers);

      validateRequiredColumns(columnIndexes);

      String line;
      int lineNumber = 1;

      while ((line = reader.readLine()) != null) {

        lineNumber++;

        if (line.isBlank()) {
          continue;
        }

        String[] values = splitCsvLine(line);

        try {

          DatasetRow row = parseRow(values, columnIndexes);

          allRows.add(row);

          if (row.fraud() == 1) {
            fraudTransactions.add(row);
          } else {
            regularTransactions.add(row);
          }

        } catch (RuntimeException exception) {

          throw new IllegalStateException(
              "Neispravan red u datasetu, linija " + lineNumber + ": " + exception.getMessage(),
              exception);
        }
      }
    }

    if (allRows.isEmpty()) {
      throw new IllegalStateException("Dataset ne sadrži nijednu transakciju.");
    }

    if (regularTransactions.isEmpty()) {
      throw new IllegalStateException("Dataset ne sadrži regularne transakcije.");
    }

    if (fraudTransactions.isEmpty()) {
      throw new IllegalStateException("Dataset ne sadrži fraud transakcije.");
    }

    fraudRate = (double) fraudTransactions.size() / allRows.size();

    featureBounds = calculateFeatureBounds(allRows);

    System.out.println("Dataset je uspešno učitan.");

    System.out.println("Ukupan broj transakcija: " + allRows.size());

    System.out.println("Regularne transakcije: " + regularTransactions.size());

    System.out.println("Fraud transakcije: " + fraudTransactions.size());

    System.out.printf(Locale.US, "Fraud stopa: %.4f%%%n", fraudRate * 100.0);
  }

  public List<TransactionDto> generateTransactions(int count) {

    if (count <= 0) {
      throw new IllegalArgumentException("Broj transakcija mora biti veći od nule.");
    }

    validateDatasetLoaded();

    int fraudCount = (int) Math.round(count * fraudRate);

    fraudCount = Math.max(0, Math.min(fraudCount, count));

    int regularCount = count - fraudCount;

    List<TransactionDto> generatedTransactions = new ArrayList<>(count);

    for (int i = 0; i < regularCount; i++) {

      DatasetRow template = getRandomRow(regularTransactions);

      generatedTransactions.add(createTransactionFromTemplate(template, 0));
    }

    for (int i = 0; i < fraudCount; i++) {

      DatasetRow template = getRandomRow(fraudTransactions);

      generatedTransactions.add(createTransactionFromTemplate(template, 1));
    }

    Collections.shuffle(generatedTransactions, random);

    return generatedTransactions;
  }

  public double getFraudRate() {
    return fraudRate;
  }

  private TransactionDto createTransactionFromTemplate(DatasetRow template, int fraud) {

    double amount =
        generateNoisyValue(
            template.amount(), featureBounds.minimumAmount(), featureBounds.maximumAmount());

    double distanceFromHome =
        generateNoisyValue(
            template.distanceFromHome(),
            featureBounds.minimumDistanceFromHome(),
            featureBounds.maximumDistanceFromHome());

    double distanceFromLastTransaction =
        generateNoisyValue(
            template.distanceFromLastTransaction(),
            featureBounds.minimumDistanceFromLastTransaction(),
            featureBounds.maximumDistanceFromLastTransaction());

    double ratioToMedianPurchasePrice =
        generateNoisyValue(
            template.ratioToMedianPurchasePrice(),
            featureBounds.minimumRatioToMedianPurchasePrice(),
            featureBounds.maximumRatioToMedianPurchasePrice());

    return new TransactionDto(
        roundToTwoDecimals(amount),
        distanceFromHome,
        distanceFromLastTransaction,
        ratioToMedianPurchasePrice,
        template.repeatRetailer(),
        template.usedChip(),
        template.usedPinNumber(),
        template.onlineOrder(),
        fraud,
        0.0,
        0);
  }

  private double generateNoisyValue(
      double originalValue, double minimumValue, double maximumValue) {

    double standardDeviation = Math.max(Math.abs(originalValue) * NOISE_PERCENTAGE, 0.0001);

    double generatedValue = originalValue + random.nextGaussian() * standardDeviation;

    return clip(generatedValue, minimumValue, maximumValue);
  }

  private DatasetRow getRandomRow(List<DatasetRow> rows) {

    int randomIndex = random.nextInt(rows.size());

    return rows.get(randomIndex);
  }

  private DatasetRow parseRow(String[] values, Map<String, Integer> indexes) {

    return new DatasetRow(
        readDouble(values, indexes, "amount"),
        readDouble(values, indexes, "distancefromhome"),
        readDouble(values, indexes, "distancefromlasttransaction"),
        readDouble(values, indexes, "ratiotomedianpurchaseprice"),
        readBinaryValue(values, indexes, "repeatretailer"),
        readBinaryValue(values, indexes, "usedchip"),
        readBinaryValue(values, indexes, "usedpinnumber"),
        readBinaryValue(values, indexes, "onlineorder"),
        readBinaryValue(values, indexes, "fraud"));
  }

  private double readDouble(String[] values, Map<String, Integer> indexes, String columnName) {

    int index = getColumnIndex(indexes, columnName);

    validateValueIndex(values, index, columnName);

    String value = values[index].trim();

    double parsedValue = Double.parseDouble(value);

    if (!Double.isFinite(parsedValue)) {
      throw new IllegalArgumentException(
          "Kolona " + columnName + " nema konačnu numeričku vrednost.");
    }

    return parsedValue;
  }

  private int readBinaryValue(String[] values, Map<String, Integer> indexes, String columnName) {

    double value = readDouble(values, indexes, columnName);

    int binaryValue = (int) Math.round(value);

    if (binaryValue != 0 && binaryValue != 1) {

      throw new IllegalArgumentException("Kolona " + columnName + " mora imati vrednost 0 ili 1.");
    }

    return binaryValue;
  }

  private FeatureBounds calculateFeatureBounds(List<DatasetRow> rows) {

    double minimumAmount = Double.POSITIVE_INFINITY;

    double maximumAmount = Double.NEGATIVE_INFINITY;

    double minimumDistanceFromHome = Double.POSITIVE_INFINITY;

    double maximumDistanceFromHome = Double.NEGATIVE_INFINITY;

    double minimumDistanceFromLastTransaction = Double.POSITIVE_INFINITY;

    double maximumDistanceFromLastTransaction = Double.NEGATIVE_INFINITY;

    double minimumRatioToMedianPurchasePrice = Double.POSITIVE_INFINITY;

    double maximumRatioToMedianPurchasePrice = Double.NEGATIVE_INFINITY;

    for (DatasetRow row : rows) {

      minimumAmount = Math.min(minimumAmount, row.amount());

      maximumAmount = Math.max(maximumAmount, row.amount());

      minimumDistanceFromHome = Math.min(minimumDistanceFromHome, row.distanceFromHome());

      maximumDistanceFromHome = Math.max(maximumDistanceFromHome, row.distanceFromHome());

      minimumDistanceFromLastTransaction =
          Math.min(minimumDistanceFromLastTransaction, row.distanceFromLastTransaction());

      maximumDistanceFromLastTransaction =
          Math.max(maximumDistanceFromLastTransaction, row.distanceFromLastTransaction());

      minimumRatioToMedianPurchasePrice =
          Math.min(minimumRatioToMedianPurchasePrice, row.ratioToMedianPurchasePrice());

      maximumRatioToMedianPurchasePrice =
          Math.max(maximumRatioToMedianPurchasePrice, row.ratioToMedianPurchasePrice());
    }

    return new FeatureBounds(
        minimumAmount,
        maximumAmount,
        minimumDistanceFromHome,
        maximumDistanceFromHome,
        minimumDistanceFromLastTransaction,
        maximumDistanceFromLastTransaction,
        minimumRatioToMedianPurchasePrice,
        maximumRatioToMedianPurchasePrice);
  }

  private Map<String, Integer> createColumnIndexMap(String[] headers) {

    Map<String, Integer> indexes = new HashMap<>();

    for (int i = 0; i < headers.length; i++) {

      String normalizedHeader = normalizeColumnName(headers[i]);

      indexes.put(normalizedHeader, i);
    }

    return indexes;
  }

  private void validateRequiredColumns(Map<String, Integer> indexes) {

    String[] requiredColumns = {
      "amount",
      "distancefromhome",
      "distancefromlasttransaction",
      "ratiotomedianpurchaseprice",
      "repeatretailer",
      "usedchip",
      "usedpinnumber",
      "onlineorder",
      "fraud"
    };

    for (String column : requiredColumns) {

      if (!indexes.containsKey(column)) {
        throw new IllegalStateException("Dataset nema obaveznu kolonu: " + column);
      }
    }
  }

  private int getColumnIndex(Map<String, Integer> indexes, String columnName) {

    Integer index = indexes.get(columnName);

    if (index == null) {
      throw new IllegalArgumentException("Kolona ne postoji: " + columnName);
    }

    return index;
  }

  private void validateValueIndex(String[] values, int index, String columnName) {

    if (index < 0 || index >= values.length) {

      throw new IllegalArgumentException("Nedostaje vrednost za kolonu: " + columnName);
    }

    if (values[index] == null || values[index].isBlank()) {

      throw new IllegalArgumentException("Prazna vrednost za kolonu: " + columnName);
    }
  }

  private void validateDatasetLoaded() {

    if (regularTransactions.isEmpty() || fraudTransactions.isEmpty() || featureBounds == null) {

      throw new IllegalStateException("Dataset još nije učitan.");
    }
  }

  private String normalizeColumnName(String columnName) {

    return removeBom(columnName)
        .trim()
        .replace("\"", "")
        .replace("_", "")
        .replace("-", "")
        .replace(" ", "")
        .toLowerCase(Locale.ROOT);
  }

  private String removeBom(String value) {

    if (value != null && !value.isEmpty() && value.charAt(0) == '\uFEFF') {

      return value.substring(1);
    }

    return value;
  }

  private String[] splitCsvLine(String line) {

    return line.split(",", -1);
  }

  private double clip(double value, double minimum, double maximum) {

    return Math.max(minimum, Math.min(value, maximum));
  }

  private double roundToTwoDecimals(double value) {

    return Math.round(value * 100.0) / 100.0;
  }

  private record DatasetRow(
      double amount,
      double distanceFromHome,
      double distanceFromLastTransaction,
      double ratioToMedianPurchasePrice,
      int repeatRetailer,
      int usedChip,
      int usedPinNumber,
      int onlineOrder,
      int fraud) {}

  private record FeatureBounds(
      double minimumAmount,
      double maximumAmount,
      double minimumDistanceFromHome,
      double maximumDistanceFromHome,
      double minimumDistanceFromLastTransaction,
      double maximumDistanceFromLastTransaction,
      double minimumRatioToMedianPurchasePrice,
      double maximumRatioToMedianPurchasePrice) {}
}
