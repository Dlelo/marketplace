package com.example.marketplace.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatsDTO {
    // Totals
    private long totalUsers;
    private long totalHouseHelps;
    private long totalHomeOwners;
    private long totalAgents;
    private long totalHireRequests;
    private long pendingHireRequests;
    private long acceptedHireRequests;

    // User registration trends
    private long usersThisWeek;
    private long usersThisMonth;
    private long houseHelpsThisMonth;
    private long homeOwnersThisMonth;

    // Revenue
    private double totalRevenue;
    private double revenueThisWeek;
    private double revenueThisMonth;
    private long successfulPayments;
    private long pendingPayments;
}
