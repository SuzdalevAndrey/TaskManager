package ru.andreyszdlv.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import ru.andreyszdlv.taskmanager.dto.auth.*;
import ru.andreyszdlv.taskmanager.enums.Role;
import ru.andreyszdlv.taskmanager.model.User;
import ru.andreyszdlv.taskmanager.repository.UserRepository;
import ru.andreyszdlv.taskmanager.service.JwtExtractorService;
import ru.andreyszdlv.taskmanager.service.JwtGenerateService;
import ru.andreyszdlv.taskmanager.service.JwtStorageService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@Testcontainers
class AuthControllerIT extends BaseIT{

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
    UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtExtractorService jwtExtractorService;

    @Autowired
    JwtStorageService jwtStorageService;

    @Autowired
    JwtGenerateService jwtGenerateService;

    @Autowired
    MockMvc mockMvc;

    String BASE_URL="/api/auth";

    @Test
    @Transactional
    void registerUser_Returns201_WhenDataValidAndUserNoExists() throws Exception {
        String name = "name";
        String email = "test@test.ru";
        String password = "password";
        RegisterUserRequestDto requestDto = new RegisterUserRequestDto(name, email, password);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BASE_URL+"/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto));

        String responseString = mockMvc.perform(request)
                .andExpectAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON)
                ).andReturn().getResponse().getContentAsString();
        RegisterUserResponseDto responseDto =
                objectMapper.readValue(responseString, RegisterUserResponseDto.class);

        assertNotNull(responseDto);
        assertEquals(responseDto.name(), requestDto.name());
        assertEquals(responseDto.email(), requestDto.email());
        User user = userRepository.findById(responseDto.id()).orElse(null);
        assertNotNull(user);
        assertEquals(requestDto.name(), user.getName());
        assertEquals(requestDto.email(), user.getEmail());
        assertTrue(passwordEncoder.matches(requestDto.password(), user.getPassword()));
    }

    @Test
    void registerUser_Returns400_WhenDataInvalid() throws Exception {
        String name = "";
        String email = "testtest.ru";
        String password = "0000";
        RegisterUserRequestDto requestDto = new RegisterUserRequestDto(name, email, password);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BASE_URL+"/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto));

        mockMvc.perform(request)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                );

        User user = userRepository.findByEmail(email).orElse(null);
        assertNull(user);
    }

    @Test
    @Transactional
    void registerUser_Returns409_WhenDataValidAndUserAlreadyExists() throws Exception {
        String name = "name";
        String email = "test@test.ru";
        String password = "password";
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.USER);
        long userId = userRepository.save(user).getId();
        RegisterUserRequestDto requestDto = new RegisterUserRequestDto(name, email, password);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BASE_URL+"/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto));

        mockMvc.perform(request)
                .andExpectAll(
                        status().isConflict(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                );

        List<User> users = userRepository.findAll();
        assertFalse(users.isEmpty());
        assertEquals(userId, users.get(0).getId());
        assertEquals(email, users.get(0).getEmail());
        assertTrue(passwordEncoder.matches(password, users.get(0).getPassword()));
    }

    @Test
    @Transactional
    void loginUser_Returns200_WhenDataValidAndUserRegistered() throws Exception {
        String name = "name";
        String email = "test@test.ru";
        String password = "password";
        Role role = Role.USER;
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        userRepository.save(user);
        LoginRequestDto requestDto = new LoginRequestDto(email, password);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BASE_URL+"/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto));

        String responseString = mockMvc.perform(request)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON)
                ).andReturn().getResponse().getContentAsString();
        LoginResponseDto responseDto = objectMapper.readValue(responseString, LoginResponseDto.class);

        assertNotNull(responseDto);
        assertEquals(email, jwtExtractorService.extractUserEmail(responseDto.accessToken()));
        assertEquals(email, jwtExtractorService.extractUserEmail(responseDto.refreshToken()));
        assertEquals(role, jwtExtractorService.extractRole(responseDto.accessToken()));
        assertEquals(role, jwtExtractorService.extractRole(responseDto.refreshToken()));
    }

    @Test
    void loginUser_Returns400_WhenDataInvalid() throws Exception {
        String email = "invalidEmail.ru";
        String password = "0000";
        LoginRequestDto requestDto = new LoginRequestDto(email, password);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BASE_URL+"/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto));

        mockMvc.perform(request)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$").exists()
                );
    }

    @Test
    void loginUser_Returns401_WhenDataValidAndUserNoRegister() throws Exception {
        String email = "email@email.ru";
        String password = "000000";
        LoginRequestDto requestDto = new LoginRequestDto(email, password);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BASE_URL+"/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto));

        mockMvc.perform(request)
                .andExpectAll(
                        status().isUnauthorized(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$").exists()
                );
    }

    @Test
    @Transactional
    void loginUser_Returns401_WhenDataValidAndUserRegisterAndPasswordNoMatch() throws Exception {
        String name = "name";
        String email = "test@test.ru";
        String passwordUser = "password";
        String passwordRequest = "passwordRequest";
        Role role = Role.USER;
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(passwordUser));
        user.setRole(role);
        userRepository.save(user);
        LoginRequestDto requestDto = new LoginRequestDto(email, passwordRequest);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BASE_URL+"/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto));

        mockMvc.perform(request)
                .andExpectAll(
                        status().isUnauthorized(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$").exists()
                );
    }

    @Test
    void refreshToken_Returns201_WhenDataValidAndTokenValid() throws Exception {
        String email = "email@email.ru";
        Role role = Role.USER;
        String refreshToken = jwtStorageService.generateRefreshToken(email, role);
        String accessToken = jwtStorageService.generateAccessToken(email, role);
        RefreshTokenRequestDto requestDto = new RefreshTokenRequestDto(refreshToken);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BASE_URL+"/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto));

        String responseString = mockMvc.perform(request)
                .andExpectAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON)
                ).andReturn().getResponse().getContentAsString();
        RefreshTokenResponseDto responseDto =
                objectMapper.readValue(responseString, RefreshTokenResponseDto.class);

        assertNotNull(responseDto);
        assertNotEquals(accessToken, responseDto.accessToken());
        assertEquals(refreshToken, responseDto.refreshToken());
        assertEquals(jwtStorageService.getAccessTokenByUserEmail(email), responseDto.accessToken());
        assertEquals(jwtStorageService.getRefreshTokenByUserEmail(email), refreshToken);
        assertEquals(email, jwtExtractorService.extractUserEmail(responseDto.accessToken()));
        assertEquals(role, jwtExtractorService.extractRole(responseDto.accessToken()));
    }

    @Test
    void refreshToken_Returns400_WhenDataInvalid() throws Exception {
        String refreshToken = "";
        RefreshTokenRequestDto requestDto = new RefreshTokenRequestDto(refreshToken);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BASE_URL+"/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto));

        mockMvc.perform(request)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$").exists()
                );
    }

    @Test
    void refreshToken_Returns409_WhenDataValidAndTokenInvalid() throws Exception {
        String email = "email@email.ru";
        Role role = Role.USER;
        String validRefreshToken = jwtStorageService.generateRefreshToken(email, role);
        String accessToken = jwtStorageService.generateAccessToken(email, role);
        String invalidRefreshToken = jwtGenerateService.generateRefreshToken(email, role);
        RefreshTokenRequestDto requestDto = new RefreshTokenRequestDto(invalidRefreshToken);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BASE_URL+"/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto));

        mockMvc.perform(request)
                .andExpectAll(
                        status().isConflict(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$").exists()
                );

        assertEquals(accessToken, jwtStorageService.getAccessTokenByUserEmail(email));
        assertEquals(validRefreshToken, jwtStorageService.getRefreshTokenByUserEmail(email));
    }

    @Test
    void logout_Returns204_WhenTokenValid() throws Exception {
        String email = "email@email.ru";
        Role role = Role.USER;
        String accessToken = jwtStorageService.generateAccessToken(email, role);
        jwtStorageService.generateRefreshToken(email, role);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BASE_URL+"/logout")
                .header("Authorization", "Bearer " + accessToken);

        mockMvc.perform(request)
                .andExpectAll(
                        status().isNoContent()
                );

        assertNull(jwtStorageService.getAccessTokenByUserEmail(email));
        assertNull(jwtStorageService.getRefreshTokenByUserEmail(email));
    }

    @Test
    void logout_Returns401_WhenTokenInvalid() throws Exception {
        String email = "email@email.ru";
        Role role = Role.USER;
        String validAccessToken = jwtStorageService.generateAccessToken(email, role);
        String invalidAccessToken = jwtGenerateService.generateAccessToken(email, role);
        String refreshToken = jwtStorageService.generateRefreshToken(email, role);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BASE_URL+"/logout")
                .header("Authorization", "Bearer " + invalidAccessToken);

        mockMvc.perform(request)
                .andExpectAll(
                        status().isUnauthorized(),
                        jsonPath("$").exists()
                );

        assertEquals(validAccessToken, jwtStorageService.getAccessTokenByUserEmail(email));
        assertEquals(refreshToken, jwtStorageService.getRefreshTokenByUserEmail(email));
    }
}