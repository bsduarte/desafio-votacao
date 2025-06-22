package com.dbserver.voting.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.dbserver.voting.config.ConstraintErrorMappingConfig;
import com.dbserver.voting.config.DBExceptionErrorMappingConfig;
import com.dbserver.voting.config.ErrorMappingConfig.ErrorConfig;
import com.dbserver.voting.dto.ApiErrorDTO;

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

    public GlobalExceptionHandler(ConstraintErrorMappingConfig constraintMappings,
                                DBExceptionErrorMappingConfig dbExceptionMappings) {
        this.constraintMappings = constraintMappings;
        this.dbExceptionMappings = dbExceptionMappings;
    }    

    @ExceptionHandler(PSQLException.class)
    public ResponseEntity<ApiErrorDTO> handlePSQLException(PSQLException e) {
        String fullMessage = e.getMessage();
        logger.error("Can't complete operation on database: ", fullMessage);
        logger.debug(ExceptionUtils.getStackTrace(e));

        try {
            ErrorConfig error = dbExceptionMappings.getError(getDbExceptionCode(fullMessage));
            return ResponseEntity
                        .badRequest()
                        .body(new ApiErrorDTO(error.getCode(), error.getMessage()));
        } catch(NullPointerException ignored) {
            return ResponseEntity.badRequest().body(
                new ApiErrorDTO("Can't complete the operation. ", extractCustomPSQLErrorMessage(fullMessage)));
        }
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorDTO> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        Throwable cause = e.getMostSpecificCause();
        String fullMessage = cause.getMessage();

        logger.error("Data integrity violation attempt: ", fullMessage);
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
