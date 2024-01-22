package org.example.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class LoginPage {

    WebDriver driver;
    public LoginPage(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }



    @FindBy(xpath = "//input[@id='username']")
    WebElement inputUsername;

    @FindBy(xpath = "//input[@id='password']")
    WebElement inputPassword;

    @FindBy(xpath = "//div[@class='ant-form-item-explain-error']")
    WebElement warningEnterUsername;

    @FindBy(xpath = "//div[@class='ant-form-item-explain-error']")
    WebElement warningEnterPassword;

    @FindBy(xpath = "//div[@class='mx-2']")
    WebElement usernameLoggedIn;

    @FindBy(xpath = "//span[normalize-space()='Prijavi se']")
    WebElement loginButton;

    @FindBy(xpath = "//span[@aria-label='close-circle']//*[name()='svg']")
    WebElement warningInvalidCredentials;









    public boolean homePageValidation(){

        if (loginButton.getText().equals("Prijavi se")) {
            return true;
        } else {
            return false;
        }
    }

    public void login(String username, String password){
        WebDriverWait wait = new WebDriverWait(driver, 5l);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='username']")));
        inputUsername.sendKeys(username);
        inputPassword.sendKeys(password);
        inputPassword.sendKeys(Keys.ENTER);


    }

    public void fillSearchCriteria(){


//        selectTipNekretnine.click();
//        WebDriverWait wait = new WebDriverWait(driver, 15l);
//        wait.until(ExpectedConditions.textToBePresentInElement(dpOptionStan,"Stan"));
//        dpOptionStan.click();
//        dobroDosli.click();
//        selectTipUslugeProdaja.click();
//        inputLokacija.sendKeys("No");
//
//        wait.until(ExpectedConditions.textToBePresentInElement(ceoTab,"Novi Sad"));
//        inputLokacija.sendKeys(Keys.ENTER);
//
//        inputCena.sendKeys("100000");
////        inputCena.sendKeys(Keys.ENTER);
//
//        inputKvadratura.sendKeys("75");
//        inputKvadratura.sendKeys(Keys.ENTER);
//        Actions actions = new Actions(driver);
//        actions.moveToElement(selectBrojSoba).click().perform();
//        wait.until(ExpectedConditions.textToBePresentInElement(ceoTab, "3.0"));
//        brojSobaTri.click();
//
//        buttonPrikaziRezultate.click();

    }

    public WebDriver getDriver() {
        return driver;
    }

    public WebElement getLoginButton() {
        return loginButton;
    }

    public WebElement getInputUsername() {
        return inputUsername;
    }

    public WebElement getInputPassword() {
        return inputPassword;
    }

    public WebElement getWarningEnterUsername() {
        return warningEnterUsername;
    }

    public WebElement getUsernameLoggedIn() {
        return usernameLoggedIn;
    }

    public WebElement getWarningEnterPassword() {
        return warningEnterPassword;
    }
}
