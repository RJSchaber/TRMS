package com.revature.service;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.revature.data.EmployeeDao;
import com.revature.models.Employee;
import com.revature.models.Role;
import com.revature.services.EmployeeService;


@RunWith(MockitoJUnitRunner.class)
public class EmployeeServiceTest {
	
	@Mock
	EmployeeDao edaoMock;
	
	@Test 
	  public void testGetEmployee() {
		EmployeeService employeeService = new EmployeeService();
		
		Employee emp = new Employee();
		emp.setFirstName("Richard");
		emp.setLastName("Schaber");
		
		when(edaoMock.getEmployeeByName("Schaber", "Richard")).thenAnswer(new Answer<Employee>() {
			public Employee answer(InvocationOnMock invocation) throws Throwable {
				Employee employee = (Employee) invocation.getArguments()[0];
				
				employee.setLastName("Schaber");
				
				return employee;
			}
		});
		
		assertNull(emp.getLastName());
		
		emp = employeeService.getEmployee("Schaber", "Richard");
		
		assertNotNull(emp.getLastName());
		
		assertTrue(emp.getLastName().equals("Schaber"));
//		
//	  employeeService = new EmployeeService();
//	  
//	  ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
//	  Whitebox.setInternalState(employeeService, "ed", edaoMock);
//	  
//	  employeeService.getEmployee(emp.getLastName(), emp.getFirstName());
//	  
//	  verify(edaoMock).getEmployeeByName(captor.capture(),"nada");
//	  
//	  assertEquals(emp.getLastName(), captor.getValue()); 
	  
	  }
	
	@Test
	public void getEmployeeReturnsNull() {
		edaoMock = mock(EmployeeDao.class);
		
		EmployeeService employeeService = new EmployeeService();
		
		when(edaoMock.getEmployeeByName(null, null)).thenReturn(null);
		
		Employee employee = employeeService.getEmployee("Schaber", "Richard");
		assertEquals("Employee should be null", null, employee);
	}

	@Test
	public void updateEmployeeSuccessfully() {
		Employee test = new Employee();
		
		test.setLastName("Smith");
		test.setFirstName("John");
		test.setRole(Role.Associate);
		EmployeeService employeeService = new EmployeeService();
		
		EmployeeDao dao = mock(EmployeeDao.class);
		
//		assert("Update employee should return true", employeeService.updateEmployee(test));
	}


}
