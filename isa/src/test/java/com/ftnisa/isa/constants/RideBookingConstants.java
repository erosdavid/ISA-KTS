package com.ftnisa.isa.constants;

import com.ftnisa.isa.dto.ride.RideBookingRequestDto;
import com.ftnisa.isa.model.ride.RouteOptimizationCriteria;

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


    public static RideBookingRequestDto returnRideBookingRequestDto(){
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


}
