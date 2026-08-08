package com.wexa.jobrecommendation.dto;

import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public class UserCreateRequestDto {

    @NotBlank(message = "User ID cannot be blank")
    private String id;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotEmpty(message = "At least one skill must be provided")
    private List<String> skills;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }
}