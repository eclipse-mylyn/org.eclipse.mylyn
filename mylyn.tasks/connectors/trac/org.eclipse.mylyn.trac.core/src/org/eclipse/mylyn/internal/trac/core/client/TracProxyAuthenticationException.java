/*******************************************************************************
 * Copyright (c) 2006, 2008 Steffen Pingel and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
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
