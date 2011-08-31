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
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.egit.github.core.util.DateUtils;

/**
 * GitHub gist model class.
 */
public class Gist implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = -2221817463228217456L;

	@SerializedName("public")
	private boolean isPublic;

	private Date createdAt;

	private Date updatedAt;

	private int comments;

	private List<GistRevision> history;

	private Map<String, GistFile> files;

	private String description;

	private String gitPullUrl;

	private String gitPushUrl;

	private String htmlUrl;

	private String id;

	private String url;

	private User user;

	/**
	 * @return isPublic
	 */
	public boolean isPublic() {
		return isPublic;
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
		return DateUtils.clone(createdAt);
	}

	/**
	 * @return comments
	 */
	public int getComments() {
		return comments;
	}

	/**
	 * @return files
	 */
	public Map<String, GistFile> getFiles() {
		return files;
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
		return description;
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
		return gitPullUrl;
	}

	/**
	 * @return gitPushUrl
	 */
	public String getGitPushUrl() {
		return gitPushUrl;
	}

	/**
	 * @return history
	 */
	public List<GistRevision> getHistory() {
		return history;
	}

	/**
	 * @return htmlUrl
	 */
	public String getHtmlUrl() {
		return htmlUrl;
	}

	/**
	 * @return id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 * @return this gist
	 */
	public Gist setId(String id) {
		this.id = id;
		return this;
	}

	/**
	 * @return updatedAt
	 */
	public Date getUpdatedAt() {
		return DateUtils.clone(updatedAt);
	}

	/**
	 * @return url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @return user
	 */
	public User getUser() {
		return user;
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
