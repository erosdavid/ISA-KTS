package org.example.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class RideCriteriaPage {

    WebDriver driver;
    public RideCriteriaPage(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }



    @FindBy(xpath = "//h2[@class='h2 mb-4 text-center']")
    WebElement title;

    @FindBy(xpath = "//input[@id='vehicleTypeId']")
    WebElement vehicleTypeDropdown;

    @FindBy(xpath = "//div[contains(text(),'STANDARD - 66.3 RSD')]")
    WebElement vehicleTypeStandard;

    @FindBy(xpath = "//div[contains(text(),'DELUXE - 265.2 RSD')]")
    WebElement vehicleTypeDeluxe;

    @FindBy(xpath = "//div[contains(text(),'COMBI - 159.12 RSD')]")
    WebElement vehicleTypeCombi;

    @FindBy(xpath = "//input[@id='numberOfPassengers']")
    WebElement numberOfPassengersInput;


    @FindBy(xpath = "//button[@id='babyTransportFlag']//span[@class='ant-switch-inner']")
    WebElement babyToggle;

    @FindBy(xpath = "//button[@id='petTransportFlag']//span[@class='ant-switch-inner']")
    WebElement petToggle;

    @FindBy(xpath = "//button[@id='scheduled']//span[@class='ant-switch-inner']")
    WebElement scheduledToggle;

    @FindBy(xpath = "//input[@id='scheduledStartTime']")
    WebElement dateTimeInput;

    @FindBy(xpath = "//span[contains(text(),'Pronađi vožnju')]")
    WebElement findRideButton;







    public void enterCriteria(Integer vehicleTypeId, String numberOfPassengers, Boolean babyTransport, Boolean petTransport, Boolean scheduled, String startTime) {
        WebDriverWait wait = new WebDriverWait(driver, 5l);
        wait.until(ExpectedConditions.visibilityOf(findRideButton));
        vehicleTypeDropdown.click();
        wait.until(ExpectedConditions.visibilityOf(vehicleTypeStandard));
        if (vehicleTypeId == 1){
            vehicleTypeStandard.click();
        } else if (vehicleTypeId == 2) {
            vehicleTypeDeluxe.click();
        } else if (vehicleTypeId == 3) {
            vehicleTypeCombi.click();
        } else {
            throw new RuntimeException("non existent vehicleTypeId! Please enter 1, 2 or 3");
        }
        numberOfPassengersInput.sendKeys(numberOfPassengers);
        if (babyTransport){
            babyToggle.click();
        }
        if (petTransport){
            petToggle.click();
        }
        if (scheduled){
            scheduledToggle.click();
            wait.until(ExpectedConditions.visibilityOf(dateTimeInput));
            dateTimeInput.sendKeys(startTime);
            dateTimeInput.sendKeys(Keys.ENTER);
        }


    }

    public WebElement getFindRideButton() {
        return findRideButton;
    }
}
