package org.example.selenium.tests;

import org.example.selenium.pages.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RideBookingTests {

    WebDriver webDriver1;

    WebDriver webDriver2;

    LoginPage homePage;

    SearchForRoutePage searchForRoutePage;

    ChooseRoutePage chooseRoutePage;

    RideCriteriaPage rideCriteriaPage;

    @BeforeEach
    public void setup(){
        System.setProperty("webdriver.chrome.driver", "/Users/david/Desktop/KTS-selenium/chromedriver");
        webDriver1 = new ChromeDriver();
        webDriver1.manage().window().maximize();
        webDriver1.get("http://localhost:3000/ISA");
        webDriver2 = new ChromeDriver();
        webDriver2.manage().window().maximize();
        webDriver2.get("http://localhost:3000/ISA");
    }

    @AfterEach
    public void tearDown(){
        webDriver1.close();
        webDriver2.close();
    }


    @Test
    public void homePageValidationTest() {
        LoginPage homePage1 = new LoginPage(webDriver1);
        boolean check1 = homePage1.homePageValidation();
        assertTrue(check1);

        LoginPage homePage2 = new LoginPage(webDriver2);
        boolean check2 = homePage2.homePageValidation();
        assertTrue(check2);


    }




    @Test
    public void loginTwoUsersSuccessfulTest(){
        HomePage homePage1 = new HomePage(webDriver1);
        homePage1.getLoginButton().click();
        LoginPage loginPage1 = new LoginPage(webDriver1);

        loginPage1.login("admin", "admin");
        WebDriverWait wait = new WebDriverWait(webDriver1, 5l);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[class='mx-2']")));
        String username = loginPage1.getUsernameLoggedIn().getText();

        assertEquals("Admin Admin", username);


        HomePage homePage2 = new HomePage(webDriver2);
        homePage2.getLoginButton().click();
        LoginPage loginPage2 = new LoginPage(webDriver2);

        loginPage2.login("dave", "1111");
        WebDriverWait wait2 = new WebDriverWait(webDriver2, 5l);
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[class='mx-2']")));
        String username2 = loginPage2.getUsernameLoggedIn().getText();

        assertEquals("Dave Dave", username2);

    }


    @Test
    public void orderStartAndFinishRideSuccessfulTest() {

        //Driver: login
        HomePage homePage2 = new HomePage(webDriver2);
        homePage2.getLoginButton().click();
        LoginPage loginPage2 = new LoginPage(webDriver2);
        loginPage2.login("multiplavozac", "1111");
        WebDriverWait wait2 = new WebDriverWait(webDriver2, 5l);
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[class='mx-2']")));
        String username2 = loginPage2.getUsernameLoggedIn().getText();
        assertEquals("multipla vozac", username2);

        //Passenger: login
        HomePage homePage1 = new HomePage(webDriver1);
        homePage1.getLoginButton().click();
        LoginPage loginPage1 = new LoginPage(webDriver1);
        loginPage1.login("dave", "1111");
        WebDriverWait wait1 = new WebDriverWait(webDriver1, 5l);
        wait1.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[class='mx-2']")));
        String username = loginPage1.getUsernameLoggedIn().getText();
        assertEquals("Dave Dave", username);

        //Passenger: Search for route
        SearchForRoutePage searchForRoutePage1 = new SearchForRoutePage(webDriver1);
        wait1.until(ExpectedConditions.textToBePresentInElement(searchForRoutePage1.getTitle(), "Zakaži vožnju:"));
        searchForRoutePage1.searchForRoute();
        //Passenger: Choose route
        ChooseRoutePage chooseRoutePage1 = new ChooseRoutePage(webDriver1);
        wait1.until(ExpectedConditions.elementToBeClickable(chooseRoutePage1.getNextButton()));
        chooseRoutePage1.getNextButton().click();

        //Passenger: enter ride criteria
        RideCriteriaPage rideCriteriaPage1 = new RideCriteriaPage(webDriver1);
        rideCriteriaPage1.enterCriteria(3,"1", false, false, false, "");
        wait1.until(ExpectedConditions.elementToBeClickable(rideCriteriaPage1.getFindRideButton()));
        rideCriteriaPage1.getFindRideButton().click();

        //Passenger: confirm ride
        ConfirmRidePage confirmRidePage1 = new ConfirmRidePage(webDriver1);
        wait1.until(ExpectedConditions.elementToBeClickable(confirmRidePage1.getConfirmRideButton()));
        confirmRidePage1.getConfirmRideButton().click();

        //Driver: start ride
        StartRidePage startRidePage2 = new StartRidePage(webDriver2);
        wait2.until(ExpectedConditions.elementToBeClickable(startRidePage2.getStartRideButton()));
        startRidePage2.getStartRideButton().click();

        //Driver: finish ride
        RideInProgressDriverPage rideInProgressDriverPage2 = new RideInProgressDriverPage(webDriver2);
        wait2.until(ExpectedConditions.elementToBeClickable(rideInProgressDriverPage2.getFinishRideButton()));
        rideInProgressDriverPage2.getFinishRideButton().click();


        //Passenger: ride finished check
        RideFinishedPage rideFinishedPage1 = new RideFinishedPage(webDriver1);
        assertTrue(rideFinishedPage1.pageValidation());

    }

    @Test
    public void rideRejectedByPassengerTest() {

        //Driver: login
        HomePage homePage2 = new HomePage(webDriver2);
        homePage2.getLoginButton().click();
        LoginPage loginPage2 = new LoginPage(webDriver2);
        loginPage2.login("multiplavozac", "1111");
        WebDriverWait wait2 = new WebDriverWait(webDriver2, 5l);
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[class='mx-2']")));
        String username2 = loginPage2.getUsernameLoggedIn().getText();
        assertEquals("multipla vozac", username2);

        //Passenger: login
        HomePage homePage1 = new HomePage(webDriver1);
        homePage1.getLoginButton().click();
        LoginPage loginPage1 = new LoginPage(webDriver1);
        loginPage1.login("dave", "1111");
        WebDriverWait wait1 = new WebDriverWait(webDriver1, 5l);
        wait1.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[class='mx-2']")));
        String username = loginPage1.getUsernameLoggedIn().getText();
        assertEquals("Dave Dave", username);

        //Passenger: Search for route
        SearchForRoutePage searchForRoutePage1 = new SearchForRoutePage(webDriver1);
        wait1.until(ExpectedConditions.textToBePresentInElement(searchForRoutePage1.getTitle(), "Zakaži vožnju:"));
        searchForRoutePage1.searchForRoute();
        //Passenger: Choose route
        ChooseRoutePage chooseRoutePage1 = new ChooseRoutePage(webDriver1);
        wait1.until(ExpectedConditions.elementToBeClickable(chooseRoutePage1.getNextButton()));
        chooseRoutePage1.getNextButton().click();

        //Passenger: enter ride criteria
        RideCriteriaPage rideCriteriaPage1 = new RideCriteriaPage(webDriver1);

        rideCriteriaPage1.enterCriteria(3, "1", false, false, false, "");
        wait1.until(ExpectedConditions.elementToBeClickable(rideCriteriaPage1.getFindRideButton()));
        rideCriteriaPage1.getFindRideButton().click();

        ConfirmRidePage confirmRidePage1 = new ConfirmRidePage(webDriver1);
        wait1.until(ExpectedConditions.elementToBeClickable(confirmRidePage1.getRejectRideButton()));
        confirmRidePage1.getRejectRideButton().click();

        wait1.until(ExpectedConditions.textToBePresentInElement(webDriver1.findElement(By.xpath("//h2[@class='h2 mb-4 text-center']")), "Odbijena vožnja:"));
        String titleText = webDriver1.findElement(By.xpath("//h2[@class='h2 mb-4 text-center']")).getText();
        assertEquals("Odbijena vožnja:", titleText);

    }



    @Test
    public void orderRideUnsuccessfulTooBigDistanceTest() {

        //Passenger: login
        HomePage homePage1 = new HomePage(webDriver1);
        homePage1.getLoginButton().click();
        LoginPage loginPage1 = new LoginPage(webDriver1);
        loginPage1.login("dave", "1111");
        WebDriverWait wait1 = new WebDriverWait(webDriver1, 5l);
        wait1.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[class='mx-2']")));
        String username = loginPage1.getUsernameLoggedIn().getText();
        assertEquals("Dave Dave", username);

        //Passenger: Search for route
        SearchForRoutePage searchForRoutePage1 = new SearchForRoutePage(webDriver1);
        wait1.until(ExpectedConditions.textToBePresentInElement(searchForRoutePage1.getTitle(), "Zakaži vožnju:"));
        searchForRoutePage1.searchForRouteTooBigDistance();


        //assert
        wait1.until(ExpectedConditions.visibilityOf(searchForRoutePage1.getWarningTooBigDistance()));
        String message = searchForRoutePage1.getWarningTooBigDistance().getText();
        assertEquals(message, "Distanca izmedju trazenih lokacija je ća od 150km.");
    }


    @Test
    public void orderRideUnsuccessfulCriteriaNotMetNoPetTest() {

        //Driver: login
        HomePage homePage2 = new HomePage(webDriver2);
        homePage2.getLoginButton().click();
        LoginPage loginPage2 = new LoginPage(webDriver2);
        loginPage2.login("multiplavozac", "1111");
        WebDriverWait wait2 = new WebDriverWait(webDriver2, 5l);
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[class='mx-2']")));
        String username2 = loginPage2.getUsernameLoggedIn().getText();
        assertEquals("multipla vozac", username2);

        //Passenger: login
        HomePage homePage1 = new HomePage(webDriver1);
        homePage1.getLoginButton().click();
        LoginPage loginPage1 = new LoginPage(webDriver1);
        loginPage1.login("dave", "1111");
        WebDriverWait wait1 = new WebDriverWait(webDriver1, 5l);
        wait1.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[class='mx-2']")));
        String username = loginPage1.getUsernameLoggedIn().getText();
        assertEquals("Dave Dave", username);

        //Passenger: Search for route
        SearchForRoutePage searchForRoutePage1 = new SearchForRoutePage(webDriver1);
        wait1.until(ExpectedConditions.textToBePresentInElement(searchForRoutePage1.getTitle(), "Zakaži vožnju:"));
        searchForRoutePage1.searchForRoute();
        //Passenger: Choose route
        ChooseRoutePage chooseRoutePage1 = new ChooseRoutePage(webDriver1);
        wait1.until(ExpectedConditions.elementToBeClickable(chooseRoutePage1.getNextButton()));
        chooseRoutePage1.getNextButton().click();

        //Passenger: enter ride criteria
        RideCriteriaPage rideCriteriaPage1 = new RideCriteriaPage(webDriver1);
        rideCriteriaPage1.enterCriteria(1, "1", false, true, false, "");
        wait1.until(ExpectedConditions.elementToBeClickable(rideCriteriaPage1.getFindRideButton()));
        rideCriteriaPage1.getFindRideButton().click();

        wait1.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='ant-notification-notice-description']")));
        WebElement warning = webDriver1.findElement(By.xpath("//div[@class='ant-notification-notice-description']"));
        assertEquals("Nažalost, trenutno nemamo dostupnih vozila sa zadatim kriterijumima.", warning.getText());
    }

    @Test
    public void orderRideUnsuccessfulCriteriaNotMetNoBabyTest() {

        //Driver: login
        HomePage homePage2 = new HomePage(webDriver2);
        homePage2.getLoginButton().click();
        LoginPage loginPage2 = new LoginPage(webDriver2);
        loginPage2.login("golfvozac", "1111");
        WebDriverWait wait2 = new WebDriverWait(webDriver2, 5l);
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[class='mx-2']")));
        String username2 = loginPage2.getUsernameLoggedIn().getText();
        assertEquals("golf vozac", username2);

        //Passenger: login
        HomePage homePage1 = new HomePage(webDriver1);
        homePage1.getLoginButton().click();
        LoginPage loginPage1 = new LoginPage(webDriver1);
        loginPage1.login("dave", "1111");
        WebDriverWait wait1 = new WebDriverWait(webDriver1, 5l);
        wait1.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[class='mx-2']")));
        String username = loginPage1.getUsernameLoggedIn().getText();
        assertEquals("Dave Dave", username);

        //Passenger: Search for route
        SearchForRoutePage searchForRoutePage1 = new SearchForRoutePage(webDriver1);
        wait1.until(ExpectedConditions.textToBePresentInElement(searchForRoutePage1.getTitle(), "Zakaži vožnju:"));
        searchForRoutePage1.searchForRoute();
        //Passenger: Choose route
        ChooseRoutePage chooseRoutePage1 = new ChooseRoutePage(webDriver1);
        wait1.until(ExpectedConditions.elementToBeClickable(chooseRoutePage1.getNextButton()));
        chooseRoutePage1.getNextButton().click();

        //Passenger: enter ride criteria
        RideCriteriaPage rideCriteriaPage1 = new RideCriteriaPage(webDriver1);
        rideCriteriaPage1.enterCriteria(1, "1", true, false, false, "");
        wait1.until(ExpectedConditions.elementToBeClickable(rideCriteriaPage1.getFindRideButton()));
        rideCriteriaPage1.getFindRideButton().click();

        wait1.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='ant-notification-notice-description']")));
        WebElement warning = webDriver1.findElement(By.xpath("//div[@class='ant-notification-notice-description']"));
        assertEquals("Nažalost, trenutno nemamo dostupnih vozila sa zadatim kriterijumima.", warning.getText());
    }




    @Test
    public void orderRideUnsuccessfulCriteriaNotMetTooManyPassengersTest() {

        //Driver: login
        HomePage homePage2 = new HomePage(webDriver2);
        homePage2.getLoginButton().click();
        LoginPage loginPage2 = new LoginPage(webDriver2);
        loginPage2.login("multiplavozac", "1111");
        WebDriverWait wait2 = new WebDriverWait(webDriver2, 5l);
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[class='mx-2']")));
        String username2 = loginPage2.getUsernameLoggedIn().getText();
        assertEquals("multipla vozac", username2);

        //Passenger: login
        HomePage homePage1 = new HomePage(webDriver1);
        homePage1.getLoginButton().click();
        LoginPage loginPage1 = new LoginPage(webDriver1);
        loginPage1.login("dave", "1111");
        WebDriverWait wait1 = new WebDriverWait(webDriver1, 5l);
        wait1.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[class='mx-2']")));
        String username = loginPage1.getUsernameLoggedIn().getText();
        assertEquals("Dave Dave", username);

        //Passenger: Search for route
        SearchForRoutePage searchForRoutePage1 = new SearchForRoutePage(webDriver1);
        wait1.until(ExpectedConditions.textToBePresentInElement(searchForRoutePage1.getTitle(), "Zakaži vožnju:"));
        searchForRoutePage1.searchForRoute();
        //Passenger: Choose route
        ChooseRoutePage chooseRoutePage1 = new ChooseRoutePage(webDriver1);
        wait1.until(ExpectedConditions.elementToBeClickable(chooseRoutePage1.getNextButton()));
        chooseRoutePage1.getNextButton().click();

        //Passenger: enter ride criteria
        RideCriteriaPage rideCriteriaPage1 = new RideCriteriaPage(webDriver1);
        rideCriteriaPage1.enterCriteria(1, "5", false, false, false, "");
        wait1.until(ExpectedConditions.elementToBeClickable(rideCriteriaPage1.getFindRideButton()));
        rideCriteriaPage1.getFindRideButton().click();

        wait1.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='ant-notification-notice-description']")));
        WebElement warning = webDriver1.findElement(By.xpath("//div[@class='ant-notification-notice-description']"));
        assertEquals("Nažalost, trenutno nemamo dostupnih vozila sa zadatim kriterijumima.", warning.getText());
    }


    @Test
    public void orderRideUnsuccessfulScheduledTimeLimitExceededTest() {

        //Driver: login
        HomePage homePage2 = new HomePage(webDriver2);
        homePage2.getLoginButton().click();
        LoginPage loginPage2 = new LoginPage(webDriver2);
        loginPage2.login("multiplavozac", "1111");
        WebDriverWait wait2 = new WebDriverWait(webDriver2, 5l);
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[class='mx-2']")));
        String username2 = loginPage2.getUsernameLoggedIn().getText();
        assertEquals("multipla vozac", username2);

        //Passenger: login
        HomePage homePage1 = new HomePage(webDriver1);
        homePage1.getLoginButton().click();
        LoginPage loginPage1 = new LoginPage(webDriver1);
        loginPage1.login("dave", "1111");
        WebDriverWait wait1 = new WebDriverWait(webDriver1, 5l);
        wait1.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[class='mx-2']")));
        String username = loginPage1.getUsernameLoggedIn().getText();
        assertEquals("Dave Dave", username);

        //Passenger: Search for route
        SearchForRoutePage searchForRoutePage1 = new SearchForRoutePage(webDriver1);
        wait1.until(ExpectedConditions.textToBePresentInElement(searchForRoutePage1.getTitle(), "Zakaži vožnju:"));
        searchForRoutePage1.searchForRoute();
        //Passenger: Choose route
        ChooseRoutePage chooseRoutePage1 = new ChooseRoutePage(webDriver1);
        wait1.until(ExpectedConditions.elementToBeClickable(chooseRoutePage1.getNextButton()));
        chooseRoutePage1.getNextButton().click();

        //Passenger: enter ride criteria
        RideCriteriaPage rideCriteriaPage1 = new RideCriteriaPage(webDriver1);
        String LDT_PATTERN = "yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter LDT_FORMATTER = DateTimeFormatter.ofPattern(LDT_PATTERN);
        String dateTimeString = LDT_FORMATTER.format(LocalDateTime.now().plusHours(6l));
        rideCriteriaPage1.enterCriteria(1, "1", false, false, true, dateTimeString);
        wait1.until(ExpectedConditions.elementToBeClickable(rideCriteriaPage1.getFindRideButton()));
        rideCriteriaPage1.getFindRideButton().click();

        wait1.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='ant-notification-notice-description']")));
        WebElement warning = webDriver1.findElement(By.xpath("//div[@class='ant-notification-notice-description']"));
        assertEquals("Izvinite, ne možete zakazati vožnju više od 5 sati unapred.", warning.getText());
    }

    @Test
    public void orderRideUnsuccessfulScheduledTimeLimitOneMinutePastTest() {

        //Driver: login
        HomePage homePage2 = new HomePage(webDriver2);
        homePage2.getLoginButton().click();
        LoginPage loginPage2 = new LoginPage(webDriver2);
        loginPage2.login("multiplavozac", "1111");
        WebDriverWait wait2 = new WebDriverWait(webDriver2, 5l);
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[class='mx-2']")));
        String username2 = loginPage2.getUsernameLoggedIn().getText();
        assertEquals("multipla vozac", username2);

        //Passenger: login
        HomePage homePage1 = new HomePage(webDriver1);
        homePage1.getLoginButton().click();
        LoginPage loginPage1 = new LoginPage(webDriver1);
        loginPage1.login("dave", "1111");
        WebDriverWait wait1 = new WebDriverWait(webDriver1, 5l);
        wait1.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[class='mx-2']")));
        String username = loginPage1.getUsernameLoggedIn().getText();
        assertEquals("Dave Dave", username);

        //Passenger: Search for route
        SearchForRoutePage searchForRoutePage1 = new SearchForRoutePage(webDriver1);
        wait1.until(ExpectedConditions.textToBePresentInElement(searchForRoutePage1.getTitle(), "Zakaži vožnju:"));
        searchForRoutePage1.searchForRoute();
        //Passenger: Choose route
        ChooseRoutePage chooseRoutePage1 = new ChooseRoutePage(webDriver1);
        wait1.until(ExpectedConditions.elementToBeClickable(chooseRoutePage1.getNextButton()));
        chooseRoutePage1.getNextButton().click();

        //Passenger: enter ride criteria
        RideCriteriaPage rideCriteriaPage1 = new RideCriteriaPage(webDriver1);
        String LDT_PATTERN = "yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter LDT_FORMATTER = DateTimeFormatter.ofPattern(LDT_PATTERN);
        String dateTimeString = LDT_FORMATTER.format(LocalDateTime.now().plusMinutes(301l));
        rideCriteriaPage1.enterCriteria(1, "1", false, false, true, dateTimeString);
        wait1.until(ExpectedConditions.elementToBeClickable(rideCriteriaPage1.getFindRideButton()));
        rideCriteriaPage1.getFindRideButton().click();

        wait1.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='ant-notification-notice-description']")));
        WebElement warning = webDriver1.findElement(By.xpath("//div[@class='ant-notification-notice-description']"));
        assertEquals("Izvinite, ne možete zakazati vožnju više od 5 sati unapred.", warning.getText());
    }


    @Test
    public void orderRideUnsuccessfulScheduledTimeLimitOneMinuteBeforeTest() {

        //Driver: login
        HomePage homePage2 = new HomePage(webDriver2);
        homePage2.getLoginButton().click();
        LoginPage loginPage2 = new LoginPage(webDriver2);
        loginPage2.login("multiplavozac", "1111");
        WebDriverWait wait2 = new WebDriverWait(webDriver2, 5l);
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[class='mx-2']")));
        String username2 = loginPage2.getUsernameLoggedIn().getText();
        assertEquals("multipla vozac", username2);

        //Passenger: login
        HomePage homePage1 = new HomePage(webDriver1);
        homePage1.getLoginButton().click();
        LoginPage loginPage1 = new LoginPage(webDriver1);
        loginPage1.login("dave", "1111");
        WebDriverWait wait1 = new WebDriverWait(webDriver1, 5l);
        wait1.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[class='mx-2']")));
        String username = loginPage1.getUsernameLoggedIn().getText();
        assertEquals("Dave Dave", username);

        //Passenger: Search for route
        SearchForRoutePage searchForRoutePage1 = new SearchForRoutePage(webDriver1);
        wait1.until(ExpectedConditions.textToBePresentInElement(searchForRoutePage1.getTitle(), "Zakaži vožnju:"));
        searchForRoutePage1.searchForRoute();
        //Passenger: Choose route
        ChooseRoutePage chooseRoutePage1 = new ChooseRoutePage(webDriver1);
        wait1.until(ExpectedConditions.elementToBeClickable(chooseRoutePage1.getNextButton()));
        chooseRoutePage1.getNextButton().click();

        //Passenger: enter ride criteria
        RideCriteriaPage rideCriteriaPage1 = new RideCriteriaPage(webDriver1);
        String LDT_PATTERN = "yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter LDT_FORMATTER = DateTimeFormatter.ofPattern(LDT_PATTERN);
        String dateTimeString = LDT_FORMATTER.format(LocalDateTime.now().plusMinutes(299l));
        rideCriteriaPage1.enterCriteria(3, "1", false, false, true, dateTimeString);
        wait1.until(ExpectedConditions.elementToBeClickable(rideCriteriaPage1.getFindRideButton()));
        rideCriteriaPage1.getFindRideButton().click();

        ConfirmRidePage confirmRidePage1 = new ConfirmRidePage(webDriver1);
        wait1.until(ExpectedConditions.elementToBeClickable(confirmRidePage1.getConfirmRideButton()));
        assertTrue(confirmRidePage1.pageValidation());
        confirmRidePage1.getRejectRideButton().click();

    }



}
