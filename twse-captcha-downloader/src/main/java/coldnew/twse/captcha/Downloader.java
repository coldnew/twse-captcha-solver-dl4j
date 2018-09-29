/*
* Downloader.java
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

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Downloader {
	public static final String BROWSER_DRIVER_KEY = "webdriver.chrome.driver";
	public static final String BASE_URL = "http://bsr.twse.com.tw/bshtm/";

	private static WebDriver driver;
	private File saveDir;

	public Downloader(File saveDir) {
		this.driver = new ChromeDriver();
		this.saveDir = saveDir;
	}

	public void quit() {
		driver.quit();
	}

	public void download() {
		// go to url
		driver.get(BASE_URL);

		// switch to page1
		driver.switchTo().frame("page1");

		// find the captcha image
		WebElement captchaElem = driver.findElement(By.cssSelector("div#Panel_bshtm img"));

		// wait until element visible
		WebDriverWait wait = new WebDriverWait(driver, 5);
		wait.until(ExpectedConditions.visibilityOf(captchaElem));

		// download the captcha image to tmp file
		downloadCaptcha(captchaElem);
	}

	private void downloadCaptcha(WebElement element) {
		// take all webpage as screenshot
		File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		try {
			// read the full screenshot
			BufferedImage fimg = ImageIO.read(screenshot);
			// get element location
			org.openqa.selenium.Point p = element.getLocation();
			// find element size
			org.openqa.selenium.Dimension size = element.getSize();
			// corp the captcha image
			BufferedImage croppedImage = fimg.getSubimage(p.getX(), p.getY(), size.getWidth(),
					size.getHeight());
			// save the capthca image
			File f = new File(this.saveDir + File.separator + randomImgName());
			ImageIO.write(croppedImage, "PNG", f);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// delete tmp file
			screenshot.delete();
		}
	}

	private String randomImgName() {
		UUID uuid = UUID.randomUUID();
		return "unknown_" + uuid + ".png";
	}
}