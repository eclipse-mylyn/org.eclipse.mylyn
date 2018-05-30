/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.github.core;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import org.eclipse.egit.github.core.FieldError;
import org.eclipse.egit.github.core.RequestError;
import org.eclipse.egit.github.core.client.RequestException;

/**
 * GitHub exception that wraps and formats a {@link RequestException}
 */
public class GitHubException extends IOException {

	/** serialVersionUID */
	private static final long serialVersionUID = -1456910662911777231L;

	/**
	 * Wraps the given {@link IOException} with a {@link GitHubException} if it
	 * is a {@link RequestException} instance.
	 *
	 * @param exception
	 * @return wrapped exception
	 */
	public static IOException wrap(IOException exception) {
		return exception instanceof RequestException ? new GitHubException(
				(RequestException) exception) : exception;
	}

	/**
	 * Create GitHub exception from {@link RequestException}
	 *
	 * @param cause
	 */
	public GitHubException(RequestException cause) {
		super();
		initCause(cause);
	}

	public String getMessage() {
		RequestError error = ((RequestException) getCause()).getError();
		String errorMessage = error.getMessage();
		if (errorMessage == null)
			errorMessage = ""; //$NON-NLS-1$
		StringBuilder message = new StringBuilder(errorMessage);
		List<FieldError> errors = error.getErrors();
		if (errors != null && errors.size() > 0) {
			message.append(':');
			for (FieldError fieldError : errors)
				message.append(' ').append(format(fieldError)).append(',');
			message.deleteCharAt(message.length() - 1);
		}
		return message.toString();
	}

	private String format(FieldError error) {
		String code = error.getCode();
		String value = error.getValue();
		String field = error.getField();
		String resource = error.getResource();

		if (FieldError.CODE_INVALID.equals(code))
			if (value != null)
				return MessageFormat
						.format(Messages.FieldError_InvalidFieldWithValue,
								value, field);
			else
				return MessageFormat.format(Messages.FieldError_InvalidField,
						field);

		if (FieldError.CODE_MISSING_FIELD.equals(code))
			return MessageFormat
					.format(Messages.FieldError_MissingField, field);

		if (FieldError.CODE_ALREADY_EXISTS.equals(code))
			return MessageFormat.format(
					Messages.FieldError_AlreadyExists,
					resource, field);

		return MessageFormat.format(Messages.FieldError_ResourceError, field,
				resource);
	}
}
