package com.revature.models;

import java.util.Date;
import java.util.List;

public class Employee {

	private String firstName;
	private String lastName;
	private Role role;
	private String departmentName;
	private String directSupervisor;
	private String departmentHead;
	private int reimbursementAvailable;
	private List<Integer> applicationHistory;
	private String lastApproved;

	public Employee() {
		super();
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getDirectSupervisor() {
		return directSupervisor;
	}

	public void setDirectSupervisor(String directSupervisor) {
		this.directSupervisor = directSupervisor;
	}

	public String getDepartmentHead() {
		return departmentHead;
	}

	public void setDepartmentHead(String departmentHead) {
		this.departmentHead = departmentHead;
	}

	public int getReimbursementAvailable() {
		return reimbursementAvailable;
	}

	public void setReimbursementAvailable(int reimbursementAvailable) {
		this.reimbursementAvailable = reimbursementAvailable;
	}

	public List<Integer> getApplicationHistory() {
		return applicationHistory;
	}

	public void setApplicationHistory(List<Integer> applicationHistory) {
		this.applicationHistory = applicationHistory;
	}

	public String getLastApproved() {
		return lastApproved;
	}

	public void setLastApproved(String lastApproved) {
		this.lastApproved = lastApproved;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((applicationHistory == null) ? 0 : applicationHistory.hashCode());
		result = prime * result + ((departmentHead == null) ? 0 : departmentHead.hashCode());
		result = prime * result + ((departmentName == null) ? 0 : departmentName.hashCode());
		result = prime * result + ((directSupervisor == null) ? 0 : directSupervisor.hashCode());
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((lastApproved == null) ? 0 : lastApproved.hashCode());
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + reimbursementAvailable;
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Employee other = (Employee) obj;
		if (applicationHistory == null) {
			if (other.applicationHistory != null)
				return false;
		} else if (!applicationHistory.equals(other.applicationHistory))
			return false;
		if (departmentHead == null) {
			if (other.departmentHead != null)
				return false;
		} else if (!departmentHead.equals(other.departmentHead))
			return false;
		if (departmentName == null) {
			if (other.departmentName != null)
				return false;
		} else if (!departmentName.equals(other.departmentName))
			return false;
		if (directSupervisor == null) {
			if (other.directSupervisor != null)
				return false;
		} else if (!directSupervisor.equals(other.directSupervisor))
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (lastApproved == null) {
			if (other.lastApproved != null)
				return false;
		} else if (!lastApproved.equals(other.lastApproved))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (reimbursementAvailable != other.reimbursementAvailable)
			return false;
		if (role != other.role)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Employee [firstName=" + firstName + ", lastName=" + lastName + ", role=" + role + ", departmentName="
				+ departmentName + ", directSupervisor=" + directSupervisor + ", departmentHead=" + departmentHead
				+ ", reimbursementAvailable=" + reimbursementAvailable + ", applicationHistory=" + applicationHistory
				+ ", lastApproved=" + lastApproved + "]";
	}
	

}
