package com.fraudmodel.deepnetts;

import deepnetts.data.DataSets;
import deepnetts.data.MLDataItem;
import deepnetts.net.FeedForwardNetwork;
import deepnetts.net.layers.AbstractLayer;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.loss.LossType;

import javax.visrec.ml.data.DataSet;
import javax.visrec.ml.eval.EvaluationMetrics;

import java.nio.file.Files;
import java.nio.file.Path;

public final class CreditCardFraudDeepNettsModel {

   
    private static final boolean TRAIN_MODEL = false;

    private static final String MODEL_PATH = "fraud_model.dnet";

    private static final int NUMBER_OF_INPUTS = 8;
    private static final int NUMBER_OF_OUTPUTS = 1;

    private CreditCardFraudDeepNettsModel() {
    }

    public static void main(String[] args) {

        try {

            if (!Files.exists(DatasetGenerator.OUTPUT_DATASET)) {

                System.out.println("Dataset sa amount kolonom ne postoji.");
                System.out.println("Generišem novi dataset...");

                DatasetGenerator.generateDataset(
                        DatasetGenerator.INPUT_DATASET,
                        DatasetGenerator.OUTPUT_DATASET
                );
            }

            System.out.println("Učitavanje dataseta...");

            DataSet<MLDataItem> dataSet =
                    (DataSet<MLDataItem>) DataSets.readCsv(
                            DatasetGenerator.OUTPUT_DATASET.toString(),
                            NUMBER_OF_INPUTS,
                            NUMBER_OF_OUTPUTS,
                            true
                    );

            DataSets.scaleToMax(dataSet);

            DataSet<MLDataItem>[] split = dataSet.split(0.8f);

            DataSet<MLDataItem> trainSet = split[0];
            DataSet<MLDataItem> testSet = split[1];

            FeedForwardNetwork neuralNetwork;

            if (TRAIN_MODEL) {

                neuralNetwork = createNewNetwork();

                neuralNetwork.getTrainer()
                        .setLearningRate(0.001f);

                neuralNetwork.getTrainer()
                        .setStopEpochs(30);

                neuralNetwork.getTrainer()
                        .setBatchSize(64);

                System.out.println("Početak treniranja...");

                neuralNetwork.train(trainSet);

                neuralNetwork.save(MODEL_PATH);

                System.out.println(
                        "Model uspešno sačuvan u: " + MODEL_PATH
                );

            } else {

                if (!Files.exists(Path.of(MODEL_PATH))) {
                    throw new IllegalStateException(
                            "Sačuvani model ne postoji: " + MODEL_PATH
                    );
                }

                System.out.println("Učitavanje sačuvanog modela...");

                FeedForwardNetwork loadedNetwork =
                        FeedForwardNetwork.load(
                                MODEL_PATH,
                                FeedForwardNetwork.class
                        );

               
                neuralNetwork = createNewNetwork();

                copyLearnedParameters(
                        loadedNetwork,
                        neuralNetwork
                );

                System.out.println(
                        "Model i naučene težine uspešno učitani."
                );
            }

            System.out.println("Evaluacija modela...");

            EvaluationMetrics metrics =
                    neuralNetwork.test(testSet);

            System.out.println(metrics);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    private static FeedForwardNetwork createNewNetwork() {

        return FeedForwardNetwork.builder()
                .addInputLayer(NUMBER_OF_INPUTS)
                .addFullyConnectedLayer(
                        128,
                        ActivationType.RELU
                )
                .addFullyConnectedLayer(
                        64,
                        ActivationType.RELU
                )
                .addOutputLayer(
                        NUMBER_OF_OUTPUTS,
                        ActivationType.SIGMOID
                )
                .lossFunction(LossType.CROSS_ENTROPY)
                .build();
    }

   
    private static void copyLearnedParameters(
            FeedForwardNetwork sourceNetwork,
            FeedForwardNetwork targetNetwork
    ) {

        if (sourceNetwork.getLayers().size()
                != targetNetwork.getLayers().size()) {

            throw new IllegalStateException(
                    "Sačuvani model nema istu arhitekturu "
                            + "kao nova mreža."
            );
        }

       
        for (int i = 1;
             i < sourceNetwork.getLayers().size();
             i++) {

            AbstractLayer sourceLayer =
                    sourceNetwork.getLayerAt(i);

            AbstractLayer targetLayer =
                    targetNetwork.getLayerAt(i);

            targetLayer.setWeights(
                    sourceLayer.getWeights()
            );

            targetLayer.setBiases(
                    sourceLayer.getBiases()
            );
        }
    }
}