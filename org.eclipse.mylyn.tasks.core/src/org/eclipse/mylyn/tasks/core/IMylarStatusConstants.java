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

package org.eclipse.mylar.tasks.core;

/**
 * @author Rob Elves
 * @author Steffen Pingel
 */
public interface IMylarStatusConstants {

	/**
	 * requires construction with repositoryUrl and error message
	 */
	public final static int REPOSITORY_ERROR = 1;

	/**
	 * Operation failed with html error from repository.
	 * 
	 */
	public final static int REPOSITORY_ERROR_HTML = 2;

	public final static int REPOSITORY_LOGIN_ERROR = 3;

	public final static int REPOSITORY_NOT_FOUND = 4;

	public final static int IO_ERROR = 5;

	public final static int REPOSITORY_COLLISION = 6;

	public final static int INTERNAL_ERROR = 7;
	
	public final static int OPERATION_CANCELLED = 8;
	
	public final static int REPOSITORY_COMMENT_REQD = 9;
	
	public final static int LOGGED_OUT_OF_REPOSITORY = 10;
	
	public final static int NETWORK_ERROR = 11;
	
	public final static int PERMISSION_DENIED_ERROR = 12;

}
