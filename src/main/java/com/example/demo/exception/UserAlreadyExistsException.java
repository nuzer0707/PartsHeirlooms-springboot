package com.example.demo.exception;

public class UserAlreadyExistsException extends CertException {
	public  UserAlreadyExistsException(String message) {
		super(message);
	}
}
