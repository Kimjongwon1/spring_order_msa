package com.encore.ordering.securities;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.security.sasl.AuthenticationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtGlobalFilter implements GlobalFilter {
    @Value("${jwt.secretKey}")
    private String secertKey;
    private final List<String> allowUrl = Arrays.asList("/member/create","/member/doLogin","/item/items","/item/*/image");
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String reqUri = request.getURI().getPath();
        AntPathMatcher antPathMatcher = new AntPathMatcher();
      boolean isAllowed =  allowUrl.stream().anyMatch(uri -> antPathMatcher.match(uri,reqUri));
        if (isAllowed){
            return chain.filter(exchange);
        }
        String bearertoken = request.getHeaders().getFirst("Authorization");
        try {

            //  if(bearertoken != null){
//                bearertoken에서 token 값만 추출
            if(bearertoken != null){
                if(!bearertoken.substring(0,7).equals("Bearer ")){
                    throw new IllegalArgumentException("토큰 형식이 맞지 않습니다.");
                }
                String token = bearertoken.substring(7);
                //토큰 검증 및 claims 추출
                Claims claims = Jwts.parser().setSigningKey(secertKey).parseClaimsJws(token).getBody();
                String email = claims.getSubject();
                String role = claims.get("role",String.class);
                request = exchange.getRequest().mutate()
                        .header("myEmail",email)
                        .header("myRole",role)
                        .build();
                exchange = exchange.mutate().request(request).build();
            }else {
                throw new RuntimeException("token is Empty");
            }
//        filterchain에서 그다음 filtering으로 넘어감
        }catch (Exception e){
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }
}
