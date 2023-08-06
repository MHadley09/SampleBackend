package com.sample.controller.account;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class AuthResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String token;

    private final String username;
    private final String displayName;
    private final List<String> roles;

    private final Date expiry;
}
