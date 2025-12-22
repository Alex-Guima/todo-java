package com.alex.guima.repository;

import java.util.List;

import com.alex.guima.domain.entity.Task;

import io.quarkus.panache.common.Page;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.NotFoundException;

@RequestScoped
public class TaskRepository {
    public Uni<List<Task>> findAll() {
        return Task.findAll().page(Page.ofSize(20)).list();
    }

    public Uni<Task> findById(Long id) {
        return Task.findById(id).onItem().ifNull().failWith(NotFoundException::new).onItem().transform(Task.class::cast);
    }

    public Uni<Task> persist(Task task) {
        return task.persist();
    }
}
