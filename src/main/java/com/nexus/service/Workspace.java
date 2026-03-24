package com.nexus.service;

import com.nexus.model.Task;
import com.nexus.model.TaskStatus;
import com.nexus.model.User;
import com.nexus.exception.NexusValidationException;
import com.nexus.model.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Serviço central de gerenciamento do ecossistema Nexus.
 * O Workspace atua como o agregador principal, gerenciando o ciclo de vida de tarefas,
 * a organização de projetos e a atribuição de usuários, além de fornecer relatórios
 * analíticos sobre o desempenho da equipe.
 */

public class Workspace {
    private List<Task> tasks = new ArrayList<>();
    private List<Project> projects = new ArrayList<>();

    /**
     * Adiciona uma tarefa ao workspace e a vincula automaticamente ao seu projeto correspondente.
     * * @param task A tarefa a ser adicionada. Deve conter o nome de um projeto já existente.
     * @throws NexusValidationException Se o projeto designado na tarefa não estiver cadastrado no workspace.
     */
    
    public void addTask(Task task) {
        tasks.add(task);

        Project project = projects.stream()
        .filter( p -> p.getProjectName().equals(task.getAssignedProjectName()) )
        .findFirst()
        .orElse(null);


        if( project == null )
            throw new NexusValidationException("Projeto designado nao existe.");
        
        project.addTask(task);
    }

    /**
     * Cadastra um novo projeto no workspace.
     * * @param project O objeto a ser registrado.
     */

    public void addProject(Project project){
        projects.add(project);
    }

    /**
     * Atribui um usuário como responsável por uma tarefa específica.
     * * @param taskID O identificador único da tarefa.
     * @param user O usuário que assumirá a responsabilidade.
     * @throws NexusValidationException Caso o ID fornecido não corresponda a nenhuma tarefa.
     */

    public void assignUser( int taskID, User user ){
        Task task = tasks.stream()
        .filter( t -> t.getId() == taskID )
        .findFirst()
        .orElse(null);

        if( task == null ) 
            throw new NexusValidationException("Tarefa inexistente.");

        task.assignOwner(user);
    }

    /**
     * Altera o status de uma tarefa gerenciando as regras de transição de estado.
     * * @param taskID O ID da tarefa a ser modificada.
     * @param newStatus O novo TaskStatus desejado.
     * @throws NexusValidationException Se a tarefa não for encontrada ou se a transição for inválida.
     */

    public void changeTaskStatus( int taskID, TaskStatus newStatus ){
        Task task = tasks.stream()
        .filter( t -> t.getId() == taskID )
        .findFirst()
        .orElse(null);

        if( task == null ) 
            throw new NexusValidationException("Tarefa inexistente.");

        switch (newStatus){
            case TaskStatus.DONE -> task.markAsDone();
            case TaskStatus.TO_DO -> task.setBlocked(false);
            case TaskStatus.BLOCKED -> task.setBlocked(true);
            case TaskStatus.IN_PROGRESS -> task.moveToInProgress();
        }
    }

    /**
     * Retorna a lista completa de tarefas.
     * * @return Uma lista imutável de todas as instâncias das Tasks.
     */

    public List<Task> getTasks() {
        // Retorna uma visão não modificável para garantir encapsulamento
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Funcao de analise dos melhores usuarios.
     * @return Lista ordenada com 3 usuarios com mais tarefas concluidas. 
     */

    public List<User> BestUsers(){
        List <User> Best = tasks.stream()
        .map(Task::getOwner)
        .distinct()
        .sorted(Comparator.comparingInt(User::consultTotalDone).reversed())
        .limit(3)
        .collect(Collectors.toList());
        return Best;
    } 

    /**
     * Funcao de analise dos usuarios mais de 10 tarefas em progresso
     * @return Lista com todos os usuarios com mais de 10 tarefas em progresso
     */

    public List<User> TenInProgress(){
        List <User> Ten = tasks.stream()
        .map(Task::getOwner)
        .filter(u -> u.consultTotalInProgress() > 10)
        .distinct()
        .collect(Collectors.toList());
        return Ten;
    }

    /**
     * Funcao de analise da progressao das tarefas.
     * @return Porcentagem de tarefas concluidas.
     */

    public double Percentage(){
        long sum = tasks.stream()
            .filter(t -> t.getStatus() == TaskStatus.DONE)
            .count();
        return (sum*1.0)/tasks.size();    
    }

    /**
     * Funcao de analise do status mais frequente das tarefas. 
     * @return Valor no Enum TaskStatus ( BLOCKED, TO_DO ou IN_PROGRESS ).
     */

    public TaskStatus BestTask(){
        long Blocked = tasks.stream()
            .filter(t -> t.getStatus() == TaskStatus.BLOCKED)
            .count();
        long To_do = tasks.stream()
        .filter(t -> t.getStatus() == TaskStatus.TO_DO)
        .count();
        long In_progress = tasks.stream()
        .filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS)
        .count();
        if(Blocked >= To_do && Blocked >= In_progress)
            return TaskStatus.BLOCKED;
        if(Blocked <= To_do && To_do >= In_progress)
            return TaskStatus.TO_DO;
        return TaskStatus.IN_PROGRESS;
    }

    /**
     * Gera um relatório formatado no console com as métricas de produtividade e saúde do workspace.
     */

    public void reportStatus(){
        List<User> bestUsers = BestUsers();

        System.out.println("Usuarios com mais tarefas concluidas: ");

        for( User user : bestUsers )
            System.out.println(user.consultUsername() + " ");
        System.out.println();


        List<User> overloadedUsers = TenInProgress();
    
        System.out.println("Usuarios com mais de 10 tarefas em andamento: ");

        for( User user : overloadedUsers )
            System.out.println(user.consultUsername() + " ");
        System.out.println();
        
        System.out.println("Porcentagem de tarefas concluidas: " + Percentage() );
        System.out.println();
            
        System.out.print("Status com maior ocorrencia dentre as tarefas (exceto DONE): ");

        TaskStatus status = BestTask();
        switch (status){
            case TaskStatus.BLOCKED -> 
                System.out.println("BLOCKED");

            case TaskStatus.IN_PROGRESS -> 
                System.out.println("IN_PROGRESS");

            case TaskStatus.TO_DO -> 
                System.out.println("TO_DO");
        }
    }
}
