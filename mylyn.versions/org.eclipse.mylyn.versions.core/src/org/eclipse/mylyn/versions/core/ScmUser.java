/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.

 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.versions.core;

/**
 * @author Steffen Pingel
 */
public class ScmUser {
	private String email;

	private String id;

	private String name;

	public ScmUser(String id, String name, String emailAddress) {
		this.id = id;
		this.name = name;
		email = emailAddress;
	}

	public String getEmail() {
		return email;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	/**
	 * This object should not be mutable
	 *
	 * @param email
	 */
	@Deprecated
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * This object should not be mutable
	 *
	 * @param id
	 */
	@Deprecated
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * This object should not be mutable
	 *
	 * @param name
	 */
	@Deprecated
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (id != null) {
			sb.append(id);
		} else if (name != null) {
			sb.append(name);
		}
		if (email != null) {
			sb.append(" <" + email + ">"); //$NON-NLS-1$//$NON-NLS-2$
		}

		return sb.toString();
	}

}
