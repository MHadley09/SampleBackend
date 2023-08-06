package com.sample.controller.account;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class AccountResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Long accountId;
    private final String firstName;
    private final String lastName;
    private final String username;
}
