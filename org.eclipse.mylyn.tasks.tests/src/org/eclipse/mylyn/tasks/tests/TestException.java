/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

/**
 * An exception for testing handing of RuntimeExceptions. Has no stack trace to reduce the size of log messages.
 */
public class TestException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public TestException() {
		super("Test of exception handling");
		setStackTrace(new StackTraceElement[] {});
	}
}