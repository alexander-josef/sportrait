package ch.unartig.selenium;

import com.thoughtworks.selenium.*;
import java.util.regex.Pattern;

public class JavaTest extends SeleneseTestCase {
	public void setUp() throws Exception {
		setUp("http://localhost:8080", "*firefox");
	}
	public void testNew() throws Exception {
		selenium.open("/index.html");
		selenium.click("link=2008-05-17, Wil, neuer Event");
		selenium.waitForPageToLoad("30000");
		selenium.click("link=eins");
		selenium.waitForPageToLoad("30000");
		selenium.click("//img[@alt='CIMG1307.JPG']");
		selenium.waitForPageToLoad("30000");
		verifyTrue(selenium.isTextPresent("CIMG1307.JPG"));
		selenium.click("logo");
		selenium.waitForPageToLoad("30000");
	}
}
