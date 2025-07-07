package com.cognixia.jump.dao;

public class TopicStats {
    public int totalUsers;
    public int planToStartCount;
    public int inProgressCount;
    public int completedCount;
    public double averageRating;
    
    public TopicStats() {}
    
    public TopicStats(int total, int plan, int progress, int complete, double avgRating) {
        this.totalUsers = total;
        this.planToStartCount = plan;
        this.inProgressCount = progress;
        this.completedCount = complete;
        this.averageRating = avgRating;
    }
}
