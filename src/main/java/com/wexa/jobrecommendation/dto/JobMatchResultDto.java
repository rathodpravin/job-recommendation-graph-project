package com.wexa.jobrecommendation.dto;

import java.util.List;

public class JobMatchResultDto {
    private String jobId;
    private String jobTitle;
    private long matchingSkillsCount;
    private long totalRequiredSkills;
    private double matchPercentage;
    private List<String> matchedSkills;
    private List<String> missingSkills;

    public JobMatchResultDto(String jobId, String jobTitle, long matchingSkillsCount, 
                             long totalRequiredSkills, double matchPercentage, 
                             List<String> matchedSkills, List<String> missingSkills) {
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.matchingSkillsCount = matchingSkillsCount;
        this.totalRequiredSkills = totalRequiredSkills;
        this.matchPercentage = matchPercentage;
        this.matchedSkills = matchedSkills;
        this.missingSkills = missingSkills;
    }

    public String getJobId() { return jobId; }
    public String getJobTitle() { return jobTitle; }
    public long getMatchingSkillsCount() { return matchingSkillsCount; }
    public long getTotalRequiredSkills() { return totalRequiredSkills; }
    public double getMatchPercentage() { return matchPercentage; }
    public List<String> getMatchedSkills() { return matchedSkills; }
    public List<String> getMissingSkills() { return missingSkills; }
}