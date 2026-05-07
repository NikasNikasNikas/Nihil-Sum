package com.nihil.nihilsum.dtos;

import com.nihil.nihilsum.models.Venue;
import lombok.Data;

@Data
public class VenueDTO {
    private Long id;
    private String country;
    private String city;
    private String address;
    private Long ownerId;
    private Integer capacity;

    public VenueDTO(Venue venue){
        this.id = venue.getId();
        this.country = venue.getCountry();
        this.city = venue.getCity();
        this.address = venue.getAddress();
        this.ownerId = venue.getOwnerId();
        this.capacity = venue.getCapacity();
    }
}
