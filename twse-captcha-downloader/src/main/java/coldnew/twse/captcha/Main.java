/*
* Main.java
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

import coldnew.twse.captcha.Downloader;
import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Main {

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(Main.class);
        logger.info("Starting twse captcha downloader ...");

        File saveDir =new File(System.getProperty("user.dir") + File.separator + "download");
        logger.info("Captcha image will be download to " + saveDir);

        Downloader d = new Downloader(saveDir);
        try {
            // download 150 captcha image
            for (int i = 0;  i < 2; i++) {
                d.download();
            }
        } finally {
            logger.info("Download captcha complete!");
            d.quit();
        }
    }

}