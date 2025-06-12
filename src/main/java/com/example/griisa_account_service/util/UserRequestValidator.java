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
