package com.imnu.story.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 阿斯亚
 */
@Data
public class UserStoriesDTO {
    private List<String> roles;
    private List<String> intentions;
    private List<String> fieldName;
    private List<String> benefits;
    private List<Integer> usLine;
    private List<String> feedback;
    private List<String> errors;
    private List<String> violatedGuidelines;
    private List<String> suggestions;
    private List<String> messages;
    private List<Boolean> flag; //进度条flag，如果前一个检测项通过了才能继续走
    private int totalProblems;
    private int errorCount;
    private int warningCount;
    private int perfectCount;
    private List<String> userSt;

    public UserStoriesDTO() {
        this.roles = new ArrayList<>();
        this.intentions = new ArrayList<>();
        this.fieldName = new ArrayList<>();
        this.benefits = new ArrayList<>();
        this.usLine = new ArrayList<>();
        this.feedback = new ArrayList<>();
        this.errors = new ArrayList<>();
        this.violatedGuidelines = new ArrayList<>();
        this.suggestions = new ArrayList<>();
        this.messages = new ArrayList<>();
        this.userSt = new ArrayList<>();
        this.flag = new ArrayList<>();
    }
}
