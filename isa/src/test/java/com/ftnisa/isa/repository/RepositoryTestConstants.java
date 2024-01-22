package com.ftnisa.isa.repository;

import com.ftnisa.isa.model.user.Driver;
import com.ftnisa.isa.model.user.Passenger;
import com.ftnisa.isa.model.user.User;

public class RepositoryTestConstants {


    public static Driver createDriver(){
        Driver driver = new Driver();
        driver.setDriverLicense("123");
        driver.setOccupied(false);
        driver.setActive(true);
        driver.setEmail("driver@me.com");
        driver.setFirstname("vozac");
        driver.setLastname("testni");

        return driver;
    }

    public static User createUser(){
        User user = new User();

        user.setEmail("user@me.com");
        user.setFirstname("putnik");
        user.setLastname("testni");

        return user;
    }
}
