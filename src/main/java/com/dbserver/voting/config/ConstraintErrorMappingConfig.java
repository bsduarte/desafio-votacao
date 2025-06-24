package com.dbserver.voting.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConfigurationProperties(prefix = "constraint")
public final class ConstraintErrorMappingConfig extends ErrorMappingConfig {
}
