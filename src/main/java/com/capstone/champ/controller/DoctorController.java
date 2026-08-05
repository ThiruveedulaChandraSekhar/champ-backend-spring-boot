package com.capstone.champ.controller;

import com.capstone.champ.payload.doctordetails.DoctorDetailsDTO;
import com.capstone.champ.payload.doctordetails.DoctorDetailsRequest;
import com.capstone.champ.payload.doctordetails.DoctorDetailsResponse;
import com.capstone.champ.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/doctor")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping("/add-details/{input}")
    public ResponseEntity<DoctorDetailsResponse> addDoctorDetails(@RequestBody DoctorDetailsRequest doctorDetailsRequest, @PathVariable String input) {
        return new ResponseEntity<>(doctorService.addDoctorDetails(doctorDetailsRequest, input), HttpStatus.CREATED);
    }

    @PutMapping("/update-details")
    public ResponseEntity<DoctorDetailsResponse> updateDoctorDetails(@RequestBody DoctorDetailsDTO doctorDetailsDTO) {
        return new ResponseEntity<>(doctorService.updateDoctorDetails(doctorDetailsDTO), HttpStatus.OK);
    }

    @GetMapping("/get-details/{input}")
    public ResponseEntity<DoctorDetailsDTO> getDoctorDetails(@PathVariable String input) {
        return new ResponseEntity<>(doctorService.getDoctorDetails(input), HttpStatus.OK);
    }

}
