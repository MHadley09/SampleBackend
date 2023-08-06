package com.sample.component;

import com.sample.model.Account;
import com.sample.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.google.common.collect.ImmutableList.toImmutableList;

@Component
public class TokenManager implements Serializable {
    private static final long serialVersionUID = 7008375124389347049L;
    public static final long TOKEN_VALIDITY = TimeUnit.HOURS.toMillis(24);
    public static final String AUTHORITIES_KEY = "authorities";

    @Value("${jwt.secret}")
    private String jwtSecret;

    public String generateJwtToken(Account account, List<Role> roles, Date expiry) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder().setClaims(claims).setSubject(account.getId().toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expiry)
                .claim(AUTHORITIES_KEY, roles.stream().map(Role::getRole).collect(toImmutableList()))
                .signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
    }

    public Boolean validateJwtToken(String token, Account account) {
        String id = getUserIdFromToken(token);
        Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        Boolean isTokenExpired = claims.getExpiration().before(new Date());
        return (id.equals(account.getId().toString()) && !isTokenExpired);
    }

    public String getUserIdFromToken(String token) {
        final Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public Optional<List<Role>> getUserRolesFromToken(String token) {
        final Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        final List<String> roles = claims.get(AUTHORITIES_KEY, List.class);

        if (roles == null || roles.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(
                roles.stream().map(Role::new).collect(toImmutableList()));
    }

    public Date generateExpiry(){
        return new Date(System.currentTimeMillis() + TOKEN_VALIDITY);
    }
}