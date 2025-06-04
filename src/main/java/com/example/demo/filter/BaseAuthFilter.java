package com.example.demo.filter;

import java.io.IOException;

import com.example.demo.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletResponse;

public class BaseAuthFilter extends HttpFilter{

	protected void sendErrorResponse(HttpServletResponse response,int status,String message)throws IOException {
		
		response.setStatus(status);
		response.setContentType("application/json;charset=UTF-8");
		ApiResponse<?> apiResponse = ApiResponse.error(status, message);
		//利用 ObjectMapper 將指定物件轉 json
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(apiResponse);
		response.getWriter().write(json);
	}
	
}
