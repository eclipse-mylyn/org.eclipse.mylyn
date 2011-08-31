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
package org.eclipse.egit.github.core;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

/**
 * GitHub Repository class.
 */
public class SearchRepository implements IRepositoryIdProvider, Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = 978627174722864632L;

	/**
	 * Create repository from url.
	 *
	 * @see SearchRepository#createFromId(String)
	 * @param url
	 * @return repository or null if parsing fails
	 */
	public static SearchRepository createFromUrl(URL url) {
		return url != null ? createFromId(url.getPath()) : null;
	}

	/**
	 * Create repository from id. The id is split on the '/' character and the
	 * last two non-empty segments are interpreted to be the repository owner
	 * and name.
	 *
	 * @param id
	 * @return repository
	 */
	public static SearchRepository createFromId(String id) {
		if (id == null)
			return null;
		String owner = null;
		String name = null;
		String[] segments = id.split("/"); //$NON-NLS-1$
		for (int i = segments.length - 1; i >= 0; i--)
			if (segments[i].length() > 0)
				if (name == null)
					name = segments[i];
				else if (owner == null)
					owner = segments[i];
				else
					break;

		return owner != null && name != null ? new SearchRepository(owner, name)
				: null;
	}

	/**
	 * Create from string url
	 *
	 * @see SearchRepository#createFromUrl(URL)
	 * @param url
	 * @return repository or null if it could not be parsed from url path
	 */
	public static SearchRepository createFromUrl(String url) {
		try {
			return url != null ? createFromUrl(new URL(url)) : null;
		} catch (MalformedURLException e) {
			return null;
		}
	}

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
	public SearchRepository(String owner, String name) {
		if (owner == null)
			throw new IllegalArgumentException("Owner cannot be null");
		if (owner.length() == 0)
			throw new IllegalArgumentException("Owner cannot be empty");
		if (name == null)
			throw new IllegalArgumentException("Name cannot be null");
		if (name.length() == 0)
			throw new IllegalArgumentException("Name cannot be empty");

		this.owner = owner;
		this.name = name;
	}

	/**
	 * Create repository
	 */
	SearchRepository() {

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
		else if (obj instanceof SearchRepository)
			return getId().equals(((SearchRepository) obj).getId());
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
		return owner + '/' + name;
	}

	/**
	 * @return owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 * @return this repository
	 */
	public SearchRepository setName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * @return fork
	 */
	public boolean isFork() {
		return fork;
	}

	/**
	 * @return hasDownloads
	 */
	public boolean isHasDownloads() {
		return hasDownloads;
	}

	/**
	 * @return hasIssues
	 */
	public boolean isHasIssues() {
		return hasIssues;
	}

	/**
	 * @return hasWiki
	 */
	public boolean isHasWiki() {
		return hasWiki;
	}

	/**
	 * @return isPrivate
	 */
	public boolean isPrivate() {
		return isPrivate;
	}

	/**
	 * @param isPrivate
	 * @return this repository
	 */
	public SearchRepository setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
		return this;
	}

	/**
	 * @return createdAt
	 */
	public Date getCreatedAt() {
		return createdAt != null ? new Date(createdAt.getTime()) : null;
	}

	/**
	 * @return pushedAt
	 */
	public Date getPushedAt() {
		return pushedAt != null ? new Date(pushedAt.getTime()) : null;
	}

	/**
	 * @return description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 * @return this repository
	 */
	public SearchRepository setDescription(String description) {
		this.description = description;
		return this;
	}

	/**
	 * @return homepage
	 */
	public String getHomepage() {
		return homepage;
	}

	/**
	 * @param homepage
	 * @return this repository
	 */
	public SearchRepository setHomepage(String homepage) {
		this.homepage = homepage;
		return this;
	}

	/**
	 * @return language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @return url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @return forks
	 */
	public int getForks() {
		return forks;
	}

	/**
	 * @return openIssues
	 */
	public int getOpenIssues() {
		return openIssues;
	}

	/**
	 * @return size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @return watchers
	 */
	public int getWatchers() {
		return watchers;
	}

	public String generateId() {
		final String owner = this.owner;
		if (owner == null || owner.length() == 0)
			return null;
		final String name = this.name;
		if (name == null || name.length() == 0)
			return null;
		return owner + "/" + name;
	}
}
