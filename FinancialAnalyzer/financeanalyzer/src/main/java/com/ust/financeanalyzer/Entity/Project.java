
package com.ust.financeanalyzer.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "project")
public class Project {
    @Id
    private String projectid;
    private String projectname;
    private String budget;
    private String budgetduration;
    public Project() {
    }
    public Project(String projectid, String projectname, String budget, String budgetduration) {
        this.projectid = projectid;
        this.projectname = projectname;
        this.budget = budget;
        this.budgetduration = budgetduration;
    }
    public String getProjectid() {
        return projectid;
    }
    public void setProjectid(String projectid) {
        this.projectid = projectid;
    }
    public String getProjectname() {
        return projectname;
    }
    public void setProjectname(String projectname) {
        this.projectname = projectname;
    }
    public String getBudget() {
        return budget;
    }
    public void setBudget(String budget) {
        this.budget = budget;
    }
    public String getBudgetduration() {
        return budgetduration;
    }
    public void setBudgetduration(String budgetduration) {
        this.budgetduration = budgetduration;
    }
    
}
