package ru.andreyszdlv.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.andreyszdlv.taskmanager.dto.comment.CommentDto;
import ru.andreyszdlv.taskmanager.dto.comment.CreateCommentRequestDto;
import ru.andreyszdlv.taskmanager.enums.Role;
import ru.andreyszdlv.taskmanager.enums.TaskPriority;
import ru.andreyszdlv.taskmanager.enums.TaskStatus;
import ru.andreyszdlv.taskmanager.model.Comment;
import ru.andreyszdlv.taskmanager.model.Task;
import ru.andreyszdlv.taskmanager.model.User;
import ru.andreyszdlv.taskmanager.repository.CommentRepository;
import ru.andreyszdlv.taskmanager.repository.TaskRepository;
import ru.andreyszdlv.taskmanager.repository.UserRepository;
import ru.andreyszdlv.taskmanager.service.JwtStorageService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@Testcontainers
class CommentControllerIT extends BaseIT {

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:latest");

    @Container
    static RedisContainer redisContainer =
            new RedisContainer(DockerImageName.parse("redis:latest")).withExposedPorts(6379).waitingFor(Wait.forListeningPort());

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);

        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", ()->redisContainer.getMappedPort(6379).toString());
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtStorageService jwtStorageService;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;

    String BASE_URL = "/api/tasks";

    @Test
    @Transactional
    void createComment_Returns201_WhenDataIsValidAndTaskExists() throws Exception {
        String email = "admin@admin.com";
        Role role = Role.ADMIN;
        User user = new User();
        user.setName("name");
        user.setEmail(email);
        user.setRole(role);
        user.setPassword("password");
        String accessToken = jwtStorageService.generateAccessToken(email, role);
        String content = "content";
        CreateCommentRequestDto requestDto = new CreateCommentRequestDto(content);
        Task task = new Task();
        task.setTitle("title");
        task.setDescription("description");
        task.setPriority(TaskPriority.HIGH);
        task.setStatus(TaskStatus.WAITING);
        task.setCreatedAt(LocalDateTime.now());
        task.setAuthor(userRepository.save(user));
        long taskId = taskRepository.save(task).getId();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BASE_URL+"/{taskId}/comments", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .header("Authorization", "Bearer " + accessToken);

        String responseString = mockMvc.perform(request).andExpectAll(
                status().isCreated(),
                content().contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse().getContentAsString();
        CommentDto responseDto = objectMapper.readValue(responseString, CommentDto.class);

        assertNotNull(responseDto);
        assertEquals(content, responseDto.content());
        assertEquals(content, commentRepository.findById(responseDto.id()).get().getContent());
    }

    @Test
    void createComment_Returns400_WhenDataInvalid() throws Exception {
        String email = "admin@admin.com";
        Role role = Role.ADMIN;
        String accessToken = jwtStorageService.generateAccessToken(email, role);
        String content = "    ";
        CreateCommentRequestDto requestDto = new CreateCommentRequestDto(content);
        long taskId = 1L;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BASE_URL+"/{taskId}/comments", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .header("Authorization", "Bearer " + accessToken);

        mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                jsonPath("$").exists()
        );

        assertEquals(0, commentRepository.findAll().size());
    }

    @Test
    void createComment_Returns404_WhenDataIsValidAndTaskNotExists() throws Exception {
        String email = "admin@admin.com";
        Role role = Role.ADMIN;
        String accessToken = jwtStorageService.generateAccessToken(email, role);
        String content = "content";
        CreateCommentRequestDto requestDto = new CreateCommentRequestDto(content);
        long taskId = 1L;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BASE_URL+"/{taskId}/comments", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .header("Authorization", "Bearer " + accessToken);

        mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                jsonPath("$").exists()
        );

        assertEquals(0, commentRepository.findAll().size());
    }

    @Test
    @Transactional
    void deleteComment_Returns204_WhenCommentExists() throws Exception {
        String email = "admin@admin.com";
        Role role = Role.ADMIN;
        String accessToken = jwtStorageService.generateAccessToken(email, role);
        String content = "content";
        User user = new User();
        user.setName("name");
        user.setEmail(email);
        user.setRole(role);
        user.setPassword("password");
        User savedUser = userRepository.save(user);
        Task task = new Task();
        task.setTitle("title");
        task.setDescription("description");
        task.setPriority(TaskPriority.HIGH);
        task.setStatus(TaskStatus.WAITING);
        task.setCreatedAt(LocalDateTime.now());
        task.setAuthor(savedUser);
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setAuthor(savedUser);
        comment.setTask(taskRepository.save(task));
        long commentId = commentRepository.save(comment).getId();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BASE_URL+"/comments/{commentId}", commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken);

        mockMvc.perform(request).andExpectAll(
                status().isNoContent()
        );

        assertEquals(0, commentRepository.findAll().size());
    }

    @Test
    void deleteComment_Returns404_WhenCommentNotExists() throws Exception {
        String email = "admin@admin.com";
        Role role = Role.ADMIN;
        String accessToken = jwtStorageService.generateAccessToken(email, role);
        long commentId = 1L;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BASE_URL + "/comments/{commentId}", commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken);

        mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                jsonPath("$").exists()
        );

        assertEquals(0, commentRepository.findAll().size());
    }
}