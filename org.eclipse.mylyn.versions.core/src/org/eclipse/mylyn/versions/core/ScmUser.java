/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
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
		this.email = emailAddress;
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
		} else {
			if (name != null) {
				sb.append(name);
			}
		}
		if (email != null) {
			sb.append(" <" + email + ">");
		}

		return sb.toString();
	}

}
