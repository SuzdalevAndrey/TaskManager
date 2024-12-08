package ru.andreyszdlv.taskmanager.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.taskmanager.enums.Role;
import ru.andreyszdlv.taskmanager.service.JwtGenerateService;
import ru.andreyszdlv.taskmanager.service.JwtStorageService;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtCacheStorageServiceImpl implements JwtStorageService {

    private final JwtGenerateService jwtGenerateService;

    @CachePut(value = "${spring.redis.accessTokenNameCache}", key = "#userEmail")
    public String generateAccessToken(String userEmail, Role role){
        return jwtGenerateService.generateAccessToken(userEmail, role);
    }

    @CachePut(value = "${spring.redis.refreshTokenNameCache}", key = "#userEmail")
    public String generateRefreshToken(String userEmail, Role role){
        return jwtGenerateService.generateRefreshToken(userEmail, role);
    }

    @Cacheable(value = "${spring.redis.accessTokenNameCache}", key = "#userEmail")
    public String getAccessTokenByUserEmail(String userEmail){
        log.info("Get access token for userEmail: {}", userEmail);
        return null;
    }

    @Cacheable(value = "${spring.redis.refreshTokenNameCache}", key = "#userEmail")
    public String getRefreshTokenByUserEmail(String userEmail){
        log.info("Get refresh token for userEmail: {}", userEmail);
        return null;
    }

    @Caching(evict = {
            @CacheEvict(value = "${spring.redis.accessTokenNameCache}", key = "#userEmail"),
            @CacheEvict(value = "${spring.redis.refreshTokenNameCache}", key = "#userEmail")
    })
    public void deleteByUserEmail(String userEmail){
        log.info("Delete access and refresh token for userEmail: {}", userEmail);
    }
}