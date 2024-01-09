package com.afs.restapi.service;

import com.afs.restapi.repository.CompanyRepository;
import com.afs.restapi.repository.EmployeeRepository;
import com.afs.restapi.repository.DepartmentRepository;
import com.afs.restapi.repository.TeamRepository;
import com.afs.restapi.repository.GroupRepository;
import com.afs.restapi.repository.CommunitRepository;
import org.springframework.stereotype.Service;

@Service
public class Example {
    private CompanyRepository companyRepository;
    private EmployeeRepository employeeRepository;
    private DepartmentRepository departmentRepository;
    private TeamRepository teamRepository;
    private GroupRepository groupRepository;
    private CommunitRepository communitRepository;

    public Example(CompanyRepository companyRepository, EmployeeRepository employeeRepository,
            DepartmentRepository departmentRepository, TeamRepository teamRepository,
            GroupRepository groupRepository, CommunitRepository communitRepository) {
        this.companyRepository = companyRepository;
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.teamRepository = teamRepository;
        this.groupRepository = groupRepository;
        this.communitRepository = communitRepository;
    }
}
