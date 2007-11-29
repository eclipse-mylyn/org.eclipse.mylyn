/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.web.core;

/**
 * An enum of the supported authentication types.
 * 
 * @since 2.2
 * @author Steffen Pingel
 */
public enum AuthenticationType {
	/**
	 * HTTP authentication, this is typically basic authentication but other methods such as digest or NTLM are used as
	 * well.
	 */
	HTTP,
	/** Proxy authentication. */
	PROXY,
	/** Task repository authentication. */
	REPOSITORY
}