/*******************************************************************************
 * Copyright (c) 2013 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.rest.core;

public class BugzillaRestResourceNotFoundException extends BugzillaRestException {

	private static final long serialVersionUID = 5227546210820677763L;

	public BugzillaRestResourceNotFoundException() {
	}

	public BugzillaRestResourceNotFoundException(String message) {
		super(message);
	}

	public BugzillaRestResourceNotFoundException(Throwable cause) {
		super(cause);
	}

	public BugzillaRestResourceNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
