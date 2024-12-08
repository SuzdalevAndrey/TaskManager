package ru.andreyszdlv.taskmanager.service.impl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.taskmanager.enums.Role;
import ru.andreyszdlv.taskmanager.service.JwtGenerateService;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class JwtGenerateServiceImpl implements JwtGenerateService {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Override
    public String generateAccessToken(String userEmail, Role role){
        log.info("Generating access token for userEmail: {}", userEmail);
        return generateToken(userEmail, role);
    }

    @Override
    public String generateRefreshToken(String userEmail, Role role) {
        log.info("Generating refresh token for userEmail: {}", userEmail);
        return generateToken(userEmail, role);
    }

    private SecretKey getSigningKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String generateToken(String userEmail, Role role){
        String uniqueId = UUID.randomUUID().toString();

        return Jwts.builder()
                .claims(Map.of(
                        "role", role.name(),
                        "uniqueId", uniqueId)
                )
                .subject(String.valueOf(userEmail))
                .issuedAt(new Date(System.currentTimeMillis()))
                .signWith(getSigningKey())
                .compact();
    }
}
