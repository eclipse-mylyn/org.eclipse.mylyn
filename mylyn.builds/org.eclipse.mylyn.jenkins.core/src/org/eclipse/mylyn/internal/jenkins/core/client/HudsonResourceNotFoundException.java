/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.jenkins.core.client;

/**
 * Indicates that a requested resource was not found, e.g. if a build is requested is for a plan that was never built.
 * 
 * @author Steffen Pingel
 */
public class HudsonResourceNotFoundException extends HudsonException {

	private static final long serialVersionUID = -5377178546833428956L;

	public HudsonResourceNotFoundException() {
	}

	public HudsonResourceNotFoundException(String message) {
		super(message);
	}

	public HudsonResourceNotFoundException(Throwable cause) {
		super(cause);
	}

	public HudsonResourceNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
