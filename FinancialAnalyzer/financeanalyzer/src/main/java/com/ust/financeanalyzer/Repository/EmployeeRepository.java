
package com.ust.financeanalyzer.Repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.ust.financeanalyzer.Entity.Employee;
import reactor.core.publisher.Flux;

public interface EmployeeRepository extends ReactiveMongoRepository<Employee,String>{
        Flux<Double> findEmployeeSalaryAssignedToProjectId(String projectId);
        Flux<Employee> findEmployeeAssignedToProjectId(String projectId);
}
