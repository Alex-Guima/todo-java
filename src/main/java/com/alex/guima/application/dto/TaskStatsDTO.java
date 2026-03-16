package com.alex.guima.application.dto;

public record TaskStatsDTO(long total, long completed, long pending, long overdue, double completionRate) {
}
