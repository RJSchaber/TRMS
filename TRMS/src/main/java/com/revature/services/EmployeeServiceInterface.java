package com.revature.services;

import java.util.List;

import com.revature.models.Employee;

public interface EmployeeServiceInterface {

	Employee getEmployee(String lastName, String firstName);

	void updateEmployee(Employee e);

	List<Employee> getEmployees();

}