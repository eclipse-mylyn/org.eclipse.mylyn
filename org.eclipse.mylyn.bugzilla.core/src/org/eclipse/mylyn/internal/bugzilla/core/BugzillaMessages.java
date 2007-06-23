/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import org.eclipse.osgi.util.NLS;

/**
 * @author Rob Elves
 */
public final class BugzillaMessages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.bugzilla.core.BugzillaMessages";//$NON-NLS-1$

	static {
		NLS.initializeMessages(BUNDLE_NAME, BugzillaMessages.class);
	}

	private BugzillaMessages() {
		// Do not instantiate
	}

	public static String repositoryLoginFailure;

	public static String repositoryNotFound;
	
	public static String repositoryCommentReqd;
	
	public static String repositoryCollision;

	public static String operationCancelled;

	public static String errorNetwork;

	public static String errorIo;

	public static String errorInternal;

	public static String errorRepository;

}
