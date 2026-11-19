package com.hiretrack.controller;

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

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class FollowUpControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createsFollowUpAndListsDue() throws Exception {
        Map<String, Object> app = Map.of(
                "company", "Umbrella",
                "role", "SRE",
                "source", "OTHER",
                "remote", false,
                "appliedDate", LocalDate.now().toString()
        );
        String created = mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(app)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long appId = objectMapper.readTree(created).get("id").asLong();

        Map<String, Object> followUp = Map.of(
                "dueDate", LocalDate.now().toString(),
                "note", "Ping recruiter"
        );
        String followUpResponse = mockMvc.perform(post("/api/applications/" + appId + "/follow-ups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(followUp)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long followUpId = objectMapper.readTree(followUpResponse).get("id").asLong();

        mockMvc.perform(get("/api/follow-ups/due"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)));

        mockMvc.perform(patch("/api/applications/" + appId + "/follow-ups/" + followUpId + "/done"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.done").value(true));
    }
}