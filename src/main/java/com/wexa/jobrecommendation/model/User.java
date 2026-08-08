package com.wexa.jobrecommendation.model;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node("User")
public class User {

    @Id
    private String id;
    private String name;

    @Relationship(type = "HAS_SKILL", direction = Relationship.Direction.OUTGOING)
    private Set<Skill> skills = new HashSet<>();

    public User() {}
    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Set<Skill> getSkills() { return skills; }
    public void setSkills(Set<Skill> skills) { this.skills = skills; }
}