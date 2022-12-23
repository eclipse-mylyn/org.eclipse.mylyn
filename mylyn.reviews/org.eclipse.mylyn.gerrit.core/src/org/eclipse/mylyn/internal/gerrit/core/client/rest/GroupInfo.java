/*******************************************************************************
 * Copyright (c) 2019 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client.rest;

import java.sql.Timestamp;
import java.util.List;

public class GroupInfo {

	private String id;

	private String name;

	private String description;

	private String url;

	private int group_id;

	private String owner;

	private String owner_id;

	private Timestamp created_on;

	private GroupOptionsInfo options;

	private boolean _more_groups;

	private List<AccountInfo> members;

	private List<GroupInfo> includes;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getGroup_id() {
		return group_id;
	}

	public void setGroup_id(int group_id) {
		this.group_id = group_id;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getOwner_id() {
		return owner_id;
	}

	public void setOwner_id(String owner_id) {
		this.owner_id = owner_id;
	}

	public Timestamp getCreated_on() {
		return created_on;
	}

	public void setCreated_on(Timestamp created_on) {
		this.created_on = created_on;
	}

	public GroupOptionsInfo getOptions() {
		return options;
	}

	public void setOptions(GroupOptionsInfo options) {
		this.options = options;
	}

	public boolean is_more_groups() {
		return _more_groups;
	}

	public void set_more_groups(boolean _more_groups) {
		this._more_groups = _more_groups;
	}

	public List<AccountInfo> getMembers() {
		return members;
	}

	public void setMembers(List<AccountInfo> members) {
		this.members = members;
	}

	public List<GroupInfo> getIncludes() {
		return includes;
	}

	public void setIncludes(List<GroupInfo> includes) {
		this.includes = includes;
	}

}
