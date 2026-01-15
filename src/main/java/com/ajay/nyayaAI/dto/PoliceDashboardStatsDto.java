package com.ajay.nyayaAI.dto;

public class PoliceDashboardStatsDto {

    private long totalFirs;
    private long pendingFirs;
    private long solvedFirs;

    public PoliceDashboardStatsDto(long totalFirs, long pendingFirs, long solvedFirs) {
        this.totalFirs = totalFirs;
        this.pendingFirs = pendingFirs;
        this.solvedFirs = solvedFirs;
    }

    public long getTotalFirs() {
        return totalFirs;
    }

    public long getPendingFirs() {
        return pendingFirs;
    }

    public long getSolvedFirs() {
        return solvedFirs;
    }
}
