package com.firefly.orderManagement.exception;

import java.util.UUID;

import lombok.Getter;

@Getter
public class NoOrderWithIdException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private UUID id;
	public NoOrderWithIdException(UUID id) {
		this.id = id;
	}
}
