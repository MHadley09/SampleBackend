package com.sample.config.filter;

import com.sample.component.TokenManager;
import com.sample.model.Account;
import com.sample.model.Role;
import com.sample.service.AccountService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.google.common.collect.ImmutableList.toImmutableList;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final AccountService accountService;
    private final TokenManager tokenManager;

    @Autowired
    public JwtFilter(AccountService accountService, TokenManager tokenManager) {
        this.accountService = accountService;
        this.tokenManager = tokenManager;
    }

    private List<Role> fetchRoles(Long userId) {
        return accountService.loadAccountRoles(userId);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String tokenHeader = request.getHeader("Authorization");
        String userId = null;
        String token = null;
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            token = tokenHeader.substring(7);
            try {
                userId = tokenManager.getUserIdFromToken(token);
            } catch (IllegalArgumentException e) {
                System.out.println("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                System.out.println("JWT Token has expired");
            }
        } else {
            System.out.println("Bearer String not found in token");
        }
        if (null != userId && SecurityContextHolder.getContext().getAuthentication() == null) {
            Account account = accountService.loadAccountById(Long.parseLong(userId))
                    .orElseThrow(IllegalAccessError::new);

            if (tokenManager.validateJwtToken(token, account)) {
                List<GrantedAuthority> authorities =
                        tokenManager.getUserRolesFromToken(token)
                                .orElseGet(() -> fetchRoles(account.getId()))
                                .stream()
                                .map(Role::getRole)
                                .map(SimpleGrantedAuthority::new)
                                .collect(toImmutableList());

                UsernamePasswordAuthenticationToken
                        authenticationToken = new UsernamePasswordAuthenticationToken(
                        account, null,
                        authorities);
                authenticationToken.setDetails(new
                        WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}