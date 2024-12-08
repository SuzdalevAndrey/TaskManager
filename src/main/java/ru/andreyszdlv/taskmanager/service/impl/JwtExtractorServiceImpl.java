package ru.andreyszdlv.taskmanager.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.taskmanager.enums.Role;
import ru.andreyszdlv.taskmanager.exception.InvalidTokenException;
import ru.andreyszdlv.taskmanager.service.JwtExtractorService;

import javax.crypto.SecretKey;
import java.util.function.Function;

@Slf4j
@Service
public class JwtExtractorServiceImpl implements JwtExtractorService {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    public String extractUserEmail(String token) {
        log.info("Extract user email from token");
        return extractClaim(token, Claims::getSubject);
    }

    public Role extractRole(String token) {
        log.info("Extract role from token");
        return Role.valueOf(extractAllClaims(token).get("role").toString());
    }

    private SecretKey getSigningKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        log.info("Extract all claims from token");
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }
        catch (Exception e) {
            log.error("Extract failed");
            throw new InvalidTokenException();
        }
    }
}
