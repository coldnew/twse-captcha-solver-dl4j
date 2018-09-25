/*
 * Main.java
 *
 * Copyright (c) 2018 Yen-Chin, Lee <coldnew.tw@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package coldnew.twse.captcha;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.PoolingType;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.MultiDataSet;
import org.nd4j.linalg.dataset.api.iterator.MultiDataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Main {

  private static final Logger logger = LoggerFactory.getLogger(Main.class);

  private static long seed = 123;
  private static int epochs = 50;
  private static int batchSize = 15;
  private static String rootPath = System.getProperty("user.dir");

  private static String modelDirPath = rootPath + File.separatorChar + "out";
  private static String modelPath = modelDirPath + File.separatorChar + "model.zip";

  public static void main(String[] args) throws Exception {
    long startTime = System.currentTimeMillis();
    logger.info("start up time: " + startTime);

    File modelDir = new File(modelDirPath);

    // create dir
    boolean hasDir = modelDir.exists() || modelDir.mkdirs();
    logger.info(modelPath);

    // create model
    ComputationGraph model = createModel();
    // monitor the model score
    UIServer uiServer = UIServer.getInstance();
    StatsStorage statsStorage = new InMemoryStatsStorage();
    uiServer.attach(statsStorage);
    // NOTE:
    model.setListeners(new ScoreIterationListener(36), new StatsListener(statsStorage));

    // construct the iterator
    MultiDataSetIterator trainMulIterator = new CaptchaSetIterator(batchSize, "train");
    MultiDataSetIterator testMulIterator = new CaptchaSetIterator(batchSize, "test");
    MultiDataSetIterator validateMulIterator = new CaptchaSetIterator(batchSize, "validate");
    // fit
    for (int i = 0; i < epochs; i++) {
      System.out.println("Epoch=====================" + i);
      model.fit(trainMulIterator);
    }
    ModelSerializer.writeModel(model, modelPath, true);
    long endTime = System.currentTimeMillis();
    System.out.println("=============run time=====================" + (endTime - startTime));

    System.out.println("=====eval model=====test==================");
    modelPredict(model, testMulIterator);

    System.out.println("=====eval model=====validate==================");
    modelPredict(model, validateMulIterator);
  }

  public static ComputationGraph createModel() {

    ComputationGraphConfiguration config =
        new NeuralNetConfiguration.Builder()
            .seed(seed)
            .gradientNormalization(GradientNormalization.RenormalizeL2PerLayer)
            .l2(1e-3)
            .updater(new Adam(1e-3))
            .weightInit(WeightInit.XAVIER_UNIFORM)
            .graphBuilder()
            .addInputs("trainFeatures")
            .setInputTypes(InputType.convolutional(60, 200, 1))
            .setOutputs("out1", "out2", "out3", "out4", "out5")
            .addLayer(
                "cnn1",
                new ConvolutionLayer.Builder(new int[] {5, 5}, new int[] {1, 1}, new int[] {0, 0})
                    .nIn(1)
                    .nOut(48)
                    .activation(Activation.RELU)
                    .build(),
                "trainFeatures")
            .addLayer(
                "maxpool1",
                new SubsamplingLayer.Builder(
                        PoolingType.MAX, new int[] {2, 2}, new int[] {2, 2}, new int[] {0, 0})
                    .build(),
                "cnn1")
            .addLayer(
                "cnn2",
                new ConvolutionLayer.Builder(new int[] {5, 5}, new int[] {1, 1}, new int[] {0, 0})
                    .nOut(64)
                    .activation(Activation.RELU)
                    .build(),
                "maxpool1")
            .addLayer(
                "maxpool2",
                new SubsamplingLayer.Builder(
                        PoolingType.MAX, new int[] {2, 1}, new int[] {2, 1}, new int[] {0, 0})
                    .build(),
                "cnn2")
            .addLayer(
                "cnn3",
                new ConvolutionLayer.Builder(new int[] {3, 3}, new int[] {1, 1}, new int[] {0, 0})
                    .nOut(128)
                    .activation(Activation.RELU)
                    .build(),
                "maxpool2")
            .addLayer(
                "maxpool3",
                new SubsamplingLayer.Builder(
                        PoolingType.MAX, new int[] {2, 2}, new int[] {2, 2}, new int[] {0, 0})
                    .build(),
                "cnn3")
            .addLayer(
                "cnn4",
                new ConvolutionLayer.Builder(new int[] {4, 4}, new int[] {1, 1}, new int[] {0, 0})
                    .nOut(256)
                    .activation(Activation.RELU)
                    .build(),
                "maxpool3")
            .addLayer(
                "maxpool4",
                new SubsamplingLayer.Builder(
                        PoolingType.MAX, new int[] {2, 2}, new int[] {2, 2}, new int[] {0, 0})
                    .build(),
                "cnn4")
            .addLayer("ffn0", new DenseLayer.Builder().nOut(3072).build(), "maxpool4")
            .addLayer("ffn1", new DenseLayer.Builder().nOut(3072).build(), "ffn0")
            .addLayer(
                "out1",
                new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                    .nOut(36)
                    .activation(Activation.SOFTMAX)
                    .build(),
                "ffn1")
            .addLayer(
                "out2",
                new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                    .nOut(36)
                    .activation(Activation.SOFTMAX)
                    .build(),
                "ffn1")
            .addLayer(
                "out3",
                new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                    .nOut(36)
                    .activation(Activation.SOFTMAX)
                    .build(),
                "ffn1")
            .addLayer(
                "out4",
                new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                    .nOut(36)
                    .activation(Activation.SOFTMAX)
                    .build(),
                "ffn1")
            .addLayer(
                "out5",
                new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                    .nOut(36)
                    .activation(Activation.SOFTMAX)
                    .build(),
                "ffn1")
            .pretrain(false)
            .backprop(true)
            .build();

    // Construct and initialize model
    ComputationGraph model = new ComputationGraph(config);
    model.init();

    return model;
  }

  public static void modelPredict(ComputationGraph model, MultiDataSetIterator iterator) {
    int sumCount = 0;
    int correctCount = 0;

    List<String> labelList =
        Arrays.asList(
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G",
            "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
            "Y", "Z");

    while (iterator.hasNext()) {
      MultiDataSet mds = iterator.next();
      INDArray[] output = model.output(mds.getFeatures());
      INDArray[] labels = mds.getLabels();
      int dataNum = batchSize > output[0].rows() ? output[0].rows() : batchSize;
      for (int dataIndex = 0; dataIndex < dataNum; dataIndex++) {
        String reLabel = "";
        String peLabel = "";
        INDArray preOutput = null;
        INDArray realLabel = null;
        for (int digit = 0; digit < 5; digit++) {
          preOutput = output[digit].getRow(dataIndex);
          peLabel += labelList.get(Nd4j.argMax(preOutput, 1).getInt(0));

          realLabel = labels[digit].getRow(dataIndex);
	  reLabel += labelList.get(Nd4j.argMax(realLabel, 1).getInt(0));
        }
        if (peLabel.equals(reLabel)) {
          correctCount++;
        }
        sumCount++;
        logger.info(
            "real image {}  prediction {} status {}", reLabel, peLabel, peLabel.equals(reLabel));
      }
    }
    iterator.reset();
    System.out.println(
        "validate result : sum count =" + sumCount + " correct count=" + correctCount);
  }
}
