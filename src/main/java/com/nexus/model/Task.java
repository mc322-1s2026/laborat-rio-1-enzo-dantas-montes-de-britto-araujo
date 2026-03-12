package com.nexus.model;

import java.time.LocalDate;

import com.nexus.exception.NexusValidationException;

public class Task {
    // Métricas Globais (Alunos implementam a lógica de incremento/decremento)
    public static int totalTasksCreated = 0;
    public static int totalValidationErrors = 0;
    public static int activeWorkload = 0;

    private static int nextId = 1;

    private final int id;
    private final LocalDate deadline; // Imutável após o nascimento
    private String title;
    private TaskStatus status;
    private User owner;

    public Task(String title, LocalDate deadline) {
        this.id = nextId++;
        this.deadline = deadline;
        this.title = title;
        this.status = TaskStatus.TO_DO;
        
        // Ação do Aluno:
        totalTasksCreated++; 
    }

    /**
     * Move a tarefa para IN_PROGRESS.
     * Regra: Só é possível se houver um owner atribuído e não estiver BLOCKED.
     */
    public void moveToInProgress(User user) {
        if( owner == null ){
            totalValidationErrors++;    
            throw new NexusValidationException("[ERRO] Esta tarefa nao possui dono e nao pode ser atualizada para EM PROGRESSO.");
        }
        if( owner.consultUsername() != user.consultUsername() || owner.consultEmail() != user.consultEmail() ){
            totalValidationErrors++;    
            throw new NexusValidationException("[ERRO] Usuario que requisitou atualizacao para EM PROGRESSO nao e dono da tarefa.");
        }
        if( status == TaskStatus.BLOCKED ){
            totalValidationErrors++;    
            throw new NexusValidationException("[ERRO] Tarefa nao pode ser atualizada para EM PROGRESSO enquando BLOQUEADA");
        }
        status = TaskStatus.IN_PROGRESS;
        activeWorkload++;
        
    }

    /**
     * Finaliza a tarefa.
     * Regra: Só pode ser movida para DONE se não estiver BLOCKED.
     */
    public void markAsDone() {
        if( status == TaskStatus.BLOCKED ){
            totalValidationErrors++;    
            throw new NexusValidationException("[ERRO] Tarefa nao pode ser CONCLUIDA enquanto estiver BLOQUEADA.");
        }
        status = TaskStatus.DONE;
        activeWorkload--;
    }

    public void setBlocked(boolean blocked) {
        if ( blocked ) {
            if( status == TaskStatus.DONE ){
                totalValidationErrors++;    
                throw new NexusValidationException("[ERRO] Tarefa nao pode ser BLOQUEADA se ja estiver CONCLUIDA.");
            }
            this.status = TaskStatus.BLOCKED;
        } else {
            this.status = TaskStatus.TO_DO; // Simplificação para o Lab
        }
    }

    // Getters
    public int getId() { return id; }
    public TaskStatus getStatus() { return status; }
    public String getTitle() { return title; }
    public LocalDate getDeadline() { return deadline; }
    public User getOwner() { return owner; }
}