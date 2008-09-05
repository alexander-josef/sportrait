package ch.unartig.selenium

import org.testng.annotations.*
import org.testng.TestNG
import org.testng.TestListenerAdapter
import org.testng.AssertJUnit

import com.thoughtworks.selenium.Selenium
import com.thoughtworks.selenium.DefaultSelenium
import org.openqa.selenium.server.*

import java.util.regex.Pattern

public class AddPhotographer {
	Selenium selenium
	String baseUrl = "http://localhost:8080"

	/**
	* Creates an instance of the DefaultSelenium implementation.
	* <br>
	* Will execute only once.
	*/
	@BeforeClass(groups=["sportrait-tests"])
	public void beforeClass(){
		selenium = new DefaultSelenium("localhost",
					SeleniumServer.getDefaultPort(),
					"*firefox",
					baseUrl)
	}

	/**
	* Starts Selenium before a test-method is executed.
	*/
	@BeforeMethod(groups=["sportrait-tests"])
	public void startSelenium(){
		selenium.start()
	}

	/**
	* Stops Selenium after a test-method was executed.
	*/
	@AfterMethod(groups=["sportrait-tests"])
	public void stopSelenium(){
		selenium.stop()
	}

	@Test(groups=["sportrait-tests"])
	public void executeIntegrationTest() throws Exception {
		selenium.open("/index.html")
		selenium.waitForPageToLoad("60000")
		selenium.type("j_username", "admin")
		selenium.type("j_password", "nimda")
		selenium.click("loginPh")
		selenium.waitForPageToLoad("60000")
		selenium.waitForPageToLoad("30000")
		selenium.click("z_45_c!a")
		selenium.waitForPageToLoad("60000")
		selenium.click("z_45_66")
		selenium.waitForPageToLoad("60000")
		selenium.type("z_45_ea", "seleniumtest1")
		selenium.type("z_45_ka", "aaaa")
		selenium.type("z_45_la", "aaaa")
		selenium.type("z_45_6b", "seleniumname")
		selenium.type("z_45_9b", "seleniumfirstname")
		selenium.type("z_45_cb", "selenium@email.com")
		selenium.type("z_45_fb", "seleniumstreet")
		selenium.type("z_45_ib", "seleniumcity")
		selenium.type("z_45_lb", "1234")
		selenium.type("z_45_ob", "selenium")
		selenium.type("z_45_rb", "1234")
		selenium.type("z_45_ub", "1234")
		selenium.type("z_45_5c", "12341")
		selenium.type("z_45_8c", "1234")
		selenium.type("z_45_bc", "1244")
		selenium.click("z_45_ec")
		selenium.waitForPageToLoad("60000")
		selenium.waitForPageToLoad("30000")
		selenium.click("z_55_c!a")
		selenium.waitForPageToLoad("60000")
		AssertJUnit.assertTrue(selenium.isTextPresent("seleniumname"))
	}
}
