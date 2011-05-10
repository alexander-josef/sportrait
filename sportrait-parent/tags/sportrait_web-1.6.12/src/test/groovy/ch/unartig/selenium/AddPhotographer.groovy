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
					"*mock",
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
        println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
	}
}
