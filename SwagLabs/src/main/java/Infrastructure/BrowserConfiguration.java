package Infrastructure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import Login.Login;

public class BrowserConfiguration {
	
	public static Playwright playwright;
	public static Page page;
	public static Browser browser;
	public static BrowserContext context;
	public static Path sessionFile;
	//below code will create instance of ReadProperties class.
	public static ReadProperties webControl = new ReadProperties();
	public static String baseURL = "https://www.saucedemo.com/";
	
	/**
	 * This static function is used to maintain the browser session across the application.
	 * <p>
	 * without this static function, The browser would close and a new instance
	 * would be created for each test method.
	 */
	
	static {
		try {
			initializeBrowser();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void initializeBrowser() {
		String OS = System.getProperty("os.name").toUpperCase();
		System.out.println("Current Operating System :" + OS);
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy || HH:mm:ss aa");
		Date date = new Date();
		String executionDate = dateFormat.format(date);
		System.out.println("Script Execution Date and Time :" + executionDate);
		
		playwright = Playwright.create();
		browser = playwright.chromium().launch(
				new BrowserType.LaunchOptions()
				.setHeadless(false)
				.setArgs(List.of("--start-maximized")));
		
		context = browser.newContext(new Browser.NewContextOptions().
				setViewportSize(null));
		
		sessionFile = Paths.get("auth.json");
		context.storageState(new BrowserContext.StorageStateOptions().setPath(sessionFile));
		
		page = context.newPage();
		page.navigate(baseURL);
		
	}
	
	/**
	 * HandleLoginSession function handling the user login session.
	 * <p>
	 * Verify if a session is already available, If session exists it validate the login page.
	 * otherwise it will check the new session, performs login and store the session in auth.json() file. 
	 * @throws IOException 
	 */
	
	public static void handleLoginSession() throws IOException {
		if(Files.exists(sessionFile)) {
			System.out.println("Use existing session for login functionality");
			page.waitForTimeout(1000);
			Locator loginContainer = page.locator("//div[@data-test='login-container']");
			if(loginContainer.isVisible()) {
				Login.userLogin("ManageLogin/Login.properties");
			}
		}else {
			System.out.println("Create new session");
			Login.userLogin("ManageLogin/Login.properties");
			// Create and store session in auto.jason file
			context.storageState(new BrowserContext.StorageStateOptions().setPath(sessionFile));
		}
	}

}
