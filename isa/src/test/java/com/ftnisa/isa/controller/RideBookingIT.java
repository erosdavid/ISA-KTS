package com.ftnisa.isa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ftnisa.isa.dto.ride.RideBookingRequestDto;
import com.ftnisa.isa.dto.ride.RideDto;
import com.ftnisa.isa.integrations.ors.responses.routing.geojson.GeoJSONIndividualRouteResponse;
import com.ftnisa.isa.model.location.Location;
import com.ftnisa.isa.model.ride.RouteOptimizationCriteria;
import com.ftnisa.isa.model.route.Route;
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

    private RideDto extractResponseEntity(ResultActions resultActions ) throws Exception {
        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(contentAsString, RideDto.class);
    }




    @WithUserDetails(value = "dave", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void bookAQuickRideSuccessfulTest() throws Exception{

        RideBookingRequestDto rideBookingRequestDto = new RideBookingRequestDto();
        rideBookingRequestDto.setPetTransportFlag(false);
        rideBookingRequestDto.setBabyTransportFlag(false);
        rideBookingRequestDto.setVehicleTypeId(3);
        rideBookingRequestDto.setNumberOfPassengers(1);
        rideBookingRequestDto.setScheduled(false);
        rideBookingRequestDto.setScheduledStartTime(OffsetDateTime.now().plusHours(1));
        rideBookingRequestDto.setRouteOptimizationCriteria(RouteOptimizationCriteria.BY_LENGTH);
        rideBookingRequestDto.setRouteId(1);

        String url = "/api/ride/booking";
        String payload = "{\"vehicleTypeId\":1,\"numberOfPassengers\":1,\"routeId\":1,\"routeOptimizationCriteria\":\"BY_TIME\"}";


        objectMapper.findAndRegisterModules();

        ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payload))
                .andExpect(status().isCreated());

        RideDto rideDto = extractResponseEntity(resultActions);



    }


}
