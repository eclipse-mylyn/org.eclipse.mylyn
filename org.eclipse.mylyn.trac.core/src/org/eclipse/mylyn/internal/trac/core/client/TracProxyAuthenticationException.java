/*******************************************************************************
* Copyright (c) 2006, 2008 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core.client;

public class TracProxyAuthenticationException extends TracException {

	private static final long serialVersionUID = 305145749259511429L;

	public TracProxyAuthenticationException(String message) {
		super(message);
	}

	public TracProxyAuthenticationException() {
	}

}
