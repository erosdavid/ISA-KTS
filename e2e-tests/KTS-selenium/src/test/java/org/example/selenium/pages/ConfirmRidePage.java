package org.example.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class ConfirmRidePage {

    WebDriver driver;
    public ConfirmRidePage(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//h2[@class='h2 mb-4 text-center']")
    WebElement title;

    @FindBy(xpath = "//span[contains(text(),'Prihvati vožnju')]")
    WebElement confirmRideButton;

    @FindBy(xpath = "//span[normalize-space()='Odustani']")
    WebElement rejectRideButton;


    public boolean pageValidation(){

        if (title.getText().equals("Potvrdite vožnju:")) {
            return true;
        } else {
            return false;
        }
    }

    public WebElement getTitle() {
        return title;
    }

    public WebElement getConfirmRideButton() {
        return confirmRideButton;
    }

    public WebElement getRejectRideButton() {
        return rejectRideButton;
    }
}
