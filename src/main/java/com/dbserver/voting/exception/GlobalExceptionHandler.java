package com.dbserver.voting.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.dbserver.voting.config.ConstraintErrorMappingConfig;
import com.dbserver.voting.config.DBExceptionErrorMappingConfig;
import com.dbserver.voting.config.ErrorMappingConfig.ErrorConfig;
import com.dbserver.voting.dto.ApiErrorDTO;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    private static final Pattern dbExceptionPattern = Pattern.compile("\\[CODE:(.+?)\\]");

    private final ConstraintErrorMappingConfig constraintMappings;
    private final DBExceptionErrorMappingConfig dbExceptionMappings;

    private static final String FIELD_ERROR = "FIELD_ERROR";
    private static final String REQUEST_BODY_ERROR = "REQUEST_BODY_ERROR";
    private static final String BODY_FORMAT_ERROR = "BODY_FORMAT_ERROR";
    private static final String TYPE_ERROR = "TYPE_ERROR";

    public GlobalExceptionHandler(ConstraintErrorMappingConfig constraintMappings,
                                DBExceptionErrorMappingConfig dbExceptionMappings) {
        this.constraintMappings = constraintMappings;
        this.dbExceptionMappings = dbExceptionMappings;
    }

    @SuppressWarnings("null")
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorDTO> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        String exMessage = e.getMessage();
        logger.error("Invalid request body: {}", exMessage);
        logger.debug(ExceptionUtils.getStackTrace(e));

        try {
            Throwable cause = e.getRootCause();

            if (cause instanceof JsonParseException) {
                return ResponseEntity.badRequest().body(
                    new ApiErrorDTO(
                        BODY_FORMAT_ERROR, 
                        "The request content is invalid. Check for missing commas, braces, or quotation marks"));

            } else if (cause instanceof MismatchedInputException mismatchedInputException) {
                String path = mismatchedInputException.getPath().stream()
                         .map(ref -> ref.getFieldName())
                         .filter(java.util.Objects::nonNull)
                         .reduce((a, b) -> a + "." + b)
                         .orElse("a field");

                var type = mismatchedInputException.getTargetType();
                String typeName = type != null ? type.getSimpleName() : "a valid type";

                return ResponseEntity.badRequest().body(
                    new ApiErrorDTO(
                        TYPE_ERROR,
                        String.format("Invalid type provided for %s. Expected: %s", path, typeName)));
            }
            
            return ResponseEntity.badRequest().body(
                new ApiErrorDTO(REQUEST_BODY_ERROR, cause.getMessage()));

        } catch (NullPointerException ignored) {}

        return ResponseEntity.badRequest().body(
                new ApiErrorDTO(REQUEST_BODY_ERROR, "Invalid/malformed request content"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String exMessage = e.getMessage();
        logger.error("Invalid argument/field: ", exMessage);
        logger.debug(ExceptionUtils.getStackTrace(e));

        try {
            // Notify just the first field error
            FieldError error = e.getBindingResult().getFieldErrors().get(0);
            return ResponseEntity.badRequest().body(
                new ApiErrorDTO(FIELD_ERROR, String.format("%s: %s", error.getField(), error.getDefaultMessage())));
        } catch (NullPointerException ignored) {}

        return ResponseEntity.badRequest().body(
                new ApiErrorDTO(FIELD_ERROR, exMessage));
    }

    @ExceptionHandler(PSQLException.class)
    public ResponseEntity<ApiErrorDTO> handlePSQLException(PSQLException e) {
        String exMessage = e.getMessage();
        logger.error("Can't complete operation on database: ", exMessage);
        logger.debug(ExceptionUtils.getStackTrace(e));

        try {
            ErrorConfig error = dbExceptionMappings.getError(getDbExceptionCode(exMessage));
            return ResponseEntity
                        .badRequest()
                        .body(new ApiErrorDTO(error.getCode(), error.getMessage()));
        } catch(NullPointerException ignored) {}

        return ResponseEntity.badRequest().body(
                new ApiErrorDTO("Can't complete the operation. ", extractCustomPSQLErrorMessage(exMessage)));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorDTO> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        Throwable cause = e.getMostSpecificCause();
        String exMessage = cause.getMessage();

        logger.error("Data integrity violation attempt: ", exMessage);
        logger.debug(ExceptionUtils.getStackTrace(e));
        
        if (cause instanceof PSQLException psqlEx) {
            String constraint = Optional.ofNullable(psqlEx.getServerErrorMessage())
                                        .map(ServerErrorMessage::getConstraint)
                                        .orElse(null);
            try {
                ErrorConfig error = constraintMappings.getError(constraint);
                return ResponseEntity
                        .badRequest()
                        .body(new ApiErrorDTO(error.getCode(), error.getMessage()));
            } catch(NullPointerException ignored) {}
        }
        
        return ResponseEntity.badRequest().body(new ApiErrorDTO("Operation not permitted", "Database Error"));
    }

    private String getDbExceptionCode(String fullMessage) {
        Matcher matcher = dbExceptionPattern.matcher(fullMessage);
        return matcher.find() ? matcher.group(1) : null;
    }

    private String extractCustomPSQLErrorMessage(String fullMessage) {
        String message = "Database Error";
        if (fullMessage != null) {
            int beginIndex = fullMessage.indexOf(":");
            int endIndex = fullMessage.lastIndexOf("Where:");
            if (beginIndex != -1 && beginIndex + 1 < fullMessage.length()) {
                message = (endIndex != -1 ? fullMessage.substring(beginIndex + 1, endIndex) 
                            : fullMessage.substring(beginIndex + 1))
                            .trim();
            }
        }
        return message;
    }
}
