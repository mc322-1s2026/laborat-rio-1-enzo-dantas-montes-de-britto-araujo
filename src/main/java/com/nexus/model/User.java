package com.nexus.model;

import java.util.List;

public class User {
    private final String username;
    private final String email;

    public User(String username, String email) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username não pode ser vazio.");
        }
        this.username = username;
        if(email == null || email.isBlank()){
            throw new IllegalArgumentException("Email não pode ser vazio.");
        }
        if(!email.contains("@") && !email.endsWith(".com")){
            throw new IllegalArgumentException("Email precisa conter @ e terminar com .com");
        }
        this.email = email;
    }   

    public String consultEmail() {
        return email;
    }

    public String consultUsername() {
        return username;
    }

    public long calculateWorkload(List<Task> lista) {
        return lista.stream()
            .filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS)
            .count(); 
    }
}