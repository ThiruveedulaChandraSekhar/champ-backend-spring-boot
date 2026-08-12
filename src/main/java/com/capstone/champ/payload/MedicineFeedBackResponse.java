package com.capstone.champ.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicineFeedBackResponse {
    private String message;
    private Boolean status;
    private List<MedicineWithFeedback> feedbacks;
}
