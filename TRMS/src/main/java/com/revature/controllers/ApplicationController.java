package com.revature.controllers;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.revature.models.Employee;
import com.revature.models.EventType;
import com.revature.models.GradingFormat;
import com.revature.models.ReimbursementForm;
import com.revature.services.ApplicationService;
import com.revature.services.ApplicationServiceInterface;
import com.revature.utils.S3Util;
import io.javalin.http.Context;
import io.javalin.http.UploadedFile;
import io.javalin.plugin.openapi.annotations.OpenApiFormParam;
import software.amazon.awssdk.core.sync.RequestBody;

public class ApplicationController {

	private static ApplicationServiceInterface as = new ApplicationService();
	private static Logger log = LogManager.getLogger(ApplicationController.class);

	public static void getForms(Context ctx) {
		log.trace("Attempting to get all forms");
		Employee e = ctx.attribute("user");

		if (e.getRole().toString().equals("Benco")) {
			List<ReimbursementForm> forms = as.getAllForms();
			log.trace("Returning list of forms");
			ctx.json(forms);
		} else {
			ctx.status(403);
			log.trace("Employee not a Benco." + ctx.status(403));
		}
	}

	public static void getActiveForms(Context ctx) {
		log.trace("Attempting to get all active forms");
		Employee e = ctx.attribute("user");

		if (e.getRole().toString().equals("Benco")) {
			List<ReimbursementForm> forms = as.getAllActive();
			if (forms == null) {
				ctx.result("There arent any active forms. ");
			} else {
				log.trace("Returning list of active forms");
				ctx.json(forms);
			}
		} else {
			ctx.status(403);
			log.trace("Employee not a Benco." + ctx.status(403));
		}
	}

	public static void getActiveFormsByUser(Context ctx) {
		Employee e = ctx.attribute("user");
		log.trace("Attempting to get all active forms for: " + e.getLastName());

		try {
			List<ReimbursementForm> forms = as.getAllActive().stream()
					.filter(form -> form.getEmployeeLastName().equals(e.getLastName())).collect(Collectors.toList());
			if (forms == null) {
				ctx.result("You do not have any active forms. ");
			} else {
				log.trace("Returning list of active forms");
				ctx.json(forms);
			}
		} catch (Exception e1) {
			log.warn(e1.getMessage());
			ctx.result("You do not have any active forms. ");
			for (StackTraceElement st : e1.getStackTrace())
				log.debug(st.toString());
		}
	}

	public static void getById(Context ctx) {
		Employee e = ctx.attribute("user");
		Integer formId = Integer.parseInt(ctx.formParam("formid"));

		ReimbursementForm form = as.getById(formId);
		ctx.json(form);
	}

	public static void waitingOnMe(Context ctx) {
		Employee e = ctx.attribute("user");

		List<ReimbursementForm> forms = as.getWaitingOnMe(e);
		if (forms == null) {
			ctx.result("You do not have any forms waiting for you. ");
		} else {
			ctx.json(forms);
		}
	}

	public static void myFormHistory(Context ctx) {
		Employee e = ctx.attribute("user");

		List<ReimbursementForm> forms = as.getMyFormHistory(e.getLastName());
		ctx.json(forms);
	}

	public static void addForm(Context ctx) {
		ReimbursementForm form = new ReimbursementForm();
		Employee e = ctx.attribute("user");

		String eventdate = ctx.formParam("eventdate");
		String location = ctx.formParam("location");
		int totalcost = Integer.parseInt(ctx.formParam("totalcost"));
		EventType eventtype = EventType.valueOf(ctx.formParam("eventtype"));
		String eventjustification = ctx.formParam("eventjustification");
		GradingFormat gradingformat = GradingFormat.valueOf(ctx.formParam("gradingformat"));
		int cutoffgrade = Integer.parseInt(ctx.formParam("cutoffgrade"));

		form.setCutOffGrade(cutoffgrade);
		form.setTotalcost(totalcost);
		form.setEventDate(eventdate);
		form.setEventJustification(eventjustification);
		form.setLocation(location);
		form.setGradingFormat(gradingformat);
		form.setEventType(eventtype);

		if (e.getReimbursementAvailable() == 0) {
			ctx.status(401);
			ctx.result("You dont have any reimbursement available " + e.getReimbursementAvailable());
		}
		
		List<String> attachments = new ArrayList<String>();
		Random r = new Random();
		if(ctx.isMultipartFormData()) {
			for (UploadedFile file : ctx.uploadedFiles()) {
				String key = r.nextInt(Integer.MAX_VALUE) + file.getFilename();
				attachments.add(key);
				log.trace("Uploading  " + r.nextInt(Integer.MAX_VALUE) + file.getFilename() + " to S3");
				S3Util.getInstance().UploadToBucket(key, RequestBody.fromInputStream(file.getContent(), file.getSize()));
			}
			log.trace("Uploads successful.  Updating form information...");
			form.setAttachments(attachments);
		}
		boolean added = as.addForm(form, e);
		if (added) {
			ctx.json(form);
		} else {
			ctx.status(409);
		}
	}

	public static void approveForm(Context ctx) {
		Employee e = ctx.attribute("user");
		
		int formId = Integer.parseInt(ctx.formParam("formid"));
		log.trace(formId);
		
		ReimbursementForm form = new ReimbursementForm();
		form = as.getById(formId);
		
		log.trace(e.getLastName() + " form " + form.getEmployeeLastName());
		
		if (e.getRole().toString().equals("Associate")) {
			ctx.status(403);
			log.trace("Employee not allowed to approve form." + ctx.status(403));
		}else if(e.getLastName().equals(form.getEmployeeLastName())) {
			ctx.status(403);
			log.trace("Cant approve your own form ." + ctx.status(403));
			ctx.result("Cant approve your own form ");
		}else if(e.getLastName().toLowerCase().equals(form.getWaitingOn().toLowerCase()) || e.getRole().toString().equals(form.getWaitingOn())) {
			as.approveForm(form, e);
			ctx.json(form);
		}else {
			ctx.status(403);
			log.trace("Can only approve a form waiting on you ." + ctx.status(403));
			ctx.result("Can only approve a form waiting on you  ");
		}
	}

	public static void denyForm(Context ctx) {
		Employee e = ctx.attribute("user");

		if (e.getRole().toString().equals("Associate")) {
			ctx.status(403);
			log.trace("Employee not allowed to deny form." + ctx.status(403));
		}

		Integer formId = Integer.parseInt(ctx.formParam("formId"));
		String denialReason = ctx.formParam("denialreason");
		ReimbursementForm form = as.getById(formId);
		form.setDenialReason(denialReason);
		as.denyForm(form, e);
		ctx.json(form);
	}

	public static void requestInformation(Context ctx) {
		Employee e = ctx.attribute("user");
		if (e.getRole().toString().equals("Benco")) {
			int formId = Integer.parseInt(ctx.formParam("formid"));
			String employeeName = ctx.formParam("lastname");
			ReimbursementForm form = as.getById(formId);

			as.requestMoreInformation(form, employeeName, e);
			log.trace("Requesting information for: " + formId + " " + employeeName);
			ctx.json(form);
		} else {
			ctx.status(403);
			log.trace("Employee not a Benco." + ctx.status(403));
		}
	}

	public static void uploadAward(Context ctx) {
		Employee emp = ctx.attribute("user");
		log.trace("Attempting to upload awards. ");

		List<String> attachments = new ArrayList<String>();
		String formid = ctx.formParam("formid");
		Random r = new Random();

		for (UploadedFile file : ctx.uploadedFiles()) {
			String key = r.nextInt(Integer.MAX_VALUE) + file.getFilename();
			attachments.add(key);
			log.trace("Uploading  " + r.nextInt(Integer.MAX_VALUE) + file.getFilename() + " to S3");
			S3Util.getInstance().UploadToBucket(key, RequestBody.fromInputStream(file.getContent(), file.getSize()));
		}

		log.trace("Uploads successful.  Updating form information...");
		ReimbursementForm form = as.getById(Integer.parseInt(formid));
		List<String> atts = new ArrayList<String>(form.getAttachments());

		log.trace("form for: " + form.getEmployeeLastName());

		for (String att : attachments) {
			atts.add(att);
			log.trace("Adding these attachments " + att);
		}

		form.setAttachments(atts);
		form.setWaitingOn("Benco");
		as.updateForm(form);

		ctx.json(attachments);
	}

	public static void getAward(Context ctx) {
		Employee emp = ctx.attribute("user");
		String name = ctx.formParam("filename");

		log.trace("Getting award by filename");

		try {
			InputStream s = S3Util.getInstance().getObject(name);
			ctx.result(s);
			log.trace("Award gotten successfully. ");
		} catch (Exception e) {
			ctx.status(500);
			log.trace(ctx.status(500));
		}
	}

	public static void getAwardByForm(Context ctx) {
		Employee emp = ctx.attribute("user");

		int formId = Integer.parseInt(ctx.formParam("formid"));
		List<String> attachments = new ArrayList<String>();
		log.trace("Getting awards for form: " + formId);

		ArrayList<String> formatt = as.getAttachments(formId);
		log.trace(formatt);

		for (String name : formatt) {
			try {
				log.trace(name);
				String s = "https://revtrmsrjs.s3.us-east-2.amazonaws.com/" + name;
				log.trace(s);
				attachments.add(s);
				log.trace("Award gotten successfully: " + name);
			} catch (Exception e) {
				ctx.status(500);
				log.error(ctx.status(500));
			}
		}

		log.trace(attachments);
		ctx.json(attachments);
	}
	
	public static void finalApproval(Context ctx) {
		Employee emp = ctx.attribute("user");
		int formId = Integer.parseInt(ctx.formParam("formid"));
		boolean passed = Boolean.parseBoolean(ctx.formParam("passed"));
		ReimbursementForm form = as.getById(formId);
		
		if(emp.getLastName().toLowerCase().equals(form.getEmployeeLastName().toLowerCase())) {
			ctx.status(403);
			log.trace("Cant approve your own form." + ctx.status(403));
			ctx.result("Cant approve your own form ");
		}else if(emp.getRole().toString() != "Benco") {
			ctx.status(403);
			log.trace("Only a Benco can make final approval." + ctx.status(403));
			ctx.result("Only a Benco can make final approval");
		}else {
			as.finalVerification(form, passed, emp);
			ctx.json(form);
		}
		
		
	}

}
