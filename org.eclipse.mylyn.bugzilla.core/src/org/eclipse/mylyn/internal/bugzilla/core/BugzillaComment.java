/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

public class BugzillaComment {

	/** Comment's number */
	private final int number;

	private boolean hasAttachment;

	private String attachmentId;

	public BugzillaComment(int commentNumber) {
		this.number = commentNumber;
	}
}
