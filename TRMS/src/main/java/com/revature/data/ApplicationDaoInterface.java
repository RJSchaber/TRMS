package com.revature.data;

import java.util.List;

import com.revature.models.ReimbursementForm;

public interface ApplicationDaoInterface {

	void createKeyspace();

	void createApplicationTable();

	ReimbursementForm getFormById(int id);

	List<ReimbursementForm> getAllForms();

	List<ReimbursementForm> getAllActive();

	List<ReimbursementForm> getWaitingOnMe(String lastName);

	List<ReimbursementForm> getFormHistory(String name);

	void addForm(ReimbursementForm form);

	void updateForm(ReimbursementForm form);

	List<ReimbursementForm> getWaitingOnBenco();

	void addDummyForms();

}