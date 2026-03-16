package com.nexus.model;

import java.util.List;

import com.nexus.exception.NexusValidationException;

public class Project {
    private String projectName; 
    private List<Task> tasks; 
    private int totalBudget = 0;
    private int currentBudget = 0;

    /**
        * Construtor do projeto.
        * @param projectName O nome do projeto.
        * @param totalBudget O budget total em horas do projeto. 
    */
    public Project( String projectName, int totalBudget ){
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

    
}