package com.fraudmodel.deepnetts;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;


public final class DatasetGenerator {

    public static final Path INPUT_DATASET =
            Paths.get("../ai-service/data/card_transdata.csv");

    public static final Path OUTPUT_DATASET =
            Paths.get("../ai-service/data/card_transdata_with_amount.csv");

    private static final long RANDOM_SEED = 42L;
    private static final int EXPECTED_COLUMN_COUNT = 8;
    private static final long MAX_ROWS = 1_000_000;

    private DatasetGenerator() {
    }

    public static void main(String[] args) {
        try {
            Path output = generateDataset(INPUT_DATASET, OUTPUT_DATASET);

            System.out.println("Dataset je uspešno generisan:");
            System.out.println(output.toAbsolutePath().normalize());
        } catch (Exception e) {
            System.err.println("Greška pri generisanju dataseta: "
                    + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static Path generateDataset(
            Path inputDataset,
            Path outputDataset
    ) throws IOException {
        if (inputDataset == null || outputDataset == null) {
            throw new IllegalArgumentException(
                    "Putanje do ulaznog i izlaznog dataseta ne smeju biti null."
            );
        }

        if (!Files.exists(inputDataset)) {
            throw new IOException(
                    "Originalni dataset nije pronađen: "
                            + inputDataset.toAbsolutePath().normalize()
            );
        }

        Path parent = outputDataset.toAbsolutePath().normalize().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        Random random = new Random(RANDOM_SEED);

        long processedRows = 0;

        try (
                BufferedReader reader = Files.newBufferedReader(
                        inputDataset,
                        StandardCharsets.UTF_8
                );
                BufferedWriter writer = Files.newBufferedWriter(
                        outputDataset,
                        StandardCharsets.UTF_8
                )
        ) {
            String header = reader.readLine();

            if (header == null || header.isBlank()) {
                throw new IOException("Ulazni CSV je prazan.");
            }

            // Amount ide pre fraud kolone.
            writer.write(
                    "distance_from_home,"
                            + "distance_from_last_transaction,"
                            + "ratio_to_median_purchase_price,"
                            + "repeat_retailer,"
                            + "used_chip,"
                            + "used_pin_number,"
                            + "online_order,"
                            + "amount,"
                            + "fraud"
            );
            writer.newLine();

            String line;
            long lineNumber = 1;

            while ((line = reader.readLine()) != null

             && processedRows < MAX_ROWS) {
                lineNumber++;

                if (line.isBlank()) {
                    continue;
                }

                String[] values = line.split(",", -1);

                if (values.length != EXPECTED_COLUMN_COUNT) {
                    throw new IOException(
                            "Neispravan broj kolona u redu "
                                    + lineNumber
                                    + ". Očekivano: "
                                    + EXPECTED_COLUMN_COUNT
                                    + ", pronađeno: "
                                    + values.length
                    );
                }

                try {
                    double ratioToMedianPurchasePrice =
                            Double.parseDouble(values[2].trim());

                    int fraud = (int) Math.round(
                            Double.parseDouble(values[7].trim())
                    );

                    double amount = AmountGenerator.generateAmount(
                            ratioToMedianPurchasePrice,
                            fraud,
                            random
                    );

                    // Prvih sedam ulaznih kolona + amount + fraud.
                    for (int i = 0; i < 7; i++) {
                        if (i > 0) {
                            writer.write(',');
                        }
                        writer.write(values[i].trim());
                    }

                    writer.write(',');
                    writer.write(Double.toString(amount));
                    writer.write(',');
                    writer.write(Integer.toString(fraud));
                    writer.newLine();

                    processedRows++;
                } catch (NumberFormatException e) {
                    throw new IOException(
                            "Neispravna brojčana vrednost u redu "
                                    + lineNumber + ": " + line,
                            e
                    );
                }
            }
        }

        System.out.println("Obrađeno redova: " + processedRows);

        return outputDataset;
    }
}
