package com.alex.guima.application.rest;

import com.alex.guima.application.dto.TaskDTO;
import com.alex.guima.application.dto.TaskStatsDTO;
import com.alex.guima.domain.entity.Task;
import com.alex.guima.domain.service.TaskService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@QuarkusTest
class TaskResourceTest {

    @InjectMock
    TaskService taskService;

    @Test
    @DisplayName("GET /task/{id} - Deve retornar 200 OK e a tarefa quando encontrada")
    void findByID_QuandoEncontrado_DeveRetornarStatus200() {
        // Arrange
        Task taskMock = new Task("Estudar Testes", false, LocalDateTime.now().plusDays(1));

        Mockito.when(taskService.findById(1L)).thenReturn(Uni.createFrom().item(taskMock));

        // Act & Assert
        given()
                .when()
                .get("/task/1")
                .then()
                .statusCode(200)
                .body("title", is("Estudar Testes"))
                .body("completed", is(false));

        Mockito.verify(taskService, Mockito.times(1)).findById(1L);
    }

    @Test
    @DisplayName("GET /task/{id} - Deve retornar 404 Not Found quando a tarefa não existir")
    void findByID_QuandoNaoEncontrado_DeveRetornarStatus404() {
        // Arrange
        // Simula o serviço retornando um Uni que falha com NotFoundException
        Mockito.when(taskService.findById(99L))
                .thenReturn(Uni.createFrom().failure(new NotFoundException()));

        // Act & Assert
        given()
                .when()
                .get("/task/99")
                .then()
                .statusCode(404);

        Mockito.verify(taskService, Mockito.times(1)).findById(99L);
    }

    @Test
    @DisplayName("GET /task - Deve retornar 200 OK e uma lista de tarefas")
    void getAllTasks_DeveRetornarStatus200ELista() {
        // Arrange
        Task taskMock = new Task("Comprar café", false, LocalDateTime.now().plusDays(1));
        List<Task> listaMock = List.of(taskMock);

        Mockito.when(taskService.listAll()).thenReturn(Uni.createFrom().item(listaMock));

        // Act & Assert
        given()
                .when()
                .get("/task")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].title", is("Comprar café"));

        Mockito.verify(taskService, Mockito.times(1)).listAll();
    }

    @Test
    @DisplayName("POST /task - Deve criar tarefa e retornar 200 OK")
    void createTask_ComPayloadValido_DeveRetornarStatus200() {
        // Arrange
        LocalDateTime dueDate = LocalDateTime.now().plusDays(5);
        TaskDTO dtoMock = new TaskDTO("Nova Feature", false, dueDate);
        Task taskRetorno = new Task("Nova Feature", false, dueDate);

        Mockito.when(taskService.createTask(any(TaskDTO.class))).thenReturn(Uni.createFrom().item(taskRetorno));

        // Act & Assert
        given()
                .contentType(ContentType.JSON)
                .body(dtoMock)
                .when()
                .post("/task")
                .then()
                .statusCode(200)
                .body("title", is("Nova Feature"));

        Mockito.verify(taskService, Mockito.times(1)).createTask(any(TaskDTO.class));
    }

    @Test
    @DisplayName("PUT /task/{id} - Deve atualizar tarefa e retornar 200 OK")
    void updateTask_ComPayloadValido_DeveRetornarStatus200() {
        // Arrange
        Long taskId = 1L;
        LocalDateTime dueDate = LocalDateTime.now().plusDays(2);
        TaskDTO dtoMock = new TaskDTO("Feature Atualizada", true, dueDate);
        Task taskRetorno = new Task("Feature Atualizada", true, dueDate);

        Mockito.when(taskService.updateTask(eq(taskId), any(TaskDTO.class)))
                .thenReturn(Uni.createFrom().item(taskRetorno));

        // Act & Assert
        given()
                .contentType(ContentType.JSON)
                .body(dtoMock)
                .when()
                .put("/task/1")
                .then()
                .statusCode(200)
                .body("title", is("Feature Atualizada"))
                .body("completed", is(true));

        Mockito.verify(taskService, Mockito.times(1)).updateTask(eq(taskId), any(TaskDTO.class));
    }

    @Test
    @DisplayName("DELETE /task/{id} - Deve deletar tarefa e retornar 204 No Content")
    void deleteTask_QuandoSucesso_DeveRetornarStatus204() {
        // Arrange
        // Para métodos que retornam Uni<Void>, usamos o Uni.createFrom().voidItem()
        Mockito.when(taskService.deleteTask(1L)).thenReturn(Uni.createFrom().voidItem());

        // Act & Assert
        given()
                .when()
                .delete("/task/1")
                .then()
                // RESTEasy Reactive traduz métodos void/Uni<Void> para HTTP 204 (No Content)
                .statusCode(204);

        Mockito.verify(taskService, Mockito.times(1)).deleteTask(1L);
    }

    @Test
    @DisplayName("GET /task/stats - Deve retornar 200 OK com estatísticas das tarefas")
    void getStats_DeveRetornarStatus200ComEstatisticas() {
        // Arrange
        TaskStatsDTO statsMock = new TaskStatsDTO(10L, 4L, 6L, 2L, 40.0);
        Mockito.when(taskService.getStats()).thenReturn(Uni.createFrom().item(statsMock));

        // Act & Assert
        given()
                .when()
                .get("/task/stats")
                .then()
                .statusCode(200)
                .body("total", is(10))
                .body("completed", is(4))
                .body("pending", is(6))
                .body("overdue", is(2))
                .body("completionRate", is(40.0f));

        Mockito.verify(taskService, Mockito.times(1)).getStats();
    }
}