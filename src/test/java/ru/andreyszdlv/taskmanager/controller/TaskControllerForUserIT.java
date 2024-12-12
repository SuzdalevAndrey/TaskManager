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
import ru.andreyszdlv.taskmanager.dto.task.*;
import ru.andreyszdlv.taskmanager.enums.Role;
import ru.andreyszdlv.taskmanager.enums.TaskPriority;
import ru.andreyszdlv.taskmanager.enums.TaskStatus;
import ru.andreyszdlv.taskmanager.mapper.TaskMapper;
import ru.andreyszdlv.taskmanager.model.Task;
import ru.andreyszdlv.taskmanager.model.User;
import ru.andreyszdlv.taskmanager.repository.TaskRepository;
import ru.andreyszdlv.taskmanager.repository.UserRepository;
import ru.andreyszdlv.taskmanager.service.JwtStorageService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@Testcontainers
public class TaskControllerForUserIT extends BaseIT{

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
    TaskRepository taskRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TaskMapper taskMapper;

    String BASE_URL = "/api/tasks";

    @Test
    @Transactional
    void getAllTasks_ReturnsListTasks_WhenTaskExistsAndUserTheseTaskAssignee() throws Exception {
        String emailAssignee = "user@user.com";
        Role roleAssignee = Role.USER;
        User assignee = new User();
        assignee.setName("user");
        assignee.setEmail(emailAssignee);
        assignee.setRole(roleAssignee);
        assignee.setPassword("password");
        userRepository.save(assignee);
        User author = new User();
        author.setName("admin");
        author.setEmail("admin@admin.com");
        author.setRole(Role.ADMIN);
        author.setPassword("password");
        userRepository.save(author);
        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setDescription("Task 1");
        task1.setStatus(TaskStatus.WAITING);
        task1.setPriority(TaskPriority.HIGH);
        task1.setCreatedAt(LocalDateTime.now());
        task1.setAuthor(author);
        task1.setAssignee(assignee);
        Task savedTask1 = taskRepository.save(task1);
        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setDescription("Task 2");
        task2.setStatus(TaskStatus.COMPLETED);
        task2.setPriority(TaskPriority.LOW);
        task2.setCreatedAt(LocalDateTime.now());
        task2.setAuthor(author);
        task2.setAssignee(assignee);
        Task savedTask2 = taskRepository.save(task2);
        String accessToken = jwtStorageService.generateAccessToken(emailAssignee, roleAssignee);
        List<TaskDto> expectedTaskDtos = List.of(
                taskMapper.toTaskDto(savedTask1),
                taskMapper.toTaskDto(savedTask2)
        );
        String expectedJson = objectMapper.writeValueAsString(expectedTaskDtos);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BASE_URL)
                .header("Authorization", "Bearer " + accessToken);

        mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                content().json(expectedJson)
        );
    }

    @Test
    void getAllTasks_ReturnsEmptyListTasks_WhenTasksNotExistWhereUserAssignee() throws Exception {
        String emailAssignee = "user@user.com";
        Role roleAssignee = Role.USER;
        String accessToken = jwtStorageService.generateAccessToken(emailAssignee, roleAssignee);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BASE_URL)
                .header("Authorization", "Bearer " + accessToken);

        mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                content().json("""
                                        []
                                        """)
        );
    }

    @Test
    @Transactional
    void getTaskById_ReturnsTask_WhenTaskExistsAndUserAssigneeThisTask() throws Exception {
        String emailAssignee = "user@user.com";
        Role roleAssignee = Role.USER;
        User assignee = new User();
        assignee.setName("user");
        assignee.setEmail(emailAssignee);
        assignee.setRole(roleAssignee);
        assignee.setPassword("password");
        userRepository.save(assignee);
        User author = new User();
        author.setName("admin");
        author.setEmail("admin@admin.com");
        author.setRole(Role.ADMIN);
        author.setPassword("password");
        userRepository.save(author);
        Task task = new Task();
        task.setTitle("Task 1");
        task.setDescription("Task 1");
        task.setStatus(TaskStatus.WAITING);
        task.setPriority(TaskPriority.HIGH);
        task.setCreatedAt(LocalDateTime.now());
        task.setAuthor(author);
        task.setAssignee(assignee);
        Task savedTask = taskRepository.save(task);
        String accessToken = jwtStorageService.generateAccessToken(emailAssignee, roleAssignee);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BASE_URL+"/{id}", savedTask.getId())
                .header("Authorization", "Bearer " + accessToken);

        String responseString = mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse().getContentAsString();
        TaskDto responseDto = objectMapper.readValue(responseString, TaskDto.class);

        assertNotNull(responseDto);
        assertEquals(taskMapper.toTaskDto(savedTask), responseDto);
    }

    @Test
    void getTaskById_Return404_WhenTaskNotExists() throws Exception {
        String email = "user@user.com";
        Role role = Role.USER;
        long taskId = 1L;
        String accessToken = jwtStorageService.generateAccessToken(email, role);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BASE_URL+"/{id}", taskId)
                .header("Authorization", "Bearer " + accessToken);

        mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                jsonPath("$").exists()
        );
    }

    @Test
    @Transactional
    void getTaskById_Return403_WhenTaskExistsAndUserNotAssigneeThisTask() throws Exception {
        String emailAssignee = "user@user.com";
        Role roleAssignee = Role.USER;
        User author = new User();
        author.setName("admin");
        author.setEmail("admin@admin.com");
        author.setRole(Role.ADMIN);
        author.setPassword("password");
        userRepository.save(author);
        Task task = new Task();
        task.setTitle("Task 1");
        task.setDescription("Task 1");
        task.setStatus(TaskStatus.WAITING);
        task.setPriority(TaskPriority.HIGH);
        task.setCreatedAt(LocalDateTime.now());
        task.setAuthor(author);
        long taskId = taskRepository.save(task).getId();
        String accessToken = jwtStorageService.generateAccessToken(emailAssignee, roleAssignee);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BASE_URL+"/{id}", taskId)
                .header("Authorization", "Bearer " + accessToken);

        mockMvc.perform(request).andExpectAll(
                status().isForbidden(),
                content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                jsonPath("$").exists()
        );
    }

    @Test
    @Transactional
    void createTask_Returns403() throws Exception {
        String email = "user@user.com";
        Role role = Role.USER;
        User user = new User();
        user.setName("user");
        user.setEmail(email);
        user.setRole(role);
        user.setPassword("password");
        userRepository.save(user);
        CreateTaskRequestDto requestDto = new CreateTaskRequestDto(
                "title",
                "description",
                TaskPriority.LOW.name(),
                TaskStatus.COMPLETED.name(),
                null
        );
        String accessToken = jwtStorageService.generateAccessToken(email, role);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .header("Authorization", "Bearer " + accessToken);

        mockMvc.perform(request).andExpectAll(
                status().isForbidden()
        );

        assertEquals(0, taskRepository.count());
    }

    @Test
    @Transactional
    void updateTaskPartial_Response403() throws Exception {
        String emailAssignee = "user@user.com";
        Role roleAssignee = Role.USER;
        User author = new User();
        author.setName("admin");
        author.setEmail("admin@admin.com");
        author.setRole(Role.ADMIN);
        author.setPassword("password");
        userRepository.save(author);
        Task task = new Task();
        task.setTitle("Task 1");
        task.setDescription("Task 1");
        task.setStatus(TaskStatus.WAITING);
        task.setPriority(TaskPriority.HIGH);
        task.setCreatedAt(LocalDateTime.now());
        task.setAuthor(author);
        long taskId = taskRepository.save(task).getId();
        UpdateTaskPartialRequestDto requestDto = new UpdateTaskPartialRequestDto(
                "title",
                "description",
                "Priority",
                null,
                null
        );
        String accessToken = jwtStorageService.generateAccessToken(emailAssignee, roleAssignee);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch(BASE_URL+"/{id}", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .header("Authorization", "Bearer " + accessToken);

        mockMvc.perform(request).andExpectAll(
                status().isForbidden()
        );

        Task validatedTask = taskRepository.findById(taskId).get();
        assertEquals(task.getTitle(), validatedTask.getTitle());
        assertEquals(task.getDescription(), validatedTask.getDescription());
        assertEquals(task.getPriority(), validatedTask.getPriority());
        assertEquals(task.getStatus(), validatedTask.getStatus());
        assertEquals(task.getCreatedAt(), validatedTask.getCreatedAt());
    }

    @Test
    @Transactional
    void updateStatusTask_Returns200_WhenTaskExistsAndUserAssigneeThisTask() throws Exception {
        String emailAssignee = "user@user.com";
        Role roleAssignee = Role.USER;
        User assignee = new User();
        assignee.setName("assignee");
        assignee.setEmail(emailAssignee);
        assignee.setRole(roleAssignee);
        assignee.setPassword("password");
        userRepository.save(assignee);
        User author = new User();
        author.setName("admin");
        author.setEmail("admin@admin.com");
        author.setRole(Role.ADMIN);
        author.setPassword("password");
        userRepository.save(author);
        Task task = new Task();
        task.setTitle("Task 1");
        task.setDescription("Task 1");
        task.setStatus(TaskStatus.WAITING);
        task.setPriority(TaskPriority.HIGH);
        task.setCreatedAt(LocalDateTime.now());
        task.setAuthor(author);
        task.setAssignee(assignee);
        long taskId = taskRepository.save(task).getId();
        String newStatus = TaskStatus.COMPLETED.name();
        UpdateStatusRequestDto requestDto = new UpdateStatusRequestDto(newStatus);
        String accessToken = jwtStorageService.generateAccessToken(emailAssignee, roleAssignee);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch(BASE_URL+"/{id}/status", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .header("Authorization", "Bearer " + accessToken);

        String responseString = mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse().getContentAsString();
        TaskDto responseDto = objectMapper.readValue(responseString, TaskDto.class);

        assertNotNull(responseDto);
        Task updatedTask = taskRepository.findById(taskId).get();
        assertEquals(task.getTitle(), updatedTask.getTitle());
        assertEquals(task.getDescription(), updatedTask.getDescription());
        assertEquals(task.getPriority(), updatedTask.getPriority());
        assertEquals(task.getCreatedAt(), updatedTask.getCreatedAt());
        assertEquals(newStatus, updatedTask.getStatus().name());
    }

    @Test
    @Transactional
    void updateStatusTask_Returns400_WhenDataInvalid() throws Exception {
        String emailAssignee = "user@user.com";
        Role roleAssignee = Role.USER;
        User author = new User();
        author.setName("admin");
        author.setEmail("admin@admin.com");
        author.setRole(Role.ADMIN);
        author.setPassword("password");
        userRepository.save(author);
        Task task = new Task();
        task.setTitle("Task 1");
        task.setDescription("Task 1");
        task.setStatus(TaskStatus.WAITING);
        task.setPriority(TaskPriority.HIGH);
        task.setCreatedAt(LocalDateTime.now());
        task.setAuthor(author);
        long taskId = taskRepository.save(task).getId();
        String newStatus = "Compl";
        UpdateStatusRequestDto requestDto = new UpdateStatusRequestDto(newStatus);
        String accessToken = jwtStorageService.generateAccessToken(emailAssignee, roleAssignee);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch(BASE_URL+"/{id}/status", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .header("Authorization", "Bearer " + accessToken);

        mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                jsonPath("$").exists()
        );

        Task validatedTask = taskRepository.findById(taskId).get();
        assertEquals(task.getTitle(), validatedTask.getTitle());
        assertEquals(task.getDescription(), validatedTask.getDescription());
        assertEquals(task.getPriority(), validatedTask.getPriority());
        assertEquals(task.getStatus(), validatedTask.getStatus());
        assertEquals(task.getCreatedAt(), validatedTask.getCreatedAt());
    }

    @Test
    void updateStatusTask_Returns404_WhenDataIsValidAndTaskNotExists() throws Exception {
        String emailAssignee = "user@user.com";
        Role roleAssignee = Role.USER;
        long taskId = 1L;
        String newStatus = TaskStatus.WAITING.name();
        UpdateStatusRequestDto requestDto = new UpdateStatusRequestDto(newStatus);
        String accessToken = jwtStorageService.generateAccessToken(emailAssignee, roleAssignee);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch(BASE_URL+"/{id}/status", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .header("Authorization", "Bearer " + accessToken);

        mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                jsonPath("$").exists()
        );
    }

    @Test
    @Transactional
    void updateStatusTask_Returns403_WhenTaskExistsAndUserNotAssigneeThisTask() throws Exception {
        String emailAssignee = "user@user.com";
        Role roleAssignee = Role.USER;
        User author = new User();
        author.setName("admin");
        author.setEmail("admin@admin.com");
        author.setRole(Role.ADMIN);
        author.setPassword("password");
        userRepository.save(author);
        Task task = new Task();
        task.setTitle("Task 1");
        task.setDescription("Task 1");
        task.setStatus(TaskStatus.WAITING);
        task.setPriority(TaskPriority.HIGH);
        task.setCreatedAt(LocalDateTime.now());
        task.setAuthor(author);
        long taskId = taskRepository.save(task).getId();
        String newStatus = TaskStatus.COMPLETED.name();
        UpdateStatusRequestDto requestDto = new UpdateStatusRequestDto(newStatus);
        String accessToken = jwtStorageService.generateAccessToken(emailAssignee, roleAssignee);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch(BASE_URL+"/{id}/status", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .header("Authorization", "Bearer " + accessToken);

        mockMvc.perform(request).andExpectAll(
                status().isForbidden(),
                content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                jsonPath("$").exists()
        );

        Task validatedTask = taskRepository.findById(taskId).get();
        assertEquals(task.getTitle(), validatedTask.getTitle());
        assertEquals(task.getDescription(), validatedTask.getDescription());
        assertEquals(task.getPriority(), validatedTask.getPriority());
        assertEquals(task.getCreatedAt(), validatedTask.getCreatedAt());
        assertEquals(task.getStatus(), validatedTask.getStatus());
    }

    @Test
    @Transactional
    void updatePriorityTask_Returns403() throws Exception {
        String emailAssignee = "user@user.com";
        Role roleAssignee = Role.USER;
        User author = new User();
        author.setName("admin");
        author.setEmail("admin@admin.com");
        author.setRole(Role.ADMIN);
        author.setPassword("password");
        userRepository.save(author);
        Task task = new Task();
        task.setTitle("Task 1");
        task.setDescription("Task 1");
        task.setStatus(TaskStatus.WAITING);
        task.setPriority(TaskPriority.HIGH);
        task.setCreatedAt(LocalDateTime.now());
        task.setAuthor(author);
        long taskId = taskRepository.save(task).getId();
        String newPriority = TaskPriority.LOW.name();
        UpdatePriorityRequestDto requestDto = new UpdatePriorityRequestDto(newPriority);
        String accessToken = jwtStorageService.generateAccessToken(emailAssignee, roleAssignee);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch(BASE_URL+"/{id}/priority", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .header("Authorization", "Bearer " + accessToken);

        mockMvc.perform(request).andExpectAll(
                status().isForbidden()
        );

        Task validatedTask = taskRepository.findById(taskId).get();
        assertEquals(task.getTitle(), validatedTask.getTitle());
        assertEquals(task.getDescription(), validatedTask.getDescription());
        assertEquals(task.getPriority(), validatedTask.getPriority());
        assertEquals(task.getStatus(), validatedTask.getStatus());
        assertEquals(task.getCreatedAt(), validatedTask.getCreatedAt());
    }

    @Test
    @Transactional
    void updateAssigneeTask_Returns403() throws Exception {
        String emailAssignee = "user@user.com";
        Role roleAssignee = Role.USER;
        User author = new User();
        author.setName("admin");
        author.setEmail("admin@admin.com");
        author.setRole(Role.ADMIN);
        author.setPassword("password");
        userRepository.save(author);
        Task task = new Task();
        task.setTitle("Task 1");
        task.setDescription("Task 1");
        task.setStatus(TaskStatus.WAITING);
        task.setPriority(TaskPriority.HIGH);
        task.setCreatedAt(LocalDateTime.now());
        task.setAuthor(author);
        long taskId = taskRepository.save(task).getId();
        long newAssigneeId = 2L;
        UpdateAssigneeRequestDto requestDto = new UpdateAssigneeRequestDto(newAssigneeId);
        String accessToken = jwtStorageService.generateAccessToken(emailAssignee, roleAssignee);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch(BASE_URL+"/{id}/assignee", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .header("Authorization", "Bearer " + accessToken);

        mockMvc.perform(request).andExpectAll(
                status().isForbidden()
        );

        Task validatedTask = taskRepository.findById(taskId).get();
        assertEquals(task.getTitle(), validatedTask.getTitle());
        assertEquals(task.getDescription(), validatedTask.getDescription());
        assertEquals(task.getPriority(), validatedTask.getPriority());
        assertEquals(task.getStatus(), validatedTask.getStatus());
        assertEquals(task.getCreatedAt(), validatedTask.getCreatedAt());
    }

    @Test
    @Transactional
    void deleteTask_Returns403() throws Exception {
        String emailAssignee = "user@user.com";
        Role roleAssignee = Role.USER;
        User author = new User();
        author.setName("admin");
        author.setEmail("admin@admin.com");
        author.setRole(Role.ADMIN);
        author.setPassword("password");
        userRepository.save(author);
        Task task = new Task();
        task.setTitle("Task 1");
        task.setDescription("Task 1");
        task.setStatus(TaskStatus.WAITING);
        task.setPriority(TaskPriority.HIGH);
        task.setCreatedAt(LocalDateTime.now());
        task.setAuthor(author);
        long taskId = taskRepository.save(task).getId();
        String accessToken = jwtStorageService.generateAccessToken(emailAssignee, roleAssignee);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BASE_URL+"/{id}/assignee", taskId)
                .header("Authorization", "Bearer " + accessToken);

        mockMvc.perform(request).andExpectAll(
                status().isForbidden()
        );

        assertTrue(taskRepository.existsById(taskId));
    }
}
