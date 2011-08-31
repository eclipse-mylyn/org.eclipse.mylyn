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
package org.eclipse.egit.github.core;

import java.io.Serializable;

/**
 * Field error model class
 */
public class FieldError implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = 1447694681624322597L;

	/**
	 * CODE_INVALID
	 */
	public static final String CODE_INVALID = "invalid"; //$NON-NLS-1$

	/**
	 * CODE_MISSING
	 */
	public static final String CODE_MISSING = "missing"; //$NON-NLS-1$

	/**
	 * CODE_MISSING_FIELD
	 */
	public static final String CODE_MISSING_FIELD = "missing_field"; //$NON-NLS-1$

	/**
	 * CODE_ALREADY_EXISTS
	 */
	public static final String CODE_ALREADY_EXISTS = "already_exists"; //$NON-NLS-1$

	private String code;

	private String field;

	private String resource;

	private String value;

	/**
	 * @return code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @return field
	 */
	public String getField() {
		return field;
	}

	/**
	 * @return resource
	 */
	public String getResource() {
		return resource;
	}

	/**
	 * @return value
	 */
	public String getValue() {
		return value;
	}
}
