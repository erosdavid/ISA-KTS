package org.example.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class RideStartedPage {

    WebDriver driver;
    public RideStartedPage(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//h2[@class='h2 mb-4 text-center']")
    WebElement title;



    public boolean pageValidation(){

        if (title.getText().equals("Započeta vožnja:")) {
            return true;
        } else {
            return false;
        }
    }

    public WebElement getTitle() {
        return title;
    }


}
