package com.revature.services;

import java.util.ArrayList;
import java.util.List;

import com.revature.models.Employee;
import com.revature.models.ReimbursementForm;

public interface ApplicationServiceInterface {

	ReimbursementForm getById(int Id);

	List<ReimbursementForm> getAllForms();

	List<ReimbursementForm> getAllActive();

	List<ReimbursementForm> getMyFormHistory(String name);

	void approveForm(ReimbursementForm form, Employee e);

	void denyForm(ReimbursementForm form, Employee e);

	boolean addForm(ReimbursementForm form, Employee e);

	List<ReimbursementForm> getWaitingOnMe(Employee e);

//	void updateFormAward(ReimbursementForm form);
	
	void finalVerification(ReimbursementForm form, Boolean passed, Employee e);

	void requestMoreInformation(ReimbursementForm form, String lastName, Employee e);
	
	void updateForm(ReimbursementForm form);

	ArrayList<String> getAttachments(Integer formId);
}