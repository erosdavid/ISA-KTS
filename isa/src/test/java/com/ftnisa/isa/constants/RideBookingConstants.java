package com.ftnisa.isa.constants;

import com.ftnisa.isa.dto.ride.RideBookingRequestDto;
import com.ftnisa.isa.integrations.ors.responses.routing.geojson.GeoJSONIndividualRouteResponse;
import com.ftnisa.isa.model.location.Location;
import com.ftnisa.isa.model.ride.Ride;
import com.ftnisa.isa.model.ride.RideStatus;
import com.ftnisa.isa.model.ride.RouteOptimizationCriteria;
import com.ftnisa.isa.model.route.Route;
import com.ftnisa.isa.model.user.Driver;
import com.ftnisa.isa.model.user.User;
import com.ftnisa.isa.repository.RouteRepository;

import java.time.Duration;
import java.time.OffsetDateTime;

public class RideBookingConstants {

    private RideBookingConstants(){}



    public static final Boolean rideBookingRequestDto_petTransportFlag = false;
    public static final Boolean rideBookingRequestDto_babyTransportFlag = false;
    public static final int rideBookingRequestDto_vehicleTypeId = 1;
    public static final int rideBookingRequestDto_numberOfPassengers = 1;
    public static final boolean rideBookingRequestDto_isScheduled = false;

    public static final OffsetDateTime rideBookingRequestDto_scheduledStartTime = null;
    public static final RouteOptimizationCriteria rideBookingRequestDto_routeOptimizationCriteria = RouteOptimizationCriteria.BY_TIME;
    public static final int rideBookingRequestDto_routeId = 1;


    public static RideBookingRequestDto createRideBookingRequestDto(){
        RideBookingRequestDto dto = new RideBookingRequestDto(
                rideBookingRequestDto_petTransportFlag,
                rideBookingRequestDto_babyTransportFlag,
                rideBookingRequestDto_vehicleTypeId,
                rideBookingRequestDto_numberOfPassengers,
                rideBookingRequestDto_isScheduled,
                rideBookingRequestDto_scheduledStartTime,
                rideBookingRequestDto_routeOptimizationCriteria,
                rideBookingRequestDto_routeId
        );
        return dto;
    }


    public static Driver createDriver(){
        Driver driver = new Driver();
        driver.setDriverLicense("123");
        driver.setOccupied(false);
        driver.setActive(true);
        driver.setEmail("driver@me.com");
        driver.setFirstname("vozac");
        driver.setLastname("testni");
        driver.setUsername("nekiVozac");

        return driver;
    }

    public static User createUser(){
        User user = new User();

        user.setEmail("user@me.com");
        user.setFirstname("putnik");
        user.setLastname("testni");
        user.setUsername("testuser");

        return user;
    }

    public static Ride createRide(){
        Ride ride = new Ride();
        return ride;
    }

    public static Route createRoute(){


        Route route = new Route();
//        GeoJSONIndividualRouteResponse geo = new GeoJSONIndividualRouteResponse();
//
//        route.setStartLocation(startLocation);
//        route.setFinishLocation(finishLocation);
//        route.setLength(1.326f);
//        route.setEstimatedDuration(Duration.ofNanos(176000000000l));
//        route.setGeo(geo);

        return route;
    }


}
