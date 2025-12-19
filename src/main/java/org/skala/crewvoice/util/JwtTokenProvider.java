package org.skala.crewvoice.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.skala.crewvoice.exception.ErrorCode;
import org.skala.crewvoice.exception.custom.InvalidCredentialsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;

    public JwtTokenProvider(@Value("${security.jwt.token.secret-key}") String secretKey,
                            @Value("${security.jwt.token.access-expire-length}") long accessTokenValidityInMilliseconds,
                            @Value("${security.jwt.token.refresh-expire-length}") long refreshTokenValidityInMilliseconds) {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds;
    }

    public String createAccessToken(String email) {
        return createToken(email, accessTokenValidityInMilliseconds);
    }

    public String createRefreshToken(String email) {
        return createToken(email, refreshTokenValidityInMilliseconds);
    }

    /**
     * 테스트용: 이미 만료된 Access Token 생성 (만료 시간 -1초)
     */
    public String createExpiredAccessToken(String email) {
        return createToken(email, -1000); // 1초 전에 만료
    }

    private String createToken(String email, long validityInMilliseconds) {
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

    /**
     * 만료된 토큰에서 subject(email)를 강제로 추출합니다.
     * <p>
     * <b>사용 목적:</b> Access Token 재발급(Refresh) 요청 시, 클라이언트는 "만료된 Access Token"과 "유효한 Refresh Token"을 함께 전송합니다.
     * 서버는 만료된 Access Token이 누구의 것인지 식별하기 위해 이 메서드를 사용하여 식별자(Email)를 꺼냅니다.
     * <p>
     * <b>보안 주의사항:</b> 이 메서드로 얻은 정보만으로 인증을 수행하면 안 됩니다.
     * 반드시 이후 로직에서 DB/Redis에 저장된 <b>Refresh Token의 유효성</b>을 검증해야 합니다.
     */
    public String getSubjectFromExpiredToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            // 만료된 토큰이어도 클레임은 추출 가능
            return e.getClaims().getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw new InvalidCredentialsException(ErrorCode.INVALID_TOKEN);
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    public Long getRemainingTime(String token) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            Date expiration = claims.getExpiration();
            Date now = new Date();
            return expiration.getTime() - now.getTime();
        } catch (JwtException | IllegalArgumentException e) {
            return 0L;
        }
    }
}
