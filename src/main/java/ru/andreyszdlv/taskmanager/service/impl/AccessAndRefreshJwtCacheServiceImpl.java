package ru.andreyszdlv.taskmanager.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.taskmanager.service.AccessAndRefreshJwtService;
import ru.andreyszdlv.taskmanager.service.JwtSecurityService;

@Service
@RequiredArgsConstructor
public class AccessAndRefreshJwtCacheServiceImpl implements AccessAndRefreshJwtService {

    private final JwtSecurityService jwtSecurityService;

    @CachePut(value = "${spring.redis.accessTokenNameCache}", key = "#userId")
    public String generateAccessToken(long userId, String role){
        return jwtSecurityService.generateToken(userId, role);
    }

    @CachePut(value = "${spring.redis.refreshTokenNameCache}", key = "#userId")
    public String generateRefreshToken(long userId, String role){
        return jwtSecurityService.generateRefreshToken(userId, role);
    }

    @Cacheable(value = "${spring.redis.accessTokenNameCache}", key = "#userId")
    public String getAccessTokenByUserId(long userId){
        return null;
    }

    @Cacheable(value = "${spring.redis.refreshTokenNameCache}", key = "#userId")
    public String getRefreshTokenByUserId(long userId){
        return null;
    }

    @Caching(evict = {
            @CacheEvict(value = "${spring.redis.accessTokenNameCache}", key = "#userId"),
            @CacheEvict(value = "${spring.redis.refreshTokenNameCache}", key = "#userId")
    })
    public void deleteByUserId(long userId){}

}