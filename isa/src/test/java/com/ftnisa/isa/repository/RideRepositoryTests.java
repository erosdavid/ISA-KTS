package com.ftnisa.isa.repository;

import com.ftnisa.isa.model.ride.Ride;
import com.ftnisa.isa.model.ride.RideStatus;
import com.ftnisa.isa.model.user.Driver;
import com.ftnisa.isa.model.user.Passenger;
import com.ftnisa.isa.model.user.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;


import javax.persistence.criteria.CriteriaBuilder;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RideRepositoryTests {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private UserRepository userRepository;



    @Test
    public void findById_givenValidId_returnsRide() {

        Ride ride = new Ride();
        rideRepository.save(ride);

        Ride foundRide = rideRepository.findById(ride.getId()).get();

        assertEquals(ride.getId(), foundRide.getId());
    }


    @Test
    public void findByDriver_givenValidDriver_returnsDriversRides() {

        Driver driver = RepositoryTestConstants.createDriver();
        Ride ride1 = new Ride();
        Ride ride2 = new Ride();
        ride1.setRideStatus(RideStatus.FINISHED);
        ride2.setRideStatus(RideStatus.ACTIVE);
        ride1.setDriver(driver);
        ride2.setDriver(driver);
        driverRepository.save(driver);
        rideRepository.save(ride1);
        rideRepository.save(ride2);

        List<Ride> foundRides = rideRepository.findByDriver(driver);

        assertEquals(2, foundRides.size());
    }

    @Test
    public void findByDriverAndStartTimeBetween_givenValidDriverAndStartTimeAndFinishTime_returnsRides() {

        Driver driver = RepositoryTestConstants.createDriver();
        Ride ride1 = new Ride();
        Ride ride2 = new Ride();
        Ride ride3 = new Ride();
        ride1.setRideStatus(RideStatus.FINISHED);
        ride2.setRideStatus(RideStatus.ACTIVE);
        ride3.setRideStatus(RideStatus.ACTIVE);
        ride1.setDriver(driver);
        ride2.setDriver(driver);
        ride1.setStartTime(LocalDateTime.now().minusDays(5));
        ride2.setStartTime(LocalDateTime.now().minusDays(4));
        ride3.setStartTime(LocalDateTime.now().plusDays(2));
        driverRepository.save(driver);
        rideRepository.save(ride1);
        rideRepository.save(ride2);

        List<Ride> foundRides = rideRepository.findByDriverAndStartTimeBetween(driver, LocalDateTime.now().minusDays(7), LocalDateTime.now());

        assertEquals(2, foundRides.size());
    }


    @Test
    public void findByDriverAndStartTimeBetweenAndRideStatus_givenValidDriverAndStatusAndStartTimeInterval_returnsRides() {

        Driver driver = RepositoryTestConstants.createDriver();
        Ride ride1 = new Ride();
        Ride ride2 = new Ride();
        Ride ride3 = new Ride();
        ride1.setRideStatus(RideStatus.FINISHED);
        ride2.setRideStatus(RideStatus.ACTIVE);
        ride3.setRideStatus(RideStatus.ACTIVE);
        ride1.setDriver(driver);
        ride2.setDriver(driver);
        ride1.setStartTime(LocalDateTime.now().minusDays(5));
        ride2.setStartTime(LocalDateTime.now().minusDays(4));
        ride3.setStartTime(LocalDateTime.now().plusDays(2));
        driverRepository.save(driver);
        rideRepository.save(ride1);
        rideRepository.save(ride2);
        rideRepository.save(ride3);

        List<Ride> foundRides = rideRepository.findByDriverAndStartTimeBetweenAndRideStatus(driver, LocalDateTime.now().minusDays(7), LocalDateTime.now(), RideStatus.ACTIVE);

        assertEquals(1, foundRides.size());
    }

    @Test
    public void findOneByDriverAndRideStatus_givenValidDriverAndStatus_returnsOneRide() {

        Driver driver = RepositoryTestConstants.createDriver();
        Ride ride1 = new Ride();
        Ride ride2 = new Ride();
        ride1.setRideStatus(RideStatus.FINISHED);
        ride2.setRideStatus(RideStatus.ACTIVE);
        ride1.setDriver(driver);
        ride2.setDriver(driver);
        driverRepository.save(driver);
        rideRepository.save(ride1);
        rideRepository.save(ride2);

        Ride foundRide = rideRepository.findOneByDriverAndRideStatus(driver, RideStatus.ACTIVE);

        assertEquals(ride2.getId(), foundRide.getId());
    }

    @Test
    public void findAllByDriverAndRideStatus_givenValidDriverAndStatus_returnsRides() {

        Driver driver = RepositoryTestConstants.createDriver();
        Ride ride1 = new Ride();
        Ride ride2 = new Ride();
        Ride ride3 = new Ride();
        ride1.setRideStatus(RideStatus.FINISHED);
        ride2.setRideStatus(RideStatus.ACTIVE);
        ride3.setRideStatus(RideStatus.ACTIVE);
        ride1.setDriver(driver);
        ride2.setDriver(driver);
        ride3.setDriver(driver);
        driverRepository.save(driver);
        rideRepository.save(ride1);
        rideRepository.save(ride2);
        rideRepository.save(ride3);

        List<Ride> foundRides = rideRepository.findAllByDriverAndRideStatus(driver, RideStatus.ACTIVE);

        assertEquals(2, foundRides.size());
    }

    @Test
    public void findAllByDriverAndRideStatusIsNotOrderByIdDesc_givenValidDriverAndStatus_returnsRides() {

        Driver driver = RepositoryTestConstants.createDriver();
        Ride ride1 = new Ride();
        Ride ride2 = new Ride();
        Ride ride3 = new Ride();
        ride1.setRideStatus(RideStatus.FINISHED);
        ride2.setRideStatus(RideStatus.ACTIVE);
        ride3.setRideStatus(RideStatus.ACTIVE);
        ride1.setDriver(driver);
        ride2.setDriver(driver);
        ride3.setDriver(driver);
        driverRepository.save(driver);
        rideRepository.save(ride1);
        rideRepository.save(ride2);
        rideRepository.save(ride3);

        List<Ride> foundRides = rideRepository.findAllByDriverAndRideStatusIsNotOrderByIdDesc(driver, RideStatus.FINISHED);

        assertEquals(2, foundRides.size());
        assertEquals(Optional.of(3), Optional.of(foundRides.get(0).getId()));
        assertEquals(Optional.of(2), Optional.of(foundRides.get(1).getId()));
    }

    @Test
    public void findOneById_givenValidId_returnsOneRide() {

        Ride ride1 = new Ride();
        Ride ride2 = new Ride();
        ride1.setRideStatus(RideStatus.FINISHED);
        ride2.setRideStatus(RideStatus.ACTIVE);
        rideRepository.save(ride1);
        rideRepository.save(ride2);

        Ride foundRide = rideRepository.findOneById(1);

        assertEquals(RideStatus.FINISHED, foundRide.getRideStatus());
    }

    @Test
    public void findAllByPassengerAndRideStatus_givenValidPassengerAndStatus_returnsRides() {

        User passenger1 = RepositoryTestConstants.createUser();
        User passenger2 = RepositoryTestConstants.createUser();
        Ride ride1 = new Ride();
        Ride ride2 = new Ride();
        Ride ride3 = new Ride();
        ride1.setRideStatus(RideStatus.FINISHED);
        ride2.setRideStatus(RideStatus.ACTIVE);
        ride3.setRideStatus(RideStatus.ACTIVE);
        ride1.setPassenger(passenger1);
        ride2.setPassenger(passenger1);
        ride3.setPassenger(passenger2);
        userRepository.save(passenger1);
        userRepository.save(passenger2);
        rideRepository.save(ride1);
        rideRepository.save(ride2);
        rideRepository.save(ride3);

        List<Ride> foundRides = rideRepository.findAllByPassengerAndRideStatus(passenger1, RideStatus.ACTIVE);

        assertEquals(1, foundRides.size());
    }


    @Test
    public void findByPassengerAndStartTimeBetween_givenValidPassengerAndStartTimeInterval_returnsRides() {

        User passenger1 = RepositoryTestConstants.createUser();
        User passenger2 = RepositoryTestConstants.createUser();
        Ride ride1 = new Ride();
        Ride ride2 = new Ride();
        Ride ride3 = new Ride();
        ride1.setRideStatus(RideStatus.FINISHED);
        ride2.setRideStatus(RideStatus.ACTIVE);
        ride3.setRideStatus(RideStatus.ACTIVE);
        ride1.setPassenger(passenger1);
        ride2.setPassenger(passenger2);
        ride3.setPassenger(passenger2);
        ride1.setStartTime(LocalDateTime.now().minusDays(5));
        ride2.setStartTime(LocalDateTime.now().minusDays(4));
        ride3.setStartTime(LocalDateTime.now().plusDays(2));
        userRepository.save(passenger1);
        userRepository.save(passenger2);
        rideRepository.save(ride1);
        rideRepository.save(ride2);
        rideRepository.save(ride3);

        List<Ride> foundRides = rideRepository.findByPassengerAndStartTimeBetween(passenger2, LocalDateTime.now().minusDays(7), LocalDateTime.now());

        assertEquals(1, foundRides.size());

    }


    @Test
    public void findByPassengerAndStartTimeBetweenAndRideStatus_givenValidPassengerAndStartTimeIntervalAndStatus_returnsRides() {

        User passenger1 = RepositoryTestConstants.createUser();
        User passenger2 = RepositoryTestConstants.createUser();
        Ride ride1 = new Ride();
        Ride ride2 = new Ride();
        Ride ride3 = new Ride();
        Ride ride4 = new Ride();
        ride1.setRideStatus(RideStatus.ACTIVE);
        ride2.setRideStatus(RideStatus.ACTIVE);
        ride3.setRideStatus(RideStatus.ACTIVE);
        ride4.setRideStatus(RideStatus.FINISHED);
        ride1.setPassenger(passenger1);
        ride2.setPassenger(passenger2);
        ride3.setPassenger(passenger2);
        ride4.setPassenger(passenger2);
        ride1.setStartTime(LocalDateTime.now().minusDays(5));
        ride2.setStartTime(LocalDateTime.now().minusDays(4));
        ride3.setStartTime(LocalDateTime.now().plusDays(2));
        ride4.setStartTime(LocalDateTime.now().minusDays(3));
        userRepository.save(passenger1);
        userRepository.save(passenger2);
        rideRepository.save(ride1);
        rideRepository.save(ride2);
        rideRepository.save(ride3);
        rideRepository.save(ride4);

        List<Ride> foundRides = rideRepository.findByPassengerAndStartTimeBetweenAndRideStatus(passenger2, LocalDateTime.now().minusDays(7), LocalDateTime.now(), RideStatus.ACTIVE);

        assertEquals(1, foundRides.size());

    }


    @Test
    public void findByStartTimeBetweenAndRideStatus_givenValidStatusAndStartTimeInterval_returnsRides() {

        Ride ride1 = new Ride();
        Ride ride2 = new Ride();
        Ride ride3 = new Ride();
        Ride ride4 = new Ride();
        ride1.setRideStatus(RideStatus.ACTIVE);
        ride2.setRideStatus(RideStatus.ACTIVE);
        ride3.setRideStatus(RideStatus.ACTIVE);
        ride4.setRideStatus(RideStatus.FINISHED);
        ride1.setStartTime(LocalDateTime.now().minusDays(5));
        ride2.setStartTime(LocalDateTime.now().minusDays(4));
        ride3.setStartTime(LocalDateTime.now().plusDays(2));
        ride4.setStartTime(LocalDateTime.now().minusDays(3));
        rideRepository.save(ride1);
        rideRepository.save(ride2);
        rideRepository.save(ride3);
        rideRepository.save(ride4);

        List<Ride> foundRides = rideRepository.findByStartTimeBetweenAndRideStatus(LocalDateTime.now().minusDays(7), LocalDateTime.now(), RideStatus.ACTIVE);

        assertEquals(2, foundRides.size());

    }

    @Test
    public void findByDriverAndRideStatusIn_givenValidStatusesAndDriver_returnsRides() {

        Driver driver1 = RepositoryTestConstants.createDriver();
        Driver driver2 = RepositoryTestConstants.createDriver();
        Ride ride1 = new Ride();
        Ride ride2 = new Ride();
        Ride ride3 = new Ride();
        Ride ride4 = new Ride();
        ride1.setRideStatus(RideStatus.ACTIVE);
        ride2.setRideStatus(RideStatus.REJECTED);
        ride3.setRideStatus(RideStatus.PENDING);
        ride4.setRideStatus(RideStatus.FINISHED);
        ride1.setDriver(driver1);
        ride2.setDriver(driver1);
        ride3.setDriver(driver1);
        ride4.setDriver(driver2);
        driverRepository.save(driver1);
        driverRepository.save(driver2);
        rideRepository.save(ride1);
        rideRepository.save(ride2);
        rideRepository.save(ride3);
        rideRepository.save(ride4);
        List<RideStatus> rideStatuses = new ArrayList<>();
        rideStatuses.add(RideStatus.FINISHED);
        rideStatuses.add(RideStatus.ACTIVE);

        Ride foundRide = rideRepository.findByDriverAndRideStatusIn(driver1, rideStatuses);
        Integer id = foundRide.getId();

        assertEquals(Optional.of(1), Optional.of(id));

    }

    @Test
    public void findByPassengerAndRideStatusIn_givenValidStatusesAndPassenger_returnsRides() {

        User passenger1 = RepositoryTestConstants.createUser();
        User passenger2 = RepositoryTestConstants.createUser();
        Ride ride1 = new Ride();
        Ride ride2 = new Ride();
        Ride ride3 = new Ride();
        Ride ride4 = new Ride();
        ride1.setRideStatus(RideStatus.ACTIVE);
        ride2.setRideStatus(RideStatus.REJECTED);
        ride3.setRideStatus(RideStatus.PENDING);
        ride4.setRideStatus(RideStatus.FINISHED);
        ride1.setPassenger(passenger1);
        ride2.setPassenger(passenger1);
        ride3.setPassenger(passenger1);
        ride4.setPassenger(passenger2);
        userRepository.save(passenger1);
        userRepository.save(passenger2);
        rideRepository.save(ride1);
        rideRepository.save(ride2);
        rideRepository.save(ride3);
        rideRepository.save(ride4);
        List<RideStatus> rideStatuses = new ArrayList<>();
        rideStatuses.add(RideStatus.FINISHED);
        rideStatuses.add(RideStatus.ACTIVE);

        Ride foundRide = rideRepository.findByPassengerAndRideStatusIn(passenger1, rideStatuses);

        assertEquals(Optional.of(1), Optional.of(foundRide.getId()));

    }

    @Test
    public void findByPassengerAndRideStatusIsNotOrderByIdDesc_givenValidStatusesAndPassenger_returnsRides() {

        User passenger1 = RepositoryTestConstants.createUser();
        User passenger2 = RepositoryTestConstants.createUser();
        Ride ride1 = new Ride();
        Ride ride2 = new Ride();
        Ride ride3 = new Ride();
        Ride ride4 = new Ride();
        ride1.setRideStatus(RideStatus.ACTIVE);
        ride2.setRideStatus(RideStatus.REJECTED);
        ride3.setRideStatus(RideStatus.PENDING);
        ride4.setRideStatus(RideStatus.FINISHED);
        ride1.setPassenger(passenger1);
        ride2.setPassenger(passenger1);
        ride3.setPassenger(passenger1);
        ride4.setPassenger(passenger2);
        userRepository.save(passenger1);
        userRepository.save(passenger2);
        rideRepository.save(ride1);
        rideRepository.save(ride2);
        rideRepository.save(ride3);
        rideRepository.save(ride4);
        List<RideStatus> rideStatuses = new ArrayList<>();
        rideStatuses.add(RideStatus.FINISHED);
        rideStatuses.add(RideStatus.ACTIVE);

        List<Ride> foundRides = rideRepository.findByPassengerAndRideStatusIsNotOrderByIdDesc(passenger1, RideStatus.PENDING);
        assertEquals(2, foundRides.size());
        assertEquals(Optional.of(2), Optional.of(foundRides.get(0).getId()));
        assertEquals(Optional.of(1), Optional.of(foundRides.get(1).getId()));

    }


}