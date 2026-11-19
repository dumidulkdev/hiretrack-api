package com.hiretrack.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class TimelineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void returnsOrderedStatusHistory() throws Exception {
        Map<String, Object> body = Map.of(
                "company", "Initech",
                "role", "Software Engineer",
                "source", "REFERRAL",
                "remote", true,
                "appliedDate", LocalDate.of(2026, 1, 10).toString()
        );
        String created = mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long id = objectMapper.readTree(created).get("id").asLong();

        mockMvc.perform(patch("/api/applications/" + id + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"SCREENING\"}"))
                .andExpect(status().isOk());

        String timelineJson = mockMvc.perform(get("/api/applications/" + id + "/timeline"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode timeline = objectMapper.readTree(timelineJson);
        assertThat(timeline).hasSize(2);
        assertThat(timeline.get(0).get("toStatus").asText()).isEqualTo("APPLIED");
        assertThat(timeline.get(1).get("toStatus").asText()).isEqualTo("SCREENING");
    }
}