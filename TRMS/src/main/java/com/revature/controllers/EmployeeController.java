package com.revature.controllers;

import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.models.Employee;
import com.revature.services.EmployeeService;
import com.revature.services.EmployeeServiceInterface;
import com.revature.utils.JJWTParser;
import com.revature.utils.S3Util;
import io.javalin.http.Context;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.http.UnauthorizedResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;

public class EmployeeController {
	private static EmployeeServiceInterface es = new EmployeeService();
	private static Logger log = LogManager.getLogger(EmployeeController.class);
	
	public static void login(Context ctx) {
		log.trace("Attempting to login user");
		if(ctx.header("Authorization") != null) {
			ctx.status(204);
			log.trace("Login Failed: " + ctx.status(204));
			return;
		}
			String userJson = null;
			
			String firstName = ctx.formParam("firstName");
			String lastName = ctx.formParam("lastName");
			Employee e = es.getEmployee(lastName, firstName);
			
			if (e == null) {
				ctx.status(401);
				log.trace("Login Failed: " + ctx.status(401));
			} else {
				ObjectMapper mapper = new ObjectMapper();
				try {
					userJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(e);
				} catch (JsonProcessingException e1) {
					for (StackTraceElement st : e1.getStackTrace())
						log.debug(st.toString());
				}
			}
			
			String secretKey = System.getenv("SECRET_KEY");
			
			//Create json web token
			String jwtStr = Jwts.builder()
					.claim("user", userJson)
					.signWith(
						SignatureAlgorithm.HS256,
						TextCodec.BASE64.decode(
								secretKey
						)
					)
					.compact();
			
			HashMap<String, String> map = new HashMap<>();
			map.put("token", jwtStr);

			ctx.json(map);
	}
	
	public static void logout(Context ctx) {
//		Employee e = ctx.attribute("user");
//		log.trace("Logging out user: " + e.getLastName() );
		
		HashMap<String, String> map = new HashMap<>();
		map.put("token", "");
		ctx.json(map);
	}
	
	public static void getEmployees(Context ctx) {
		Employee e = ctx.attribute("user");
		
		if(e.getRole().toString().equals("Benco")) {
			List<Employee> employees = es.getEmployees();
			log.trace("Returning list of employees");
			ctx.json(employees);
		}else {
			ctx.status(403);
			log.trace("Employee not a Benco." + ctx.status(403));
		}
		
	}
	
	public static void authenticate(Context ctx) {
		log.trace("Authenticating");
		
		String authHeader = ctx.header("Authorization");
		
		if(authHeader == null || authHeader == "") {
			log.warn("No auth token in request");
			throw new UnauthorizedResponse();
		}
		
		String jwtStr = authHeader.split(" ")[1]; // extract the token

		JJWTParser parser = new JJWTParser();
		Employee loggedIn = parser.Parser(jwtStr);
		ctx.attribute("user", loggedIn);
	}
}
