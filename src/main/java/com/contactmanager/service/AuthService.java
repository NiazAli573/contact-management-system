package com.contactmanager.service;

import com.contactmanager.dto.AuthResponse;
import com.contactmanager.dto.ChangePasswordRequest;
import com.contactmanager.dto.LoginRequest;
import com.contactmanager.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse changePassword(String authenticatedEmail, ChangePasswordRequest request);
}

