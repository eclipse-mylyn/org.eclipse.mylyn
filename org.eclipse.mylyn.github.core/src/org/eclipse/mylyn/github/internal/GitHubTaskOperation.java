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
 * Enumeration of task operations
 */
public enum GitHubTaskOperation {

	/**
	 * LEAD
	 */
	LEAVE("Leave "),

	/**
	 * REOPEN
	 */
	REOPEN("Reopen"),

	/**
	 * CLOSE
	 */
	CLOSE("Close");

	private final String label;

	private GitHubTaskOperation(String label) {
		this.label = label;
	}

	/**
	 * Get label
	 * 
	 * @return label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Get id
	 * 
	 * @return id
	 */
	public String getId() {
		return name();
	}

	/**
	 * get the operation by its id
	 * 
	 * @param opId
	 *            the id, or null
	 * @return the operation, or null if the id was null or did not match any
	 *         operation
	 */
	public static GitHubTaskOperation fromId(String opId) {
		for (GitHubTaskOperation op : values()) {
			if (op.getId().equals(opId)) {
				return op;
			}
		}
		return null;
	}

}
