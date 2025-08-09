/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     BREDEX GmbH - fix for bug 295050
 *******************************************************************************/

package org.eclipse.mylyn.commons.net;

/**
 * An enum of the supported authentication types.
 *
 * @since 2.2
 * @author Steffen Pingel
 */
public enum AuthenticationType {
	/**
	 * HTTP authentication, this is typically basic authentication but other methods such as digest or NTLM are used as well.
	 */
	HTTP,
	/** Proxy authentication. */
	PROXY,
	/** Task repository authentication. */
	REPOSITORY,
	/**
	 * Client authentication using certificates.
	 *
	 * @since 3.6
	 */
	CERTIFICATE,
}
