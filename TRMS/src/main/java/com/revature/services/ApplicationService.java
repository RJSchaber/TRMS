package com.revature.services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.data.ApplicationDao;
import com.revature.data.ApplicationDaoInterface;
import com.revature.data.EmployeeDao;
import com.revature.data.EmployeeDaoInterface;
import com.revature.models.ApprovalResponse;
import com.revature.models.Employee;
import com.revature.models.FinalStatus;
import com.revature.models.GradingFormat;
import com.revature.models.ReimbursementForm;
import com.revature.models.Role;
import com.revature.utils.S3Util;

public class ApplicationService implements ApplicationServiceInterface {
	
	private ApplicationDaoInterface ad = new ApplicationDao();
	private EmployeeDaoInterface ed = new EmployeeDao();
	private EmailService es = new EmailService();
	private static Logger log = LogManager.getLogger(ApplicationService.class);
	
	@Override
	public ReimbursementForm getById(int id) {
		log.trace("Getting form by id: " + id);
		return ad.getFormById(id);
	}
	
	@Override
	public List<ReimbursementForm> getAllForms(){
		log.trace("Getting all forms");
		return ad.getAllForms();
	}
	
	@Override
	public List<ReimbursementForm> getAllActive(){
		log.trace("Getting all active forms");
		return ad.getAllActive();
	}
	
	@Override
	public List<ReimbursementForm> getWaitingOnMe(Employee e){
		log.trace("Getting all forms waiting on: " + e.getLastName());
		
		if(e.getRole().equals(Role.Benco)) {
			return ad.getWaitingOnBenco();
		}
		
		return ad.getWaitingOnMe(e.getLastName().toLowerCase());
	}
	
	@Override
	public List<ReimbursementForm> getMyFormHistory(String name){
		log.trace("Getting form history for: " + name);
		return ad.getFormHistory(name);
	}
	
	@Override
	public boolean addForm(ReimbursementForm form, Employee employee) {
		List<ReimbursementForm> forms = getAllForms();
		int id = forms.stream().mapToInt(form1 -> form1.getFormId()).max().orElse(0) + 1;
		
		log.trace("Adding a new form for: " + employee.getLastName());
		
		Date date = Calendar.getInstance().getTime();
	    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");  
	    String strDate = formatter.format(date);  
	    
		form.setEmployeeFirstName(employee.getFirstName());
		form.setEmployeeLastName(employee.getLastName());
		form.setSupervisorApproval(ApprovalResponse.NotChecked);
		form.setBencoApproval(ApprovalResponse.NotChecked);
		form.setDepartmentHeadApproval(ApprovalResponse.NotChecked);
		form.setSupervisorApprovedOn(null);
		form.setDepartmentHeadApprovedOn(null);
		form.setEventResult(null);
		form.setFormStatus("Active");
		form.setFinalStatus(FinalStatus.Pending);
		form.setFormId(id);
		
		form.setSubmittedDate(strDate);
		
		log.trace(form.getAttachments());
		
		if(employee.getRole().equals(Role.Associate) && form.getAttachments().isEmpty()) {
			form.setWaitingOn(employee.getDirectSupervisor().toLowerCase());
		}else if(employee.getRole().equals(Role.DirectSupervisor) || form.getAttachments().size() > 0) {
			form.setSupervisorApproval(ApprovalResponse.Approved);
			form.setSupervisorApprovedOn(strDate);
			form.setWaitingOn(employee.getDepartmentHead().toLowerCase());
		}else {
			form.setWaitingOn("Benco");
		}
		
		int projectedreimbursement = 0;
		
		switch(form.getEventType()) {
		
		case UniversityCourse:
			projectedreimbursement = (int) (form.getTotalcost() * .8);
			break;
		case Seminar:
			projectedreimbursement = (int) (form.getTotalcost() * .6);
			break;
		case CertificationPreparation:
			projectedreimbursement = (int) (form.getTotalcost() * .75);
			break;
		case Certification:
			projectedreimbursement = form.getTotalcost();
			break;
		case TechnicalTraining:
			projectedreimbursement = (int) (form.getTotalcost() * .9);
			break;
		default:
			projectedreimbursement = (int) (form.getTotalcost() * .3);
		}
		
		if(projectedreimbursement > 1000) {
			
			projectedreimbursement = 1000;
		}else 
			if(projectedreimbursement > employee.getReimbursementAvailable()) {
			
			projectedreimbursement = employee.getReimbursementAvailable();
		}
		
		form.setProjectedReimbursement(projectedreimbursement);
	
		try {
			ad.addForm(form);
			List<Integer> formHistory = employee.getApplicationHistory();
			formHistory.add(form.getFormId());
			employee.setApplicationHistory(formHistory);
			employee.setReimbursementAvailable(employee.getReimbursementAvailable() - projectedreimbursement);
			ed.updateEmployee(employee);
			log.trace(form);
			return true;
		}catch(Exception e) {
			for (StackTraceElement st : e.getStackTrace())
				log.debug(st.toString());
			return false;
		}
	}
	
	@Override
	public void approveForm(ReimbursementForm form, Employee e) {
		Date date = Calendar.getInstance().getTime();
	    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");  
	    String strDate = formatter.format(date);  
	    Employee empRequest = ed.getEmployeeByName(form.getEmployeeLastName(), form.getEmployeeFirstName());
	    String subject = "Youre Approved!";
	    String body = "Congratulations " + empRequest.getFirstName() + ". You have been approved for reimbursement by: " + e.getFirstName() + " " + e.getLastName() + ". Good luck passing your " + form.getEventType().toString();
	    
		log.trace(e.getLastName() + " is approving form for: " + empRequest.getLastName());
		
		if(e.getRole().equals(Role.DirectSupervisor)) {
			form.setSupervisorApproval(ApprovalResponse.Approved);
			form.setWaitingOn(empRequest.getDepartmentHead().toLowerCase());
			form.setSupervisorApprovedOn(strDate);
		}else if(e.getRole().equals(Role.DepartmentHead)) {
			form.setDepartmentHeadApproval(ApprovalResponse.Approved);
			form.setWaitingOn("Benco");
			form.setDepartmentHeadApprovedOn(strDate);
		}else if(e.getRole().equals(Role.Benco)) {
			form.setBencoApproval(ApprovalResponse.AwaitingAward);
			if(form.getGradingFormat().toString().equals("Graded")) {
				form.setWaitingOn(form.getEmployeeLastName().toLowerCase());
			}
			
			es.SendEmail(subject, body);
		}
		ad.updateForm(form);
	}
	
	@Override
	public void denyForm(ReimbursementForm form, Employee e) {
		String subject = "Your request for reimbursement has been denied";
	    String body = "We're sorry " + form.getEmployeeFirstName() + ". But your request for Reimbursement has been denied by: " + e.getFirstName() + " " + e.getLastName() + ". For the following: " + form.getDenialReason();
		log.trace(e.getLastName() + " is denying form for: " + form.getEmployeeLastName());
		
		switch(e.getRole()) {
		case DirectSupervisor:
			form.setSupervisorApproval(ApprovalResponse.Denied);
			break;
		case DepartmentHead:
			form.setDepartmentHeadApproval(ApprovalResponse.Denied);
			break;
		case Benco:
			form.setBencoApproval(ApprovalResponse.Denied);
			break;
		default:
			break;
		}
		
		form.setDeniedBy(e.getLastName().toLowerCase());
		form.setFormStatus("Inactive");
		form.setFinalStatus(FinalStatus.Denied);
		form.setActualReimbursement(0);
		form.setWaitingOn(null);
		e.setReimbursementAvailable(e.getReimbursementAvailable() + form.getProjectedReimbursement());
		ed.updateEmployee(e);
		es.SendEmail(subject, body);
		ad.updateForm(form);
	}

	@Override
	public void requestMoreInformation(ReimbursementForm form, String lastName, Employee e) {
		log.trace(e.getLastName() + " needs more information for form: " + form.getFormId() + " from: " + lastName);
		form.setWaitingOn(lastName.toLowerCase());
		String subject = "We need more info!";
	    String body = "Sorry " + lastName + " but " + e.getLastName() + "needs more information before your request can be approved.";
	    
		es.SendEmail(subject, body);
		
		ad.updateForm(form);
	}
	
	@Override
	public void finalVerification(ReimbursementForm form, Boolean passed, Employee e) {
		log.trace("Final verification for: " + form.getEmployeeLastName() + " on form: " + form.getFormId());
		Employee empRequest = ed.getEmployeeByName(form.getEmployeeLastName(), form.getEmployeeFirstName());
		String subject = "Request Processed.";
		String passfail = new String();

	    
	    
		String pattern = "MM/dd/yyyy";
		DateFormat df = new SimpleDateFormat(pattern);
		Date today = Calendar.getInstance().getTime(); 
		String todayAsString = df.format(today);
		
		if(passed) {
			form.setFinalStatus(FinalStatus.Approved);
			empRequest.setLastApproved(todayAsString);
			form.setEventResult("Pass");
			form.setBencoApproval(ApprovalResponse.Approved);
			passfail = "passed";
		}else {
			form.setFinalStatus(FinalStatus.Denied);
			form.setActualReimbursement(0);
			form.setBencoApproval(ApprovalResponse.Denied);
			form.setDeniedBy(e.getLastName().toLowerCase());
			form.setEventResult("Fail");
			passfail = "failed";
		}
		
		String body = "Hello " + form.getEmployeeLastName() + ". You " + passfail + " final verification.";
		es.SendEmail(subject, body);
		form.setFormStatus("Inactive");
		form.setWaitingOn(null);
		ed.updateEmployee(empRequest);
		ad.updateForm(form);
	}
	
	//needed so that user can update form with needed information like award
//	@Override
//	public void updateFormAward(ReimbursementForm form) {
//		log.trace("Uploading award for: " + form.getEmployeeLastName() + " on form: " + form.getFormId());
//		
//		form.setWaitingOn("Benco");
//		form.setEventResult(null);
//		es.SendEmail();
//		ad.updateForm(form);
//	}

	@Override
	public void updateForm(ReimbursementForm form) {
		log.trace("Uploading award for: " + form.getEmployeeLastName() + " on form: " + form.getFormId());

		ad.updateForm(form);
	}
	
	@Override
	public ArrayList<String> getAttachments(Integer formId){
		
		return (ArrayList<String>) ad.getFormById(formId).getAttachments();
	}
}
