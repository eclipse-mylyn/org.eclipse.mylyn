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

import java.util.List;

/**
 * GitHub request error class
 *
 * @author Kevin Sawicki (kevin@github.com)
 */
public class RequestError {

	private String message;

	private List<String> errors;

	/**
	 * @return message
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * Get errors
	 *
	 * @return list of errors
	 */
	public List<String> getErrors() {
		return this.errors;
	}

}
