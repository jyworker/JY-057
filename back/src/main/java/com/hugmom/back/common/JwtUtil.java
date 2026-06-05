package com.hugmom.back.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;

/**
 * JWT 工具类
 * 
 * @author HugMom Team
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${hugmom.token.secret}")
    private String secret;

    @Value("${hugmom.token.expire-time}")
    private Long expireTime;

    /** HS256 要求密钥至少 256 位（32 字节），将配置的 secret 转为合法密钥 */
    private SecretKey getSigningKey() {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                bytes = md.digest(bytes);
            } catch (Exception e) {
                throw new IllegalStateException("JWT 密钥派生失败", e);
            }
        }
        return Keys.hmacShaKeyFor(bytes);
    }

    /**
     * 生成 JWT Token
     * 
     * @param userId 用户ID
     * @return Token字符串
     */
    public String generateToken(String userId) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expireTime);

        SecretKey key = getSigningKey();

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(key)
                .compact();
    }

    /**
     * 从 Token 中解析用户ID
     * 
     * @param token Token字符串
     * @return 用户ID
     */
    public String getUserIdFromToken(String token) {
        try {
            SecretKey key = getSigningKey();
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (Exception e) {
            log.error("解析Token失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 验证 Token 是否有效
     * 
     * @param token Token字符串
     * @return true-有效 false-无效
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = getSigningKey();
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.error("Token验证失败: {}", e.getMessage());
            return false;
        }
    }
}
