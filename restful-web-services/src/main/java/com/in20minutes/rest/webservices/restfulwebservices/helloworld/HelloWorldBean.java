package com.in20minutes.rest.webservices.restfulwebservices.helloworld;

public class HelloWorldBean {

	private String message;

	public String getMessage() {
		return message;
	}

	public HelloWorldBean(String msg) {
		this.message = msg;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "HelloWorldBean [message=" + message + "]";
	}

}
