/**
 * Copyright (c) 2013 Puppet Labs, Inc. and other contributors, as listed below.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Contributors:
 *   Puppet Labs
 */
package com.puppetlabs.puppetdb.javaclient;

import java.io.IOException;

/**
 * Signals that an API exception of some sort has occurred. This
 * class is the general class of exceptions produced by this API
 */
public class APIException extends IOException {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an {@code APIException} with the specified detail message.
	 * 
	 * @param message
	 *            The detail message (which is saved for later retrieval
	 *            by the {@link #getMessage()} method)
	 */
	public APIException(String message) {
		super(message);
	}

	/**
	 * Constructs an {@code APIException} with the specified detail message
	 * and cause.
	 * 
	 * <p>
	 * Note that the detail message associated with {@code cause} is <i>not</i> automatically incorporated into this exception's detail message.
	 * 
	 * @param message
	 *            The detail message (which is saved for later retrieval
	 *            by the {@link #getMessage()} method)
	 * 
	 * @param cause
	 *            The cause (which is saved for later retrieval by the {@link #getCause()} method). (A null value is permitted,
	 *            and indicates that the cause is nonexistent or unknown.)
	 * 
	 */
	public APIException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs an {@code APIException} with the specified cause and a
	 * detail message of {@code (cause==null ? null : cause.toString())} (which typically contains the class and detail message of {@code cause}).
	 * This constructor is useful for IO exceptions that are little more
	 * than wrappers for other throwables.
	 * 
	 * @param cause
	 *            The cause (which is saved for later retrieval by the {@link #getCause()} method). (A null value is permitted,
	 *            and indicates that the cause is nonexistent or unknown.)
	 * 
	 */
	public APIException(Throwable cause) {
		super(cause);
	}
}
