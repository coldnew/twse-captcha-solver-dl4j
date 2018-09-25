/*
* SolverDL4j.java
*
* Copyright (c) 2018 Yen-Chin, Lee <coldnew.tw@gmail.com>
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software an

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
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.factory.Nd4j;

/**
 * <code>SolverDL4j</code> , a twse's captcha solver based on <code>deeplearning4j</code>.
 *
 * @author <a href="mailto:coldnew.tw@gmail.com">Yen-Chin Lee</a>
 * @version 1.0
 */
public class SolverDL4j {

  private ComputationGraph model = null;

  /**
   * Creates a new <code>SolverDL4j</code> instance.
   *
   * @exception IOException if an error occurs
   */
  public SolverDL4j() throws IOException {
    InputStream is = SolverDL4j.class.getClass().getResourceAsStream("/model.zip");
    model = ModelSerializer.restoreComputationGraph(is);
  }

  /**
   * Creates a new <code>SolverDL4j</code> instance.
   *
   * @param modelPath a <code>String</code> value
   * @exception IOException if an error occurs
   */
  public SolverDL4j(String modelPath) throws IOException {
    File f = new File(modelPath);
    if (f.exists()) {
      model = ModelSerializer.restoreComputationGraph(modelPath);
    } else {
      throw new IOException("Could not find model.zip from " + modelPath);
    }
  }

  /** Describe <code>finalize</code> method here. */
  protected void finalize() {}

  /**
   * <code>solve</code> the twse's captcha.
   *
   * @param captchaPath a <code>File</code> value
   * @return a <code>String</code> value
   * @exception IOException if an error occurs
   */
  public String solve(File captchaPath) throws IOException {
    List<String> labelList =
	Arrays.asList(
	    "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G",
	    "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
	    "Y", "Z");

    INDArray image = loadImage(captchaPath);
    INDArray[] output = model.output(image);

    String captcha = "";
    for (int digit = 0; digit < 5; digit++) {
      INDArray poutput = output[digit].getRow(0);
      int index = Nd4j.argMax(poutput, 1).getInt(0);
      captcha += labelList.get(index);
      System.out.format("captcha[%d] = %2d, Char = %s\n", digit, index, labelList.get(index));
    }

    return captcha;
  }

  /**
   * Describe <code>loadImage</code> method here.
   *
   * @param path a <code>File</code> value
   * @return an <code>INDArray</code> value
   * @exception IOException if an error occurs
   */
  private INDArray loadImage(File path) throws IOException {
    int height = 60;
    int width = 200;
    int channels = 1;

    // height, width, channels
    NativeImageLoader loader = new NativeImageLoader(height, width, channels);
    INDArray image = loader.asMatrix(path);

    DataNormalization scaler = new ImagePreProcessingScaler(0, 1);
    scaler.transform(image);

    return image;
  }

  /**
   * Simple <code>main</code> function to test.
   *
   * @param args a <code>String</code> value
   * @exception Exception if an error occurs
   */
  public static void main(String[] args) throws Exception {
    SolverDL4j solver = new SolverDL4j();
    File f = new File(args[0]);

    System.out.println("");

    if (f.exists()) {
      System.out.println("Try to solve captcha from file: " + f + "\n");
      String captcha = solver.solve(f);
      System.out.println("\nCaptcha => " + captcha);
    } else {
      throw new IllegalArgumentException("File " + args[0] + " not exist!");
    }
  }
}
