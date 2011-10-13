/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.egit.github.core;

/**
 * Repository hook response model class
 */
public class RepositoryHookResponse {

	private int code;

	private String message;

	/**
	 * @return code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @param code
	 * @return this repsonse
	 */
	public RepositoryHookResponse setCode(int code) {
		this.code = code;
		return this;
	}

	/**
	 * @return message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 * @return this response
	 */
	public RepositoryHookResponse setMessage(String message) {
		this.message = message;
		return this;
	}
}
