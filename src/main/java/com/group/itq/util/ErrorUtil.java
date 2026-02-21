package com.group.itq.util;

import jakarta.validation.ConstraintViolation;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ErrorUtil {

    public static String buildErrorMessage(BindingResult bindingResult) {
        StringBuilder errors = new StringBuilder();
        ErrorUtil.recordErrors(errors, bindingResult);
        return errors.toString();
    }

    public static String buildErrorMessage(Set<ConstraintViolation<?>> violations) {
        StringBuilder errors = new StringBuilder();
        ErrorUtil.recordErrors(errors, violations);
        return errors.toString();
    }

    private static void recordErrors(StringBuilder errors, BindingResult bindingResult) {
        bindingResult.getFieldErrors().forEach(
                error -> errors
                        .append(error.getDefaultMessage())
                        .append("\n")
        );
        sortErrorsByLength(errors);
    }

    private static void recordErrors(StringBuilder errors, Set<ConstraintViolation<?>> violations) {
        for (ConstraintViolation<?> violation : violations) {
            errors.append(violation.getMessage()).append("\n");
        }
        sortErrorsByLength(errors);
    }

    private static void sortErrorsByLength(StringBuilder errors) {
        String sortedErrors = Arrays.stream(errors.toString().split("\n"))
                .filter(str -> !str.isBlank())
                .sorted(Comparator.comparingInt(String::length))
                .collect(Collectors.joining("\n"));
        errors.setLength(0);
        errors.append(sortedErrors);
    }
}