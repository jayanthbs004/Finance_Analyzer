
package com.ust.financeanalyzer.Repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.ust.financeanalyzer.Entity.Employee;

public interface EmployeeRepository extends ReactiveMongoRepository<Employee,String>{

}
