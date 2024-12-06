package ru.andreyszdlv.taskmanager.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.taskmanager.exception.InvalidTokenException;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class JwtSecurityService {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    private SecretKey getSigningKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String userEmail, String role){
        log.info("Executing generateToken in JwtSecurityService");
        return Jwts.builder()
                .claims(Map.of("role", role))
                .subject(userEmail)
                .issuedAt(new Date(System.currentTimeMillis()))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(String userEmail, String role) {
        log.info("Executing generateRefreshToken in JwtSecurityService");
        return Jwts.builder()
                .claims(Map.of("role", role))
                .subject(String.valueOf(userEmail))
                .issuedAt(new Date(System.currentTimeMillis()))
                .signWith(getSigningKey())
                .compact();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        log.info("extractAllClaims");
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }
        catch (Exception e) {
            throw new InvalidTokenException();
        }
    }

    public String extractUserEmail(String token) {
        log.info("Extract user email from token");
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        log.info("Extract role from token");
        return extractAllClaims(token).get("role").toString();
    }
}