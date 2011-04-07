/*******************************************************************************
 * Copyright (c) 2011 Red Hat and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green <david.green@tasktop.com> - initial contribution
 *     Christian Trutz <christian.trutz@gmail.com> - initial contribution
 *     Chris Aniszczyk <caniszczyk@gmail.com> - initial contribution
 *******************************************************************************/
package org.eclipse.mylyn.github.ui.internal;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;

public class GitHubUi {
	public static final String BUNDLE_ID = "org.eclipse.mylyn.github.ui";
	
	public static IStatus createStatus(int severity,String message) {
		return new Status(severity,BUNDLE_ID,message);
	}
	
	public static IStatus createStatus(int severity,String message,Throwable e) {
		return new Status(severity,BUNDLE_ID,message,e);
	}
	
	public static IStatus createErrorStatus(String message) {
		return createStatus(IStatus.ERROR, message);
	}
	
	public static IStatus createErrorStatus(String message,Throwable t) {
		return createStatus(IStatus.ERROR, message,t);
	}

	public static IStatus createErrorStatus(Throwable e) {
		return createStatus(IStatus.ERROR, "Unexpected error: "+e.getMessage(),e);
	}
	
	public static ILog getLog() {
		return Platform.getLog(Platform.getBundle(BUNDLE_ID));
	}
	
	public static void logError(String message,Throwable t) {
		getLog().log(createErrorStatus(message, t));
	}
	
	public static void logError(Throwable t) {
		getLog().log(createErrorStatus(t.getMessage(), t));
	}
}
