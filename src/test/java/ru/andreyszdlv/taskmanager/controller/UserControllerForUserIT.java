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
import ru.andreyszdlv.taskmanager.dto.user.UserDto;
import ru.andreyszdlv.taskmanager.enums.Role;
import ru.andreyszdlv.taskmanager.model.User;
import ru.andreyszdlv.taskmanager.repository.UserRepository;
import ru.andreyszdlv.taskmanager.service.JwtStorageService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@Testcontainers
public class UserControllerForUserIT extends BaseIT {

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
    void makeAdmin_Returns403() throws Exception {
        String emailUser1 = "user1@user1.com";
        Role roleUser1 = Role.USER;
        User user2 = new User();
        user2.setName("user2");
        user2.setEmail("user2@user2.com");
        user2.setRole(Role.USER);
        user2.setPassword("password");
        long userId = userRepository.save(user2).getId();
        String accessToken = jwtStorageService.generateAccessToken(emailUser1, roleUser1);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BASE_URL + "/{id}/make-admin", userId)
                .header("Authorization", "Bearer " + accessToken);

        mockMvc.perform(request).andExpect(status().isForbidden());

        User validatedUser = userRepository.findById(userId).get();
        assertEquals(user2.getName(), validatedUser.getName());
        assertEquals(user2.getEmail(), validatedUser.getEmail());
        assertEquals(user2.getPassword(), validatedUser.getPassword());
        assertEquals(Role.USER, validatedUser.getRole());
    }

    @Test
    @Transactional
    void getAllUsers_Returns403() throws Exception {
        String emailUser1 = "user1@user1.com";
        Role roleUser1 = Role.USER;
        User user1 = new User();
        user1.setName("user1");
        user1.setEmail(emailUser1);
        user1.setRole(roleUser1);
        user1.setPassword("password1");
        userRepository.save(user1);
        User user2 = new User();
        user2.setName("user2");
        user2.setEmail("user2@user2.com");
        user2.setRole(Role.USER);
        user2.setPassword("password2");
        userRepository.save(user2);
        String accessToken = jwtStorageService.generateAccessToken(emailUser1, roleUser1);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BASE_URL)
                .header("Authorization", "Bearer " + accessToken);

        mockMvc.perform(request).andExpectAll(
                status().isForbidden()
        );
    }
}
