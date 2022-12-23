/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
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