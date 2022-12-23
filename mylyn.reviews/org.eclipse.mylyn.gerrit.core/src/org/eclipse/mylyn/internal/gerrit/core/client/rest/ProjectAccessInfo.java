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

import java.util.List;
import java.util.Map;

public class ProjectAccessInfo {

	private String revision;

	private ProjectInfo inherits_from;

	private Map<String, AccessSectionInfo> local;

	private boolean is_owner;

	private boolean can_upload;

	private boolean can_add;

	private boolean can_add_tags;

	private boolean config_visible;

	private Map<String, GroupInfo> groups;

	private List<String> owner_of;

	private List<String> configWebLinks;

	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

	public ProjectInfo getInherits_from() {
		return inherits_from;
	}

	public void setInherits_from(ProjectInfo inherits_from) {
		this.inherits_from = inherits_from;
	}

	public Map<String, AccessSectionInfo> getLocal() {
		return local;
	}

	public void setLocal(Map<String, AccessSectionInfo> local) {
		this.local = local;
	}

	public boolean is_owner() {
		return is_owner;
	}

	public void setIs_owner(boolean is_owner) {
		this.is_owner = is_owner;
	}

	public boolean isCan_upload() {
		return can_upload;
	}

	public void setCan_upload(boolean can_upload) {
		this.can_upload = can_upload;
	}

	public boolean isCan_add() {
		return can_add;
	}

	public void setCan_add(boolean can_add) {
		this.can_add = can_add;
	}

	public boolean isCan_add_tags() {
		return can_add_tags;
	}

	public void setCan_add_tags(boolean can_add_tags) {
		this.can_add_tags = can_add_tags;
	}

	public boolean isConfig_visible() {
		return config_visible;
	}

	public void setConfig_visible(boolean config_visible) {
		this.config_visible = config_visible;
	}

	public Map<String, GroupInfo> getGroups() {
		return groups;
	}

	public void setGroups(Map<String, GroupInfo> groups) {
		this.groups = groups;
	}

	public List<String> getOwner_of() {
		return owner_of;
	}

	public void setOwner_of(List<String> owner_of) {
		this.owner_of = owner_of;
	}

	public List<String> getConfigWebLinks() {
		return configWebLinks;
	}

	public void setConfigWebLinks(List<String> configWebLinks) {
		this.configWebLinks = configWebLinks;
	}

}
