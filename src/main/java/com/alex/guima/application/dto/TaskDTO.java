package com.alex.guima.application.dto;

import java.time.LocalDateTime;

public record TaskDTO(String title, boolean completed, LocalDateTime dueDate) {
    public TaskDTO {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or blank");
        }

        if (dueDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Due date cannot be in the past");
        }
    }
}
