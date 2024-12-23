package com.ust.financeanalyzer.service;


import com.ust.financeanalyzer.Entity.Employee;
import com.ust.financeanalyzer.Entity.Project;
import com.ust.financeanalyzer.Repository.EmployeeRepository;
import com.ust.financeanalyzer.Repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FinancialApplicationService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ProjectRepository projectRepository;

    public Mono<Project> addProject(Project project) {
        return projectRepository.save(project);
    }

    public Mono<Employee> addEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Mono<Employee> assignProjectToEmployee(String id, String projectId) {
        return employeeRepository.findById(id)
                .map(emp -> {
                    emp.setProjectid(projectId);
                    return emp;
                });
    }

    public Mono<String> assignEmployeeToProject(String projectId, List<String> id) {
        Mono<Project> projectMono = projectRepository.findById(projectId);

        return projectMono.flatMap(project -> {
            int teamSize = project.getTeamSize();

            if (teamSize == 0) {
                return Mono.error(new RuntimeException("Project team size exceeded"));
            }

            List<Mono<Employee>> assignments = id.stream()
                    .map(employeeId -> employeeRepository.findById(employeeId)
                            .flatMap(employee -> {
                                if (teamSize <= 0) {
                                    return Mono.error(new RuntimeException("Project team size exceeded"));
                                }
                                employee.setProjectid(projectId);
                                return employeeRepository.save(employee).then(Mono.just(employee));
                            }))
                    .collect(Collectors.toList());

            return Mono.when(assignments)
                    .then(Mono.just("Employees assigned to project successfully"));
        });
    }



}
