package com.akerumort.OrderManagementService.utils;

import com.akerumort.OrderManagementService.exceptions.CustomValidationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

public class ValidationUtil {

    public static void validateBindingResult(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            throw new CustomValidationException("Validation errors: " + errors.toString());
        }
    }
}