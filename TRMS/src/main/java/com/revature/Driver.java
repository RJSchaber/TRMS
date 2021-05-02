package com.revature;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static io.javalin.apibuilder.ApiBuilder.*;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.revature.data.ApplicationDao;
import com.revature.data.ApplicationDaoInterface;
import com.revature.data.EmployeeDao;
import com.revature.services.EmailService;
import com.revature.services.EmailServiceInterface;
import com.revature.utils.CassandraUtil;
import com.revature.utils.S3Util;
import com.revature.controllers.ApplicationController;
import com.revature.controllers.EmployeeController;
import io.javalin.Javalin;
import io.javalin.core.util.RouteOverviewPlugin;

public class Driver {
	public static EmployeeDao ed = new EmployeeDao();
	public static ApplicationDaoInterface ad = new ApplicationDao();
	private static Logger log = LogManager.getLogger(Driver.class);
	
	public static void main(String[] args) {
//		ed.addDummies();
//		ad.createApplicationTable();		
//		ad.addDummyForms();
//		EmailServiceInterface sendEmail = new EmailService();
//		sendEmail.SendEmail();
		
		javalin();
	}
	
	private static void javalin() {
		Javalin app = Javalin.create(conf -> {
			conf.requestLogger((ctx, responseTime) -> {
				log.debug(ctx.method() + " -> " + ctx.path() + " -> " + responseTime + "ms");
			});
			conf.enableDevLogging();
		}).start(8080);
		
		app.before("applications", EmployeeController::authenticate);
		app.before("application", EmployeeController::authenticate);
		app.before("/applications/*", EmployeeController::authenticate);
		app.before("/application/*", EmployeeController::authenticate);
		app.before("employees/getall", EmployeeController::authenticate);
		app.routes(() -> {
			path("employees", () -> {
				post(EmployeeController::login);
				delete(EmployeeController::logout);
				get("/getall", EmployeeController::getEmployees);
			});
			path("applications", ()-> {
				get(ApplicationController::getForms);
				get("/active", ApplicationController::getActiveForms);
				get("/waitingonme", ApplicationController::waitingOnMe);
				get("/formhistory", ApplicationController::myFormHistory);
			});
			path("application", ()-> {
				get(ApplicationController::getById);
				post(ApplicationController::addForm);
				put("/approveform", ApplicationController::approveForm);
				put("/denyform", ApplicationController::denyForm);
				put("/requestinformation", ApplicationController::requestInformation);
				post("/uploadaward", ApplicationController::uploadAward);
				get("/getaward", ApplicationController::getAward);
				get("/getawards", ApplicationController::getAwardByForm);
				get("/getactivebyuser", ApplicationController::getActiveFormsByUser);
				put("/finalapproval", ApplicationController::finalApproval);
			});
		});
		
	}
	
}