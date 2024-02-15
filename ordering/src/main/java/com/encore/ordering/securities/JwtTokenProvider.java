package com.encore.ordering.securities;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secretKey}")
    private String secertKey;

    @Value("${jwt.expiration}")
    private int expiration;
    public String createToken(String email, String role){
//        claims : 토큰사용자에 대한 속성이나 데이터 포함, 주로 payload 의미
//        builder 형식으로 써도 됨
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role",role);
        Date now = new Date();
        JwtBuilder jwtBuilder = Jwts.builder();
        jwtBuilder.setClaims(claims);
        jwtBuilder.setIssuedAt(now);
        jwtBuilder.setExpiration(new Date(now.getTime() + expiration*60*1000L));
        jwtBuilder.signWith(SignatureAlgorithm.HS256,"mysecret");
        return jwtBuilder.compact();
    }
}
