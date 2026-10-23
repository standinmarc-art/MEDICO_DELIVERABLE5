package com.scc.model;

public record User(
        String username,
        String password,
        String role,
        String accountStatus
) {}