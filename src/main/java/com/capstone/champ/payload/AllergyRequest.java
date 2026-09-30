package com.capstone.champ.payload;

import com.capstone.champ.model.AllergyType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllergyRequest {
    private String title;
    private String description;
    private LocalDate date;
    private Short severity;
    private AllergyType allergyType;
}
