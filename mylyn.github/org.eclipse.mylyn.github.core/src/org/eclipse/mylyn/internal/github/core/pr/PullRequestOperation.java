/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.mylyn.internal.github.core.pr;

/**
 * Enumeration of task operations
 */
public enum PullRequestOperation {

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

	private PullRequestOperation(String label) {
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
	public static PullRequestOperation fromId(final String opId) {
		for (PullRequestOperation op : values())
			if (op.getId().equals(opId))
				return op;
		return null;
	}

}
