package com.wexa.jobrecommendation.service;

import com.wexa.jobrecommendation.dto.JobMatchResultDto;
import com.wexa.jobrecommendation.dto.UserCreateRequestDto;

import java.util.List;
import java.util.Map;

public interface RecommendationService {
    List<JobMatchResultDto> getRecommendationsForUser(String userId);
    List<Map<String, Object>> getAllOpenJobs();
    List<Map<String, Object>> getAllUsers();
    void createUser(UserCreateRequestDto request);
    void seedAdditionalJobs();
}