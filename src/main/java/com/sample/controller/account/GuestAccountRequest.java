package com.sample.controller.account;

import lombok.Data;

@Data
public class GuestAccountRequest {
    //Nullable
    private String phoneNumber;
    private String emailAddress;
    private String firstName;
    private String lastName;

    //Not nullable
    private String username;
    private String displayName;
}
