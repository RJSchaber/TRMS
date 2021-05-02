package com.revature.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.data.ApplicationDao;
import com.revature.data.ApplicationDaoInterface;
import com.revature.data.EmployeeDao;
import com.revature.data.EmployeeDaoInterface;
import com.revature.models.Employee;
import com.revature.models.ReimbursementForm;
import com.revature.utils.S3Util;

public class EmployeeService implements EmployeeServiceInterface {
	private EmployeeDaoInterface ed = new EmployeeDao();
	private ApplicationDaoInterface ad = new ApplicationDao();
	private static Logger log = LogManager.getLogger(EmployeeService.class);

	@Override
	public Employee getEmployee(String lastName, String firstName) {
		Employee emp = ed.getEmployeeByName(lastName, firstName);
		List<ReimbursementForm> forms = new ArrayList<ReimbursementForm>();
		
		Date date = Calendar.getInstance().getTime();
	    Date date1 = null;
	    
	    forms = ad.getAllActive().stream().filter(form -> form.getEmployeeLastName().equals(lastName)).collect(Collectors.toList());
	    
		try {
			date1 = new SimpleDateFormat("MM/dd/yyyy").parse(emp.getLastApproved());
		} catch (ParseException e) {
			e.printStackTrace();
		}
	    Calendar c1 = Calendar.getInstance();
	    Calendar c2 = Calendar.getInstance();

	    c1.setTime(date);
	    c2.setTime(date1);
	    
	    if(c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR) > 0 && forms.size() < 1 && emp.getReimbursementAvailable() < 1000) {
	    	emp.setReimbursementAvailable(1000);
	    	ed.updateEmployee(emp);
	    }
		
		return emp;
	}

	@Override
	public void updateEmployee(Employee e) {
		ed.updateEmployee(e);
	}

	@Override
	public List<Employee> getEmployees() {
		return ed.getEmployees();
	}
	
}
