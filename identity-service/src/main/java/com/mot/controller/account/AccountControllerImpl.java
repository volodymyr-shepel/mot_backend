package com.mot.controller.account;

import com.mot.dtos.EmailAddressDTO;
import com.mot.dtos.UpdatePasswordDTO;
import com.mot.service.account.AccountServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/auth/account/v1")
public class AccountControllerImpl implements AccountController{
    private final AccountServiceImpl accountServiceImpl;

    public AccountControllerImpl(AccountServiceImpl accountServiceImpl) {
        this.accountServiceImpl = accountServiceImpl;
    }
    @Override
    public ResponseEntity<String> forgetPassword(@RequestBody EmailAddressDTO emailAddressDTO){
        return accountServiceImpl.forgetPassword(emailAddressDTO);
    }
    @Override
    public ResponseEntity<String> activateAccount(@RequestParam("token") String token) {
        return accountServiceImpl.activateAccount(token);
    }
    @Override
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token){
        return accountServiceImpl.resetPassword(token);
    }
    @Override
    public ResponseEntity<String> updatePassword(@RequestBody UpdatePasswordDTO updatePasswordDTO){
        return accountServiceImpl.updatePassword(updatePasswordDTO);
    }

    @Override
    public ResponseEntity<String> resendVerificationEmail(@RequestBody EmailAddressDTO emailAddressDTO) {
        return accountServiceImpl.resendVerificationEmail(emailAddressDTO);
    }
}
