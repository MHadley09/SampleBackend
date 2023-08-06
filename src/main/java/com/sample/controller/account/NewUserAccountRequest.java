package com.sample.controller.account;

import lombok.Data;

@Data
public class NewUserAccountRequest {
    //Nullable
    private String phoneNumber;
    private String emailAddress;
    private String firstName;
    private String lastName;

    //Not nullable
    private String displayName;
    private String password;
    private String username;
}
