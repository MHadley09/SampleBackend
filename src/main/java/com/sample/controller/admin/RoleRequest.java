package com.sample.controller.admin;

import com.sample.model.RoleType;
import lombok.Data;

import java.io.Serializable;

@Data
public class RoleRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private RoleType role;
}
