package BaseFile;

import java.io.IOException;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;

import Infrastructure.BrowserConfiguration;
import Infrastructure.WriteData;

public class CommonAnnotation {
	
	public String propertyFileName, className;
	
	/**
	* Initializes test setup before each method.
	• If the test class is not 'UserLogin', it ensures the user session is active.
	* @param PropertyFileName : Property file for element locators
	* @param ClassName : name of the test class name.
	*/
	@Parameters({"PropertyFileName", "ClassName"})
	@BeforeMethod(alwaysRun = true)
	public void beforeMethod(String propertyName, String className) throws IOException {
		
		this.propertyFileName = propertyName;
		this.className=className;
		if(!className.equalsIgnoreCase("UserLogin")) {
			BrowserConfiguration.handleLoginSession(); 
		} 
	}
	
	
	/**
	* Writes the test result status to the Excel sheet after each test method execution.
	* Records the result status, test method name, actual result, and execution time.
	* @param result the result of the executed test method
	*/
	@AfterMethod(alwaysRun = true)	
	public void WriteStatus(ITestResult result) throws IOException {
	
	long difference = result.getEndMillis() - result.getStartMillis();
	String FormattedTime = FormatTime(difference);
		
	switch (result.getStatus()) {
		case ITestResult.SUCCESS:
			WriteData.writeResult("Passed", "TestResult/saucedemo.xlsx", "SwagLabs", result.getMethod().getMethodName(), WriteData.actualResult, FormattedTime);
			break;
		case ITestResult.FAILURE:
			WriteData.writeResult("Failed", "TestResult/saucedemo.xlsx", "SwagLabs", result.getMethod().getMethodName(), WriteData.actualResult, FormattedTime);
			break;	
		case ITestResult.SKIP:
			WriteData.writeResult("Skipped", "TestResult/saucedemo.xlsx", "SwagLabs", result.getMethod().getMethodName(), WriteData.actualResult, FormattedTime);
			break;
		default :
			System.out.println(result.getStatus() + "Result is not found");
		}
	
	}
	
	private String FormatTime(long timeInMilliseconds) {
		long minutes = (timeInMilliseconds / 1000) / 60;
		long seconds = (timeInMilliseconds / 1000) % 60;
		long milliseconds = timeInMilliseconds % 1000;
		return String.format("%02d:%02d:%02d", minutes, seconds, milliseconds);
	}
}
