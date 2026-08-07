package com.wexa.jobrecommendation.model;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node("JobRole")
public class JobRole {
	
	@Id
    private String id;
    private String title;

    @Relationship(type = "REQUIRES_SKILL", direction = Relationship.Direction.OUTGOING)
    private Set<Skill> requiredSkills = new HashSet<>();

    public JobRole() {}
    public JobRole(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public Set<Skill> getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(Set<Skill> requiredSkills) { this.requiredSkills = requiredSkills; }

}
