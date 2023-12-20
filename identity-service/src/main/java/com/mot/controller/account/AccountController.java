package com.mot.controller.account;

import com.mot.dtos.EmailAddressDTO;
import com.mot.dtos.UpdatePasswordDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface AccountController {

    @PostMapping(path = "/forgetPassword")
    ResponseEntity<String> forgetPassword(@RequestBody EmailAddressDTO emailAddressDTO);

    @GetMapping(path = "/activate")
    ResponseEntity<String> activateAccount(@RequestParam("token") String token);

    @GetMapping(path = "/reset-password")
    ResponseEntity<String> resetPassword(@RequestParam("token") String token);

    @PutMapping(path = "/reset-password/update")
    ResponseEntity<String> updatePassword(@RequestBody UpdatePasswordDTO updatePasswordDTO);

    @PostMapping(path = "/resend-verification-email")
    ResponseEntity<String> resendVerificationEmail(@RequestBody EmailAddressDTO emailAddressDTO);
}
