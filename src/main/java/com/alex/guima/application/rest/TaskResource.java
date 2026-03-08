package com.alex.guima.application.rest;

import com.alex.guima.application.dto.TaskDTO;
import com.alex.guima.domain.entity.Task;
import com.alex.guima.domain.service.TaskService;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;

import java.util.List;

@Path("/task")
@ApplicationScoped
public class TaskResource {
    private final TaskService taskService;

    @Inject
    TaskResource(TaskService taskService) {
        this.taskService = taskService;
    }

    @GET
    @Path("/{id}")
    public Uni<Task> findByID(Long id) {
        return taskService.findById(id);
    }

    @GET
    public Uni<List<Task>> getAllTasks() {
        return taskService.listAll();
    }

    @POST
    @WithTransaction
    public Uni<Task> createTask(TaskDTO task) {
        return taskService.createTask(task);
    }

    @PUT
    @Path("/{id}")
    @WithTransaction
    public Uni<Task> updateTask(Long id, TaskDTO task) {
        return taskService.updateTask(id, task);
    }

    @DELETE
    @Path("/{id}")
    @WithTransaction
    public Uni<Void> deleteTask(Long id) {
        return taskService.deleteTask(id);
    }
}
