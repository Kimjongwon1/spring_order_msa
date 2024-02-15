package com.encore.ordering.securities;

import com.encore.ordering.common.ErrorResponseDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.security.sasl.AuthenticationException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtAuthFilter extends GenericFilter {

    @Value("${jwt.secretKey}")
    private String secertKey;
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            String bearertoken = ((HttpServletRequest)request).getHeader("Authorization");
          //  if(bearertoken != null){
//                bearertoken에서 token 값만 추출
        if(bearertoken != null){
            if(!bearertoken.substring(0,7).equals("Bearer ")){
                throw new AuthenticationException("토큰 형식이 맞지 않습니다.");
            }
            String token = bearertoken.substring(7);
//                검증 후 검출 후 Authentication 객체 생성
//                Jws<Claims> claimsJws = Jwts.parser().setSigningKey("mysecret").parseClaimsJws(token);
            //여기서 바디를 꺼내는 과정에서 key값 비교를 하는것 자체가 검증

            //토큰 검증 및 claims 추출
            Claims claimsJws = Jwts.parser().setSigningKey(secertKey).parseClaimsJws(token).getBody();
//            Authentication 객체를 생성하기 위한 UserDetail 생성
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + claimsJws.get("role")));
            UserDetails userDetails = new User(claimsJws.getSubject(),"",authorities);

            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails,"",userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
//        filterchain에서 그다음 filtering으로 넘어감
        chain.doFilter(request,response);
        }catch (Exception e){
            HttpServletResponse httpServletResponse = (HttpServletResponse)response;
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            httpServletResponse.setContentType("application/json");
            httpServletResponse.getWriter().write(ErrorResponseDto.makeMessage(HttpStatus.UNAUTHORIZED,e.getMessage()).toString());
//            erroresponsemessage
        }


           // }
    }


}
