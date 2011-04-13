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
 * GitHub user class.
 *
 * @author Kevin Sawicki (kevin@github.com)
 */
public class User {

	private String blob;

	private String company;

	private String email;

	private String gravatarUrl;

	private String location;

	private String login;

	private String name;

	private String type;

	private String url;

	/**
	 * @return blob
	 */
	public String getBlob() {
		return this.blob;
	}

	/**
	 * @return company
	 */
	public String getCompany() {
		return this.company;
	}

	/**
	 * @return email
	 */
	public String getEmail() {
		return this.email;
	}

	/**
	 * @return gravatarUrl
	 */
	public String getGravatarUrl() {
		return this.gravatarUrl;
	}

	/**
	 * @return location
	 */
	public String getLocation() {
		return this.location;
	}

	/**
	 * @return login
	 */
	public String getLogin() {
		return this.login;
	}

	/**
	 * @param login
	 * @return this user
	 */
	public User setLogin(String login) {
		this.login = login;
		return this;
	}

	/**
	 * @return name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name
	 * @return this user
	 */
	public User setName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * @return type
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * @return url
	 */
	public String getUrl() {
		return this.url;
	}

}
