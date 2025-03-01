package com.flight.booking.flight.mapper;

import com.flight.booking.flight.dto.FlightRequest;
import com.flight.booking.flight.dto.FlightResponse;
import com.flight.booking.flight.model.Flight;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FlightMapper {
    private final SeatMapper seatMapper;

    public FlightResponse mapToDTO(Flight flight) {
        return FlightResponse.builder()
                .flightId(flight.getFlightId())
                .flightNumber(flight.getFlightNumber())
                .company(flight.getCompany())
                .totalSeats(flight.getTotalSeats())
                .seats(flight.getSeats() != null
                        ? flight.getSeats().stream().map(seatMapper::mapToDTO).collect(Collectors.toList())
                        : Collections.emptyList())
                .build();
    }

    public Flight mapToModel(FlightRequest flightRequest) {
        return Flight.builder()
                .flightId(flightRequest.getFlightId())
                .flightNumber(flightRequest.getFlightNumber())
                .company(flightRequest.getCompany())
                .totalSeats(flightRequest.getTotalSeats())
                .build();
    }
}
