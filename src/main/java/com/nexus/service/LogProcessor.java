package com.nexus.service;

import com.nexus.model.*;
import com.nexus.exception.NexusValidationException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class LogProcessor {

    public void processLog(String fileName, Workspace workspace, List<User> users) {
        try {
            // Busca o arquivo dentro da pasta de recursos do projeto (target/classes)
            var resource = getClass().getClassLoader().getResourceAsStream(fileName);
            
            if (resource == null) {
                throw new IOException("Arquivo não encontrado no classpath: " + fileName);
            }

            try (java.util.Scanner s = new java.util.Scanner(resource).useDelimiter("\\A")) {
                String content = s.hasNext() ? s.next() : "";
                List<String> lines = List.of(content.split("\\R"));
                
                for (String line : lines) {
                    if (line.isBlank() || line.startsWith("#")) continue;

                    String[] p = line.split(";");
                    String action = p[0];

                    try {
                        switch (action) {
                            case "CREATE_USER" -> {
                                users.add(new User(p[1], p[2]));
                                System.out.println("[LOG] Usuário criado: " + p[1]);
                            }
                            case "CREATE_TASK" -> {
                                Task t = new Task(p[1], LocalDate.parse(p[2]), Integer.parseInt(p[3]), p[4]);
                                workspace.addTask(t);

                                System.out.println("[LOG] Tarefa criada: " + p[1]);
                            }
                            case "CREATE_PROJECT" -> {
                                Project project = new Project(p[1], Integer.parseInt(p[2]));
                                workspace.addProject(project);

                                System.out.println("[LOG] Projeto criado: " + p[1]);
                            }
                            case "ASSIGN_USER" -> {
                                User user = users.stream()
                                .filter( u -> u.consultUsername().equals(p[2]) )
                                .findFirst()
                                .orElse(null);

                                if( user == null )
                                        throw new NexusValidationException("Usuario inexistente");

                                workspace.assignUser( Integer.parseInt(p[1]), user );
                                System.out.println("[LOG] Dono da tarefa designado: Tarefa ID " + p[1] + " Usuario " + p[2]);

                            }
                            case "CHANGE_STATUS" -> {
                                TaskStatus newStatus;
                                switch (p[2]){
                                    case "DONE" -> newStatus = TaskStatus.DONE;
                                    case "IN_PROGRESS" ->  newStatus = TaskStatus.IN_PROGRESS;
                                    case "BLOCKED" -> newStatus = TaskStatus.BLOCKED; 
                                    case "TO_DO" -> newStatus = TaskStatus.TO_DO;
                                    default ->  throw new NexusValidationException("Status da tarefa desconhecido.");
                                }

                                workspace.changeTaskStatus( Integer.parseInt(p[1]), newStatus );

                                System.out.println("[LOG] Status da tarefa alterado: Tarefa ID " + p[1] + " Novo Status " + p[2]);

                            }
                            case "REPORT_STATUS" -> {
                                workspace.reportStatus();
                            }
                            default -> System.err.println("[WARN] Ação desconhecida: " + action);
                        }
                    } catch (NexusValidationException e) {
                        System.err.println("[ERRO DE REGRAS] Falha no comando '" + line + "': " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[ERRO FATAL] " + e.getMessage());
        }
    }
}