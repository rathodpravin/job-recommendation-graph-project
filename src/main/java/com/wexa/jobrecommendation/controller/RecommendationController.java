package com.wexa.jobrecommendation.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wexa.jobrecommendation.dto.JobMatchResultDto;
import com.wexa.jobrecommendation.dto.UserCreateRequestDto;
import com.wexa.jobrecommendation.service.RecommendationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/recommendations")
@CrossOrigin(origins = "*")
@Tag(name = "Job Recommendations API", description = "Production Graph Engine for Skill Gap Analysis & Matching")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @Operation(summary = "Get candidate recommendations with match % and skill gaps", description = "Calculates matching percentages and missing skill gaps using graph traversals.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recommendations successfully generated",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = JobMatchResultDto.class)))),
        @ApiResponse(responseCode = "404", description = "User ID not found in database")
    })
    @GetMapping("/jobs/{userId}")
    public ResponseEntity<List<JobMatchResultDto>> getRecommendationsForUser(@PathVariable String userId) {
        List<JobMatchResultDto> recommendations = recommendationService.getRecommendationsForUser(userId);
        return ResponseEntity.ok(recommendations);
    }

    @Operation(summary = "Get all open job roles", description = "Retrieves all registered jobs along with required skill sets.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved job roles")
    @GetMapping("/jobs")
    public ResponseEntity<List<Map<String, Object>>> getAllOpenJobs() {
        return ResponseEntity.ok(recommendationService.getAllOpenJobs());
    }

    @Operation(summary = "Get all user profiles", description = "Retrieves all user nodes and their current associated skills.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved user profiles")
    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        return ResponseEntity.ok(recommendationService.getAllUsers());
    }

    @Operation(summary = "Create or update user candidate", description = "Ingests candidate nodes and connects skill edges dynamically.")
    @ApiResponse(responseCode = "201", description = "User created or updated successfully")
    @PostMapping("/users")
    public ResponseEntity<Map<String, String>> createUser(@Valid @RequestBody UserCreateRequestDto request) {
        recommendationService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "User node and skill edges created successfully", "userId", request.getId()));
    }

    @Operation(summary = "Seed additional job roles", description = "Triggers bulk Graph MERGE queries to seed demo job positions.")
    @ApiResponse(responseCode = "200", description = "Jobs successfully seeded")
    @PostMapping("/jobs/seed")
    public ResponseEntity<Map<String, String>> seedJobs() {
        recommendationService.seedAdditionalJobs();
        return ResponseEntity.ok(Map.of("message", "Job roles successfully seeded into CognoDB!"));
    }
}