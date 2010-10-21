/*********************************************************************
 * Copyright (c) 2010 Sony Ericsson/ST Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *********************************************************************/
package org.eclipse.mylyn.internal.gerrit.core;

import java.util.Date;

/**
 * GerritTask. Represents a change issue in Gerrit.
 * @author Mikael Kober, Sony Ericsson
 * @author Tomas Westling, Sony Ericsson -
 *         thomas.westling@sonyericsson.com
 *
 */
public class GerritTask {
	
	private final String id;
	private String title;
	private String changeId;
	private String owner;
	private String project;
	private String branch;
	private Date uploaded;
	private Date updated;
	private String url;
	private String status;
    private String description;
	
	/**
	 * Constructor.
	 * @param id
	 * @param changeId
	 * @param title
	 * @param owner
	 * @param project
	 * @param branch
	 * @param uploaded
	 * @param updated
	 * @param status
	 */
	public GerritTask(String id, String changeId, String title, String owner,
			String project, String branch, Date uploaded, Date updated, String status) {
		super();
		this.id = id;
		this.title = title;
		this.changeId = changeId;
		this.owner = owner;
		this.project = project;
		this.branch = branch;
		this.uploaded = uploaded;
		this.updated = updated;
		this.status = status;
	}

	/**
	 * Constructor.
	 * @param id
	 * @param title
	 */
	public GerritTask(String id, String title) {
		this.id = id;
		this.title = title;
	}
	
	public String getChangeId() {
		return changeId;
	}

	public void setChangeId(String changeId) {
		this.changeId = changeId;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public Date getUploaded() {
		return uploaded;
	}

	public void setUploaded(Date uploaded) {
		this.uploaded = uploaded;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getId() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}

    public String getDescription() {
        return description;
    }
    public void setDescription(String description){
        this.description = description;
    }

	

}
