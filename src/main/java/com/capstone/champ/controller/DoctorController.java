package com.capstone.champ.controller;

import com.capstone.champ.payload.VisitRequest;
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

    @PostMapping("/details/{input}")
    public ResponseEntity<DoctorDetailsResponse> addDoctorDetails(@RequestBody DoctorDetailsRequest doctorDetailsRequest, @PathVariable String input) {
        return new ResponseEntity<>(doctorService.addDoctorDetails(doctorDetailsRequest, input), HttpStatus.CREATED);
    }

    @PutMapping("/details")
    public ResponseEntity<DoctorDetailsResponse> updateDoctorDetails(@RequestBody DoctorDetailsDTO doctorDetailsDTO) {
        return new ResponseEntity<>(doctorService.updateDoctorDetails(doctorDetailsDTO), HttpStatus.OK);
    }

    @GetMapping("/details/{input}")
    public ResponseEntity<DoctorDetailsDTO> getDoctorDetails(@PathVariable String input) {
        return new ResponseEntity<>(doctorService.getDoctorDetails(input), HttpStatus.OK);
    }

    @PostMapping("/visit/{doctor}/{patient}")
    public ResponseEntity<?> addVisit(@PathVariable String doctor, @PathVariable String patient, @RequestBody VisitRequest visitRequest) {
        return new ResponseEntity<>(doctorService.addVisit(doctor, patient, visitRequest), HttpStatus.CREATED);
    }



}
