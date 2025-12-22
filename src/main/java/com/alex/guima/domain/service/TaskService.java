package com.alex.guima.domain.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.alex.guima.domain.entity.Task;
import com.alex.guima.dto.TaskDTO;
import com.alex.guima.repository.TaskRepository;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TaskService {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_DATE_TIME;
    private final TaskRepository taskRepository;

    @Inject
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;

    }

    public Uni<List<Task>> listAll() {
        return taskRepository.findAll();
    }

    public Uni<Task> findById(Long id) {
        return taskRepository.findById(id);
    }

    @WithTransaction
    public Uni<Task> createTask(TaskDTO task) {
        LocalDateTime dueDate = task.dueDate() != null ? LocalDate.parse(task.dueDate()).atStartOfDay() : null;
        return taskRepository.persist(new Task(task.title(), task.completed(), dueDate));
    }

    @WithTransaction
    public Uni<Task> updateTask(Long id, TaskDTO updatedTask) {
        Uni<Task> original = findById(id);
        return original.onItem().transformToUni(task -> {
            task.setCompleted(updatedTask.completed());
            task.setDueDate(updatedTask.dueDate() != null ? LocalDate.parse(updatedTask.dueDate()).atStartOfDay() : null);
            task.setTitle(updatedTask.title());
            return taskRepository.persist(task);
        }).onFailure().recoverWithNull().log();
    }
}
