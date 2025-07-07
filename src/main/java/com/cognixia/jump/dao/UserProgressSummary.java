package com.cognixia.jump.dao;

public class UserProgressSummary {
    public int totalTracking;
    public int planToStart;
    public int inProgress;
    public int completed;
    
    public UserProgressSummary() {}
    
    public UserProgressSummary(int total, int plan, int progress, int complete) {
        this.totalTracking = total;
        this.planToStart = plan;
        this.inProgress = progress;
        this.completed = complete;
    }
}
