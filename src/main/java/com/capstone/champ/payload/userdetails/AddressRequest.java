package com.capstone.champ.payload.userdetails;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequest {
    private String pinCode;
    private String doorNumber;
    private String street;
    private String city;
    private String state;
}