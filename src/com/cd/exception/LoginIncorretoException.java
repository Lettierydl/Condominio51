package com.cd.exception;

public class LoginIncorretoException extends Exception {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LoginIncorretoException(){
		super("Login ou Senha Inválida");
	}
	
	public LoginIncorretoException(String msg){
		super(msg);
	}
}
