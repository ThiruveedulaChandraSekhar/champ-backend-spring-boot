package com.capstone.champ.payload.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsernamesDTO {
    private Boolean status;
    private String message;
    private List<AadhaarDetailsDTO> aadhaarNumbers;
}
