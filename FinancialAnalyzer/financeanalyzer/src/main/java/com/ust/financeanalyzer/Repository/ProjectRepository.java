
package com.ust.financeanalyzer.Repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.ust.financeanalyzer.Entity.Project;

public interface ProjectRepository extends ReactiveMongoRepository<Project,String> {

}
