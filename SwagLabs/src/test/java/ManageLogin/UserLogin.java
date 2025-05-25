package ManageLogin;

import java.io.IOException;

import org.testng.annotations.Test;

import BaseFile.CommonAnnotation;
import Infrastructure.BrowserConfiguration;
import Login.Login;

public class UserLogin extends CommonAnnotation {
	

	BrowserConfiguration configuration = new BrowserConfiguration();

	@Test (priority=1)
	public void testLoginUser() throws IOException, InterruptedException
	{ 
		Login.userLogin(propertyFileName);
	}
}