package com.revature.data;

import java.util.List;

import com.revature.models.Employee;

public interface EmployeeDaoInterface {

	void addDummies();

	Employee getEmployeeByName(String lastName, String firstName);

	void updateEmployee(Employee e);

	List<Employee> getEmployees();

}