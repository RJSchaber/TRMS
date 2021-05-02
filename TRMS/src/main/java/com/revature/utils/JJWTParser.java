package com.revature.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.models.Employee;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.TextCodec;

public class JJWTParser {
	private static Logger log = LogManager.getLogger(JJWTParser.class);

	public Employee Parser(String jwtStr) {
		try {
			Jws<Claims> jws = Jwts.parser()
					.setSigningKey(TextCodec.BASE64.decode(System.getenv("SECRET_KEY")))
					.parseClaimsJws(jwtStr);
			
			return new ObjectMapper().readValue(jws.getBody().get("user").toString(), Employee.class);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

}
