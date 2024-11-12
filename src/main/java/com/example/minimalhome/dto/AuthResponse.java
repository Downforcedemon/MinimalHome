package com.example.minimalhome.dto;

import lombok.Data;

@Data
public class AuthResponse<T> {
    private String success;
    private String message;
    private T data;
}