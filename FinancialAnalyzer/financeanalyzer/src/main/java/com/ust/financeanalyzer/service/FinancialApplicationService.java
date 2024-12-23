package com.ust.financeanalyzer.service;


import com.ust.financeanalyzer.Entity.Employee;
import com.ust.financeanalyzer.Entity.Project;
import com.ust.financeanalyzer.Repository.EmployeeRepository;
import com.ust.financeanalyzer.Repository.ProjectRepository;
import com.ust.financeanalyzer.dto.Employeedto;
import com.ust.financeanalyzer.dto.Projectdto;
import com.ust.financeanalyzer.dto.Responsedto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
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

    public Mono<Responsedto> getStatisticsOfProject(String projectId) {
        Mono<Double> salaries = employeeRepository.findEmployeeSalaryAssignedToProjectId(projectId)
                .reduce(0.0, (sum, salary) -> sum + salary);

        Mono<Project> project = projectRepository.findById(projectId);

        Flux<Employee> empList = employeeRepository.findEmployeeAssignedToProjectId(projectId);

        return salaries.zipWith(project)
                .flatMap(tuple -> {
                    Double totalSalary = tuple.getT1();
                    Project projectData = tuple.getT2();

                    Projectdto projectdto = new Projectdto(
                            projectData.getProjectid(),
                            projectData.getProjectname(),
                            projectData.getBudget(),
                            projectData.getBudgetduration(),
                            projectData.getTeamSize(),
                            0.0,  // Expenditure will be calculated later
                            0.0   // Income will be calculated later
                    );

                    return empList.collectList()
                            .map(empListList -> {
                                double totalExpenditure =0.0;

                                Flux<Employeedto> empDTOList = Flux.fromIterable(empListList).map(employee -> {
                                    Employeedto empDTO = new Employeedto();
                                    empDTO.setId(employee.getId());
                                    empDTO.setName(employee.getName());
                                    empDTO.setContact(employee.getContact());
                                    empDTO.setEmail(employee.getEmail());
                                    empDTO.setProjectid(employee.getProjectid());

                                    double salary = employee.getSalary();
                                    double tax = 0.10 * salary;
                                    empDTO.setTax(tax);

                                    double adjustedSalary = calculateAdjustedSalary(salary, projectData.getBudgetduration());

                                    empDTO.setSalary(adjustedSalary);

                                    totalExpenditure += adjustedSalary + tax;

                                    return empDTO;
                                });

                                double income = projectData.getBudget() - totalExpenditure;

                                projectdto.setExpenditure(totalExpenditure);
                                projectdto.setIncome(income);

                                Responsedto response = new Responsedto();
                                response.setProjectdto(projectdto);
                                response.setEmpdto(empDTOList);

                                return response;
                            });
                });
    }

    private double calculateAdjustedSalary(double salary, String budgetDuration) {
        switch (budgetDuration.toLowerCase()) {
            case "yearly":
                return salary;
            case "monthly":
                return salary / 12;
            case "quarterly":
                return salary / 4;
            case "halfyearly":
                return salary / 2;
            default:
                return salary;
        }
    }

}
