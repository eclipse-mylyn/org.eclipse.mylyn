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

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import org.eclipse.core.runtime.Assert;

/**
 * GitHub Repository class.
 * 
 * @author Kevin Sawicki (kevin@github.com)
 */
public class Repository {

	private boolean fork;
	private boolean hasDownloads;
	private boolean hasIssues;
	private boolean hasWiki;
	@SerializedName("private")
	private boolean isPrivate;

	private Date createdAt;
	private Date pushedAt;

	private String description;
	private String homepage;
	private String language;
	private String name;
	private String owner;
	private String url;

	private int forks;
	private int openIssues;
	private int size;
	private int watchers;

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
	 * Create repository
	 */
	Repository() {

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

	/**
	 * @return fork
	 */
	public boolean isFork() {
		return this.fork;
	}

	/**
	 * @return hasDownloads
	 */
	public boolean isHasDownloads() {
		return this.hasDownloads;
	}

	/**
	 * @return hasIssues
	 */
	public boolean isHasIssues() {
		return this.hasIssues;
	}

	/**
	 * @return hasWiki
	 */
	public boolean isHasWiki() {
		return this.hasWiki;
	}

	/**
	 * @return isPrivate
	 */
	public boolean isPrivate() {
		return this.isPrivate;
	}

	/**
	 * @return createdAt
	 */
	public Date getCreatedAt() {
		return this.createdAt;
	}

	/**
	 * @return pushedAt
	 */
	public Date getPushedAt() {
		return this.pushedAt;
	}

	/**
	 * @return description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @return homepage
	 */
	public String getHomepage() {
		return this.homepage;
	}

	/**
	 * @return language
	 */
	public String getLanguage() {
		return this.language;
	}

	/**
	 * @return url
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * @return forks
	 */
	public int getForks() {
		return this.forks;
	}

	/**
	 * @return openIssues
	 */
	public int getOpenIssues() {
		return this.openIssues;
	}

	/**
	 * @return size
	 */
	public int getSize() {
		return this.size;
	}

	/**
	 * @return watchers
	 */
	public int getWatchers() {
		return this.watchers;
	}

}
