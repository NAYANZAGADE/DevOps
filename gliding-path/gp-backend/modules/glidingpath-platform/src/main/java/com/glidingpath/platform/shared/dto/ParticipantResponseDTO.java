package com.glidingpath.platform.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantResponseDTO {
    private String accountNumber;    
    private String fullName;         
    private String status;           
    private String portfolioType;    
    private String balance;          
    private String vestedStatus;     
}