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

import org.eclipse.core.runtime.Assert;

/**
 * GitHub Repository class.
 * 
 * @author Kevin Sawicki (kevin@github.com)
 */
public class Repository {

	private String owner;
	private String name;

	/**
	 * Create repository with owner and name
	 * 
	 * @param owner
	 * @param name
	 */
	public Repository(String owner, String name) {
		Assert.isNotNull(owner, "Owner cannot be null"); //$NON-NLS-1$
		Assert.isLegal(owner.length() > 0, "Owner cannot be empty"); //$NON-NLS-1$
		Assert.isNotNull(name, "Name cannot be null"); //$NON-NLS-1$
		Assert.isLegal(name.length() > 0, "Name cannot be empty"); //$NON-NLS-1$

		this.owner = owner;
		this.name = name;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return getId().hashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		else if (obj instanceof Repository)
			return getId().equals(((Repository) obj).getId());
		else
			return false;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getId();
	}

	/**
	 * Get unique identifier for repository
	 * 
	 * @return id
	 */
	public String getId() {
		return this.owner + '/' + this.name;
	}

	/**
	 * @return owner
	 */
	public String getOwner() {
		return this.owner;
	}

	/**
	 * @return name
	 */
	public String getName() {
		return this.name;
	}

}
