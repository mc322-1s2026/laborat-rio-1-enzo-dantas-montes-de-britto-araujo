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
        .sorted(Comparator.comparingInt(User::consultTotal).reversed())
        .limit(3)
        .collect(Collectors.toList());
        return Best;
    } 

}