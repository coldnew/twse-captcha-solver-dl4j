/*
 * CaptchaLoader.java
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.transform.ImageTransform;
import org.nd4j.linalg.api.concurrency.AffinityManager;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.MultiDataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CaptchaLoader extends NativeImageLoader implements Serializable {

  private static final Logger logger = LoggerFactory.getLogger(CaptchaLoader.class);

  // PNG image data, 200 x 60, 8-bit/color RGBA, non-interlaced
  private static int width = 200;
  private static int height = 60;
  private static int channels = 1;

  private File fullDir = null;
  private Iterator<File> fileIterator;
  private int numExample = 0;

  private static List<String> labelList =
      Arrays.asList(
	  "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H",
	  "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z");

  public CaptchaLoader(String dataSetType) {
    this(height, width, channels, null, dataSetType);
  }

  public CaptchaLoader(ImageTransform imageTransform, String dataSetType) {
    this(height, width, channels, imageTransform, dataSetType);
  }

  public CaptchaLoader(
      int height, int width, int channels, ImageTransform imageTransform, String dataSetType) {
    super(height, width, channels, imageTransform);
    this.height = height;
    this.width = width;
    this.channels = channels;
    try {
      this.fullDir = new File("src/main/resources");
      logger.info("fullDir: " + fullDir);
    } catch (Exception e) {
      logger.error("The datasets directory failed, please checking.", e);
      throw new RuntimeException(e);
    }
    this.fullDir = new File(fullDir, dataSetType);
    load();
  }

  protected void load() {
    try {
      List<File> dataFiles = (List<File>) FileUtils.listFiles(fullDir, new String[] {"png"}, true);
      Collections.shuffle(dataFiles);
      fileIterator = dataFiles.iterator();
      numExample = dataFiles.size();
    } catch (Exception var4) {
      throw new RuntimeException(var4);
    }
  }

  public MultiDataSet convertDataSet(int num) throws Exception {
    int batchNumCount = 0;

    INDArray[] featuresMask = null;
    INDArray[] labelMask = null;

    List<MultiDataSet> multiDataSets = new ArrayList<>();

    while (batchNumCount != num && fileIterator.hasNext()) {
      File image = fileIterator.next();
      String imageName = image.getName().substring(0, image.getName().lastIndexOf('.'));
      String[] imageNames = imageName.split("");
      INDArray feature = asMatrix(image);
      INDArray[] features = new INDArray[] {feature};
      INDArray[] labels = new INDArray[5];

      Nd4j.getAffinityManager().ensureLocation(feature, AffinityManager.Location.DEVICE);
      for (int i = 0; i < imageNames.length; i++) {
	int digit = labelList.indexOf(imageNames[i]);
	labels[i] = Nd4j.zeros(1, labelList.size()).putScalar(new int[] {0, digit}, 1);
      }

      feature = feature.muli(1.0 / 255.0);

      multiDataSets.add(new MultiDataSet(features, labels, featuresMask, labelMask));

      batchNumCount++;
    }

    MultiDataSet result = MultiDataSet.merge(multiDataSets);
    return result;
  }

  public MultiDataSet next(int batchSize) {
    try {
      MultiDataSet result = convertDataSet(batchSize);
      return result;
    } catch (Exception e) {
      logger.error("the next function shows error", e);
    }
    return null;
  }

  public void reset() {
    load();
  }

  public int totalExamples() {
    return numExample;
  }
}
