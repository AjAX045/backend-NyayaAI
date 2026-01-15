package com.ajay.nyayaAI.dto;

public class SectionAnalysisResponse {

    private String section;
    private String title;
    private String category;
    private int matchPercentage;
    private String description;
    private String punishment;

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getMatchPercentage() { return matchPercentage; }
    public void setMatchPercentage(int matchPercentage) { this.matchPercentage = matchPercentage; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPunishment() { return punishment; }
    public void setPunishment(String punishment) { this.punishment = punishment; }
}
