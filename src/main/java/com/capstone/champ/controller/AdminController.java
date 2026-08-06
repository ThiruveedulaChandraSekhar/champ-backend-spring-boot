package com.capstone.champ.controller;

import com.capstone.champ.model.Role;
import com.capstone.champ.payload.GeneralResponse;
import com.capstone.champ.payload.authentication.DeleteResponse;
import com.capstone.champ.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AuthenticationService authenticationService;

    @PostMapping("/verify-doctor/{input}")
    public ResponseEntity<GeneralResponse> verifyDoctor(@PathVariable String input) {
        return new ResponseEntity<>(authenticationService.verifyUser(input), HttpStatus.OK);
    }

    @PostMapping("/verify-user/{input}")
    public ResponseEntity<GeneralResponse> verifyUser(@PathVariable String input) {
        return new ResponseEntity<>(authenticationService.verifyUser(input), HttpStatus.OK);
    }

    @PostMapping("not-valid-user/{input}")
    public ResponseEntity<DeleteResponse> notVerifyUser(@PathVariable String input) {
        return new ResponseEntity<>(authenticationService.deleteUser(input), HttpStatus.OK);
    }

    @GetMapping("get-verification-pending/{input}")
    public ResponseEntity<?> getListOfUsers(@PathVariable String input) {
        if(input.equals(Role.DOCTOR.toString()))
            return new ResponseEntity<>(authenticationService.getPendingDoctors(), HttpStatus.OK);
        return new ResponseEntity<>(authenticationService.getPendingUsers(), HttpStatus.OK);
    }
}
