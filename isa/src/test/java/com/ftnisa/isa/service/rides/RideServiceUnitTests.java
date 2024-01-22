package com.ftnisa.isa.service.rides;

import com.ftnisa.isa.constants.RideBookingConstants;
import com.ftnisa.isa.dto.ride.RideBookingRequestDto;
import com.ftnisa.isa.exception.TempRouteExpired;
import com.ftnisa.isa.model.location.Location;
import com.ftnisa.isa.model.ride.Panic;
import com.ftnisa.isa.model.ride.Ride;
import com.ftnisa.isa.model.ride.RideStatus;
import com.ftnisa.isa.model.route.Route;
import com.ftnisa.isa.model.user.Driver;
import com.ftnisa.isa.model.user.Passenger;
import com.ftnisa.isa.model.user.User;
import com.ftnisa.isa.model.vehicle.VehicleType;
import com.ftnisa.isa.repository.*;
import com.ftnisa.isa.service.DriverService;
import com.ftnisa.isa.service.NotificationService;
import com.ftnisa.isa.service.RideServiceImpl;
import com.ftnisa.isa.service.RouteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RideServiceUnitTests {



    @Autowired
    private RideServiceImpl rideService;

    @MockBean
    RideRepository rideRepositoryMocked;

    @MockBean
    RouteRepository routeRepositoryMocked;

    @MockBean
    VehicleTypeRepository vehicleTypeRepositoryMocked;

    @MockBean
    NotificationService notificationServiceMocked;

    @MockBean
    SimpMessagingTemplate template;

    @MockBean
    DriverService driverServiceMocked;

    @MockBean
    RouteService routeServiceMocked;

    @MockBean
    UserRepository userRepositoryMocked;

    @MockBean
    PanicRepository panicRepositoryMocked;



    @Test
    public void requestQuickRideBookingRejectedBecauseActiveRideTest() throws Exception {

        User user = Mockito.mock(User.class);
        Mockito.when(user.getUsername()).thenReturn("Dave");
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);
        Mockito.when(userRepositoryMocked.findByUsername("Dave")).thenReturn(user);

        Ride ride = new Ride();
        List<Ride> rideList = new ArrayList<>();
        rideList.add(ride);

        Mockito.when(rideRepositoryMocked.findAllByPassengerAndRideStatus(any(), any())).thenReturn(rideList);

        rideService.requestQuickRideBooking(ride);

        verify(rideRepositoryMocked, atLeastOnce()).save(ride);
        assertEquals(RideStatus.REJECTED, ride.getRideStatus());
        assertEquals("Izvinite, ne možete zakazati novu vožnju, dok imate drugu aktivnu vožnju.", ride.getRejection().getRejectionReason());

    }

    @Test
    public void requestQuickRideBookingRejectedBecauseNoActiveDrivers() throws Exception {

        User user = Mockito.mock(User.class);
        Mockito.when(user.getUsername()).thenReturn("Dave");
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);
        Mockito.when(userRepositoryMocked.findByUsername("Dave")).thenReturn(user);

        Ride ride = new Ride();
        List<Ride> rideList = new ArrayList<>();
        List<Driver> driverList = new ArrayList<>();
        Mockito.when(rideRepositoryMocked.findAllByPassengerAndRideStatus(any(), any())).thenReturn(rideList);
        Mockito.when(driverServiceMocked.getActiveDrivers()).thenReturn(driverList);

        rideService.requestQuickRideBooking(ride);

        verify(rideRepositoryMocked, atLeastOnce()).save(ride);
        assertEquals(RideStatus.REJECTED, ride.getRideStatus());
        assertEquals("Nažalost, trenutno nema aktivnih vozača.", ride.getRejection().getRejectionReason());

    }


    @Test
    public void requestQuickRideBookingRejectedBecauseNoFreeActiveAndNoUnbookedDriversTest() throws Exception {

        User user = Mockito.mock(User.class);
        Mockito.when(user.getUsername()).thenReturn("Dave");
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);
        Mockito.when(userRepositoryMocked.findByUsername("Dave")).thenReturn(user);

        Ride ride = new Ride();
        List<Ride> rideList = new ArrayList<>();
        List<Driver> activeDriverList = new ArrayList<>();
        Driver driver = Mockito.mock(Driver.class);
        activeDriverList.add(driver);
        List<Driver> emptyDriverList = new ArrayList<>();
        Mockito.when(rideRepositoryMocked.findAllByPassengerAndRideStatus(any(), any())).thenReturn(rideList);
        Mockito.when(driverServiceMocked.getActiveDrivers()).thenReturn(activeDriverList);
        Mockito.when(driverServiceMocked.getFreeActiveDrivers(activeDriverList)).thenReturn(emptyDriverList);
        Mockito.when(driverServiceMocked.getDriversWithoutNextBooking(activeDriverList)).thenReturn(emptyDriverList);


        rideService.requestQuickRideBooking(ride);

        verify(rideRepositoryMocked, atLeastOnce()).save(ride);
        assertEquals(RideStatus.REJECTED, ride.getRideStatus());
        assertEquals("Nažalost, trenutno nema dostupnih vozača za Vašu vožnju. Pokušajte malo kasnije.", ride.getRejection().getRejectionReason());

    }

    @Test
    public void requestQuickRideBookingRejectedBecauseNoAppropriateDriversTest() throws Exception {

        User user = Mockito.mock(User.class);
        Mockito.when(user.getUsername()).thenReturn("Dave");
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);
        Mockito.when(userRepositoryMocked.findByUsername("Dave")).thenReturn(user);

        Ride ride = new Ride();
        List<Ride> rideList = new ArrayList<>();
        List<Driver> activeDriverList = new ArrayList<>();
        Driver driver = Mockito.mock(Driver.class);
        activeDriverList.add(driver);
        List<Driver> emptyDriverList = new ArrayList<>();
        Mockito.when(rideRepositoryMocked.findAllByPassengerAndRideStatus(any(), any())).thenReturn(rideList);
        Mockito.when(driverServiceMocked.getActiveDrivers()).thenReturn(activeDriverList);
        Mockito.when(driverServiceMocked.getFreeActiveDrivers(activeDriverList)).thenReturn(activeDriverList);
        Mockito.when(driverServiceMocked.getDriversWithoutNextBooking(activeDriverList)).thenReturn(emptyDriverList);

        Route route = Mockito.mock(Route.class);
        List<Route> routeList = new ArrayList<>();
        routeList.add(route);
        ride.setRoutes(routeList);
        ride.setPetTransportFlag(false);
        ride.setBabyTransportFlag(false);
        VehicleType vehicleType = Mockito.mock(VehicleType.class);
        ride.setVehicleType(vehicleType);
        ride.setNumberOfPassengers(2);
        Mockito.when(routeServiceMocked.fetchRouteLengthMeters(route)).thenReturn(4000l);
        Mockito.when(routeServiceMocked.fetchRouteDurationMinutes(route)).thenReturn(6f);

        RideServiceImpl rideServiceImpl = new RideServiceImpl(rideRepositoryMocked, driverServiceMocked, routeServiceMocked, userRepositoryMocked, panicRepositoryMocked, routeRepositoryMocked, notificationServiceMocked, vehicleTypeRepositoryMocked, template );
        RideServiceImpl rideServiceSpy = Mockito.spy(rideServiceImpl);
        Mockito.doReturn(emptyDriverList).when(rideServiceSpy).filterDriversByRideCriteria(activeDriverList, false, false, vehicleType, 2, 6f);

        rideServiceSpy.requestQuickRideBooking(ride);

        verify(rideRepositoryMocked, atLeastOnce()).save(ride);
        assertEquals(RideStatus.REJECTED, ride.getRideStatus());
        assertEquals("Nažalost, trenutno nemamo dostupnih vozila sa zadatim kriterijumima.", ride.getRejection().getRejectionReason());


    }


    @Test
    public void requestQuickRideBookingRejectedBecauseNoSchedulableDriversTest() throws Exception {

        User user = Mockito.mock(User.class);
        Mockito.when(user.getUsername()).thenReturn("Dave");
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);
        Mockito.when(userRepositoryMocked.findByUsername("Dave")).thenReturn(user);

        Ride ride = new Ride();
        List<Ride> rideList = new ArrayList<>();
        List<Driver> activeDriverList = new ArrayList<>();
        Driver driver = Mockito.mock(Driver.class);
        activeDriverList.add(driver);
        List<Driver> emptyDriverList = new ArrayList<>();
        Mockito.when(rideRepositoryMocked.findAllByPassengerAndRideStatus(any(), any())).thenReturn(rideList);
        Mockito.when(driverServiceMocked.getActiveDrivers()).thenReturn(activeDriverList);
        Mockito.when(driverServiceMocked.getFreeActiveDrivers(activeDriverList)).thenReturn(activeDriverList);
        Mockito.when(driverServiceMocked.getDriversWithoutNextBooking(activeDriverList)).thenReturn(emptyDriverList);

        Route route = Mockito.mock(Route.class);
        List<Route> routeList = new ArrayList<>();
        routeList.add(route);
        ride.setRoutes(routeList);
        ride.setPetTransportFlag(false);
        ride.setBabyTransportFlag(false);
        VehicleType vehicleType = Mockito.mock(VehicleType.class);
        ride.setVehicleType(vehicleType);
        ride.setNumberOfPassengers(2);
        Mockito.when(routeServiceMocked.fetchRouteLengthMeters(route)).thenReturn(4000l);
        Mockito.when(routeServiceMocked.fetchRouteDurationMinutes(route)).thenReturn(6f);

        RideServiceImpl rideServiceImpl = new RideServiceImpl(rideRepositoryMocked, driverServiceMocked, routeServiceMocked, userRepositoryMocked, panicRepositoryMocked, routeRepositoryMocked, notificationServiceMocked, vehicleTypeRepositoryMocked, template );
        RideServiceImpl rideServiceSpy = Mockito.spy(rideServiceImpl);
        Mockito.doReturn(activeDriverList).when(rideServiceSpy).filterDriversByRideCriteria(activeDriverList, false, false, vehicleType, 2, 6f);
        Mockito.doReturn(emptyDriverList).when(rideServiceSpy).filterDriversBySchedule(activeDriverList, ride);

        rideServiceSpy.requestQuickRideBooking(ride);

        verify(rideRepositoryMocked, atLeastOnce()).save(ride);
        assertEquals(RideStatus.REJECTED, ride.getRideStatus());
        assertEquals("Nažalost, trenutno nemamo dostupne vozace sa traženim kriterijumima.", ride.getRejection().getRejectionReason());


    }


    @Test
    public void requestQuickRideBookingSuccessfulExistingFreeDriverTest() throws Exception {

        User user = Mockito.mock(User.class);
        Mockito.when(user.getUsername()).thenReturn("Dave");
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);
        Mockito.when(userRepositoryMocked.findByUsername("Dave")).thenReturn(user);

        Ride ride = new Ride();
        List<Ride> rideList = new ArrayList<>();
        List<Driver> activeDriverList = new ArrayList<>();
        Driver driver = Mockito.mock(Driver.class);
        activeDriverList.add(driver);
        List<Driver> emptyDriverList = new ArrayList<>();
        Mockito.when(rideRepositoryMocked.findAllByPassengerAndRideStatus(nullable(Passenger.class), nullable(RideStatus.class))).thenReturn(rideList);
        Mockito.when(driverServiceMocked.getActiveDrivers()).thenReturn(activeDriverList);
        Mockito.when(driverServiceMocked.getFreeActiveDrivers(activeDriverList)).thenReturn(activeDriverList);
        Mockito.when(driverServiceMocked.getDriversWithoutNextBooking(activeDriverList)).thenReturn(emptyDriverList);

        Route route = Mockito.mock(Route.class);
        Location startLocation = Mockito.mock(Location.class);
        route.setStartLocation(startLocation);
        List<Route> routeList = new ArrayList<>();
        routeList.add(route);
        ride.setRoutes(routeList);
        ride.setPetTransportFlag(false);
        ride.setBabyTransportFlag(false);
        VehicleType vehicleType = Mockito.mock(VehicleType.class);
        ride.setVehicleType(vehicleType);
        ride.setNumberOfPassengers(2);
        Mockito.when(routeServiceMocked.fetchRouteLengthMeters(route)).thenReturn(4000l);
        Mockito.when(routeServiceMocked.fetchRouteDurationMinutes(route)).thenReturn(6f);

        RideServiceImpl rideServiceImpl = new RideServiceImpl(rideRepositoryMocked, driverServiceMocked, routeServiceMocked, userRepositoryMocked, panicRepositoryMocked, routeRepositoryMocked, notificationServiceMocked, vehicleTypeRepositoryMocked, template );
        RideServiceImpl rideServiceSpy = Mockito.spy(rideServiceImpl);
        Mockito.doReturn(activeDriverList).when(rideServiceSpy).filterDriversByRideCriteria(activeDriverList, false, false, vehicleType, 2, 6f);
        Mockito.doReturn(activeDriverList).when(rideServiceSpy).filterDriversBySchedule(activeDriverList, ride);

        Mockito.when(driverServiceMocked.selectCurrentlyClosestDriver(any(), any())).thenReturn(driver);
        LocalDateTime startTime = LocalDateTime.now().plusMinutes(10l);
        Mockito.doReturn(startTime).when(rideServiceSpy).estimateDriversTimeOfArrival(ride, driver);
        Mockito.doReturn(320f).when(rideServiceSpy).calculateRidePrice(4000l, vehicleType);


        rideServiceSpy.requestQuickRideBooking(ride);


        verify(rideRepositoryMocked, atLeastOnce()).save(ride);
        assertEquals(driver, ride.getDriver());
        assertEquals(startTime, ride.getStartTime());
        assertEquals(startTime.plusMinutes(6l), ride.getFinishTime());
        assertEquals(RideStatus.PENDING, ride.getRideStatus());
        assertEquals(vehicleType, ride.getVehicleType());

    }

    @Test
    public void requestQuickRideBookingRejectedBecauseNoAppropriateDriversWithoutNextBookingTest() throws Exception {

        User user = Mockito.mock(User.class);
        Mockito.when(user.getUsername()).thenReturn("Dave");
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);
        Mockito.when(userRepositoryMocked.findByUsername("Dave")).thenReturn(user);

        Ride ride = new Ride();
        List<Ride> rideList = new ArrayList<>();
        List<Driver> activeDriverList = new ArrayList<>();
        Driver driver = Mockito.mock(Driver.class);
        activeDriverList.add(driver);
        List<Driver> emptyDriverList = new ArrayList<>();
        Mockito.when(rideRepositoryMocked.findAllByPassengerAndRideStatus(any(), any())).thenReturn(rideList);
        Mockito.when(driverServiceMocked.getActiveDrivers()).thenReturn(activeDriverList);
        Mockito.when(driverServiceMocked.getFreeActiveDrivers(activeDriverList)).thenReturn(emptyDriverList);
        Mockito.when(driverServiceMocked.getDriversWithoutNextBooking(activeDriverList)).thenReturn(activeDriverList);

        Route route = Mockito.mock(Route.class);
        List<Route> routeList = new ArrayList<>();
        routeList.add(route);
        ride.setRoutes(routeList);
        ride.setPetTransportFlag(false);
        ride.setBabyTransportFlag(false);
        VehicleType vehicleType = Mockito.mock(VehicleType.class);
        ride.setVehicleType(vehicleType);
        ride.setNumberOfPassengers(2);
        Mockito.when(routeServiceMocked.fetchRouteLengthMeters(route)).thenReturn(4000l);
        Mockito.when(routeServiceMocked.fetchRouteDurationMinutes(route)).thenReturn(6f);

        RideServiceImpl rideServiceImpl = new RideServiceImpl(rideRepositoryMocked, driverServiceMocked, routeServiceMocked, userRepositoryMocked, panicRepositoryMocked, routeRepositoryMocked, notificationServiceMocked, vehicleTypeRepositoryMocked, template );
        RideServiceImpl rideServiceSpy = Mockito.spy(rideServiceImpl);
        Mockito.doReturn(emptyDriverList).when(rideServiceSpy).filterDriversByRideCriteria(activeDriverList, false, false, vehicleType, 2, 6f);

        rideServiceSpy.requestQuickRideBooking(ride);

        verify(rideRepositoryMocked, atLeastOnce()).save(ride);
        assertEquals(RideStatus.REJECTED, ride.getRideStatus());
        assertEquals("Nažalost, trenutno nemamo dostupnih vozila sa zadatim kriterijumima.", ride.getRejection().getRejectionReason());


    }


    @Test
    public void requestQuickRideBookingRejectedBecauseNoSchedulableBetweenDriversWithoutNextBookingTest() throws Exception {

        User user = Mockito.mock(User.class);
        Mockito.when(user.getUsername()).thenReturn("Dave");
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);
        Mockito.when(userRepositoryMocked.findByUsername("Dave")).thenReturn(user);

        Ride ride = new Ride();
        List<Ride> rideList = new ArrayList<>();
        List<Driver> activeDriverList = new ArrayList<>();
        Driver driver = Mockito.mock(Driver.class);
        activeDriverList.add(driver);
        List<Driver> emptyDriverList = new ArrayList<>();
        Mockito.when(rideRepositoryMocked.findAllByPassengerAndRideStatus(any(), any())).thenReturn(rideList);
        Mockito.when(driverServiceMocked.getActiveDrivers()).thenReturn(activeDriverList);
        Mockito.when(driverServiceMocked.getFreeActiveDrivers(activeDriverList)).thenReturn(emptyDriverList);
        Mockito.when(driverServiceMocked.getDriversWithoutNextBooking(activeDriverList)).thenReturn(activeDriverList);

        Route route = Mockito.mock(Route.class);
        List<Route> routeList = new ArrayList<>();
        routeList.add(route);
        ride.setRoutes(routeList);
        ride.setPetTransportFlag(false);
        ride.setBabyTransportFlag(false);
        VehicleType vehicleType = Mockito.mock(VehicleType.class);
        ride.setVehicleType(vehicleType);
        ride.setNumberOfPassengers(2);
        Mockito.when(routeServiceMocked.fetchRouteLengthMeters(route)).thenReturn(4000l);
        Mockito.when(routeServiceMocked.fetchRouteDurationMinutes(route)).thenReturn(6f);

        RideServiceImpl rideServiceImpl = new RideServiceImpl(rideRepositoryMocked, driverServiceMocked, routeServiceMocked, userRepositoryMocked, panicRepositoryMocked, routeRepositoryMocked, notificationServiceMocked, vehicleTypeRepositoryMocked, template );
        RideServiceImpl rideServiceSpy = Mockito.spy(rideServiceImpl);
        Mockito.doReturn(activeDriverList).when(rideServiceSpy).filterDriversByRideCriteria(activeDriverList, false, false, vehicleType, 2, 6f);
        Mockito.doReturn(emptyDriverList).when(rideServiceSpy).filterDriversBySchedule(activeDriverList, ride);

        rideServiceSpy.requestQuickRideBooking(ride);

        verify(rideRepositoryMocked, atLeastOnce()).save(ride);
        assertEquals(RideStatus.REJECTED, ride.getRideStatus());
        assertEquals("Nažalost, trenutno nemamo dostupne vozace sa traženim kriterijumima.", ride.getRejection().getRejectionReason());


    }



    @Test
    public void requestQuickRideBookingSuccessfulExistingDriverWithoutNextBookingTest() throws Exception {

        User user = Mockito.mock(User.class);
        Mockito.when(user.getUsername()).thenReturn("Dave");
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);
        Mockito.when(userRepositoryMocked.findByUsername("Dave")).thenReturn(user);

        Ride ride = new Ride();
        List<Ride> rideList = new ArrayList<>();
        List<Driver> activeDriverList = new ArrayList<>();
        Driver driver = Mockito.mock(Driver.class);
        activeDriverList.add(driver);
        List<Driver> emptyDriverList = new ArrayList<>();
        Mockito.when(rideRepositoryMocked.findAllByPassengerAndRideStatus(nullable(Passenger.class), nullable(RideStatus.class))).thenReturn(rideList);
        Mockito.when(driverServiceMocked.getActiveDrivers()).thenReturn(activeDriverList);
        Mockito.when(driverServiceMocked.getFreeActiveDrivers(activeDriverList)).thenReturn(emptyDriverList);
        Mockito.when(driverServiceMocked.getDriversWithoutNextBooking(activeDriverList)).thenReturn(activeDriverList);

        Route route = Mockito.mock(Route.class);
        Location startLocation = Mockito.mock(Location.class);
        route.setStartLocation(startLocation);
        List<Route> routeList = new ArrayList<>();
        routeList.add(route);
        ride.setRoutes(routeList);
        ride.setPetTransportFlag(false);
        ride.setBabyTransportFlag(false);
        VehicleType vehicleType = Mockito.mock(VehicleType.class);
        ride.setVehicleType(vehicleType);
        ride.setNumberOfPassengers(2);
        Mockito.when(routeServiceMocked.fetchRouteLengthMeters(route)).thenReturn(4000l);
        Mockito.when(routeServiceMocked.fetchRouteDurationMinutes(route)).thenReturn(6f);

        RideServiceImpl rideServiceImpl = new RideServiceImpl(rideRepositoryMocked, driverServiceMocked, routeServiceMocked, userRepositoryMocked, panicRepositoryMocked, routeRepositoryMocked, notificationServiceMocked, vehicleTypeRepositoryMocked, template );
        RideServiceImpl rideServiceSpy = Mockito.spy(rideServiceImpl);
        Mockito.doReturn(activeDriverList).when(rideServiceSpy).filterDriversByRideCriteria(activeDriverList, false, false, vehicleType, 2, 6f);
        Mockito.doReturn(activeDriverList).when(rideServiceSpy).filterDriversBySchedule(activeDriverList, ride);

        Mockito.when(driverServiceMocked.selectClosestDriverAfterCurrentRide(any(), any())).thenReturn(driver);
        LocalDateTime startTime = LocalDateTime.now().plusMinutes(10l);
        Mockito.doReturn(startTime).when(rideServiceSpy).estimateDriversTimeOfArrival(ride, driver);
        Mockito.doReturn(320f).when(rideServiceSpy).calculateRidePrice(4000l, vehicleType);


        rideServiceSpy.requestQuickRideBooking(ride);


        verify(rideRepositoryMocked, atLeastOnce()).save(ride);
        assertEquals(driver, ride.getDriver());
        assertEquals(startTime, ride.getStartTime());
        assertEquals(startTime.plusMinutes(6l), ride.getFinishTime());
        assertEquals(RideStatus.PENDING, ride.getRideStatus());
        assertEquals(vehicleType, ride.getVehicleType());

    }

    @Test
    public void scheduledRideBookingRejectedPassengerHasActiveRideTest() throws Exception {
        User user = Mockito.mock(User.class);
        Mockito.when(user.getUsername()).thenReturn("Dave");
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);
        Mockito.when(userRepositoryMocked.findByUsername("Dave")).thenReturn(user);

        Ride ride = new Ride();
        List<Ride> rideList = new ArrayList<>();
        rideList.add(ride);

        Mockito.when(rideRepositoryMocked.findAllByPassengerAndRideStatus(any(), any())).thenReturn(rideList);

        rideService.scheduledRideBooking(ride);

        verify(rideRepositoryMocked, atLeastOnce()).save(ride);
        assertEquals(RideStatus.REJECTED, ride.getRideStatus());
        assertEquals("Izvinite, ne možete zakazati novu vožnju, dok imate drugu aktivnu vožnju.", ride.getRejection().getRejectionReason());
    }


    @Test
    public void scheduledRideBookingRejectedBecauseMoreThanFiveHoursInAdvanceTest() throws Exception {
        User user = Mockito.mock(User.class);
        Mockito.when(user.getUsername()).thenReturn("Dave");
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);
        Mockito.when(userRepositoryMocked.findByUsername("Dave")).thenReturn(user);

        Ride ride = new Ride();
        LocalDateTime rideStartTime = LocalDateTime.now().plusHours(8l);
        ride.setStartTime(rideStartTime);
        List<Ride> rideList = new ArrayList<>();

        Mockito.when(rideRepositoryMocked.findAllByPassengerAndRideStatus(any(), any())).thenReturn(rideList);


        rideService.scheduledRideBooking(ride);

        verify(rideRepositoryMocked, atLeastOnce()).save(ride);
        assertEquals(RideStatus.REJECTED, ride.getRideStatus());
        assertEquals("Izvinite, ne možete zakazati vožnju više od 5 sati unapred.", ride.getRejection().getRejectionReason());
    }


    @Test
    public void scheduledRideBookingRejectedBecauseNoActiveDriversTest() throws Exception {
        User user = Mockito.mock(User.class);
        Mockito.when(user.getUsername()).thenReturn("Dave");
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);
        Mockito.when(userRepositoryMocked.findByUsername("Dave")).thenReturn(user);

        Ride ride = new Ride();
        LocalDateTime rideStartTime = LocalDateTime.now().plusHours(2l);
        ride.setStartTime(rideStartTime);
        List<Ride> rideList = new ArrayList<>();
        List<Driver> driverList = new ArrayList<>();

        Mockito.when(rideRepositoryMocked.findAllByPassengerAndRideStatus(any(), any())).thenReturn(rideList);
        Mockito.when(rideRepositoryMocked.findAllByPassengerAndRideStatus(any(), any())).thenReturn(rideList);
        Mockito.when(driverServiceMocked.getActiveDrivers()).thenReturn(driverList);

        rideService.scheduledRideBooking(ride);

        verify(rideRepositoryMocked, atLeastOnce()).save(ride);
        assertEquals(RideStatus.REJECTED, ride.getRideStatus());
        assertEquals("Nažalost, trenutno nema aktivnih vozača.", ride.getRejection().getRejectionReason());
    }



    @Test
    public void scheduledRideBookingRejectedBecauseNoAppropriateDriversTest() throws Exception {
        User user = Mockito.mock(User.class);
        Mockito.when(user.getUsername()).thenReturn("Dave");
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);
        Mockito.when(userRepositoryMocked.findByUsername("Dave")).thenReturn(user);

        Ride ride = new Ride();
        LocalDateTime rideStartTime = LocalDateTime.now().plusHours(2l);
        ride.setStartTime(rideStartTime);
        List<Ride> rideList = new ArrayList<>();
        List<Driver> activeDriverList = new ArrayList<>();
        Driver driver = Mockito.mock(Driver.class);
        activeDriverList.add(driver);
        List<Driver> emptyDriverList = new ArrayList<>();

        Mockito.when(rideRepositoryMocked.findAllByPassengerAndRideStatus(any(), any())).thenReturn(rideList);
        Mockito.when(driverServiceMocked.getActiveDrivers()).thenReturn(activeDriverList);

        Route route = Mockito.mock(Route.class);
        List<Route> routeList = new ArrayList<>();
        routeList.add(route);
        ride.setRoutes(routeList);
        ride.setPetTransportFlag(false);
        ride.setBabyTransportFlag(false);
        VehicleType vehicleType = Mockito.mock(VehicleType.class);
        ride.setVehicleType(vehicleType);
        ride.setNumberOfPassengers(2);
        Mockito.when(routeServiceMocked.fetchRouteLengthMeters(route)).thenReturn(4000l);
        Mockito.when(routeServiceMocked.fetchRouteDurationMinutes(route)).thenReturn(6f);

        RideServiceImpl rideServiceImpl = new RideServiceImpl(rideRepositoryMocked, driverServiceMocked, routeServiceMocked, userRepositoryMocked, panicRepositoryMocked, routeRepositoryMocked, notificationServiceMocked, vehicleTypeRepositoryMocked, template );
        RideServiceImpl rideServiceSpy = Mockito.spy(rideServiceImpl);
        Mockito.doReturn(activeDriverList).when(rideServiceSpy).filterDriversByRideCriteria(activeDriverList, false, false, vehicleType, 2, 6f);
        Mockito.doReturn(emptyDriverList).when(rideServiceSpy).filterDriversBySchedule(any(), any());


        rideServiceSpy.scheduledRideBooking(ride);


        verify(rideRepositoryMocked, atLeastOnce()).save(ride);
        assertEquals(RideStatus.REJECTED, ride.getRideStatus());
        assertEquals("Za zadati termin nemamo dostupne vozace sa traženim kriterijumima.", ride.getRejection().getRejectionReason());
    }



    @Test
    public void scheduledRideBookingSuccessfulTest() throws Exception {
        User user = Mockito.mock(User.class);
        Mockito.when(user.getUsername()).thenReturn("Dave");
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);
        Mockito.when(userRepositoryMocked.findByUsername("Dave")).thenReturn(user);

        Ride ride = new Ride();
        LocalDateTime rideStartTime = LocalDateTime.now().plusHours(2l);
        ride.setStartTime(rideStartTime);
        List<Ride> rideList = new ArrayList<>();
        List<Driver> activeDriverList = new ArrayList<>();
        Driver driver = Mockito.mock(Driver.class);
        activeDriverList.add(driver);
        List<Driver> emptyDriverList = new ArrayList<>();

        Mockito.when(rideRepositoryMocked.findAllByPassengerAndRideStatus(any(), any())).thenReturn(rideList);
        Mockito.when(driverServiceMocked.getActiveDrivers()).thenReturn(activeDriverList);

        Route route = Mockito.mock(Route.class);
        List<Route> routeList = new ArrayList<>();
        routeList.add(route);
        ride.setRoutes(routeList);
        ride.setPetTransportFlag(false);
        ride.setBabyTransportFlag(false);
        VehicleType vehicleType = Mockito.mock(VehicleType.class);
        ride.setVehicleType(vehicleType);
        ride.setNumberOfPassengers(2);
        Mockito.when(routeServiceMocked.fetchRouteLengthMeters(route)).thenReturn(4000l);
        Mockito.when(routeServiceMocked.fetchRouteDurationMinutes(route)).thenReturn(6f);

        RideServiceImpl rideServiceImpl = new RideServiceImpl(rideRepositoryMocked, driverServiceMocked, routeServiceMocked, userRepositoryMocked, panicRepositoryMocked, routeRepositoryMocked, notificationServiceMocked, vehicleTypeRepositoryMocked, template );
        RideServiceImpl rideServiceSpy = Mockito.spy(rideServiceImpl);
        Mockito.doReturn(activeDriverList).when(rideServiceSpy).filterDriversByRideCriteria(activeDriverList, false, false, vehicleType, 2, 6f);
        Mockito.doReturn(activeDriverList).when(rideServiceSpy).filterDriversBySchedule(activeDriverList, ride);

        Mockito.when(driverServiceMocked.selectCurrentlyClosestDriver(any(), any())).thenReturn(driver);

        Mockito.doReturn(320f).when(rideServiceSpy).calculateRidePrice(4000l, vehicleType);


        rideServiceSpy.scheduledRideBooking(ride);


        verify(rideRepositoryMocked, atLeastOnce()).save(ride);
        assertEquals(driver, ride.getDriver());
        assertEquals(rideStartTime, ride.getStartTime());
        assertEquals(rideStartTime.plusMinutes(6l), ride.getFinishTime());
        assertEquals(RideStatus.PENDING, ride.getRideStatus());
        assertEquals(vehicleType, ride.getVehicleType());
        assertEquals(true, ride.getScheduled());

    }



    @Test
    public void bookAQuickRideSuccessfulTest() throws Exception {

        RideBookingRequestDto rideBookingRequestDto = RideBookingConstants.returnRideBookingRequestDto();
        Route route = Mockito.mock(Route.class);
        Mockito.when(routeRepositoryMocked.findById(any())).thenReturn(Optional.of(route));
        VehicleType vehicleType = Mockito.mock(VehicleType.class);
        Mockito.when(vehicleTypeRepositoryMocked.findById(any())).thenReturn(Optional.of(vehicleType));
        RideServiceImpl rideServiceImpl = new RideServiceImpl(rideRepositoryMocked, driverServiceMocked, routeServiceMocked, userRepositoryMocked, panicRepositoryMocked, routeRepositoryMocked, notificationServiceMocked, vehicleTypeRepositoryMocked, template );
        RideServiceImpl rideServiceSpy = Mockito.spy(rideServiceImpl);
        Mockito.doReturn(null).when(rideServiceSpy).requestQuickRideBooking(any());


        Ride ride = rideServiceSpy.bookARide(rideBookingRequestDto);


        verify(rideServiceSpy, atLeastOnce()).requestQuickRideBooking(any());
        assertEquals(rideBookingRequestDto.getBabyTransportFlag(), ride.getBabyTransportFlag());
        assertEquals(rideBookingRequestDto.getPetTransportFlag(), ride.getPetTransportFlag());
        assertEquals(rideBookingRequestDto.getNumberOfPassengers(), ride.getNumberOfPassengers());
        assertEquals(rideBookingRequestDto.getRouteOptimizationCriteria(), ride.getRouteOptimizationCriteria());

    }




    @Test
    public void bookARideNoRouteTest() {

        assertThrows(Exception.class,
                ()->{
                    RideBookingRequestDto rideBookingRequestDto = RideBookingConstants.returnRideBookingRequestDto();
                    Mockito.when(routeRepositoryMocked.findById(any())).thenReturn(null);
                    rideService.bookARide(rideBookingRequestDto);
                });

    }

    @Test
    public void bookARideNoVehicleTypeTest(){

        assertThrows(Exception.class,
                ()->{
                    RideBookingRequestDto rideBookingRequestDto = RideBookingConstants.returnRideBookingRequestDto();
                    Mockito.when(vehicleTypeRepositoryMocked.findById(any())).thenReturn(null);
                    rideService.bookARide(rideBookingRequestDto);
                });

    }

    @Test
    public void bookAScheduledRideSuccessfulTest() throws Exception {

        RideBookingRequestDto rideBookingRequestDto = RideBookingConstants.returnRideBookingRequestDto();
        rideBookingRequestDto.setScheduled(true);
        rideBookingRequestDto.setScheduledStartTime(OffsetDateTime.now().plusHours(1l));
        Route route = Mockito.mock(Route.class);
        Mockito.when(routeRepositoryMocked.findById(any())).thenReturn(Optional.of(route));
        VehicleType vehicleType = Mockito.mock(VehicleType.class);
        Mockito.when(vehicleTypeRepositoryMocked.findById(any())).thenReturn(Optional.of(vehicleType));
        RideServiceImpl rideServiceImpl = new RideServiceImpl(rideRepositoryMocked, driverServiceMocked, routeServiceMocked, userRepositoryMocked, panicRepositoryMocked, routeRepositoryMocked, notificationServiceMocked, vehicleTypeRepositoryMocked, template );
        RideServiceImpl rideServiceSpy = Mockito.spy(rideServiceImpl);
        Mockito.doReturn(null).when(rideServiceSpy).scheduledRideBooking(any());


        Ride ride = rideServiceSpy.bookARide(rideBookingRequestDto);


        verify(rideServiceSpy, atLeastOnce()).scheduledRideBooking(any());
        assertEquals(rideBookingRequestDto.getBabyTransportFlag(), ride.getBabyTransportFlag());
        assertEquals(rideBookingRequestDto.getPetTransportFlag(), ride.getPetTransportFlag());
        assertEquals(rideBookingRequestDto.getNumberOfPassengers(), ride.getNumberOfPassengers());
        assertEquals(rideBookingRequestDto.getRouteOptimizationCriteria(), ride.getRouteOptimizationCriteria());

    }



    // startRideByDriver() existing rideId
    @Test
    public void startRideByDriverTest(){

        Ride ride = new Ride();
        Driver driver = Mockito.mock(Driver.class);
        ride.setDriver(driver);
        Passenger passenger = Mockito.mock(Passenger.class);
        ride.setPassenger(passenger);
        Mockito.when(rideRepositoryMocked.findOneById(any())).thenReturn(ride);

        Ride expectedRide = rideService.startRideByDriver(1);

        assertEquals(RideStatus.ACTIVE, expectedRide.getRideStatus());
        verify(rideRepositoryMocked, atLeastOnce()).save(ride);
        verify(notificationServiceMocked, atLeastOnce()).createInstantNotification(
                passenger, "Vaša vožnja je započeta.");
        verify(driver, atLeastOnce()).setOccupied(true);


    }

    // startRideByDriver() non existing rideId
    @Test
    public void shouldThrowNullPointerExceptionStartRideByDriverNonExistingRideIdTest(){
        assertThrows(NullPointerException.class,
                ()->{
                    Mockito.when(rideRepositoryMocked.findOneById(any())).thenReturn(null);
                    Ride expectedRide = rideService.startRideByDriver(1);
                });
    }


    @Test
    public void finishRideByDriverTest(){

        Ride ride = new Ride();
        Driver driver = Mockito.mock(Driver.class);
        ride.setDriver(driver);
        Passenger passenger = Mockito.mock(Passenger.class);
        ride.setPassenger(passenger);
        Mockito.when(rideRepositoryMocked.findOneById(any())).thenReturn(ride);

        Ride expectedRide = rideService.finishRideByDriver(1);

        assertEquals(RideStatus.FINISHED, expectedRide.getRideStatus());
        verify(rideRepositoryMocked, atLeastOnce()).save(ride);
        verify(notificationServiceMocked, atLeastOnce()).createInstantNotification(
                passenger, "Vaša vožnja je završena.");
        verify(driver, atLeastOnce()).setOccupied(false);


    }

    @Test
    public void shouldThrowNullPointerExceptionFinishRideByDriverNonExistingRideIdTest(){
        assertThrows(NullPointerException.class,
                ()->{
                    Mockito.when(rideRepositoryMocked.findOneById(any())).thenReturn(null);
                    Ride expectedRide = rideService.finishRideByDriver(1);
                });
    }




    @Test
    public void shouldRejectRideByDriverWhenExistingRideIdTest(){

        User user = Mockito.mock(User.class);
        Mockito.when(user.getUsername()).thenReturn("Dave");
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);

        RideStatus rideStatus = RideStatus.REJECTED;

        Ride ride = new Ride();
        Passenger passenger = Mockito.mock(Passenger.class);
        ride.setPassenger(passenger);
        Mockito.when(rideRepositoryMocked.findOneById(any())).thenReturn(ride);

        Integer rideId = 1;
        String reason = "why not";

        rideService.rejectRideByDriver(rideId, reason);

        assertEquals(RideStatus.REJECTED, ride.getRideStatus());
        assertEquals(reason, ride.getRejection().getRejectionReason());
        verify(notificationServiceMocked, atLeastOnce()).createInstantNotification(
                passenger, "Vašu vožnju je vozač nažalost morao da otkaže.");
        verify(rideRepositoryMocked, atLeastOnce()).save(ride);

    }


    @Test
    public void shouldRejectRideByDriverWhenExistingRideIdNoReasonTest(){

        User user = Mockito.mock(User.class);
        Mockito.when(user.getUsername()).thenReturn("Dave");
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);

        RideStatus rideStatus = RideStatus.REJECTED;

        Ride ride = new Ride();
        Passenger passenger = Mockito.mock(Passenger.class);
        ride.setPassenger(passenger);
        Mockito.when(rideRepositoryMocked.findOneById(any())).thenReturn(ride);

        Integer rideId = 1;
        String reason = null;

        rideService.rejectRideByDriver(rideId, reason);

        assertEquals(RideStatus.REJECTED, ride.getRideStatus());
        assertEquals(reason, ride.getRejection().getRejectionReason());
        verify(rideRepositoryMocked, atLeastOnce()).save(ride);

    }



    @Test
    public void shouldThrowNullPointerExceptionWhenRejectRideByDriverNonExistingRideIdTest(){

        assertThrows(NullPointerException.class,
                ()->{
                    Mockito.when(rideRepositoryMocked.findOneById(any())).thenReturn(null);
                    rideService.rejectRideByDriver(1, "Ne ide mi se");
                });

    }



    @Test
    public void finalizeRideBookingAcceptedTest(){

        Ride ride = new Ride();
        Passenger passenger = Mockito.mock(Passenger.class);
        ride.setPassenger(passenger);
        Driver driver = Mockito.mock(Driver.class);
        ride.setDriver(driver);
        ride.setStartTime(LocalDateTime.now().plusMinutes(10l));
        Mockito.when(ride.getDriver().getUsername()).thenReturn("Vozac Jovan");
        Mockito.when(rideRepositoryMocked.findOneById(any())).thenReturn(ride);

        rideService.finalizeRideBooking(true, 1);

        assertEquals(RideStatus.ACCEPTED, ride.getRideStatus());
        verify(notificationServiceMocked, atLeastOnce()).createInstantNotification(
                passenger, "Vaša vožnja je upravo zakazana. Vozilo stiže za 9 minuta. Status vožnje možete pratiti na Vašem dešbordu.");
        verify(notificationServiceMocked, atLeastOnce()).createInstantNotification(
                driver, "Zakazana Vam je nova vožnja. Putnik Vas očekuje za 9 minuta. Detalje vožnje možete proveriti na Vašem dešbordu.");
        verify(rideRepositoryMocked, atLeastOnce()).save(ride);

    }


    @Test
    public void shouldThrowNullPointerExceptionWhenFinalizeRideBookingAcceptedNonExistingRideIdTest(){

        assertThrows(NullPointerException.class,
                ()->{
                    Mockito.when(rideRepositoryMocked.findOneById(any())).thenReturn(null);
                    rideService.finalizeRideBooking(true, 1);
                });

    }


    @Test
    public void finalizeRideBookingRejectedTest(){

        Ride ride = new Ride();
        Passenger passenger = Mockito.mock(Passenger.class);
        ride.setPassenger(passenger);
        Mockito.when(rideRepositoryMocked.findOneById(any())).thenReturn(ride);

        rideService.finalizeRideBooking(false, 1);

        assertEquals(RideStatus.REJECTED, ride.getRideStatus());
        assertEquals("Passenger did not accept the ride", ride.getRejection().getRejectionReason());
        verify(rideRepositoryMocked, atLeastOnce()).save(ride);

    }

    @Test
    public void shouldThrowNullPointerExceptionWhenFinalizeRideBookingRejectedNonExistingRideIdTest(){

        assertThrows(NullPointerException.class,
                ()->{
                    Mockito.when(rideRepositoryMocked.findOneById(any())).thenReturn(null);
                    rideService.finalizeRideBooking(false, 1);
                });

    }


    @ParameterizedTest
    @CsvSource({"3250, 50f, 282.5f", "3250, 120f, 510f", "500, 200f, 220f", "0, 50f, 120f"})
    public void calculatePriceTest(long rideLength, float pricePerKm, float expectedPrice){
        VehicleType vehicleType = Mockito.mock(VehicleType.class);
        Mockito.when(vehicleType.getPricePerKm()).thenReturn(pricePerKm);

        float price = rideService.calculateRidePrice(rideLength, vehicleType);

        assertEquals(expectedPrice, price);
    }


//    @Test
//    public void panicTest(){
//
//        User user = new User();
//        Mockito.when(user.getId()).thenReturn(2);
//        Integer rideId = 1;
//        String panicReason = "Cudno me gleda ovaj!";
//        Ride ride = new Ride();
//        Mockito.when(rideRepositoryMocked.findById(1)).thenReturn(Optional.of(ride));
//
//        Panic panic = rideService.panic(user, rideId, panicReason);
//
//        assertEquals(user, panic.getUser());
//        assertEquals(ride, panic.getRide());
//        assertEquals(LocalDateTime.now(), panic.getPanicTime());
//        verify(rideRepositoryMocked, atLeastOnce()).save(ride);
//    }




















}
