/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core;

public class TracProxyAuthenticationException extends TracException {

	private static final long serialVersionUID = 305145749259511429L;

	public TracProxyAuthenticationException(String message) {
		super(message);
	}

	public TracProxyAuthenticationException() {
	}

}
