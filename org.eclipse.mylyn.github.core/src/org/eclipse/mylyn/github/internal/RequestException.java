/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.github.internal;

import java.io.IOException;
import java.util.List;

/**
 * Request exception class that wraps an {@link RequestError} object.
 * 
 * @author Kevin Sawicki (kevin@github.com)
 */
public class RequestException extends IOException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1197051396535284852L;

	private RequestError error;
	private int status;

	/**
	 * Create request exception
	 * 
	 * @param error
	 * @param status
	 */
	public RequestException(RequestError error, int status) {
		super();
		this.error = error;
		this.status = status;
	}

	@Override
	public String getMessage() {
		StringBuilder message = new StringBuilder(error.getMessage());
		List<FieldError> errors = error.getErrors();
		if (errors != null && errors.size() > 0) {
			message.append(':');
			for (FieldError error : errors)
				message.append(' ').append(error.format()).append(',');
			message.deleteCharAt(message.length() - 1);
		}
		return message.toString();
	}

	/**
	 * Get error
	 * 
	 * @return error
	 */
	public RequestError getError() {
		return this.error;
	}

	/**
	 * Get status
	 * 
	 * @return status
	 */
	public int getStatus() {
		return this.status;
	}

}
