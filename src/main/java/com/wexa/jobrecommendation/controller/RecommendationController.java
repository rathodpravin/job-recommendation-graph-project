package com.wexa.jobrecommendation.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wexa.jobrecommendation.repository.UserRepository;

@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "*")
public class RecommendationController {
	
	private final UserRepository userRepository;

    public RecommendationController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/jobs/{userId}")
    public ResponseEntity<?> getRecommendedJobs(@PathVariable String userId) {
        try {
            List<Map<String, Object>> recommendations = userRepository.recommendJobsForUser(userId);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Database connection issue: " + e.getMessage()));
        }
    }

    @GetMapping("/gap/{userId}/{jobId}")
    public ResponseEntity<?> getSkillGap(@PathVariable String userId, @PathVariable String jobId) {
        return ResponseEntity.ok(userRepository.findMissingSkillsForJob(userId, jobId));
    }

}
