package org.example.selenium.pages;


import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;


public class HomePage {

    WebDriver driver;
    public HomePage(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }



    @FindBy(xpath = "//span[normalize-space()='Prijavi se']")
    WebElement loginButton;




    public WebElement getLoginButton() {
        return loginButton;
    }
}
