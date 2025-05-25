package Infrastructure;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PlaywrightAssertions;

public class WebControls {
	
	/**
	 * setvalueForTextbox() function fills a text box with a value using x-path from the specified property file.
	 * @param propertyFileName : Property file to fetch the x-path.
	 * @param xpath : Key for x-path locator.
	 * @param value : value to set in the text box.
	 */
	public static void setvalueForTextbox(String propertyFileName, String xpath, String value) {
		try {
			xpath = BrowserConfiguration.webControl.getpropertyValues(propertyFileName, xpath);
			BrowserConfiguration.page.locator(xpath).fill(value);
		} catch (Exception e) {
			System.out.println("Element is not found");
		}
	}
	
	/**
	 * clickonButton() function clicks a button using x-path fetched from property file.
	 * @param propertyFileName : Property file to fetch the x-path.
	 * @param xpath : Key for x-path locator.
	 */
	public static void clickonButton(String propertyFileName, String xpath) {
		try {
			xpath = BrowserConfiguration.webControl.getpropertyValues(propertyFileName, xpath);
			BrowserConfiguration.page.click(xpath);
		} catch (Exception e) {
			System.out.println("Element is not found");
		}
	}
	
	/**
	 * assertVisibleById() function asserts that an element identified by x-path (from the property file)
	 * is visible on the page.
	 * @param propertyFileName : Property file to fetch the x-path.
	 * @param xpath : Key for x-path locator.
	 */
	public static void assertVisibleById(String propertyFileName, String xpath) {
		try {
			xpath = BrowserConfiguration.webControl.getpropertyValues(propertyFileName, xpath);
			assertThat(BrowserConfiguration.page.locator(xpath).first()).isVisible();
		} catch (Exception e) {
			System.out.println("Element is not found");
		}
	}
	
	/**
	 * assertVisibleById() function asserts that an element identified by x-path (from the property file)
	 * is visible on the page.
	 * @param propertyFileName : Property file to fetch the x-path.
	 * @param xpath : Key for x-path locator.
	 */
	public static void assertByURL(String url) {
		try {
			assertThat(BrowserConfiguration.page).hasURL(Pattern.compile(url));
		} catch (Exception e) {
			String currentUrl = BrowserConfiguration.page.url();
			WriteData.actualResult = "Expected URL pattern '" + url + "' but found '" + currentUrl + "'" ;
			LoggerUtil.log(WriteData.actualResult);
			e.printStackTrace();
		}
	}
	
	/**
	 * selectValueByOption() function selects a drop down option x-path from the property file.
	 * @param propertyFileName : Property file to fetch the x-path.
	 * @param xpath : Key for x-path locator.
	 * @param value : value to select in the drop down.
	 */
	public static void selectValueByOption(String propertyFileName, String xpath, String value) {
		try {
			xpath = BrowserConfiguration.webControl.getpropertyValues(propertyFileName, xpath);
			BrowserConfiguration.page.selectOption(xpath, value);
		} catch (Exception e) {
			System.out.println("Element is not found");
		}
	}
	
	/**
	 * handleProductImages() function verifies the all product images on the page have a valid 'src' attribute.
	 * @param propertyFileName : Property file to fetch the x-path.
	 * @param page : the playwright page instance.
	 */
	public static void handleProductImages(String propertyFileName, Page page) {
		try {
			String productImages = BrowserConfiguration.webControl.getpropertyValues(propertyFileName, "ProductImage");
			Locator images = page.locator(productImages);
			
			int count = images.count();
			for(int i=0; i<count; i++) {

				String src = images.nth(i).getAttribute("src");
				if(src == null || src.trim().isEmpty()) {
					WriteData.actualResult = "Failed: Images src missing at index"+ i;
					LoggerUtil.log(WriteData.actualResult);
					throw new AssertionError(WriteData.actualResult);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * loginValidation() function validates the login result by checking for an error message on the page.
	 * Logs and throws an error if login fails and logs success if login passes.
	 */
	public static void loginValidation() {
		Locator loginErrorMessage = BrowserConfiguration.page.locator("h3[data-test='error']");
		if(loginErrorMessage.isVisible()) {
			String errorText = loginErrorMessage.textContent().trim();
			WriteData.actualResult = errorText;
			LoggerUtil.log("Login failed for user :" + errorText);
			throw new AssertionError("Login failed : " + errorText);
		}
	}
	
}
