package com.dbserver.voting.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "constraint")
public final class ConstraintErrorMappingConfig extends ErrorMappingConfig {
}
