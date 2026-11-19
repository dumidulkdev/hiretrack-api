package com.hiretrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class StatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long applicationId;

    @BeforeEach
    void createApplication() throws Exception {
        Map<String, Object> body = Map.of(
                "company", "Globex",
                "role", "Platform Engineer",
                "source", "COMPANY_SITE",
                "remote", false,
                "appliedDate", LocalDate.of(2026, 3, 1).toString()
        );
        String response = mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        applicationId = objectMapper.readTree(response).get("id").asLong();
    }

    @Test
    void updatesStatusThroughPipeline() throws Exception {
        mockMvc.perform(patch("/api/applications/" + applicationId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"SCREENING\",\"note\":\"Recruiter replied\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("SCREENING")));
    }

    @Test
    void invalidTransitionReturns409() throws Exception {
        mockMvc.perform(patch("/api/applications/" + applicationId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"OFFER\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)));
    }
}