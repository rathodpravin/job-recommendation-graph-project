package com.wexa.jobrecommendation.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wexa.jobrecommendation.model.User;

@Repository
public interface UserRepository extends Neo4jRepository<User, String>{
	
	@Query("MATCH (u:User {id: $userId})-[:HAS_SKILL]->(s:Skill)<-[:REQUIRES_SKILL]-(j:JobRole) " +
	           "RETURN j.id AS jobId, j.title AS jobTitle, COUNT(s) AS matchingSkillsCount " +
	           "ORDER BY matchingSkillsCount DESC LIMIT 5")
	    List<Map<String, Object>> recommendJobsForUser(@Param("userId") String userId);

	    // Multi-Hop Skill Gap Query (Awkward in Relational SQL)
	    @Query("MATCH (u:User {id: $userId}) " +
	           "MATCH (j:JobRole {id: $jobId})-[:REQUIRES_SKILL]->(reqSkill:Skill) " +
	           "WHERE NOT (u)-[:HAS_SKILL]->(reqSkill) " +
	           "RETURN reqSkill.name AS missingSkill")
	    List<String> findMissingSkillsForJob(@Param("userId") String userId, @Param("jobId") String jobId);

}
