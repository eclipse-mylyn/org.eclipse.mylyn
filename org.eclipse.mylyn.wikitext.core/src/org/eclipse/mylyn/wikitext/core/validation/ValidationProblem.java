/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.core.validation;

/**
 * A validation problem is an indication of an error or warning that occurred while validating a document. A problem has
 * a marker id, severity, message, and optional offset and length.
 * 
 * Default comparison semantics order problems by increasing offset.
 * 
 * @author David Green
 */
public class ValidationProblem implements Comparable<ValidationProblem> {
	public enum Severity {
		WARNING, ERROR
	}

	private String markerId = "org.eclipse.mylyn.wikitext.core.validation.problem"; //$NON-NLS-1$

	private Severity severity;

	private String message;

	private int offset;

	private int length;

	/**
	 * create a validation problem
	 * 
	 * @param severity
	 *            a severity indicating the severity of the problem
	 * @param message
	 *            the message describing the problem
	 * @param offset
	 *            the offset into the document that the problem starts
	 * @param length
	 *            the length of the problem, which may be 0
	 * 
	 * @throws IllegalArgumentException
	 *             if the severity is invalid, the offset is < 0, the length is < 0, or if no message is provided
	 */
	public ValidationProblem(Severity severity, String message, int offset, int length) {
		setSeverity(severity);
		setMessage(message);
		setOffset(offset);
		setLength(length);
	}

	public String getMarkerId() {
		return markerId;
	}

	public void setMarkerId(String markerId) {
		this.markerId = markerId;
	}

	public Severity getSeverity() {
		return severity;
	}

	/**
	 * @param severity
	 *            a severity
	 */
	public void setSeverity(Severity severity) {
		if (severity == null) {
			throw new IllegalArgumentException();
		}
		this.severity = severity;
	}

	/**
	 * the text message as it is displayed to the user
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the text message as it is displayed to the user
	 */
	public void setMessage(String message) {
		if (message == null || message.length() == 0) {
			throw new IllegalArgumentException();
		}
		this.message = message;
	}

	/**
	 * the character offset in the document of the problem
	 */
	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		if (offset < 0) {
			throw new IllegalArgumentException();
		}
		this.offset = offset;
	}

	/**
	 * the length of the problem in characters
	 */
	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		if (length < 0) {
			throw new IllegalArgumentException();
		}
		this.length = length;
	}

	@Override
	public String toString() {
		return severity + "[" + offset + "," + length + "]: " + message; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public int compareTo(ValidationProblem o2) {
		if (this == o2) {
			return 0;
		}
		int offset1 = this.getOffset();
		int offset2 = o2.getOffset();
		if (offset1 < offset2) {
			return -1;
		} else if (offset2 < offset1) {
			return 1;
		} else {
			int length1 = this.getLength();
			int length2 = o2.getLength();
			if (length1 > length2) {
				return -1;
			} else if (length2 > length1) {
				return 1;
			} else {
				int i = this.getMessage().compareTo(o2.getMessage());
				if (i == 0) {
					i = this.getMarkerId().compareTo(o2.getMarkerId());
				}
				return i;
			}
		}
	}
}
