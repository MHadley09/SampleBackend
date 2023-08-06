package com.sample.component;

import com.sample.model.Account;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedAccountFacade {
    public Account getCurrentAccount() {
        return (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
