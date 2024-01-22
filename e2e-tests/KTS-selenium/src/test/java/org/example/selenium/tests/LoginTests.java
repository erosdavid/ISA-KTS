package org.example.selenium.tests;

import org.example.selenium.pages.HomePage;
import org.example.selenium.pages.LoginPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginTests {

    WebDriver webDriver;

    LoginPage homePage;

    @BeforeEach
    public void setup(){
        System.setProperty("webdriver.chrome.driver", "/Users/david/Desktop/KTS-selenium/chromedriver");
        webDriver = new ChromeDriver();
        webDriver.manage().window().maximize();
        webDriver.get("http://localhost:3000/ISA");
    }

    @AfterEach
    public void tearDown(){
        webDriver.close();
    }


    @Test
    public void homePageValidationTest() {
        LoginPage homePage = new LoginPage(webDriver);
        boolean check = homePage.homePageValidation();
        assertTrue(check);
    }



    @Test
    public void loginSuccessfulTest(){
        HomePage homePage = new HomePage(webDriver);
        homePage.getLoginButton().click();
        LoginPage loginPage = new LoginPage(webDriver);

        loginPage.login("admin", "admin");
        WebDriverWait wait = new WebDriverWait(webDriver, 5l);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[class='mx-2']")));
        String username = loginPage.getUsernameLoggedIn().getText();

        assertEquals("Admin Admin", username);
    }




    @Test
    public void loginExistingUsernameWrongPassword(){
        HomePage homePage = new HomePage(webDriver);
        homePage.getLoginButton().click();

        LoginPage loginPage = new LoginPage(webDriver);
        loginPage.login("admin", "jbcf398q4y2398fh");
        WebDriverWait wait = new WebDriverWait(webDriver, 5l);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[@aria-label='close-circle']//*[name()='svg']")));

    }

    @Test
    public void loginNonExistingUsernameAndPassword(){
        HomePage homePage = new HomePage(webDriver);
        homePage.getLoginButton().click();

        LoginPage loginPage = new LoginPage(webDriver);
        loginPage.login("qwed43tgeras", "jbcf398q4y2398fh");
        WebDriverWait wait = new WebDriverWait(webDriver, 5l);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[@aria-label='close-circle']//*[name()='svg']")));

    }

    @Test
    public void loginMissingUsername(){
        HomePage homePage = new HomePage(webDriver);
        homePage.getLoginButton().click();

        LoginPage loginPage = new LoginPage(webDriver);
        loginPage.getInputPassword().sendKeys("admin");
        loginPage.getInputPassword().sendKeys(Keys.ENTER);
        WebDriverWait wait = new WebDriverWait(webDriver, 5l);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".ant-form-item-explain-error")));
        String warningText = loginPage.getWarningEnterPassword().getText();
        assertEquals("Unesite korisničko ime!", warningText);
    }

    @Test
    public void loginMissingPassword(){
        HomePage homePage = new HomePage(webDriver);
        homePage.getLoginButton().click();

        LoginPage loginPage = new LoginPage(webDriver);
        loginPage.getInputUsername().sendKeys("admin");
        loginPage.getInputUsername().sendKeys(Keys.ENTER);
        WebDriverWait wait = new WebDriverWait(webDriver, 5l);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".ant-form-item-explain-error")));
        String warningText = loginPage.getWarningEnterPassword().getText();
        assertEquals("Unesite lozinku!", warningText);

    }


}
