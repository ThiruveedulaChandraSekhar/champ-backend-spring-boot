package com.capstone.champ.controller;

import com.capstone.champ.payload.AllergyResponse;
import com.capstone.champ.payload.VisitResponse;
import com.capstone.champ.payload.userdetails.UserDetailsDTO;
import com.capstone.champ.payload.userdetails.UserDetailsRequest;
import com.capstone.champ.payload.userdetails.UserDetailsResponse;
import com.capstone.champ.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/details/{input}")
    public ResponseEntity<UserDetailsResponse> addUserDetails(@PathVariable String input, @RequestBody UserDetailsRequest userDetailsRequest) {
        return new ResponseEntity<>(userService.addUserDetails(input, userDetailsRequest), HttpStatus.CREATED);
    }

    @PutMapping("/details")
    public ResponseEntity<UserDetailsResponse> updateUserDetails(@RequestBody UserDetailsDTO userDetailsDTO) {
        return new ResponseEntity<>(userService.updateUserDetails(userDetailsDTO), HttpStatus.OK);
    }

    @GetMapping("/details/{input}")
    public ResponseEntity<UserDetailsDTO> getUserDetails(@PathVariable String input) {
        return new ResponseEntity<>(userService.getUserDetails(input), HttpStatus.OK);
    }

    @GetMapping("/visits/{input}")
    public ResponseEntity<VisitResponse> getVisits(@PathVariable String input) {
        return new ResponseEntity<>(userService.getVisits(input), HttpStatus.OK);
    }

    @GetMapping("/allergy/{input}")
    public ResponseEntity<AllergyResponse> getAllergies(@PathVariable String input) {
        return new ResponseEntity<>(userService.getAllergy(input), HttpStatus.OK);
    }


}
