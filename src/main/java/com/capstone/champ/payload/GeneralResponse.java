package com.capstone.champ.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GeneralResponse {
    private Boolean status;
    private String message;
}
