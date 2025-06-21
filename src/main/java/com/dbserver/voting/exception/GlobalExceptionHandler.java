package com.dbserver.voting.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final Map<String, String> constraintMsgMap = new HashMap<String, String>() {
        {
            put("assembly_pkey", "This assembly already exists");
            put("associated_pkey", "This associated already exists");
            put("associated_email_key", "An associated with this email already exists");
            put("associated_phone_key", "An associated with this phone already exists");
            put("associated_voting_associated_voting_key", "This associated has already voted in this voting");
            put("subject_pkey", "This subject already exists");
            put("subject_assembly_subject_assembly_key", "The subject is already associated with this assembly");
            put("vote_pkey", "This vote is already registered");
            put("voting_pkey", "This voting is already created");
        }
    };

    @ExceptionHandler(PSQLException.class)
    public ResponseEntity<String> handlePSQLException(PSQLException e) {
        logger.error("Can't complete operation on database: ", e.getMessage());
        logger.debug(ExceptionUtils.getStackTrace(e), e);
        return ResponseEntity.badRequest().body(
            "Can't complete the operation. " + extractCustomPSQLErrorMessage(e.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        Throwable cause = e.getMostSpecificCause();
        String fullMessage = cause.getMessage();

        logger.error("Data integrity violation attempt: ", fullMessage);
        logger.debug(ExceptionUtils.getStackTrace(e));
        
        String message = "Database Error";
        if (cause instanceof PSQLException psqlEx) {
            String constraint = Optional.ofNullable(psqlEx.getServerErrorMessage())
                                        .map(ServerErrorMessage::getConstraint)
                                        .orElse(null);
            try {
                String constraintMessage = constraintMsgMap.get(constraint);
                if (constraintMessage != null) {
                    message = constraintMessage;
                }
            } catch(NullPointerException ignored) {}
        }
        
        return ResponseEntity.badRequest().body("Operation not permitted. " + message);
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
