package com.nexus.model;

import java.util.List;

public class User {
    private final String username;
    private final String email;
    private int totalDone = 0, totalToDo = 0, totalInProgress = 0, totalBlocked = 0;

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

    public int consultTotalBlocked(){
        return totalBlocked;
    }

    public int consultTotalToDo(){
        return totalToDo;
    }
    
    public int consultTotalInProgress(){
        return totalInProgress;
    }
    
    public int consultTotalDone(){
        return totalDone;
    }

    public void addToDo(int x){
        totalToDo += x;
        return;
    }

    public void addDone(int x){
        totalDone += x;
        return;
    }

    public void addInProgress(int x){
        totalInProgress += x;
        return;
    }

    public void addBlocked(int x){
        totalBlocked += x;
        return;
    }

   
    public long calculateWorkload(List<Task> lista) {
        return lista.stream()
            .filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS)
            .count(); 
    }
}