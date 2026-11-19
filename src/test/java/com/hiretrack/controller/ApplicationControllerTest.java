package com.hiretrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiretrack.domain.ApplicationSource;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class ApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void crudLifecycle() throws Exception {
        Map<String, Object> createBody = Map.of(
                "company", "Acme Corp",
                "role", "Backend Engineer",
                "source", "LINKEDIN",
                "remote", true,
                "appliedDate", LocalDate.of(2026, 2, 1).toString()
        );

        String createResponse = mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createBody)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("APPLIED")))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(get("/api/applications/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.company", is("Acme Corp")));

        Map<String, Object> updateBody = Map.of(
                "company", "Acme Corp",
                "role", "Senior Backend Engineer",
                "source", "LINKEDIN",
                "salaryMin", 90000,
                "salaryMax", 120000,
                "remote", true,
                "appliedDate", LocalDate.of(2026, 2, 1).toString()
        );

        mockMvc.perform(put("/api/applications/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role", is("Senior Backend Engineer")));

        mockMvc.perform(delete("/api/applications/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/applications/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectsInvalidSalaryRange() throws Exception {
        Map<String, Object> body = Map.of(
                "company", "Acme",
                "role", "Engineer",
                "source", ApplicationSource.JOB_BOARD.name(),
                "salaryMin", 150000,
                "salaryMax", 100000,
                "remote", false,
                "appliedDate", LocalDate.now().toString()
        );

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }
}