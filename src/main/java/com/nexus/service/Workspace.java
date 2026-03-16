package com.nexus.service;

import com.nexus.model.Task;
import com.nexus.model.TaskStatus;
import com.nexus.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Comparator;

public class Workspace {
    private List<Task> tasks = new ArrayList<>();

    public void addTask(Task task) {
        tasks.add(task);
    }

    public List<Task> getTasks() {
        // Retorna uma visão não modificável para garantir encapsulamento
        return Collections.unmodifiableList(tasks);
    }

    public List<User> BestUsers(){
        List <User> Best = tasks.stream()
        .map(Task::getOwner)
        .sorted(Comparator.comparingInt(User::consultTotalDone).reversed())
        .limit(3)
        .collect(Collectors.toList());
        return Best;
    } 

    public List<User> TenInProgress(){
        List <User> Ten = tasks.stream()
        .map(Task::getOwner)
        .filter(u -> u.consultTotalInProgress() > 10)
        .collect(Collectors.toList());
        return Ten;
    }

    public double Percentage(){
        long sum = tasks.stream()
            .filter(t -> t.getStatus() == TaskStatus.DONE)
            .count();
        return (sum*1.0)/tasks.size();    
    }

    public TaskStatus BastTask(){
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
}