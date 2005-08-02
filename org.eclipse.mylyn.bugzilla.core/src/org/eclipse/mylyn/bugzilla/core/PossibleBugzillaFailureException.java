/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.bugzilla.core;

import java.io.PrintStream;
import java.io.PrintWriter;

public class PossibleBugzillaFailureException extends Exception {
	/** Automatically generated serialVersionUID */
	private static final long serialVersionUID = 3257849887386449974L;

	private Throwable cause;

	/**
	 * Constructor for BugzillaException.
	 */
	public PossibleBugzillaFailureException() {
		super();
	}

	/**
	 * Constructor for BugzillaException.
	 * @param detailMessage
	 */
	public PossibleBugzillaFailureException(String detailMessage) {
		super(detailMessage);
	}
	
	public PossibleBugzillaFailureException(String detailMessage,Throwable cause) {
		super(detailMessage);
		this.cause = cause;
	}

	public PossibleBugzillaFailureException(Throwable cause) {
		this.cause = cause;
	}
	
	@Override
	public synchronized void printStackTrace(PrintStream err) {
		super.printStackTrace(err);
    	if (cause != null) {
    		err.println("\n--- Cause was:");
    		cause.printStackTrace(err);
    	}
	}

	@Override
	public synchronized void printStackTrace(PrintWriter err) {
		super.printStackTrace(err);
    	if (cause != null) {
    		err.println("\n--- Cause was:");
    		cause.printStackTrace(err);
    	}
	}	

}
