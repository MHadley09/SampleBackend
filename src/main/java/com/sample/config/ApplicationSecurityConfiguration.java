package com.sample.config;

import com.sample.config.filter.JwtFilter;
import com.sample.model.RoleType;
import com.sample.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class ApplicationSecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private JwtAuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private AccountService accountService;
    @Autowired
    private JwtFilter jwtFilter;

    public ApplicationSecurityConfiguration(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                                            AccountService accountService,
                                            JwtFilter jwtFilter) {
        this.authenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.accountService = accountService;
        this.jwtFilter = jwtFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests().antMatchers("/authenticate","/user/new", "/guest/new", "/guestAuth").permitAll()
                .antMatchers("/admin/**").hasAuthority(RoleType.ADMIN.getValue())
                .anyRequest().hasAuthority(RoleType.USER.getValue())
                .and()
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }
}