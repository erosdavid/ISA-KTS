package com.ftnisa.isa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ftnisa.isa.dto.ride.RideBookingRequestDto;
import com.ftnisa.isa.dto.ride.RideDto;
import com.ftnisa.isa.integrations.ors.responses.routing.geojson.GeoJSONIndividualRouteResponse;
import com.ftnisa.isa.model.location.Location;
import com.ftnisa.isa.model.ride.Rejection;
import com.ftnisa.isa.model.ride.Ride;
import com.ftnisa.isa.model.ride.RideStatus;
import com.ftnisa.isa.model.ride.RouteOptimizationCriteria;
import com.ftnisa.isa.model.route.Route;
import com.ftnisa.isa.model.user.User;
import com.ftnisa.isa.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.testcontainers.shaded.com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.time.Duration;
import java.time.OffsetDateTime;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Sql("/integration-test-import.sql")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RideBookingIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    RideRepository rideRepository;

    private RideDto extractResponseEntity(ResultActions resultActions ) throws Exception {
        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(contentAsString, RideDto.class);
    }




    //Testing: booking a quick ride, booking should succeed
    @WithUserDetails(value = "dave", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void bookAQuickRideSuccessfulTest() throws Exception{

        //When
        RideBookingRequestDto rideBookingRequestDto = new RideBookingRequestDto();
        rideBookingRequestDto.setPetTransportFlag(false);
        rideBookingRequestDto.setBabyTransportFlag(false);
        rideBookingRequestDto.setVehicleTypeId(3);
        rideBookingRequestDto.setNumberOfPassengers(1);
        rideBookingRequestDto.setScheduled(false);
        rideBookingRequestDto.setScheduledStartTime(OffsetDateTime.now().plusHours(1));
        rideBookingRequestDto.setRouteOptimizationCriteria(RouteOptimizationCriteria.BY_LENGTH);
        rideBookingRequestDto.setRouteId(1);

        String routeOptimizationCriteria = rideBookingRequestDto.getRouteOptimizationCriteria().toString();
        String url = "/api/ride/booking";
        String payload = String.format("{\"vehicleTypeId\":%d,\"numberOfPassengers\":%d,\"routeId\":%d,\"routeOptimizationCriteria\":\"%s\"}",
                rideBookingRequestDto.getVehicleTypeId(),
                rideBookingRequestDto.getNumberOfPassengers(),
                rideBookingRequestDto.getRouteId(),
                routeOptimizationCriteria
                );

        //Then
        ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payload))
                .andExpect(status().isCreated());

        //Assert
        RideDto rideDto = extractResponseEntity(resultActions);
        assertEquals(RideStatus.PENDING, rideDto.getRideStatus());

    }


    //Quick ride booking should be unsuccessful, as there are no pet friendly drivers in the system with STANDARD vehicle
    @WithUserDetails(value = "dave", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void bookAQuickRideUnsuccessfulNoAppropriateDriverTest() throws Exception{

        //When
        RideBookingRequestDto rideBookingRequestDto = new RideBookingRequestDto();
        rideBookingRequestDto.setPetTransportFlag(true);
        rideBookingRequestDto.setBabyTransportFlag(true);
        rideBookingRequestDto.setVehicleTypeId(1);
        rideBookingRequestDto.setNumberOfPassengers(1);
        rideBookingRequestDto.setScheduled(false);
        rideBookingRequestDto.setScheduledStartTime(OffsetDateTime.now().plusHours(1));
        rideBookingRequestDto.setRouteOptimizationCriteria(RouteOptimizationCriteria.BY_LENGTH);
        rideBookingRequestDto.setRouteId(1);

        //Then
        String url = "/api/ride/booking";
        objectMapper.findAndRegisterModules();
        ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(rideBookingRequestDto)))
                .andExpect(status().isCreated());

        //Assert
        RideDto rideDto = extractResponseEntity(resultActions);

        assertEquals(RideStatus.REJECTED, rideDto.getRideStatus());
        Ride ride = rideRepository.findOneById(rideDto.getId());
        String rejectionReason = ride.getRejection().getRejectionReason();
        assertEquals("Nažalost, trenutno nemamo dostupnih vozila sa zadatim kriterijumima.", rejectionReason);

    }


    //Quick ride booking should be unsuccessful, as there are too many passengers
    @WithUserDetails(value = "dave", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void bookAQuickRideUnsuccessfulTooManyPassengerTest() throws Exception{

        //When
        RideBookingRequestDto rideBookingRequestDto = new RideBookingRequestDto();
        rideBookingRequestDto.setPetTransportFlag(true);
        rideBookingRequestDto.setBabyTransportFlag(true);
        rideBookingRequestDto.setVehicleTypeId(1);
        rideBookingRequestDto.setNumberOfPassengers(6);
        rideBookingRequestDto.setScheduled(false);
        rideBookingRequestDto.setScheduledStartTime(OffsetDateTime.now().plusHours(1));
        rideBookingRequestDto.setRouteOptimizationCriteria(RouteOptimizationCriteria.BY_LENGTH);
        rideBookingRequestDto.setRouteId(1);

        String url = "/api/ride/booking";
        //Then
        objectMapper.findAndRegisterModules();
        ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(rideBookingRequestDto)))
                .andExpect(status().isCreated());

        //Assert
        RideDto rideDto = extractResponseEntity(resultActions);
        assertEquals(RideStatus.REJECTED, rideDto.getRideStatus());
        Ride ride = rideRepository.findOneById(rideDto.getId());
        String rejectionReason = ride.getRejection().getRejectionReason();
        assertEquals("Nažalost, trenutno nemamo dostupnih vozila sa zadatim kriterijumima.", rejectionReason);

    }



    //Scheduled ride booking should be successful
    @WithUserDetails(value = "dave", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void bookScheduledRideSuccessfulTest() throws Exception{

        //When
        RideBookingRequestDto rideBookingRequestDto = new RideBookingRequestDto();
        rideBookingRequestDto.setPetTransportFlag(false);
        rideBookingRequestDto.setBabyTransportFlag(false);
        rideBookingRequestDto.setVehicleTypeId(1);
        rideBookingRequestDto.setNumberOfPassengers(1);
        rideBookingRequestDto.setScheduled(true);
        rideBookingRequestDto.setScheduledStartTime(OffsetDateTime.now().plusHours(1));
        rideBookingRequestDto.setRouteOptimizationCriteria(RouteOptimizationCriteria.BY_LENGTH);
        rideBookingRequestDto.setRouteId(1);

        //Then
        String url = "/api/ride/booking";
        objectMapper.findAndRegisterModules();
        ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(rideBookingRequestDto)))
                .andExpect(status().isCreated());

        //Assert
        RideDto rideDto = extractResponseEntity(resultActions);
        assertEquals(RideStatus.PENDING, rideDto.getRideStatus());

    }


    //Scheduled ride booking should be unsuccessful, as the scheduled start time is more than 5 hours later
    // 5 hours in advance is the maximum
    @WithUserDetails(value = "dave", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void bookScheduledRideUnsuccessfulTooEarlyTest() throws Exception{

        //When
        RideBookingRequestDto rideBookingRequestDto = new RideBookingRequestDto();
        rideBookingRequestDto.setPetTransportFlag(false);
        rideBookingRequestDto.setBabyTransportFlag(false);
        rideBookingRequestDto.setVehicleTypeId(1);
        rideBookingRequestDto.setNumberOfPassengers(1);
        rideBookingRequestDto.setScheduled(true);
        rideBookingRequestDto.setScheduledStartTime(OffsetDateTime.now().plusHours(6));
        rideBookingRequestDto.setRouteOptimizationCriteria(RouteOptimizationCriteria.BY_LENGTH);
        rideBookingRequestDto.setRouteId(1);

        //Then
        String url = "/api/ride/booking";
        objectMapper.findAndRegisterModules();
        ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(rideBookingRequestDto)))
                .andExpect(status().isCreated());

        //Assert
        RideDto rideDto = extractResponseEntity(resultActions);
        assertEquals(RideStatus.REJECTED, rideDto.getRideStatus());

        Ride ride = rideRepository.findOneById(rideDto.getId());
        String rejectionReason = ride.getRejection().getRejectionReason();
        assertEquals("Izvinite, ne možete zakazati vožnju više od 5 sati unapred.", rejectionReason);

    }


}
