package com.dbserver.voting.config;

import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public abstract class ErrorMappingConfig {
    protected Map<String, ErrorConfig> error;

    public ErrorConfig getError(String errKey) {
        return error.get(errKey);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ErrorConfig {
        private String code;
        private String message;
    }    
}
