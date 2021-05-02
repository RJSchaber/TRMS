package com.revature.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.DefaultConsistencyLevel;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.cql.SimpleStatementBuilder;
import com.revature.models.Employee;
import com.revature.models.Role;
import com.revature.utils.CassandraUtil;

public class EmployeeDao implements EmployeeDaoInterface{
	private CqlSession session = CassandraUtil.getInstance().getSession();
	private static Logger log = LogManager.getLogger(EmployeeDao.class);
	
	public static void createKeyspace() {
		StringBuilder trmsKeyspace = new StringBuilder("CREATE KEYSPACE IF NOT EXISTS ").append("TRMS with replication = {")
				.append("'class':'SimpleStrategy','replication_factor':1};");
		CassandraUtil.getInstance().getSession().execute(trmsKeyspace.toString());
	}
	
	public static void createEmployeeTable() {
		StringBuilder employeeTable = new StringBuilder("CREATE TABLE IF NOT EXISTS Employee (")
				.append("firstname text, lastname text, role text, departmentname text, supervisor text,")
				.append("departmenthead text, reimbursementavailable int, applicationhistory list<int>, lastapproved text, primary key(lastname, firstname));");
		CassandraUtil.getInstance().getSession().execute(employeeTable.toString());
	}

	@Override
	public void addDummies() {
//		createKeyspace();
//		createEmployeeTable();
//		
//		
//		Employee chip = new Employee();
//		chip.setFirstName("Chip");
//		chip.setLastName("Douglas");
//		chip.setDepartmentHead("francis");
//		chip.setDepartmentName("development");
//		chip.setDirectSupervisor("mcallister");
//		chip.setRole(Role.Associate);
//		chip.setLastApproved("09/02/2020");
//		chip.setReimbursementAvailable(200);
//		
//		Employee kevin = new Employee();
//		kevin.setFirstName("Kevin");
//		kevin.setLastName("Mcallister");
//		kevin.setDepartmentHead("francis");
//		kevin.setDepartmentName("development");
//		kevin.setDirectSupervisor(null);
//		kevin.setRole(Role.DirectSupervisor);
//		kevin.setLastApproved("04/12/2020");
//		kevin.setReimbursementAvailable(750);
//		
//		Employee steven = new Employee();
//		steven.setFirstName("Steven");
//		steven.setLastName("Francis");
//		steven.setDepartmentHead(null);
//		steven.setDepartmentName("development");
//		steven.setDirectSupervisor(null);
//		steven.setRole(Role.DepartmentHead);
//		steven.setLastApproved("11/11/2020");
//		steven.setReimbursementAvailable(500);
//		
//		Employee jeffrey = new Employee();
//		jeffrey.setFirstName("Jeffrey");
//		jeffrey.setLastName("Smith");
//		jeffrey.setDepartmentHead("francis");
//		jeffrey.setDepartmentName("development");
//		jeffrey.setDirectSupervisor("mcallister");
//		jeffrey.setRole(Role.Associate);
//		jeffrey.setLastApproved("01/17/2020");
//		jeffrey.setReimbursementAvailable(900);
//		
//		Employee diana = new Employee();
//		diana.setFirstName("Diana");
//		diana.setLastName("Kraft");
//		diana.setDepartmentHead("francis");
//		diana.setDepartmentName("development");
//		diana.setDirectSupervisor("mcallister");
//		diana.setRole(Role.Associate);
//		diana.setLastApproved("09/02/2015");
//		diana.setReimbursementAvailable(1000);
//		
//		Employee karen = new Employee();
//		karen.setFirstName("Karen");
//		karen.setLastName("Schroedinger");
//		karen.setDepartmentHead("francis");
//		karen.setDepartmentName("development");
//		karen.setDirectSupervisor("mcallister");
//		karen.setRole(Role.Associate);
//		karen.setLastApproved("12/05/2020");
//		karen.setReimbursementAvailable(435);
//		
//		Employee richard = new Employee();
//		richard.setFirstName("Richard");
//		richard.setLastName("Schaber");
//		richard.setDepartmentHead("brees");
//		richard.setDepartmentName("Benco");
//		richard.setDirectSupervisor(null);
//		richard.setRole(Role.Benco);
//		richard.setLastApproved("12/05/2019");
//		richard.setReimbursementAvailable(1000);
//		
//		Employee drew = new Employee();
//		drew.setFirstName("Drew");
//		drew.setLastName("Brees");
//		drew.setDepartmentHead(null);
//		drew.setDepartmentName("Benco");
//		drew.setDirectSupervisor(null);
//		drew.setRole(Role.Benco);
//		drew.setLastApproved("12/05/2019");
//		drew.setReimbursementAvailable(1000);
//		
//		Employee megan = new Employee();
//		megan.setFirstName("Megan");
//		megan.setLastName("Lindsay");
//		megan.setDepartmentHead("brees");
//		megan.setDepartmentName("Benco");
//		megan.setDirectSupervisor("schaber");
//		megan.setRole(Role.Benco);
//		megan.setLastApproved("12/05/2020");
//		megan.setReimbursementAvailable(435);
//		
//		List<Employee> dummies = new ArrayList<Employee>();
//		dummies.add(karen);
//		dummies.add(jeffrey);
//		dummies.add(steven);
//		dummies.add(kevin);
//		dummies.add(diana);
//		dummies.add(chip);
//		dummies.add(megan);
//		dummies.add(drew);
//		dummies.add(richard);
//		
//		String query = "Insert into employee (firstname, lastname, role, departmentname, supervisor, departmenthead, reimbursementavailable, lastapproved) values (?,?,?,?,?,?,?,?); ";
//		SimpleStatement s = new SimpleStatementBuilder(query)
//				.setConsistencyLevel(DefaultConsistencyLevel.LOCAL_QUORUM).build();
//		
//		for(Employee e : dummies) {
//			BoundStatement bound = session.prepare(s).bind(e.getFirstName(), e.getLastName(), e.getRole().toString(), e.getDepartmentName(), 
//					e.getDirectSupervisor(), e.getDepartmentHead(), e.getReimbursementAvailable(), e.getLastApproved());
//			session.execute(bound);
//		}
		
	}
	
	@Override
	public Employee getEmployeeByName(String lastName, String firstName) {
		Employee e = null;
		
		String query = "Select firstname, lastname, role, departmentname, supervisor, departmenthead, reimbursementavailable, lastapproved, applicationhistory from employee where lastname = ?;";
		
		BoundStatement bound = session.prepare(query).bind(lastName);
		ResultSet rs = session.execute(bound);
		Row data = rs.one();
		if(data != null) {
			e = new Employee();
			e.setFirstName(data.getString("firstname"));
			e.setLastName(data.getString("lastname"));
			e.setRole(Role.valueOf(data.getString("role")));
			e.setDepartmentName(data.getString("departmentname"));
			e.setDirectSupervisor(data.getString("supervisor"));
			e.setDepartmentHead(data.getString("departmenthead"));
			e.setReimbursementAvailable(data.getInt("reimbursementavailable"));
			e.setLastApproved(data.getString("lastapproved"));
			e.setApplicationHistory(data.getList("applicationhistory", Integer.class));
		}
		return e;
	}
	
	@Override
	public void updateEmployee(Employee e) {
		String query = "update employee set reimbursementavailable = ?, lastapproved = ?, applicationhistory = ? where lastname = ? And firstname = ?";
		SimpleStatement s = new SimpleStatementBuilder(query)
				.setConsistencyLevel(DefaultConsistencyLevel.LOCAL_QUORUM).build();
		BoundStatement bound = session.prepare(s).bind(e.getReimbursementAvailable(), e.getLastApproved(), e.getApplicationHistory(), e.getLastName(), e.getFirstName());
		session.execute(bound);
	}
	
	@Override
	public List<Employee> getEmployees() {
		List<Employee> employees = new ArrayList<Employee>();
		
		String query = "select firstname, lastname, role, departmentname, supervisor, departmenthead, reimbursementavailable, lastapproved, applicationhistory from employee";
		ResultSet rs = session.execute(query);
		
		rs.forEach(data -> {
			Employee e = new Employee();
			e.setFirstName(data.getString("firstname"));
			e.setLastName(data.getString("lastname"));
			e.setRole(Role.valueOf(data.getString("role")));
			e.setDepartmentName(data.getString("departmentname"));
			e.setDirectSupervisor(data.getString("supervisor"));
			e.setDepartmentHead(data.getString("departmenthead"));
			e.setReimbursementAvailable(data.getInt("reimbursementavailable"));
			e.setLastApproved(data.getString("lastapproved"));
			e.setApplicationHistory(data.getList("applicationhistory", Integer.class));
			employees.add(e);
		});
		
		return employees;
	}

}
