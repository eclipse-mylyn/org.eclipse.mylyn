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

import java.sql.Timestamp;
import java.util.Map;

import org.eclipse.mylyn.internal.gerrit.core.GerritUtil;

import com.google.gerrit.common.data.ChangeInfo;
import com.google.gerrit.reviewdb.Change.Status;

/**
 * @author Steffen Pingel
 */
public class GerritQueryResult {

	private int _number;

	private String branch;

	private Timestamp created;

	private String id;

	private Map<String, GerritLabel> labels;

	private GerritPerson owner;

	private String project;

	private String status;

	private String subject;

	private Timestamp updated;

	public GerritQueryResult(ChangeInfo changeInfo) {
		setNumber(changeInfo.getId().get());
		setId(changeInfo.getKey().get());
		setProject(changeInfo.getProject().getName());
		setSubject(changeInfo.getSubject());
		Status status = changeInfo.getStatus();
		if (GerritUtil.isDraft(status)) {
			setStatus("DRAFT"); //$NON-NLS-1$
		} else {
			setStatus(status.toString());
		}
		setUpdated(changeInfo.getLastUpdatedOn());
	}

	public GerritQueryResult() {
	}

	public String getBranch() {
		return branch;
	}

	public Timestamp getCreated() {
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

	public Timestamp getUpdated() {
		return updated;
	}

	private void setId(String id) {
		this.id = id;
	}

	private void setNumber(int number) {
		this._number = number;
	}

	private void setProject(String project) {
		this.project = project;
	}

	private void setStatus(String status) {
		this.status = status;
	}

	private void setSubject(String subject) {
		this.subject = subject;
	}

	private void setUpdated(Timestamp updated) {
		this.updated = updated;
	}

}
