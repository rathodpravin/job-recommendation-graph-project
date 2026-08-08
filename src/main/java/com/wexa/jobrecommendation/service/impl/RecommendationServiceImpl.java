package com.wexa.jobrecommendation.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wexa.jobrecommendation.dto.JobMatchResultDto;
import com.wexa.jobrecommendation.dto.UserCreateRequestDto;
import com.wexa.jobrecommendation.service.RecommendationService;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    private final Neo4jClient neo4jClient;

    public RecommendationServiceImpl(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    @Override
    @Transactional(value = "transactionManager", readOnly = true)
    public List<JobMatchResultDto> getRecommendationsForUser(String userId) {
        String cypher = 
            "MATCH (u:User {id: $userId}) " +
            "MATCH (j:JobRole)-[:REQUIRES_SKILL]->(reqSkill:Skill) " +
            "WITH u, j, collect(reqSkill.name) AS requiredSkills " +
            "OPTIONAL MATCH (u)-[:HAS_SKILL]->(userSkill:Skill) " +
            "WITH u, j, requiredSkills, collect(userSkill.name) AS userSkills " +
            "WITH j, requiredSkills, userSkills, " +
            "     [s IN requiredSkills WHERE s IN userSkills] AS matchedSkills, " +
            "     [s IN requiredSkills WHERE NOT s IN userSkills] AS missingSkills " +
            "WHERE size(matchedSkills) > 0 " +
            "RETURN j.id AS jobId, j.title AS jobTitle, " +
            "       size(matchedSkills) AS matchingCount, " +
            "       size(requiredSkills) AS totalCount, " +
            "       matchedSkills, missingSkills " +
            "ORDER BY matchingCount DESC, size(missingSkills) ASC";

        Collection<Map<String, Object>> rows = neo4jClient.query(cypher)
                .bind(userId).to("userId")
                .fetch()
                .all();

        List<JobMatchResultDto> results = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            String jobId = (String) row.get("jobId");
            String jobTitle = (String) row.get("jobTitle");
            long matchingCount = ((Number) row.get("matchingCount")).longValue();
            long totalCount = ((Number) row.get("totalCount")).longValue();
            
            @SuppressWarnings("unchecked")
            List<String> matched = (List<String>) row.get("matchedSkills");
            @SuppressWarnings("unchecked")
            List<String> missing = (List<String>) row.get("missingSkills");

            double matchPercentage = totalCount > 0 
                ? Math.round(((double) matchingCount / totalCount) * 100.0) 
                : 0.0;

            results.add(new JobMatchResultDto(jobId, jobTitle, matchingCount, totalCount, matchPercentage, matched, missing));
        }

        return results;
    }

    @Override
    @Transactional(value = "transactionManager", readOnly = true)
    public List<Map<String, Object>> getAllOpenJobs() {
        String cypher = 
            "MATCH (j:JobRole)-[:REQUIRES_SKILL]->(s:Skill) " +
            "RETURN j.id AS jobId, j.title AS jobTitle, collect(s.name) AS requiredSkills";

        Collection<Map<String, Object>> rawResults = neo4jClient.query(cypher).fetch().all();
        List<Map<String, Object>> response = new ArrayList<>();

        for (Map<String, Object> row : rawResults) {
            Map<String, Object> item = new HashMap<>();
            item.put("jobId", row.get("jobId"));
            item.put("jobTitle", row.get("jobTitle"));
            item.put("requiredSkills", row.get("requiredSkills"));
            response.add(item);
        }

        return response;
    }

    @Override
    @Transactional(value = "transactionManager", readOnly = true)
    public List<Map<String, Object>> getAllUsers() {
        String cypher = 
            "MATCH (u:User) " +
            "OPTIONAL MATCH (u)-[:HAS_SKILL]->(s:Skill) " +
            "RETURN u.id AS userId, u.name AS userName, collect(s.name) AS skills";

        return new ArrayList<>(neo4jClient.query(cypher).fetch().all());
    }

    @Override
    @Transactional("transactionManager")
    public void createUser(UserCreateRequestDto request) {
        neo4jClient.query("MERGE (u:User {id: $id}) SET u.name = $name")
                .bind(request.getId()).to("id")
                .bind(request.getName()).to("name")
                .run();

        if (request.getSkills() != null) {
            for (String skill : request.getSkills()) {
                if (!skill.trim().isEmpty()) {
                    String cypher = 
                        "MERGE (s:Skill {name: $skillName}) " +
                        "WITH s MATCH (u:User {id: $userId}) " +
                        "MERGE (u)-[:HAS_SKILL]->(s)";
                    neo4jClient.query(cypher)
                            .bind(skill.trim()).to("skillName")
                            .bind(request.getId()).to("userId")
                            .run();
                }
            }
        }
    }

    @Override
    @Transactional("transactionManager")
    public void seedAdditionalJobs() {
        String cypher = 
            "MERGE (j1:JobRole {id: 'j103'}) SET j1.title = 'Full Stack Java Engineer' " +
            "MERGE (j2:JobRole {id: 'j104'}) SET j2.title = 'Data Engineer' " +
            "MERGE (j3:JobRole {id: 'j105'}) SET j3.title = 'DevOps & Cloud Specialist' " +
            "MERGE (j4:JobRole {id: 'j106'}) SET j4.title = 'Frontend React Developer' " +
            "MERGE (s1:Skill {name: 'Java'}) " +
            "MERGE (s2:Skill {name: 'Spring Boot'}) " +
            "MERGE (s3:Skill {name: 'React'}) " +
            "MERGE (s4:Skill {name: 'Python'}) " +
            "MERGE (s5:Skill {name: 'SQL'}) " +
            "MERGE (s6:Skill {name: 'Docker'}) " +
            "MERGE (s7:Skill {name: 'AWS'}) " +
            "MERGE (s8:Skill {name: 'TypeScript'}) " +
            "MERGE (j1)-[:REQUIRES_SKILL]->(s1) " +
            "MERGE (j1)-[:REQUIRES_SKILL]->(s2) " +
            "MERGE (j1)-[:REQUIRES_SKILL]->(s3) " +
            "MERGE (j2)-[:REQUIRES_SKILL]->(s4) " +
            "MERGE (j2)-[:REQUIRES_SKILL]->(s5) " +
            "MERGE (j3)-[:REQUIRES_SKILL]->(s6) " +
            "MERGE (j3)-[:REQUIRES_SKILL]->(s7) " +
            "MERGE (j4)-[:REQUIRES_SKILL]->(s3) " +
            "MERGE (j4)-[:REQUIRES_SKILL]->(s8)";

        neo4jClient.query(cypher).run();
    }
}