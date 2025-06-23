package com.dbserver.voting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.web.servlet.MockMvc;

import com.dbserver.voting.config.ConstraintErrorMappingConfig;
import com.dbserver.voting.config.DBExceptionErrorMappingConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

@EnableConfigurationProperties(value = {ConstraintErrorMappingConfig.class, DBExceptionErrorMappingConfig.class})
public abstract class ControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;
}
