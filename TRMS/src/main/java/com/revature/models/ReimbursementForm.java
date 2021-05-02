package com.revature.models;

import java.util.Date;
import java.util.List;

public class ReimbursementForm {
	
	private int formId;
	private String employeeFirstName;
	private String employeeLastName;
	private String submittedDate;
	private String eventDate;
	
	private String location;
	private int totalcost;
	
	private ApprovalResponse supervisorApproval;
	private String supervisorApprovedOn; 
	private ApprovalResponse departmentHeadApproval;
	private String departmentHeadApprovedOn;
	private ApprovalResponse bencoApproval;
	
	private int projectedReimbursement;
	private int actualReimbursement;
	
	private EventType eventType;
	private String eventJustification;
	private String eventResult;
	private GradingFormat gradingFormat;
	private int cutOffGrade;
	
	private List<String> attachments;
	private String denialReason;
	private String formStatus; //active - inactive
	private String deniedBy;
	
	private FinalStatus finalStatus;
	private String waitingOn;
	
	public ReimbursementForm() {
		super();
	}

	public int getFormId() {
		return formId;
	}

	public void setFormId(int formId) {
		this.formId = formId;
	}

	public String getEmployeeFirstName() {
		return employeeFirstName;
	}

	public void setEmployeeFirstName(String employeeFirstName) {
		this.employeeFirstName = employeeFirstName;
	}

	public String getEmployeeLastName() {
		return employeeLastName;
	}

	public void setEmployeeLastName(String employeeLastName) {
		this.employeeLastName = employeeLastName;
	}

	public String getSubmittedDate() {
		return submittedDate;
	}

	public void setSubmittedDate(String submittedDate) {
		this.submittedDate = submittedDate;
	}

	public String getEventDate() {
		return eventDate;
	}

	public void setEventDate(String eventDate) {
		this.eventDate = eventDate;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public ApprovalResponse getSupervisorApproval() {
		return supervisorApproval;
	}

	public void setSupervisorApproval(ApprovalResponse supervisorApproval) {
		this.supervisorApproval = supervisorApproval;
	}

	public String getSupervisorApprovedOn() {
		return supervisorApprovedOn;
	}

	public void setSupervisorApprovedOn(String supervisorApprovedOn) {
		this.supervisorApprovedOn = supervisorApprovedOn;
	}

	public ApprovalResponse getDepartmentHeadApproval() {
		return departmentHeadApproval;
	}

	public void setDepartmentHeadApproval(ApprovalResponse departmentHeadApproval) {
		this.departmentHeadApproval = departmentHeadApproval;
	}

	public String getDepartmentHeadApprovedOn() {
		return departmentHeadApprovedOn;
	}

	public void setDepartmentHeadApprovedOn(String departmentHeadApprovedOn) {
		this.departmentHeadApprovedOn = departmentHeadApprovedOn;
	}

	public ApprovalResponse getBencoApproval() {
		return bencoApproval;
	}

	public void setBencoApproval(ApprovalResponse bencoApproval) {
		this.bencoApproval = bencoApproval;
	}

	public int getProjectedReimbursement() {
		return projectedReimbursement;
	}

	public void setProjectedReimbursement(int projectedReimbursement) {
		this.projectedReimbursement = projectedReimbursement;
	}

	public int getActualReimbursement() {
		return actualReimbursement;
	}

	public void setActualReimbursement(int actualReimbursement) {
		this.actualReimbursement = actualReimbursement;
	}

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	public String getEventJustification() {
		return eventJustification;
	}

	public void setEventJustification(String eventJustification) {
		this.eventJustification = eventJustification;
	}

	public String getEventResult() {
		return eventResult;
	}

	public void setEventResult(String eventResult) {
		this.eventResult = eventResult;
	}

	public GradingFormat getGradingFormat() {
		return gradingFormat;
	}

	public void setGradingFormat(GradingFormat gradingFormat) {
		this.gradingFormat = gradingFormat;
	}

	public int getCutOffGrade() {
		return cutOffGrade;
	}

	public void setCutOffGrade(int cutOffGrade) {
		this.cutOffGrade = cutOffGrade;
	}

	public List<String> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<String> attachments) {
		this.attachments = attachments;
	}

	public String getDenialReason() {
		return denialReason;
	}

	public void setDenialReason(String denialReason) {
		this.denialReason = denialReason;
	}

	public String getFormStatus() {
		return formStatus;
	}

	public void setFormStatus(String formStatus) {
		this.formStatus = formStatus;
	}

	public String getDeniedBy() {
		return deniedBy;
	}

	public void setDeniedBy(String deniedBy) {
		this.deniedBy = deniedBy;
	}

	public FinalStatus getFinalStatus() {
		return finalStatus;
	}

	public void setFinalStatus(FinalStatus finalStatus) {
		this.finalStatus = finalStatus;
	}

	public String getWaitingOn() {
		return waitingOn;
	}

	public void setWaitingOn(String waitingOn) {
		this.waitingOn = waitingOn;
	}

	public Integer getTotalcost() {
		return totalcost;
	}

	public void setTotalcost(int totalcost) {
		this.totalcost = totalcost;
	}

}
