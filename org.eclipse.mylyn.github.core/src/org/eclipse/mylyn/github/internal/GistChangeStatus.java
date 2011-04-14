/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.github.internal;

/**
 * Gist change status class.
 */
public class GistChangeStatus {

	private int additions;

	private int deletions;

	private int total;

	/**
	 * @return additions
	 */
	public int getAdditions() {
		return this.additions;
	}

	/**
	 * @return deletions
	 */
	public int getDeletions() {
		return this.deletions;
	}

	/**
	 * @return total
	 */
	public int getTotal() {
		return this.total;
	}

}
