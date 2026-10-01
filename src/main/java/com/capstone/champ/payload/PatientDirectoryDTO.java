package com.capstone.champ.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class PatientDirectoryDTO {
    private Long id;
    private String accountIdentifier;
    private String fullName;
    private String mobileNumber;
}
