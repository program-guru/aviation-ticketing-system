package com.flight.booking.airport.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class AirportRequest{
    String airportName;
    String city;
    int id;
    String country;
}
