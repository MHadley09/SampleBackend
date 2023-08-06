package com.sample.controller.account;

import lombok.Data;

import java.io.Serializable;
import java.util.Optional;

@Data
public class AuthRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String displayName;
    private String password;
    private Optional<Boolean> includeRefresherToken;
}
