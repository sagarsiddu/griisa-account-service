package com.example.griisa_account_service.util;

import com.example.griisa_account_service.dto.UserRequestDTO;
import jakarta.validation.*;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class UserRequestValidator {
    private final Validator validator;

    public UserRequestValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    public void validate(UserRequestDTO dto) {
        Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}

//package com.example.griisa_account_service.util;
//
//import com.example.griisa_account_service.dto.UserRequestDTO;
//
//public class UserRequestValidator {
//
//    public static boolean isValid(UserRequestDTO dto) {
//        return isValidEmail(dto.getEmail())
//                && isValidPhone(dto.getPhoneNumber())
//                && isValidAadhaar(dto.getAadhaarNumber())
//                && isValidPan(dto.getPanNumber());
//    }
//
//    private static boolean isValidEmail(String email) {
//        return email != null && email.contains("@") && !email.startsWith("@") && !email.endsWith("@");
//    }
//
//    private static boolean isValidPhone(String phone) {
//        return phone != null && phone.matches("\\d{10}");
//    }
//
//    private static boolean isValidAadhaar(String aadhaar) {
//        return aadhaar != null && aadhaar.matches("\\d{12}");
//    }
//
//    private static boolean isValidPan(String pan) {
//        return pan != null && pan.matches("[A-Z]{5}[0-9]{4}[A-Z]");
//    }
//}