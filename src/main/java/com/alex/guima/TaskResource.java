package com.alex.guima;

import java.util.List;

import com.alex.guima.domain.entity.Task;
import com.alex.guima.domain.service.TaskService;
import com.alex.guima.dto.TaskDTO;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;

@Path("/task")
@ApplicationScoped
public class TaskResource {
    private final TaskService taskService;

    @Inject
    public TaskResource(TaskService taskService) {
        this.taskService = taskService;
    }


    @GET
    public Uni<List<Task>> listAll() {
        return taskService.listAll();
    }

    @GET
    @Path("/{id}")
    public Uni<Task> findById(Long id) {
        return taskService.findById(id);
    }

    @POST
    @WithTransaction
    public Uni<Task> createTask(TaskDTO task) {
        return taskService.createTask(task);
    }

    @PUT
    @Path("/{id}")
    @WithTransaction
    public Uni<Task> update(Long id, TaskDTO updatedTask) {
        return taskService.updateTask(id, updatedTask);
    }
}
