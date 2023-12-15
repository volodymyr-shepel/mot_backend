package com.mot.controller;

import com.mot.dtos.EmailAddressDTO;
import com.mot.dtos.UpdatePasswordDTO;
import com.mot.service.account.AccountService;
import com.mot.service.email.VerificationEmailService;
import com.mot.service.verificationToken.VerificationTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/auth/account")
public class AccountController {
    private final AccountService accountService;


    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping(path = "/forgetPassword")
    public ResponseEntity<String> forgetPassword(@RequestBody EmailAddressDTO emailAddressDTO){
        return accountService.forgetPassword(emailAddressDTO);
    }
    @GetMapping(path = "/activate")
    public ResponseEntity<String> activateAccount(@RequestParam("token") String token) {
        return accountService.activateAccount(token);
    }
    @GetMapping(path = "/reset-password")
    public String resetPassword(@RequestParam("token") String token){
        // TODO: verify token and redirect the user to the forgetPassword form
        return "Token verified and user redirected to the page";
    }
    @PutMapping(path = "/reset-password/update")
    public ResponseEntity<String> updatePassword(@RequestBody UpdatePasswordDTO updatePasswordDTO){
        return accountService.updatePassword(updatePasswordDTO);
    }
}
