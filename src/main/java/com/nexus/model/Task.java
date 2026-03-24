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
    private int estimatedEffort;
    private String assignedProjectName;

    /**
     * Construtor da classe Task
     * @param title Titulo da tarefa.
     * @param deadline Prazo de entrega. 
     * @param estimatedEffort Esforco estimado em horas. 
     */
    public Task( String title, LocalDate deadline, int estimatedEffort, String assignedProjectName ){

        if( title == null || title.isBlank() ){
            totalValidationErrors++;
            throw new NexusValidationException("Nome da tarefa nao deve estar em branco.");
        }

        if( estimatedEffort < 0 ){
            totalValidationErrors++;
            throw new NexusValidationException("Esforco estimado nao deve ser negativo.");
        }

        if( assignedProjectName == null || assignedProjectName.isBlank() ){
            totalValidationErrors++;
            throw new NexusValidationException("Nome do projeto designado nao deve estar em branco.");
        }


        this.id = nextId++;
        this.deadline = deadline;
        this.title = title;
        this.status = TaskStatus.TO_DO;
        this.estimatedEffort = estimatedEffort; 
        this.assignedProjectName = assignedProjectName;

        totalTasksCreated++; 
    }

    /**
     * Construtor da classe Task com estimatedEffort = 0 e assignedProjectName = null.
     * @param title Titulo da tarefa.
     * @param deadline Prazo de entrega. 
     */
    public Task(String title, LocalDate deadline) {
        this( title, deadline, 0, null);
    }

    /**
     * Metodo para designar o dono da tarefa
     * @param user O usuario do dono da tarefa.
     */

    public void assignOwner( User user ){
        this.owner = user; 
    }

    /**
     * Move a tarefa para IN_PROGRESS.
     * Regra: Só é possível se houver um owner atribuído e não estiver BLOCKED.
     * @param user O usuario que esta movendo a Task
     */

    void changeStatus(TaskStatus s){
        if(status == TaskStatus.TO_DO){
            owner.addToDo(-1);
        }
        if(status == TaskStatus.DONE){
            owner.addDone(-1);
        }
        if(status == TaskStatus.BLOCKED){
            owner.addBlocked(-1);
        }
        if(status == TaskStatus.IN_PROGRESS){
            owner.addInProgress(-1);
        }
        status = s;
        if(status == TaskStatus.TO_DO){
            owner.addToDo(+1);
        }
        if(status == TaskStatus.DONE){
            owner.addDone(+1);
        }
        if(status == TaskStatus.BLOCKED){
            owner.addBlocked(+1);
        }
        if(status == TaskStatus.IN_PROGRESS){
            owner.addInProgress(+1);
        }
    }

    public void moveToInProgress() {
        if( owner == null ){
            totalValidationErrors++;    
            throw new NexusValidationException("[ERRO] Esta tarefa nao possui dono e nao pode ser atualizada para EM PROGRESSO.");
        }
        
        changeStatus(TaskStatus.IN_PROGRESS);
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
        changeStatus(TaskStatus.DONE);
        activeWorkload--;
    }

    /**
     * Move a tarefa para bloqueada ou nao.
     * @param blocked True para BLOCKED, False para TO_DO
     */

    public void setBlocked(boolean blocked) {
        if ( blocked ) {
            if( status == TaskStatus.DONE ){
                totalValidationErrors++;    
                throw new NexusValidationException("[ERRO] Tarefa nao pode ser BLOQUEADA se ja estiver CONCLUIDA.");
            }
            changeStatus(TaskStatus.BLOCKED);
        } else {
            changeStatus(TaskStatus.TO_DO);
            this.status = TaskStatus.TO_DO; // Simplificação para o Lab
        }
    }

    // Getters
    public int getId() { return id; }
    public TaskStatus getStatus() { return status; }
    public String getTitle() { return title; }
    public LocalDate getDeadline() { return deadline; }
    public User getOwner() { return owner; }
    public int getEstimatedEffort(){ return estimatedEffort; }
    public String getAssignedProjectName() { return assignedProjectName; }
}