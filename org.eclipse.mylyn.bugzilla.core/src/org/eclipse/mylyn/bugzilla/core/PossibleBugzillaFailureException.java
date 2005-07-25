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
