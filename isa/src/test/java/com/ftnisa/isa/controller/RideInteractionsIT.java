package com.ftnisa.isa.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ftnisa.isa.constants.RideBookingConstants;
import com.ftnisa.isa.dto.ride.RideAcceptanceDto;
import com.ftnisa.isa.dto.ride.RideBookingRequestDto;
import com.ftnisa.isa.dto.ride.RideRejectionRequestDto;
import com.ftnisa.isa.integrations.ors.responses.routing.geojson.GeoJSONIndividualRouteResponse;
import com.ftnisa.isa.model.location.Location;
import com.ftnisa.isa.model.ride.Ride;
import com.ftnisa.isa.model.ride.RideStatus;
import com.ftnisa.isa.model.route.Route;
import com.ftnisa.isa.model.user.Driver;
import com.ftnisa.isa.model.user.Role;
import com.ftnisa.isa.model.user.User;
import com.ftnisa.isa.model.user.UserType;
import com.ftnisa.isa.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@SpringBootTest
//@AutoConfigureMockMvc
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
//@Sql("/import.sql")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RideInteractionsIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    DriverRepository driverRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    RouteRepository routeRepository;

    @Autowired
    UserNotificationRepository userNotificationRepository;



    @Transactional
    @BeforeEach
    public void setup(){

        Driver driver = RideBookingConstants.createDriver();
        driver.setUsername("testvozac1");
        driver.setUserType(UserType.DRIVER);
        List<Role> roles1 = roleRepository.findByName("DRIVER");
        driver.setRoles(roles1);
        driverRepository.save(driver);

        User user = RideBookingConstants.createUser();
        user.setUsername("testputnik1");
        user.setUserType(UserType.PASSENGER);
        List<Role> roles2 = roleRepository.findByName("USER");
        user.setRoles(roles2);
        userRepository.save(user);

    }


    @WithMockUser(username = "nekiputnik", roles={"USER"})
    @Test
    public void rejectDriveByUserSuccessfulTest() throws Exception{

        RideAcceptanceDto rideAcceptanceDto =new RideAcceptanceDto();
        rideAcceptanceDto.setAccepted(false);
        Ride ride = new Ride();
        ride.setStartTime(LocalDateTime.now().plusMinutes(6));
        ride.setFinishTime(LocalDateTime.now().plusMinutes(11));
        ride.setRideStatus(RideStatus.PENDING);
        Driver driver = RideBookingConstants.createDriver();
        User user = RideBookingConstants.createUser();
        ride.setPassenger(user);
        ride.setDriver(driver);
        driverRepository.save(driver);
        userRepository.save(user);
        rideRepository.save(ride);
        String url = String.format("/api/ride/%d/accept", ride.getId());

        ResultActions resultActions = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rideAcceptanceDto)));

        resultActions.andExpect(status().isOk());

        assertEquals("Passenger did not accept the ride", rideRepository.findOneById(ride.getId()).getRejection().getRejectionReason());
    }

    @WithMockUser(username = "nekiputnik", roles={"USER"})
    @Test
    public void acceptDriveByUserSuccessfulTest() throws Exception{

        RideAcceptanceDto rideAcceptanceDto =new RideAcceptanceDto();
        rideAcceptanceDto.setAccepted(true);
        Ride ride = new Ride();
        ride.setStartTime(LocalDateTime.now().plusMinutes(6));
        ride.setFinishTime(LocalDateTime.now().plusMinutes(11));
        ride.setRideStatus(RideStatus.PENDING);
        Driver driver = RideBookingConstants.createDriver();
        User user = RideBookingConstants.createUser();
        ride.setPassenger(user);
        ride.setDriver(driver);
        driverRepository.save(driver);
        userRepository.save(user);
        rideRepository.save(ride);
        String url = String.format("/api/ride/%d/accept", ride.getId());

        ResultActions resultActions = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rideAcceptanceDto)));

        resultActions.andExpect(status().isOk());

        String passengerMessage = userNotificationRepository
                .findByUserAndActivationTimeBeforeOrderByActivationTimeDesc(user, LocalDateTime.now().plusSeconds(2)).get(0).getDescription();
        assertEquals("Vaša vožnja je upravo zakazana. Vozilo stiže za 5 minuta. Status vožnje možete pratiti na Vašem dešbordu.", passengerMessage);

        String driverMessage = userNotificationRepository
                .findByUserAndActivationTimeBeforeOrderByActivationTimeDesc(driver, LocalDateTime.now().plusSeconds(2)).get(0).getDescription();
        assertEquals("Zakazana Vam je nova vožnja. Putnik Vas očekuje za 5 minuta. Detalje vožnje možete proveriti na Vašem dešbordu.", driverMessage);
    }



    @WithUserDetails(value = "testvozac1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void rejectDriveByDriverSuccessfulTest() throws Exception{

        RideRejectionRequestDto rideRejectionRequestDto = new RideRejectionRequestDto();
        rideRejectionRequestDto.setReason("Ne voza mi se danas");
        Ride ride = new Ride();
        ride.setRideStatus(RideStatus.PENDING);
        Driver driver = RideBookingConstants.createDriver();
        User user = RideBookingConstants.createUser();
        ride.setPassenger(user);
        ride.setDriver(driver);
        driverRepository.save(driver);
        userRepository.save(user);
        rideRepository.save(ride);

        String url = String.format("/api/ride/%d/reject", ride.getId());

        ResultActions resultActions = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rideRejectionRequestDto)));

        resultActions.andExpect(status().isOk());



        assertEquals(RideStatus.REJECTED, rideRepository.findOneById(ride.getId()).getRideStatus());

    }



    @WithMockUser(username = "nekivozac", roles={"DRIVER"})
    @Test
    public void startDriveSuccessfulTest() throws Exception{

        Ride ride = new Ride();
        ride.setRideStatus(RideStatus.ACCEPTED);
        Driver driver = RideBookingConstants.createDriver();
        User user = RideBookingConstants.createUser();
        ride.setPassenger(user);
        ride.setDriver(driver);
        driverRepository.save(driver);
        userRepository.save(user);
        rideRepository.save(ride);
        String url = String.format("/api/ride/%d/start", ride.getId());


        mockMvc.perform(put(url))
                .andExpect(status().isOk());


        assertEquals(RideStatus.ACTIVE, rideRepository.findOneById(ride.getId()).getRideStatus());
        assertTrue(driverRepository.findById(driver.getId()).get().isOccupied());
    }


    @WithMockUser(username = "nekiuser", roles={"USER"})
    @Test
    public void startDriveUnsuccessfulNotDriverTest() throws Exception {
        //create a ride
        Ride ride = new Ride();
        ride.setRideStatus(RideStatus.ACCEPTED);
        Driver driver = RideBookingConstants.createDriver();
        User user = RideBookingConstants.createUser();
        ride.setPassenger(user);
        ride.setDriver(driver);
        driverRepository.save(driver);
        userRepository.save(user);
        rideRepository.save(ride);
        String url = String.format("/api/ride/%d/start", ride.getId());


        mockMvc.perform(put(url))
                .andExpect(status().is4xxClientError());
    }

    @WithMockUser(username = "nekivozac", roles={"DRIVER"})
    @Test
    public void finishDriveSuccessfulTest() throws Exception{

        Ride ride = new Ride();
        ride.setRideStatus(RideStatus.ACTIVE);
        Driver driver = RideBookingConstants.createDriver();
        User user = RideBookingConstants.createUser();
        ride.setPassenger(user);
        ride.setDriver(driver);
        driverRepository.save(driver);
        userRepository.save(user);
        rideRepository.save(ride);
        String url = String.format("/api/ride/%d/finish", ride.getId());


        mockMvc.perform(put(url))
                .andExpect(status().isOk());


        assertEquals(RideStatus.FINISHED, rideRepository.findOneById(ride.getId()).getRideStatus());
        assertFalse(driverRepository.findById(driver.getId()).get().isOccupied());
    }


    @WithMockUser(username = "nekiuser", roles={"USER"})
    @Test
    public void finishDriveUnsuccessfulNotDriverTest() throws Exception {

        Ride ride = new Ride();
        ride.setRideStatus(RideStatus.ACTIVE);
        Driver driver = RideBookingConstants.createDriver();
        User user = RideBookingConstants.createUser();
        ride.setPassenger(user);
        ride.setDriver(driver);
        driverRepository.save(driver);
        userRepository.save(user);
        rideRepository.save(ride);
        String url = String.format("/api/ride/%d/finish", ride.getId());


        mockMvc.perform(put(url))
                .andExpect(status().is4xxClientError());
    }



//    @WithUserDetails(value = "testputnik1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    @Test
//    public void bookAQuickRideSuccessfulTest() throws Exception{
//        Location startLocation = new Location();
//        startLocation.setLongitude(45.24321f);
//        startLocation.setLatitude(19.831858f);
//        Location finishLocation = new Location();
//        finishLocation.setLongitude(45.240356f);
//        finishLocation.setLatitude(19.819715f);
//        Double[][] coordinates = new Double[2][2];
//        coordinates[0][0] = (double) startLocation.getLongitude();
//        coordinates[0][1] = (double) startLocation.getLatitude();
//        coordinates[1][0] = (double) finishLocation.getLongitude();
//        coordinates[1][1] = (double) finishLocation.getLatitude();
//
////        GeoJSONRouteResponse geo = routeService.searchRoute(coordinates);
////        GeoJSONIndividualRouteResponse individualGeo = geo.getRoutes().get(0);
//        GeoJSONIndividualRouteResponse individualGeo = new GeoJSONIndividualRouteResponse();
//
//        Route route = new Route();
//        route.setStartLocation(startLocation);
//        route.setFinishLocation(finishLocation);
//        route.setLength(1.326f);
//        route.setEstimatedDuration(Duration.ofNanos(176000000000l));
//        route.setGeo(individualGeo);
//        routeRepository.save(route);
//
//        RideBookingRequestDto rideBookingRequestDto = new RideBookingRequestDto();
//        rideBookingRequestDto.setPetTransportFlag(false);
//        rideBookingRequestDto.setBabyTransportFlag(false);
//        rideBookingRequestDto.setVehicleTypeId(3);
//        rideBookingRequestDto.setNumberOfPassengers(1);
//        rideBookingRequestDto.setScheduled(false);
//        rideBookingRequestDto.setScheduledStartTime(OffsetDateTime.now().plusHours(1));
//        rideBookingRequestDto.setRouteId(route.getId());
//
//        String url = "/api/ride/booking";
//
//        try {
//            ResultActions resultActions = mockMvc.perform(post(url)
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(objectMapper.writeValueAsString(rideBookingRequestDto)));
//
//            resultActions.andExpect(status().isCreated()).andDo(MockMvcResultHandlers.print());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        //resultActions.andExpect(status().isOk());
//
//    }





}
