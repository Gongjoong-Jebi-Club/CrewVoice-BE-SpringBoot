package org.skala.crewvoice.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long validityInMilliseconds;

    /**
     * JwtTokenProvider 생성자
     * @param secretKey Base64로 인코딩된 HMAC-SHA 키.
     *                  키는 반드시 256비트(32바이트) 이상의 길이를 가져야 합니다.
     *                  예: `java.security.SecureRandom random = new java.security.SecureRandom(); byte[] keyBytes = new byte[32]; random.nextBytes(keyBytes); String encodedKey = java.util.Base64.getEncoder().encodeToString(keyBytes);`
     * @param validityInMilliseconds 토큰 만료 시간 (밀리초)
     */
    public JwtTokenProvider(@Value("${security.jwt.token.secret-key}") String secretKey,
                            @Value("${security.jwt.token.expire-length}") long validityInMilliseconds) {
        // Base64 디코딩을 통해 SecretKey 객체 생성
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.validityInMilliseconds = validityInMilliseconds;
    }

    public String createToken(String email) {
        Claims claims = Jwts.claims().setSubject(email);
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getSubject(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
}
