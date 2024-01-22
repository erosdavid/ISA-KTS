package org.example.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class SearchForRoutePage {

    WebDriver driver;
    public SearchForRoutePage(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }



    @FindBy(xpath = "//h2[@class='h2 mb-4 text-center']")
    WebElement title;

    @FindBy(xpath = "//input[@id='start']")
    WebElement inputStartLocation;

    @FindBy(xpath = "//div[@title='44, Tolstojeva, Grbavica, МЗ 7. Јули, Novi Sad, City of Novi Sad, South Backa Administrative District, Vojvodina, 21102, Serbia']//div[1]")
    WebElement startAddressDropdown;

    @FindBy(xpath = "//div[@title='17, Dalmatinska, Telep, МЗ Никола Тесла телеп, Novi Sad, City of Novi Sad, South Backa Administrative District, Vojvodina, 21102, Serbia']//div[1]")
    WebElement finishAddressDalmatinskaDropdown;

    @FindBy(xpath = "//div[@title='Niš, City of Niš, Nisava Administrative District, Central Serbia, 18101, Serbia']//div[1]")
    WebElement finishAddressNisDropdown;

    @FindBy(xpath = "//input[@id='destination']")
    WebElement inputFinishLocation;


    @FindBy(xpath = "//span[contains(text(),'Pretraži')]")
    WebElement searchButton;

    @FindBy(xpath = "//div[@class='ant-notification-notice-description']")
    WebElement warningTooBigDistance;







    public void searchForRoute()  {
        WebDriverWait wait = new WebDriverWait(driver, 5l);
//        wait.until(ExpectedConditions.textToBePresentInElement(title, "Zakaži vožnju:"));

        inputStartLocation.sendKeys("tolstojeva 44");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@title='44, Tolstojeva, Grbavica, МЗ 7. Јули, Novi Sad, City of Novi Sad, South Backa Administrative District, Vojvodina, 21102, Serbia']//div[1]")));
        startAddressDropdown.click();


        inputFinishLocation.sendKeys("dalmatinska 17, telep");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@title='17, Dalmatinska, Telep, МЗ Никола Тесла телеп, Novi Sad, City of Novi Sad, South Backa Administrative District, Vojvodina, 21102, Serbia']//div[1]")));
        finishAddressDalmatinskaDropdown.click();

        searchButton.click();
    }

    public void searchForRouteTooBigDistance()  {
        WebDriverWait wait = new WebDriverWait(driver, 5l);


        inputStartLocation.sendKeys("tolstojeva 44");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@title='44, Tolstojeva, Grbavica, МЗ 7. Јули, Novi Sad, City of Novi Sad, South Backa Administrative District, Vojvodina, 21102, Serbia']//div[1]")));
        startAddressDropdown.click();


        inputFinishLocation.sendKeys("nis");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@title='Niš, City of Niš, Nisava Administrative District, Central Serbia, 18101, Serbia']//div[1]")));
        finishAddressNisDropdown.click();

        searchButton.click();
    }

    public WebElement getTitle() {
        return title;
    }

    public WebElement getWarningTooBigDistance() {
        return warningTooBigDistance;
    }
}
