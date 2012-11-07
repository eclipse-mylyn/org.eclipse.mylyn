/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client.data;

import java.util.Date;
import java.util.Map;

/**
 * @author Steffen Pingel
 */
public class GerritQueryResult {

	private int _number;

	private String branch;

	private Date created;

	private String id;

	private Map<String, GerritLabel> labels;

	private GerritPerson owner;

	private String project;

	private String status;

	private String subject;

	private Date updated;

	public String getBranch() {
		return branch;
	}

	public Date getCreated() {
		return created;
	}

	public String getId() {
		return id;
	}

	public Map<String, GerritLabel> getLabels() {
		return labels;
	}

	public int getNumber() {
		return _number;
	}

	public GerritPerson getOwner() {
		return owner;
	}

	public String getProject() {
		return project;
	}

	public String getStatus() {
		return status;
	}

	public String getSubject() {
		return subject;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLabels(Map<String, GerritLabel> labels) {
		this.labels = labels;
	}

	public void setNumber(int number) {
		this._number = number;
	}

	public void setOwner(GerritPerson owner) {
		this.owner = owner;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

}
