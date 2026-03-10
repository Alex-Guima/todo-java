package com.alex.guima.application.graphql;

import com.alex.guima.application.dto.TaskDTO;
import com.alex.guima.domain.entity.Task;
import com.alex.guima.domain.service.TaskService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;

@QuarkusTest
class TaskGraphQLResourceTest {

    @InjectMock
    TaskService taskService;

    @Test
    @DisplayName("Query: allTasks - Deve retornar 200 OK e a lista de tarefas envelopada no GraphQL")
    void allTasks_DeveRetornarListaDeTasks() {
        // Arrange
        Task taskMock = new Task("Estudar GraphQL", false, LocalDateTime.now().plusDays(1));
        List<Task> listaMock = List.of(taskMock);

        Mockito.when(taskService.listAll()).thenReturn(Uni.createFrom().item(listaMock));

        // Payload padrão do GraphQL: um JSON com a chave "query"
        String graphqlQuery = """
            {
                "query": "{ allTasks { title completed } }"
            }
            """;

        // Act & Assert
        given()
                .contentType(ContentType.JSON)
                .body(graphqlQuery)
                .when()
                .post("/graphql")
                .then()
                .statusCode(200)
                // O GraphQL envelopa a resposta de sucesso dentro do nó "data"
                .body("data.allTasks", hasSize(1))
                .body("data.allTasks[0].title", is("Estudar GraphQL"));

        Mockito.verify(taskService, Mockito.times(1)).listAll();
    }

    @Test
    @DisplayName("Query: findById - Deve retornar 200 OK e os dados da tarefa solicitada")
    void findById_QuandoEncontrado_DeveRetornarTarefa() {
        // Arrange
        Task taskMock = new Task("Tarefa Específica", true, LocalDateTime.now().plusDays(2));
        Mockito.when(taskService.findById(1L)).thenReturn(Uni.createFrom().item(taskMock));

        String graphqlQuery = """
            {
                "query": "{ findById(taskId: 1) { title completed } }"
            }
            """;

        // Act & Assert
        given()
                .contentType(ContentType.JSON)
                .body(graphqlQuery)
                .when()
                .post("/graphql")
                .then()
                .statusCode(200)
                .body("data.findById.title", is("Tarefa Específica"))
                .body("data.findById.completed", is(true));

        Mockito.verify(taskService, Mockito.times(1)).findById(1L);
    }

    @Test
    @DisplayName("Mutation: createTask - Deve repassar o DTO ao serviço e retornar os dados da nova tarefa")
    void createTask_ComPayloadValido_DeveRetornarTarefaCriada() {
        // Arrange
        LocalDateTime dueDate = LocalDateTime.now().plusDays(5);
        // O SmallRye gera automaticamente um Input Type no schema GraphQL baseado no seu DTO
        Task taskRetorno = new Task("Nova Mutation", false, dueDate);

        Mockito.when(taskService.createTask(any(TaskDTO.class))).thenReturn(Uni.createFrom().item(taskRetorno));

        // Para mutações com objetos complexos, passamos os parâmetros no formato esperado pelo GraphQL Input Type
        String graphqlMutation = """
            {
                "query": "mutation { createTask(task: { title: \\"Nova Mutation\\", completed: false, dueDate: \\"2026-12-31T23:59:59\\" }) { title } }"
            }
            """;

        // Act & Assert
        given()
                .contentType(ContentType.JSON)
                .body(graphqlMutation)
                .when()
                .post("/graphql")
                .then()
                .statusCode(200)
                .body("data.createTask.title", is("Nova Mutation"));

        Mockito.verify(taskService, Mockito.times(1)).createTask(any(TaskDTO.class));
    }

    @Test
    @DisplayName("Mutation: updateTask - Deve atualizar tarefa existente e retornar os novos dados")
    void updateTask_ComPayloadValido_DeveRetornarTarefaAtualizada() {
        // Arrange
        LocalDateTime dueDate = LocalDateTime.now().plusDays(3);
        Task taskAtualizada = new Task("Atualizada", true, dueDate);

        Mockito.when(taskService.updateTask(Mockito.eq(1L), any(TaskDTO.class)))
                .thenReturn(Uni.createFrom().item(taskAtualizada));

        String graphqlMutation = """
            {
                "query": "mutation { updateTask(taskId: 1, task: { title: \\"Atualizada\\", completed: true, dueDate: \\"2026-12-31T23:59:59\\" }) { title completed } }"
            }
            """;

        // Act & Assert
        given()
                .contentType(ContentType.JSON)
                .body(graphqlMutation)
                .when()
                .post("/graphql")
                .then()
                .statusCode(200)
                .body("data.updateTask.title", is("Atualizada"))
                .body("data.updateTask.completed", is(true));

        Mockito.verify(taskService, Mockito.times(1)).updateTask(Mockito.eq(1L), any(TaskDTO.class));
    }

    @Test
    @DisplayName("Mutation: deleteTask - Deve retornar true quando exclusão for bem-sucedida")
    void deleteTask_QuandoExistir_DeveRetornarTrue() {
        // Arrange
        Mockito.when(taskService.deleteTask(1L)).thenReturn(Uni.createFrom().voidItem());

        String graphqlMutation = """
            {
                "query": "mutation { deleteTask(taskId: 1) }"
            }
            """;

        // Act & Assert
        given()
                .contentType(ContentType.JSON)
                .body(graphqlMutation)
                .when()
                .post("/graphql")
                .then()
                .statusCode(200)
                .body("data.deleteTask", is(true));

        Mockito.verify(taskService, Mockito.times(1)).deleteTask(1L);
    }
}
