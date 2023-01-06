/*******************************************************************************
 * Copyright (c) 2011 Red Hat and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green <david.green@tasktop.com> - initial contribution
 *     Christian Trutz <christian.trutz@gmail.com> - initial contribution
 *     Chris Aniszczyk <caniszczyk@gmail.com> - initial contribution
 *******************************************************************************/
package org.eclipse.mylyn.internal.github.core.issue;

/**
 * Enumeration of task operations
 */
public enum IssueOperation {

	/**
	 * LEAD
	 */
	LEAVE("Leave "), //$NON-NLS-1$

	/**
	 * REOPEN
	 */
	REOPEN("Reopen"), //$NON-NLS-1$

	/**
	 * CLOSE
	 */
	CLOSE("Close"); //$NON-NLS-1$

	private final String label;

	private IssueOperation(String label) {
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
	public static IssueOperation fromId(String opId) {
		for (IssueOperation op : values())
			if (op.getId().equals(opId))
				return op;
		return null;
	}

}
