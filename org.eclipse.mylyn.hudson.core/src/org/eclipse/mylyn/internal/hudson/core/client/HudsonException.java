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
 * @author Steffen Pingel
 */
public class HudsonException extends Exception {

	private static final long serialVersionUID = -4419540659554920327L;

	public HudsonException() {
	}

	public HudsonException(String message) {
		super(message);
	}

	public HudsonException(Throwable cause) {
		super(cause.getMessage(), cause);
	}

	public HudsonException(String message, Throwable cause) {
		super(message, cause);
	}

}
