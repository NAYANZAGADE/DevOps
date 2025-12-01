package com.glidingpath.core.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessAddress {
    private String street;
    private String apt;
    private String city;
    private String state;
    private String postalCode;
    private String phoneNumber;
    private boolean mailingDifferent;
}