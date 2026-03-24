package com.nexus.model;

import java.util.ArrayList;
import java.util.List;

import com.nexus.exception.NexusValidationException;

/**
 * Representa um projeto dentro do sistema Nexus.
 * Um projeto é definido por um nome e um orçamento total de horas (budget).
 * Ele gerencia uma coleção de tarefas, garantindo que o esforço somado de todas 
 * as tarefas não ultrapasse o limite de horas estabelecido na criação.
 */

public class Project {
    private String projectName; 
    private List<Task> tasks = new ArrayList<>(); 
    private int totalBudget = 0;
    private int currentBudget = 0;

    /**
        * Construtor do projeto.
        * @param projectName O nome do projeto.
        * @param totalBudget O budget total em horas do projeto. 
    */
    public Project( String projectName, int totalBudget ){
        if( projectName == null || projectName.isBlank() )
            throw new NexusValidationException("Nome do projeto nao pode estar em branco.");
        if( totalBudget < 0 )
            throw new NexusValidationException("Budget total do projeto nao pode ser negativo.");

        this.projectName = projectName; 
        this.totalBudget = totalBudget;
    }

    /**
        * Metodo para adicionar Task no projeto.
        * Falha caso a nova Task exceda o totalBudget do projeto.
        * @param t A Task a ser adicionada.
    */

    public void addTask( Task t ){
        if( currentBudget + t.getEstimatedEffort() > totalBudget )
            throw new NexusValidationException("Maximo de horas do projeto excedido.");
        
        currentBudget += t.getEstimatedEffort();
        tasks.add(t);
    }

    // Getters

    public String getProjectName(){ return projectName; }
    
}
