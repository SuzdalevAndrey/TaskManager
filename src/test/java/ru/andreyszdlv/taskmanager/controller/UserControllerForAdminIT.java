package ru.andreyszdlv.taskmanager.controller;

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
import ru.andreyszdlv.taskmanager.enums.Role;
import ru.andreyszdlv.taskmanager.model.User;
import ru.andreyszdlv.taskmanager.repository.UserRepository;
import ru.andreyszdlv.taskmanager.service.JwtStorageService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@Testcontainers
public class UserControllerForAdminIT extends BaseIT{

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
    UserRepository userRepository;

    String BASE_URL = "/api/users";

    @Test
    @Transactional
    void makeAdmin_Returns200_WhenUserExists() throws Exception {
        String emailAdmin = "admin@admin.com";
        Role roleAdmin = Role.ADMIN;
        User user = new User();
        user.setName("user");
        user.setEmail("user@user.com");
        user.setRole(Role.USER);
        user.setPassword("password");
        long userId = userRepository.save(user).getId();
        String accessToken = jwtStorageService.generateAccessToken(emailAdmin, roleAdmin);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BASE_URL + "/{id}/make-admin", userId)
                .header("Authorization", "Bearer " + accessToken);

        mockMvc.perform(request).andExpectAll(
                status().isOk()
        );

        User updatedUser = userRepository.findById(userId).get();
        assertEquals(user.getName(), updatedUser.getName());
        assertEquals(user.getEmail(), updatedUser.getEmail());
        assertEquals(user.getPassword(), updatedUser.getPassword());
        assertEquals(Role.ADMIN, updatedUser.getRole());
    }

    @Test
    void makeAdmin_Returns404_WhenUserNotExists() throws Exception {
        String emailAdmin = "admin@admin.com";
        Role roleAdmin = Role.ADMIN;
        long userId = 1L;
        String accessToken = jwtStorageService.generateAccessToken(emailAdmin, roleAdmin);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BASE_URL + "/{id}/make-admin", userId)
                .header("Authorization", "Bearer " + accessToken);

        mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                jsonPath("$").exists()
        );
    }
}
