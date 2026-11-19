package com.hiretrack.validation;

import com.hiretrack.dto.ApplicationRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SalaryRangeValidator implements ConstraintValidator<SalaryRange, ApplicationRequest> {

    @Override
    public boolean isValid(ApplicationRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true;
        }
        Integer min = request.salaryMin();
        Integer max = request.salaryMax();
        if (min == null || max == null) {
            return true;
        }
        return min <= max;
    }
}