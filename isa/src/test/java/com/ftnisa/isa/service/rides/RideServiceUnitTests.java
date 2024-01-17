package com.ftnisa.isa.service.rides;

import com.ftnisa.isa.constants.RideBookingConstants;
import com.ftnisa.isa.dto.ride.RideBookingRequestDto;
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
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
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


//    @BeforeAll
//    public static void setUp() throws ParseException {
//
//        User user = Mockito.mock(User.class);
//        Mockito.when(user.getUsername()).thenReturn("Dave");
//        Authentication authentication = mock(Authentication.class);
//        SecurityContext securityContext = mock(SecurityContext.class);
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        SecurityContextHolder.setContext(securityContext);
//        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);
//
//
//    }


    @Test
    public void bookAQuickRideSuccessfulTest() throws Exception {


        RideBookingRequestDto rideBookingRequestDto = RideBookingConstants.returnRideBookingRequestDto();
        Route route = Mockito.mock(Route.class);
        Mockito.when(routeRepositoryMocked.findById(any())).thenReturn(Optional.of(route));
        VehicleType vehicleType = Mockito.mock(VehicleType.class);
        Mockito.when(vehicleTypeRepositoryMocked.findById(any())).thenReturn(Optional.of(vehicleType));
        //Mockito.when(rideService.requestQuickRideBooking(any())).thenReturn(null);
        RideServiceImpl rideServiceImpl = new RideServiceImpl(rideRepositoryMocked, driverServiceMocked, routeServiceMocked, userRepositoryMocked, panicRepositoryMocked, routeRepositoryMocked, notificationServiceMocked, vehicleTypeRepositoryMocked, template );
        RideServiceImpl rideServiceSpy = Mockito.spy(rideServiceImpl);
        Mockito.doReturn(null).when(rideServiceSpy).requestQuickRideBooking(any());
        //RideService rideServiceMocked = Mockito.mock(RideService.class);
        //Mockito.when(rideServiceSpy.requestQuickRideBooking(any(Ride.class))).thenReturn(null);
        //Mockito.doNothing().when(rideServiceSpy).requestQuickRideBooking(any(Ride.class));


        Ride rideN = rideServiceSpy.bookARide(rideBookingRequestDto);
        verify(rideServiceSpy, atLeastOnce()).requestQuickRideBooking(any());



    }

    @Test
    public void bookAQuickRideNoRouteTest(){

    }

    @Test
    public void bookAQuickRideNoVehicleTypeTest(){

    }

    @Test
    public void bookAScheduledRideSuccessfulTest(){

    }

    @Test
    public void bookAScheduledRideNoRouteTest(){

    }

    @Test
    public void bookAScheduledRideNoVehicleTypeTest(){

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
