/*
 * CaptchaSetIterator.java
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

import org.datavec.image.transform.ImageTransform;
import org.nd4j.linalg.dataset.MultiDataSet;
import org.nd4j.linalg.dataset.api.MultiDataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.MultiDataSetIterator;

public class CaptchaSetIterator implements MultiDataSetIterator {
  private int batchSize = 0;
  private int batchNum = 0;
  private int numExample = 0;
  private CaptchaLoader load;
  private MultiDataSetPreProcessor preProcessor;

  public CaptchaSetIterator(int batchSize, String dataSetType) {
    this(batchSize, null, dataSetType);
  }

  public CaptchaSetIterator(int batchSize, ImageTransform imageTransform, String dataSetType) {
    this.batchSize = batchSize;
    load = new CaptchaLoader(imageTransform, dataSetType);
    numExample = load.totalExamples();
  }

  @Override
  public MultiDataSet next(int i) {
    batchNum += i;
    MultiDataSet mds = load.next(i);
    if (preProcessor != null) {
      preProcessor.preProcess(mds);
    }
    return mds;
  }

  @Override
  public void setPreProcessor(MultiDataSetPreProcessor multiDataSetPreProcessor) {
    this.preProcessor = preProcessor;
  }

  @Override
  public MultiDataSetPreProcessor getPreProcessor() {
    return preProcessor;
  }

  @Override
  public boolean resetSupported() {
    return true;
  }

  @Override
  public boolean asyncSupported() {
    return true;
  }

  @Override
  public void reset() {
    batchNum = 0;
    load.reset();
  }

  @Override
  public boolean hasNext() {
    if (batchNum < numExample) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public MultiDataSet next() {
    return next(batchSize);
  }
}
