/*******************************************************************************
 * Copyright (c) 2011 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client;

/**
 * Indicates an error during repository access.
 * 
 * @author Steffen Pingel
 */
public class GerritException extends Exception {

	private static final long serialVersionUID = 1929614326467463462L;

	private int code;

	public GerritException() {
		// ignore
	}

	public GerritException(String message) {
		super(message);
	}

	public GerritException(String message, Throwable cause) {
		super(message, cause);
	}

	public GerritException(Throwable cause) {
		super(cause.getMessage(), cause);
	}

	public GerritException(String message, int code) {
		super(message);
		this.code = code;
	}

	public int getCode() {
		return code;
	}

}
