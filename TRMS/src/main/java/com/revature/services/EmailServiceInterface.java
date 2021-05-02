package com.revature.services;

import com.revature.models.Employee;

public interface EmailServiceInterface {
	
	 void SendEmail (String subject, String body);
	 
}