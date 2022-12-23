/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.repositories.core.auth;

/**
 * Indicates that a resource is not available.
 */
public class UnavailableException extends Exception {
	private static final long serialVersionUID = 6528925039337188836L;

	public UnavailableException() {
		super();
	}

	public UnavailableException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public UnavailableException(String arg0) {
		super(arg0);
	}

	public UnavailableException(Throwable arg0) {
		super(arg0);
	}

}