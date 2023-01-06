package com.cdm.exception;

public class BussinesException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BussinesException(String errorMessage) {
        super(errorMessage);
    }
	
}
