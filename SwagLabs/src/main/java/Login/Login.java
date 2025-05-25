package Login;

import java.io.IOException;

import Infrastructure.AccessibilityUtil;
import Infrastructure.BrowserConfiguration;
import Infrastructure.LoggerUtil;
import Infrastructure.VisualTestUtil;
import Infrastructure.WebControls;
import Infrastructure.WriteData;

public class Login {
	
	/**
	* Performs user login using predefined credentials and validates successful login.
	* @param PropertyFileName property file to fetch element locators
	 * @throws IOException 
	*/
 	public static void userLogin(String PropertyFileName) throws IOException {

		//Handle Acccesiblity of login page.	
 		AccessibilityUtil.handleAccessibility(BrowserConfiguration.page, "login");
		//Take a screenshots for visual test.	
        	VisualTestUtil.runVisualTest(BrowserConfiguration.page, "Login", "login");
 		// Read user name and password from Excel
 		String username = Infrastructure.ReadData.getCellValue("TestData/LoginDetails.xlsx", "Logincredentials", 1, 0); 
 		String password = Infrastructure.ReadData.getCellValue("TestData/LoginDetails.xlsx", "Logincredentials", 1, 1);
 		WebControls.setvalueForTextbox(PropertyFileName, "Username", username);
  		WebControls.setvalueForTextbox(PropertyFileName, "Password", password);
  		WebControls.clickonButton(PropertyFileName, "Login");
  		WebControls.loginValidation();
  		WriteData.actualResult = "Login successfully with register customer.";
  		LoggerUtil.log(WriteData.actualResult);
  		WebControls.assertVisibleById(PropertyFileName,"Product");
 		}
}
