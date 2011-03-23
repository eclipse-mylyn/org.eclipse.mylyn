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
package org.eclipse.mylyn.github.internal;

/**
 * Container of multiple GitHub Issues, used when returning JSON objects
 */
public class GitHubIssues {

	private GitHubIssue[] issues;

	/**
	 * Getter for all issues inside this object
	 * 
	 * @return The array of individual GitHub Issues
	 */
	public GitHubIssue[] getIssues() {
		return issues;
	}

}
