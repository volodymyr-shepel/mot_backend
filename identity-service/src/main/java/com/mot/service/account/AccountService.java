package com.mot.service.account;

import com.mot.dtos.EmailAddressDTO;
import com.mot.dtos.UpdatePasswordDTO;
import org.springframework.http.ResponseEntity;

public interface AccountService {

    ResponseEntity<String> forgetPassword(EmailAddressDTO emailAddressDTO);

    ResponseEntity<String> updatePassword(UpdatePasswordDTO updatePasswordDTO);

    ResponseEntity<String> activateAccount(String token);

    ResponseEntity<String> resendVerificationEmail(EmailAddressDTO emailAddressDTO);


}
