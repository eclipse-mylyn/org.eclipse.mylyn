/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.hudson.core.client;

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
