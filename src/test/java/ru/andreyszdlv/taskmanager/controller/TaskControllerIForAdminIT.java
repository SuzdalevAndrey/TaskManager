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
class TaskControllerIForAdminIT extends BaseIT {

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
    void getAllTasks_ReturnsListTasks_WhenTasksExist() throws Exception {
        String email = "admin@admin.com";
        Role role = Role.ADMIN;
        User user = new User();
        user.setName("admin");
        user.setEmail(email);
        user.setRole(role);
        user.setPassword("password");
        userRepository.save(user);
        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setDescription("Task 1");
        task1.setStatus(TaskStatus.WAITING);
        task1.setPriority(TaskPriority.HIGH);
        task1.setCreatedAt(LocalDateTime.now());
        task1.setAuthor(user);
        Task savedTask1 = taskRepository.save(task1);
        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setDescription("Task 2");
        task2.setStatus(TaskStatus.COMPLETED);
        task2.setPriority(TaskPriority.LOW);
        task2.setCreatedAt(LocalDateTime.now());
        task2.setAuthor(user);
        Task savedTask2 = taskRepository.save(task2);
        String accessToken = jwtStorageService.generateAccessToken(email, role);
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
    void getAllTasks_ReturnsEmptyListTasks_WhenTasksNotExist() throws Exception {
        String email = "admin@admin.com";
        Role role = Role.ADMIN;
        String accessToken = jwtStorageService.generateAccessToken(email, role);
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
    void getTaskById_ReturnsTask_WhenTaskExists() throws Exception {
        String email = "admin@admin.com";
        Role role = Role.ADMIN;
        User user = new User();
        user.setName("admin");
        user.setEmail(email);
        user.setRole(role);
        user.setPassword("password");
        userRepository.save(user);
        Task task = new Task();
        task.setTitle("Task 1");
        task.setDescription("Task 1");
        task.setStatus(TaskStatus.WAITING);
        task.setPriority(TaskPriority.HIGH);
        task.setCreatedAt(LocalDateTime.now());
        task.setAuthor(user);
        Task savedTask1 = taskRepository.save(task);
        String accessToken = jwtStorageService.generateAccessToken(email, role);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BASE_URL+"/{id}", savedTask1.getId())
                .header("Authorization", "Bearer " + accessToken);

        String responseString = mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse().getContentAsString();
        TaskDto responseDto = objectMapper.readValue(responseString, TaskDto.class);

        assertNotNull(responseDto);
        assertEquals(taskMapper.toTaskDto(savedTask1), responseDto);
    }

    @Test
    void getTaskById_Return404_WhenTaskNotExists() throws Exception {
        String email = "admin@admin.com";
        Role role = Role.ADMIN;
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
    void createTask_Returns201_WhenDataIsValid() throws Exception {
        String email = "admin@admin.com";
        Role role = Role.ADMIN;
        User user = new User();
        user.setName("admin");
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

        String responseString = mockMvc.perform(request).andExpectAll(
                status().isCreated(),
                content().contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse().getContentAsString();
        TaskDto responseDto = objectMapper.readValue(responseString, TaskDto.class);

        assertNotNull(responseDto);
        assertEquals(requestDto.title(), responseDto.title());
        assertEquals(requestDto.description(), responseDto.description());
        assertEquals(requestDto.priority(), responseDto.priority().name());
        assertEquals(requestDto.status(), responseDto.status().name());
        Task task = taskRepository.findById(responseDto.id()).get();
        assertEquals(requestDto.title(), task.getTitle());
        assertEquals(requestDto.description(), task.getDescription());
        assertEquals(requestDto.priority(), task.getPriority().name());
        assertEquals(requestDto.status(), task.getStatus().name());
    }

    @Test
    void createTask_Returns400_WhenDataInvalid() throws Exception {
        String email = "admin@admin.com";
        Role role = Role.ADMIN;
        CreateTaskRequestDto requestDto = new CreateTaskRequestDto(
                "  ",
                "    ",
                TaskPriority.LOW.name(),
                "Status",
                null
        );
        String accessToken = jwtStorageService.generateAccessToken(email, role);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .header("Authorization", "Bearer " + accessToken);

        mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                jsonPath("$").exists()
        );

        assertEquals(0, taskRepository.count());
    }

    @Test
    @Transactional
    void updateTaskPartial_Response200_WhenDataIsValidAndTaskExists() throws Exception {
        String email = "admin@admin.com";
        Role role = Role.ADMIN;
        User user = new User();
        user.setName("admin");
        user.setEmail(email);
        user.setRole(role);
        user.setPassword("password");
        userRepository.save(user);
        Task task = new Task();
        task.setTitle("Task 1");
        task.setDescription("Task 1");
        task.setStatus(TaskStatus.WAITING);
        task.setPriority(TaskPriority.HIGH);
        task.setCreatedAt(LocalDateTime.now());
        task.setAuthor(user);
        long taskId = taskRepository.save(task).getId();
        UpdateTaskPartialRequestDto requestDto = new UpdateTaskPartialRequestDto(
                null,
                "description",
                TaskPriority.HIGH.name(),
                null,
                null
        );
        String accessToken = jwtStorageService.generateAccessToken(email, role);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch(BASE_URL+"/{id}", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .header("Authorization", "Bearer " + accessToken);

        String responseString = mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse().getContentAsString();
        TaskDto responseDto = objectMapper.readValue(responseString, TaskDto.class);

        assertNotNull(responseDto);
        assertEquals(task.getTitle(), responseDto.title());
        assertEquals(requestDto.description(), responseDto.description());
        assertEquals(requestDto.priority(), responseDto.priority().name());
        assertEquals(task.getStatus(), responseDto.status());
        assertEquals(task.getCreatedAt(), responseDto.createdAt());
        Task updatedTask = taskRepository.findById(taskId).get();
        assertEquals(task.getTitle(), updatedTask.getTitle());
        assertEquals(requestDto.description(), updatedTask.getDescription());
        assertEquals(requestDto.priority(), updatedTask.getPriority().name());
        assertEquals(task.getStatus(), updatedTask.getStatus());
        assertEquals(task.getCreatedAt(), updatedTask.getCreatedAt());
    }

    @Test
    @Transactional
    void updateTaskPartial_Response400_WhenDataInvalid() throws Exception {
        String email = "admin@admin.com";
        Role role = Role.ADMIN;
        User user = new User();
        user.setName("admin");
        user.setEmail(email);
        user.setRole(role);
        user.setPassword("password");
        userRepository.save(user);
        Task task = new Task();
        task.setTitle("Task 1");
        task.setDescription("Task 1");
        task.setStatus(TaskStatus.WAITING);
        task.setPriority(TaskPriority.HIGH);
        task.setCreatedAt(LocalDateTime.now());
        task.setAuthor(user);
        long taskId = taskRepository.save(task).getId();
        UpdateTaskPartialRequestDto requestDto = new UpdateTaskPartialRequestDto(
                null,
                "     ",
                "Priority",
                null,
                null
        );
        String accessToken = jwtStorageService.generateAccessToken(email, role);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch(BASE_URL+"/{id}", taskId)
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
    void updateTaskPartial_Response404_WhenDataIsValidAndTaskNotExists() throws Exception {
        String email = "admin@admin.com";
        Role role = Role.ADMIN;
        long taskId = 1L;
        UpdateTaskPartialRequestDto requestDto = new UpdateTaskPartialRequestDto(
                null,
                "description",
                TaskPriority.HIGH.name(),
                null,
                null
        );
        String accessToken = jwtStorageService.generateAccessToken(email, role);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch(BASE_URL+"/{id}", taskId)
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
    void updateStatusTask_Returns200_WhenDataIsValidAndTaskExists() throws Exception {
        String email = "admin@admin.com";
        Role role = Role.ADMIN;
        User user = new User();
        user.setName("admin");
        user.setEmail(email);
        user.setRole(role);
        user.setPassword("password");
        userRepository.save(user);
        Task task = new Task();
        task.setTitle("Task 1");
        task.setDescription("Task 1");
        task.setStatus(TaskStatus.WAITING);
        task.setPriority(TaskPriority.HIGH);
        task.setCreatedAt(LocalDateTime.now());
        task.setAuthor(user);
        long taskId = taskRepository.save(task).getId();
        String newStatus = TaskStatus.COMPLETED.name();
        UpdateStatusRequestDto requestDto = new UpdateStatusRequestDto(newStatus);
        String accessToken = jwtStorageService.generateAccessToken(email, role);
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
        String email = "admin@admin.com";
        Role role = Role.ADMIN;
        User user = new User();
        user.setName("admin");
        user.setEmail(email);
        user.setRole(role);
        user.setPassword("password");
        userRepository.save(user);
        Task task = new Task();
        task.setTitle("Task 1");
        task.setDescription("Task 1");
        task.setStatus(TaskStatus.WAITING);
        task.setPriority(TaskPriority.HIGH);
        task.setCreatedAt(LocalDateTime.now());
        task.setAuthor(user);
        long taskId = taskRepository.save(task).getId();
        String newStatus = "Compl";
        UpdateStatusRequestDto requestDto = new UpdateStatusRequestDto(newStatus);
        String accessToken = jwtStorageService.generateAccessToken(email, role);
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
        String email = "admin@admin.com";
        Role role = Role.ADMIN;
        long taskId = 1L;
        String newStatus = TaskStatus.WAITING.name();
        UpdateStatusRequestDto requestDto = new UpdateStatusRequestDto(newStatus);
        String accessToken = jwtStorageService.generateAccessToken(email, role);
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
    void updatePriorityTask_Returns200_WhenDataIsValidAndTaskExists() throws Exception {
        String email = "admin@admin.com";
        Role role = Role.ADMIN;
        User user = new User();
        user.setName("admin");
        user.setEmail(email);
        user.setRole(role);
        user.setPassword("password");
        userRepository.save(user);
        Task task = new Task();
        task.setTitle("Task 1");
        task.setDescription("Task 1");
        task.setStatus(TaskStatus.WAITING);
        task.setPriority(TaskPriority.HIGH);
        task.setCreatedAt(LocalDateTime.now());
        task.setAuthor(user);
        long taskId = taskRepository.save(task).getId();
        String newPriority = TaskPriority.LOW.name();
        UpdatePriorityRequestDto requestDto = new UpdatePriorityRequestDto(newPriority);
        String accessToken = jwtStorageService.generateAccessToken(email, role);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch(BASE_URL+"/{id}/priority", taskId)
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
        assertEquals(task.getStatus(), updatedTask.getStatus());
        assertEquals(task.getCreatedAt(), updatedTask.getCreatedAt());
        assertEquals(newPriority, updatedTask.getPriority().name());
    }

    @Test
    @Transactional
    void updatePriorityTask_Returns400_WhenDataInvalid() throws Exception {
        String email = "admin@admin.com";
        Role role = Role.ADMIN;
        User user = new User();
        user.setName("admin");
        user.setEmail(email);
        user.setRole(role);
        user.setPassword("password");
        userRepository.save(user);
        Task task = new Task();
        task.setTitle("Task 1");
        task.setDescription("Task 1");
        task.setStatus(TaskStatus.WAITING);
        task.setPriority(TaskPriority.HIGH);
        task.setCreatedAt(LocalDateTime.now());
        task.setAuthor(user);
        long taskId = taskRepository.save(task).getId();
        String newPriority = "L";
        UpdatePriorityRequestDto requestDto = new UpdatePriorityRequestDto(newPriority);
        String accessToken = jwtStorageService.generateAccessToken(email, role);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch(BASE_URL+"/{id}/priority", taskId)
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
    void updatePriorityTask_Returns404_WhenDataIsValidAndTaskNotExists() throws Exception {
        String email = "admin@admin.com";
        Role role = Role.ADMIN;
        long taskId = 1L;
        String newPriority = TaskPriority.MEDIUM.name();
        UpdatePriorityRequestDto requestDto = new UpdatePriorityRequestDto(newPriority);
        String accessToken = jwtStorageService.generateAccessToken(email, role);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch(BASE_URL+"/{id}/priority", taskId)
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
    void updateAssigneeTask_Returns200_WhenDataIsValidAndTaskExistsAndAssigneeExists() throws Exception {
        String emailAdmin = "admin@admin.com";
        Role roleAdmin = Role.ADMIN;
        User admin = new User();
        admin.setName("admin");
        admin.setEmail(emailAdmin);
        admin.setRole(roleAdmin);
        admin.setPassword("password");
        userRepository.save(admin);
        User user = new User();
        user.setName("user");
        user.setEmail("user@user.com");
        user.setRole(Role.USER);
        user.setPassword("password");
        long assigneeId = userRepository.save(user).getId();
        Task task = new Task();
        task.setTitle("Task 1");
        task.setDescription("Task 1");
        task.setStatus(TaskStatus.WAITING);
        task.setPriority(TaskPriority.HIGH);
        task.setCreatedAt(LocalDateTime.now());
        task.setAuthor(admin);
        long taskId = taskRepository.save(task).getId();
        UpdateAssigneeRequestDto requestDto = new UpdateAssigneeRequestDto(assigneeId);
        String accessToken = jwtStorageService.generateAccessToken(emailAdmin, roleAdmin);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch(BASE_URL+"/{id}/assignee", taskId)
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
        assertEquals(task.getStatus(), updatedTask.getStatus());
        assertEquals(task.getPriority(), updatedTask.getPriority());
        assertEquals(task.getCreatedAt(), updatedTask.getCreatedAt());
        assertEquals(assigneeId, updatedTask.getAssignee().getId());
    }

    @Test
    @Transactional
    void updateAssigneeTask_Returns400_WhenDataInvalid() throws Exception {
        String emailAdmin = "admin@admin.com";
        Role roleAdmin = Role.ADMIN;
        User admin = new User();
        admin.setName("admin");
        admin.setEmail(emailAdmin);
        admin.setRole(roleAdmin);
        admin.setPassword("password");
        userRepository.save(admin);
        Task task = new Task();
        task.setTitle("Task 1");
        task.setDescription("Task 1");
        task.setStatus(TaskStatus.WAITING);
        task.setPriority(TaskPriority.HIGH);
        task.setCreatedAt(LocalDateTime.now());
        task.setAuthor(admin);
        long taskId = taskRepository.save(task).getId();
        UpdateAssigneeRequestDto requestDto = new UpdateAssigneeRequestDto(null);
        String accessToken = jwtStorageService.generateAccessToken(emailAdmin, roleAdmin);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch(BASE_URL+"/{id}/assignee", taskId)
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
        assertNull(validatedTask.getAssignee());
    }

    @Test
    @Transactional
    void updateAssigneeTask_Returns404_WhenDataIsValidAndTaskNotExists() throws Exception {
        String email = "admin@admin.com";
        Role role = Role.ADMIN;
        long taskId = 1L;
        User user = new User();
        user.setName("user");
        user.setEmail("user@user.com");
        user.setRole(Role.USER);
        user.setPassword("password");
        long assigneeId = userRepository.save(user).getId();
        UpdateAssigneeRequestDto requestDto = new UpdateAssigneeRequestDto(assigneeId);
        String accessToken = jwtStorageService.generateAccessToken(email, role);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch(BASE_URL+"/{id}/assignee", taskId)
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
    void updateAssigneeTask_Returns404_WhenDataIsValidAndTaskExistsAndAssigneeNotExists() throws Exception {
        String emailAdmin = "admin@admin.com";
        Role roleAdmin = Role.ADMIN;
        User admin = new User();
        admin.setName("admin");
        admin.setEmail(emailAdmin);
        admin.setRole(roleAdmin);
        admin.setPassword("password");
        userRepository.save(admin);
        Task task = new Task();
        task.setTitle("Task 1");
        task.setDescription("Task 1");
        task.setStatus(TaskStatus.WAITING);
        task.setPriority(TaskPriority.HIGH);
        task.setCreatedAt(LocalDateTime.now());
        task.setAuthor(admin);
        long taskId = taskRepository.save(task).getId();
        long assigneeId = 1L;
        UpdateAssigneeRequestDto requestDto = new UpdateAssigneeRequestDto(assigneeId);
        String accessToken = jwtStorageService.generateAccessToken(emailAdmin, roleAdmin);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch(BASE_URL+"/{id}/assignee", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .header("Authorization", "Bearer " + accessToken);

        mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                jsonPath("$").exists()
        );

        Task validatedTask = taskRepository.findById(taskId).get();
        assertEquals(task.getTitle(), validatedTask.getTitle());
        assertEquals(task.getDescription(), validatedTask.getDescription());
        assertEquals(task.getPriority(), validatedTask.getPriority());
        assertEquals(task.getStatus(), validatedTask.getStatus());
        assertEquals(task.getCreatedAt(), validatedTask.getCreatedAt());
        assertNull(validatedTask.getAssignee());
    }

    @Test
    @Transactional
    void deleteTask_Returns204_WhenTaskExists() throws Exception {
        String emailAdmin = "admin@admin.com";
        Role roleAdmin = Role.ADMIN;
        User admin = new User();
        admin.setName("admin");
        admin.setEmail(emailAdmin);
        admin.setRole(roleAdmin);
        admin.setPassword("password");
        userRepository.save(admin);
        Task task = new Task();
        task.setTitle("Task 1");
        task.setDescription("Task 1");
        task.setStatus(TaskStatus.WAITING);
        task.setPriority(TaskPriority.HIGH);
        task.setCreatedAt(LocalDateTime.now());
        task.setAuthor(admin);
        long taskId = taskRepository.save(task).getId();
        String accessToken = jwtStorageService.generateAccessToken(emailAdmin, roleAdmin);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BASE_URL+"/{id}", taskId)
                .header("Authorization", "Bearer " + accessToken);

        mockMvc.perform(request).andExpectAll(
                status().isNoContent()
        );

        assertFalse(taskRepository.existsById(taskId));
    }

    @Test
    void deleteTask_Returns404_WhenTaskNotExists() throws Exception {
        String emailAdmin = "admin@admin.com";
        Role roleAdmin = Role.ADMIN;
        long taskId = 1L;
        String accessToken = jwtStorageService.generateAccessToken(emailAdmin, roleAdmin);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BASE_URL+"/{id}", taskId)
                .header("Authorization", "Bearer " + accessToken);

        mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                jsonPath("$").exists()
        );

        assertFalse(taskRepository.existsById(taskId));
    }
}