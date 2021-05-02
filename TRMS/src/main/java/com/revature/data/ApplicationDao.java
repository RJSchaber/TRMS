package com.revature.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.DefaultConsistencyLevel;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.cql.SimpleStatementBuilder;
import com.revature.models.ApprovalResponse;
import com.revature.models.Employee;
import com.revature.models.EventType;
import com.revature.models.FinalStatus;
import com.revature.models.GradingFormat;
import com.revature.models.ReimbursementForm;
import com.revature.utils.CassandraUtil;
import com.revature.utils.S3Util;

public class ApplicationDao implements ApplicationDaoInterface {
	private CqlSession session = CassandraUtil.getInstance().getSession();
	private static Logger log = LogManager.getLogger(ApplicationDao.class);
	
	@Override
	public void createKeyspace() {
		StringBuilder trmsKeyspace = new StringBuilder("CREATE KEYSPACE IF NOT EXISTS ").append("TRMS with replication = {")
				.append("'class':'SimpleStrategy','replication_factor':1};");
		CassandraUtil.getInstance().getSession().execute(trmsKeyspace.toString());
	}
	
	@Override
	public void createApplicationTable() {
		StringBuilder appTable = new StringBuilder("CREATE TABLE IF NOT EXISTS Application (")
				.append("formid int, firstname text, lastname text, totalcost int, submitteddate text, eventdate text, location text, projectedreimbursement int, actualreimbursement int, eventtype text, eventresult text, justification text, gradingformat text, cutoffgrade int, attachments list<text>, ")
				.append("supervisorapproval text, supervisorapprovedon text, departmentheadapproval text, departmentheadapprovedon text, bencoapproval text, denialreason text, formstatus text, deniedby text, finalstatus text, waitingon text, primary key(formid));");
		CassandraUtil.getInstance().getSession().execute(appTable.toString());
	}
	
	@Override
	public ReimbursementForm getFormById(int id) {
		ReimbursementForm form = new ReimbursementForm();
		log.trace("Getting form for ID: " + id);
		
		String query = "Select * from application where formid = ?;";
		BoundStatement bound = session.prepare(query).bind(id);
		ResultSet rs = session.execute(bound);
		Row data = rs.one();
		if(data != null) {
			form = new ReimbursementForm();
			
			form.setFormId(data.getInt("formid"));
			form.setEmployeeFirstName(data.getString("firstname"));
			form.setEmployeeLastName(data.getString("lastname"));
			form.setActualReimbursement(data.getInt("actualreimbursement"));
			form.setAttachments(data.getList("attachments", String.class));
			form.setBencoApproval(ApprovalResponse.valueOf(data.getString("bencoapproval")));
			form.setCutOffGrade(data.getInt("cutoffgrade"));
			form.setDenialReason(data.getString("denialreason"));
			form.setDeniedBy(data.getString("deniedby"));
			form.setTotalcost(data.getInt("totalcost"));
			
			form.setDepartmentHeadApproval(ApprovalResponse.valueOf(data.getString("departmentheadapproval")));
			form.setDepartmentHeadApprovedOn(data.getString("departmentheadapprovedon"));
			form.setSupervisorApprovedOn(data.getString("supervisorapprovedon"));
			form.setEventDate(data.getString("eventdate"));
			form.setEventJustification(data.getString("justification"));
			form.setEventResult(data.getString("eventresult"));
			form.setEventType(EventType.valueOf(data.getString("eventtype")));
			
			form.setFinalStatus(FinalStatus.valueOf(data.getString("finalstatus")));
			form.setFormStatus(data.getString("formstatus"));
			form.setGradingFormat(GradingFormat.valueOf(data.getString("gradingformat")));
			form.setLocation(data.getString("location"));
			form.setProjectedReimbursement(data.getInt("projectedreimbursement"));
			form.setSubmittedDate(data.getString("submitteddate"));
			form.setSupervisorApproval(ApprovalResponse.valueOf(data.getString("supervisorapproval")));
			form.setWaitingOn(data.getString("waitingon"));
			
		}
		
		log.trace("Returning form: " + form);
		return form;
	}

	@Override
	public List<ReimbursementForm> getAllForms() {
		
		log.trace("Getting all forms");
		List<ReimbursementForm> forms = new ArrayList<ReimbursementForm>();
		
		String query = "select * from application";
		ResultSet rs = session.execute(query);
		
		rs.forEach(data -> {
			ReimbursementForm form = new ReimbursementForm();
			
			form.setFormId(data.getInt("formid"));
			form.setEmployeeFirstName(data.getString("firstname"));
			form.setEmployeeLastName(data.getString("lastname"));
			form.setActualReimbursement(data.getInt("actualreimbursement"));
			form.setAttachments(data.getList("attachments", String.class));
			form.setBencoApproval(ApprovalResponse.valueOf(data.getString("bencoapproval")));
			form.setCutOffGrade(data.getInt("cutoffgrade"));
			form.setDenialReason(data.getString("denialreason"));
			form.setDeniedBy(data.getString("deniedby"));
			form.setTotalcost(data.getInt("totalcost"));
			
			form.setDepartmentHeadApproval(ApprovalResponse.valueOf(data.getString("departmentheadapproval")));
			form.setDepartmentHeadApprovedOn(data.getString("departmentheadapprovedon"));
			form.setSupervisorApprovedOn(data.getString("supervisorapprovedon"));
			form.setEventDate(data.getString("eventdate"));
			form.setEventJustification(data.getString("justification"));
			form.setEventResult(data.getString("eventresult"));
			form.setEventType(EventType.valueOf(data.getString("eventtype")));
			
			form.setFinalStatus(FinalStatus.valueOf(data.getString("finalstatus")));
			form.setFormStatus(data.getString("formstatus"));
			form.setGradingFormat(GradingFormat.valueOf(data.getString("gradingformat")));
			form.setLocation(data.getString("location"));
			form.setProjectedReimbursement(data.getInt("projectedreimbursement"));
			form.setSubmittedDate(data.getString("submitteddate"));
			form.setSupervisorApproval(ApprovalResponse.valueOf(data.getString("supervisorapproval")));
			form.setWaitingOn(data.getString("waitingon"));
			forms.add(form);
		});
		
		log.trace(forms);
		return forms;
	}

	@Override
	public List<ReimbursementForm> getAllActive() {
		List<ReimbursementForm> forms = new ArrayList<ReimbursementForm>();
		log.trace("Getting all active forms");
		
		try {
			forms = getAllForms().stream().filter(form -> form.getFormStatus().equals("Active")).collect(Collectors.toList());
		}catch (Exception e) {
			log.warn(e.getMessage());
			for (StackTraceElement st : e.getStackTrace())
				log.debug(st.toString());
			return null;
		}
		
		log.trace(forms);
		return forms;
	}

	@Override
	public List<ReimbursementForm> getWaitingOnMe(String lastName) {
		List<ReimbursementForm> forms = new ArrayList<ReimbursementForm>();
		log.trace("Getting forms waiting on: " + lastName);
		
		try {
			forms = getAllForms().stream().filter(form -> form.getWaitingOn() != null).collect(Collectors.toList());
			forms = forms.stream().filter(form -> form.getWaitingOn().equals(lastName)).collect(Collectors.toList());
	}catch (Exception e) {
		log.warn(e.getMessage());
		for (StackTraceElement st : e.getStackTrace())
			log.debug(st.toString());
		return null;
	}
		log.trace(forms);
		return forms;
	}

	@Override
	public List<ReimbursementForm> getFormHistory(String name) {
		List<ReimbursementForm> forms = new ArrayList<ReimbursementForm>();
		log.trace("Getting form history for: " + name);
		
		forms = getAllForms().stream().filter(form -> form.getEmployeeLastName().equals(name)).collect(Collectors.toList());
		
		log.trace(forms);
		return forms;
	}

	@Override
	public void addForm(ReimbursementForm form) {
		log.trace("Adding form for: " + form.getEmployeeLastName());
		
		try {
			String query = "Insert into application (formid, firstname, lastname, totalcost, submitteddate, eventdate, location, projectedreimbursement, actualreimbursement, eventtype, eventresult, justification, gradingformat, cutoffgrade, attachments, supervisorapproval, supervisorapprovedon, departmentheadapproval, departmentheadapprovedon, bencoapproval, denialreason, formstatus, deniedby, finalstatus, waitingon) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); ";
			SimpleStatement s = new SimpleStatementBuilder(query)
					.setConsistencyLevel(DefaultConsistencyLevel.LOCAL_QUORUM).build();
			BoundStatement bound = session.prepare(s).bind(form.getFormId(), form.getEmployeeFirstName(),form.getEmployeeLastName(),form.getTotalcost(),form.getSubmittedDate(),form.getEventDate(),form.getLocation(), form.getProjectedReimbursement(), form.getActualReimbursement(), form.getEventType().toString(), form.getEventResult(), form.getEventJustification(),form.getGradingFormat().toString(),form.getCutOffGrade(), form.getAttachments(), form.getSupervisorApproval().toString(), form.getSupervisorApprovedOn(), form.getDepartmentHeadApproval().toString(), form.getDepartmentHeadApprovedOn(), form.getBencoApproval().toString(), form.getDenialReason(), form.getFormStatus(), form.getDeniedBy(), form.getFinalStatus().toString(), form.getWaitingOn());
			session.execute(bound);
			log.trace("Form Added Successfully");
		}catch(Exception e) {
			log.warn(e.getMessage());
			for (StackTraceElement st : e.getStackTrace())
				log.debug(st.toString());
		}
	}

	@Override
	public void updateForm(ReimbursementForm form) {
		log.trace("Updating form for: " + form.getEmployeeLastName());

		try {
			String query = "Update application set supervisorapproval = ?, supervisorapprovedon = ?, departmentheadapproval = ?, departmentheadapprovedon = ?, actualreimbursement = ?, eventresult = ?, bencoapproval = ?, attachments = ?, denialreason = ?, formstatus = ?, deniedby = ?, finalstatus = ?, waitingon = ? where formid = ?";
			SimpleStatement s = new SimpleStatementBuilder(query)
					.setConsistencyLevel(DefaultConsistencyLevel.LOCAL_QUORUM).build();
			log.trace(form.getSupervisorApproval().toString()+ " " + form.getSupervisorApprovedOn()+ " " + form.getDepartmentHeadApproval().toString()+ " " + form.getDepartmentHeadApprovedOn()+ " " + form.getActualReimbursement()+ " " + form.getEventResult() + " " + form.getBencoApproval().toString()+ " " + form.getAttachments()+ " " + form.getDenialReason()+ " " + form.getFormStatus()+ " " + form.getDeniedBy()+ " " + form.getFinalStatus().toString()+ " " + form.getWaitingOn()+ " " + form.getFormId());
			
			BoundStatement bound = session.prepare(s).bind(form.getSupervisorApproval().toString(), form.getSupervisorApprovedOn(), form.getDepartmentHeadApproval().toString(), form.getDepartmentHeadApprovedOn(), form.getActualReimbursement(), form.getEventResult(), form.getBencoApproval().toString(), form.getAttachments(), form.getDenialReason(), form.getFormStatus(), form.getDeniedBy(), form.getFinalStatus().toString(), form.getWaitingOn(), form.getFormId());
			session.execute(bound);
		}catch(Exception e) {
			log.warn(e.getMessage());
			for (StackTraceElement st : e.getStackTrace())
				log.debug(st.toString());
		}
	}

	@Override
	public List<ReimbursementForm> getWaitingOnBenco() {
		log.trace("Getting forms that are waiting on a Benco");
		List<ReimbursementForm> forms = new ArrayList<ReimbursementForm>();
		forms = getAllForms().stream().filter(form -> form.getWaitingOn() != null).collect(Collectors.toList());

		forms = forms.stream().filter(form -> form.getWaitingOn().equals("Benco")).collect(Collectors.toList());
		
		log.trace(forms);
		return forms;
	}

	
	
	@Override
	public void addDummyForms() {
//		List<ReimbursementForm> forms = new ArrayList<ReimbursementForm>();
//		
//		ReimbursementForm form0 = new ReimbursementForm();
//		form0.setFormId(0);
//		form0.setEmployeeFirstName("Richard");
//		form0.setEmployeeLastName("Schaber");
//		form0.setActualReimbursement(550);
//		form0.setBencoApproval(ApprovalResponse.Approved);
//		form0.setCutOffGrade(65);
//		form0.setDenialReason(null);
//		form0.setDeniedBy(null);
//		form0.setTotalcost(550);
//		
//		form0.setDepartmentHeadApproval(ApprovalResponse.Approved);
//		form0.setDepartmentHeadApprovedOn("12/08/2017");
//		form0.setSupervisorApprovedOn("12/07/2017");
//		form0.setEventDate("12/17/2017");
//		form0.setEventJustification("Relevant training in new technologies that will increase application performance.");
//		form0.setEventResult("Pass");
//		form0.setEventType(EventType.Certification);
//		
//		form0.setFinalStatus(FinalStatus.Approved);
//		form0.setFormStatus("Inactive");
//		form0.setGradingFormat(GradingFormat.Graded);
//		form0.setLocation("Springfield");
//		form0.setProjectedReimbursement(550);
//		form0.setSubmittedDate("12/05/2017");
//		form0.setSupervisorApproval(ApprovalResponse.Approved);
//		form0.setWaitingOn(null);
//		forms.add(form0);
//		
//		//new form
//		ReimbursementForm form1 = new ReimbursementForm();
//		form1.setFormId(1);
//		form1.setEmployeeFirstName("Chip");
//		form1.setEmployeeLastName("Douglas");
//		form1.setActualReimbursement(225);
//		form1.setBencoApproval(ApprovalResponse.Approved);
//		form1.setCutOffGrade(70);
//		form1.setDenialReason(null);
//		form1.setDeniedBy(null);
//		form1.setTotalcost(450);
//		
//		form1.setDepartmentHeadApproval(ApprovalResponse.Approved);
//		form1.setDepartmentHeadApprovedOn("01/17/2019");
//		form1.setSupervisorApprovedOn("01/27/2019");
//		form1.setEventDate("02/17/2019");
//		form1.setEventJustification("Relevant training in new technologies that will increase application performance.");
//		form1.setEventResult("Pass");
//		form1.setEventType(EventType.CertificationPreparation);
//		
//		form1.setFinalStatus(FinalStatus.Approved);
//		form1.setFormStatus("Inactive");
//		form1.setGradingFormat(GradingFormat.Graded);
//		form1.setLocation("Springfield");
//		form1.setProjectedReimbursement(300);
//		form1.setSubmittedDate("01/10/2019");
//		form1.setSupervisorApproval(ApprovalResponse.Approved);
//		form1.setWaitingOn(null);
//		forms.add(form1);
//		
//		//new form
//		ReimbursementForm form2 = new ReimbursementForm();
//		form2.setFormId(2);
//		form2.setEmployeeFirstName("Steven");
//		form2.setEmployeeLastName("Francis");
//		form2.setActualReimbursement(800);
//		form2.setBencoApproval(ApprovalResponse.Approved);
//		form2.setCutOffGrade(85);
//		form2.setDenialReason(null);
//		form2.setDeniedBy(null);
//		form2.setTotalcost(1000);
//		
//		form2.setDepartmentHeadApproval(ApprovalResponse.Approved);
//		form2.setDepartmentHeadApprovedOn("10/08/2018");
//		form2.setSupervisorApprovedOn("10/07/2018");
//		form2.setEventDate("10/16/2018");
//		form2.setEventJustification("Relevant training in new technologies that will increase application performance.");
//		form2.setEventResult("Pass");
//		form2.setEventType(EventType.UniversityCourse);
//		
//		form2.setFinalStatus(FinalStatus.Approved);
//		form2.setFormStatus("Inactive");
//		form2.setGradingFormat(GradingFormat.Graded);
//		form2.setLocation("Springfield");
//		form2.setProjectedReimbursement(800);
//		form2.setSubmittedDate("10/04/2018");
//		form2.setSupervisorApproval(ApprovalResponse.Approved);
//		form2.setWaitingOn(null);
//		forms.add(form2);
//		
//		//new form
//		ReimbursementForm form3 = new ReimbursementForm();
//		form3.setFormId(3);
//		form3.setEmployeeFirstName("Richard");
//		form3.setEmployeeLastName("Schaber");
//		form3.setActualReimbursement(345);
//		form3.setBencoApproval(ApprovalResponse.Approved);
//		form3.setCutOffGrade(90);
//		form3.setDenialReason(null);
//		form3.setDeniedBy(null);
//		form3.setTotalcost(500);
//		
//		form3.setDepartmentHeadApproval(ApprovalResponse.Approved);
//		form3.setDepartmentHeadApprovedOn("07/12/2018");
//		form3.setSupervisorApprovedOn("07/08/2018");
//		form3.setEventDate("07/22/2018");
//		form3.setEventJustification("Relevant training in new technologies that will increase application performance.");
//		form3.setEventResult("Pass");
//		form3.setEventType(EventType.Certification);
//		
//		form3.setFinalStatus(FinalStatus.Approved);
//		form3.setFormStatus("Inactive");
//		form3.setGradingFormat(GradingFormat.Graded);
//		form3.setLocation("Springfield");
//		form3.setProjectedReimbursement(450);
//		form3.setSubmittedDate("07/02/2018");
//		form3.setSupervisorApproval(ApprovalResponse.Approved);
//		form3.setWaitingOn(null);
//		forms.add(form3);
//		
//		//new form
//		ReimbursementForm form4 = new ReimbursementForm();
//		form4.setFormId(4);
//		form4.setEmployeeFirstName("Diana");
//		form4.setEmployeeLastName("Kraft");
//		form4.setActualReimbursement(0);
//		form4.setBencoApproval(ApprovalResponse.Denied);
//		form4.setCutOffGrade(0);
//		form4.setDenialReason(null);
//		form4.setDeniedBy(null);
//		form4.setTotalcost(800);
//		
//		form4.setDepartmentHeadApproval(ApprovalResponse.Approved);
//		form4.setDepartmentHeadApprovedOn("04/08/2018");
//		form4.setSupervisorApprovedOn("04/08/2018");
//		form4.setEventDate("04/19/2018");
//		form4.setEventJustification("Relevant training in new technologies that will increase application performance.");
//		form4.setEventResult("Fail");
//		form4.setEventType(EventType.Seminar);
//		
//		form4.setFinalStatus(FinalStatus.Denied);
//		form4.setFormStatus("Inactive");
//		form4.setGradingFormat(GradingFormat.NotGraded);
//		form4.setLocation("Springfield");
//		form4.setProjectedReimbursement(550);
//		form4.setSubmittedDate("04/04/2018");
//		form4.setSupervisorApproval(ApprovalResponse.Approved);
//		form4.setWaitingOn(null);
//		forms.add(form4);
//		
//		
//		String query = "Insert into application (formid, firstname, lastname, totalcost, submitteddate, eventdate, location, projectedreimbursement, actualreimbursement, eventtype, eventresult, justification, gradingformat, cutoffgrade, attachments, supervisorapproval, supervisorapprovedon, departmentheadapproval, departmentheadapprovedon, bencoapproval, denialreason, formstatus, deniedby, finalstatus, waitingon) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); ";
//		SimpleStatement s = new SimpleStatementBuilder(query)
//				.setConsistencyLevel(DefaultConsistencyLevel.LOCAL_QUORUM).build();
//		
//		for(ReimbursementForm form : forms) {
//			BoundStatement bound = session.prepare(s).bind(form.getFormId(), form.getEmployeeFirstName(),form.getEmployeeLastName(),form.getTotalcost(),form.getSubmittedDate(),form.getEventDate(),form.getLocation(), form.getProjectedReimbursement(), form.getActualReimbursement(), form.getEventType().toString(), form.getEventResult(), form.getEventJustification(),form.getGradingFormat().toString(),form.getCutOffGrade(), form.getAttachments(), form.getSupervisorApproval().toString(), form.getSupervisorApprovedOn(), form.getDepartmentHeadApproval().toString(), form.getDepartmentHeadApprovedOn(), form.getBencoApproval().toString(), form.getDenialReason(), form.getFormStatus(), form.getDeniedBy(), form.getFinalStatus().toString(), form.getWaitingOn());
//			session.execute(bound);
//		}
		
		
	}
	

}
