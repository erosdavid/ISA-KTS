package com.ftnisa.isa.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ftnisa.isa.constants.RideBookingConstants;
import com.ftnisa.isa.controller.rest.RideController;
import com.ftnisa.isa.dto.ride.RideAcceptanceDto;
import com.ftnisa.isa.dto.ride.RideBookingRequestDto;
import com.ftnisa.isa.dto.ride.RideRejectionRequestDto;
import com.ftnisa.isa.dto.route.FindRouteDto;
import com.ftnisa.isa.integrations.ors.responses.routing.geojson.GeoJSONIndividualRouteResponse;
import com.ftnisa.isa.integrations.ors.responses.routing.geojson.GeoJSONRouteResponse;
import com.ftnisa.isa.model.location.Location;
import com.ftnisa.isa.model.ride.Ride;
import com.ftnisa.isa.model.ride.RideStatus;
import com.ftnisa.isa.model.route.Route;
import com.ftnisa.isa.model.user.Driver;
import com.ftnisa.isa.model.user.Role;
import com.ftnisa.isa.model.user.User;
import com.ftnisa.isa.model.user.UserType;
import com.ftnisa.isa.repository.*;
import com.ftnisa.isa.service.RouteService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.ftnisa.isa.model.user.Role.DRIVER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RideControllerIT {
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

    @WithUserDetails(value = "testputnik1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void bookAQuickRideSuccessfulTest() throws Exception{
        Location startLocation = new Location();
        startLocation.setLongitude(45.24321f);
        startLocation.setLatitude(19.831858f);
        Location finishLocation = new Location();
        finishLocation.setLongitude(45.240356f);
        finishLocation.setLatitude(19.819715f);
//        Double[][] coordinates = new Double[2][2];
//        coordinates[0][0] = 45.24321;
//        coordinates[0][1] = 19.831858;
//        coordinates[1][0] = 45.240356;
//        coordinates[1][1] = 19.819715;
//
//        GeoJSONRouteResponse geo = routeService.searchRoute(coordinates);
        GeoJSONIndividualRouteResponse geo = new GeoJSONIndividualRouteResponse();
        Route route = new Route();
        routeRepository.save(route);
        route.setStartLocation(startLocation);
        route.setFinishLocation(finishLocation);
        route.setLength(1.326f);
        route.setEstimatedDuration(Duration.ofNanos(176000000000l));
        //route.setGeo(geo);


        RideBookingRequestDto rideBookingRequestDto = new RideBookingRequestDto();
        rideBookingRequestDto.setPetTransportFlag(false);
        rideBookingRequestDto.setBabyTransportFlag(false);
        rideBookingRequestDto.setVehicleTypeId(1);
        rideBookingRequestDto.setNumberOfPassengers(1);
        rideBookingRequestDto.setScheduled(false);

        String url = "/api/ride/booking";

        ResultActions resultActions = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rideBookingRequestDto)));

        resultActions.andExpect(status().isOk());






    }





//    @WithMockUser(username = "nekiputnik", roles={"USER"})
//    @Test
//    public void rejectDriveByUserSuccessfulTest() throws Exception{
//
//        RideAcceptanceDto rideAcceptanceDto =new RideAcceptanceDto();
//        rideAcceptanceDto.setAccepted(false);
//        Ride ride = new Ride();
//        ride.setStartTime(LocalDateTime.now().plusMinutes(6));
//        ride.setFinishTime(LocalDateTime.now().plusMinutes(11));
//        ride.setRideStatus(RideStatus.PENDING);
//        Driver driver = RideBookingConstants.createDriver();
//        User user = RideBookingConstants.createUser();
//        ride.setPassenger(user);
//        ride.setDriver(driver);
//        driverRepository.save(driver);
//        userRepository.save(user);
//        rideRepository.save(ride);
//        String url = String.format("/api/ride/%d/accept", ride.getId());
//
//        ResultActions resultActions = mockMvc.perform(put(url)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(rideAcceptanceDto)));
//
//        resultActions.andExpect(status().isOk());

//        assertEquals("Passenger did not accept the ride", rideRepository.findOneById(ride.getId()).getRejection().getRejectionReason());
//    }

//    @WithMockUser(username = "nekiputnik", roles={"USER"})
//    @Test
//    public void acceptDriveByUserSuccessfulTest() throws Exception{
//
//        RideAcceptanceDto rideAcceptanceDto =new RideAcceptanceDto();
//        rideAcceptanceDto.setAccepted(true);
//        Ride ride = new Ride();
//        ride.setStartTime(LocalDateTime.now().plusMinutes(6));
//        ride.setFinishTime(LocalDateTime.now().plusMinutes(11));
//        ride.setRideStatus(RideStatus.PENDING);
//        Driver driver = RideBookingConstants.createDriver();
//        User user = RideBookingConstants.createUser();
//        ride.setPassenger(user);
//        ride.setDriver(driver);
//        driverRepository.save(driver);
//        userRepository.save(user);
//        rideRepository.save(ride);
//        String url = String.format("/api/ride/%d/accept", ride.getId());
//
//        ResultActions resultActions = mockMvc.perform(put(url)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(rideAcceptanceDto)));
//
//        resultActions.andExpect(status().isOk());

//        assertEquals("Passenger did not accept the ride", rideRepository.findOneById(ride.getId()).getRejection().getRejectionReason());
//    }






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





}
