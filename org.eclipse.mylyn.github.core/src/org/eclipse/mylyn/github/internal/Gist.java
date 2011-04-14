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

import java.util.Date;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

/**
 * GitHub gist class.
 */
public class Gist {

	@SerializedName("public")
	private boolean isPublic;

	private Date createdAt;

	private int comments;

	private Map<String, GistFile> files;

	private String description;

	private String gitPullUrl;

	private String gitPushUrl;

	private String id;

	private String repo;

	private String url;

	private User user;

	/**
	 * @return isPublic
	 */
	public boolean isPublic() {
		return this.isPublic;
	}

	/**
	 * @param isPublic
	 * @return this gist
	 */
	public Gist setPublic(boolean isPublic) {
		this.isPublic = isPublic;
		return this;
	}

	/**
	 * @return createdAt
	 */
	public Date getCreatedAt() {
		return this.createdAt;
	}

	/**
	 * @return comments
	 */
	public int getComments() {
		return this.comments;
	}

	/**
	 * @return files
	 */
	public Map<String, GistFile> getFiles() {
		return this.files;
	}

	/**
	 * @param files
	 * @return this gist
	 */
	public Gist setFiles(Map<String, GistFile> files) {
		this.files = files;
		return this;
	}

	/**
	 * @return description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description
	 * @return this gist
	 */
	public Gist setDescription(String description) {
		this.description = description;
		return this;
	}

	/**
	 * @return gitPullUrl
	 */
	public String getGitPullUrl() {
		return this.gitPullUrl;
	}

	/**
	 * @return gitPushUrl
	 */
	public String getGitPushUrl() {
		return this.gitPushUrl;
	}

	/**
	 * @return id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @return repo
	 */
	public String getRepo() {
		return this.repo;
	}

	/**
	 * @param repo
	 * @return this gist
	 */
	public Gist setRepo(String repo) {
		this.repo = repo;
		return this;
	}

	/**
	 * @return url
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * @return user
	 */
	public User getUser() {
		return this.user;
	}

	/**
	 * @param user
	 * @return this gist
	 */
	public Gist setUser(User user) {
		this.user = user;
		return this;
	}

}
