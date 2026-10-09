package com.shelfup.dto;

public record AuthResponse(String token, String role, String email) {}
