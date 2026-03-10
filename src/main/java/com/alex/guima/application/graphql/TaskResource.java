package com.alex.guima.application.graphql;

import com.alex.guima.application.dto.TaskDTO;
import com.alex.guima.domain.entity.Task;
import com.alex.guima.domain.service.TaskService;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import org.eclipse.microprofile.graphql.*;

import java.util.List;

@GraphQLApi
public class TaskResource {
    private final TaskService taskService;

    @Inject
    TaskResource(TaskService taskService) {
        this.taskService = taskService;
    }

    @Query("allTasks")
    @Description("Find all tasks in the system")
    @WithSession
    public Uni<List<Task>> findAll() {
        return taskService.listAll();
    }

    @Query
    @Description("Find a task by its ID")
    @WithSession
    public Uni<Task> findById(@Name("taskId") Long id) {
        return taskService.findById(id);
    }

    @Mutation
    @WithTransaction
    public Uni<Task> createTask(TaskDTO task) {
        return taskService.createTask(task);
    }

    @Mutation
    @WithTransaction
    public Uni<Task> updateTask(@Name("taskId") Long id, TaskDTO task) {
        return taskService.updateTask(id, task);
    }

    @Mutation
    @WithTransaction
    public Uni<Boolean> deleteTask(@Name("taskId") Long id) {
        return taskService.deleteTask(id).replaceWith(true);
    }
}
