/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.tasks.core;


/**
 * @author Mik Kersten
 */
public abstract class AbstractQueryHit implements ITaskListElement {

	protected String repositoryUrl;
	
	protected String description;

	protected String priority;
	
	protected String id;

	protected boolean isNotified = false;

	private AbstractRepositoryQuery parent;
	
	protected AbstractQueryHit(String repositoryUrl, String description, String id) {
		this.repositoryUrl = repositoryUrl;
		this.description = description;
		this.id = id;
	}
	
	public AbstractRepositoryQuery getParent() {
		return parent;
	}

	public void setParent(AbstractRepositoryQuery parent) {
		this.parent = parent;
	}
	
	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	public void setRepositoryUrl(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}

	public abstract AbstractRepositoryTask getOrCreateCorrespondingTask();
 
	/**
	 * @return null if there is no corresponding report
	 */
	public abstract AbstractRepositoryTask getCorrespondingTask();

	public abstract boolean isCompleted();
	
	public abstract void setCorrespondingTask(AbstractRepositoryTask task);

	public String getHandleIdentifier() {
		return AbstractRepositoryTask.getHandle(repositoryUrl, id);
	}

	public String getId() {
		return id;
	}
	
	public boolean isNotified() {
		return isNotified;
	}
	
	public void setNotified(boolean notified) {
		isNotified = notified;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof AbstractQueryHit)) {
			return false;
		}
		AbstractQueryHit hit = (AbstractQueryHit)obj;
		return hit.getHandleIdentifier().equals(this.getHandleIdentifier());		
	}

	@Override
	public int hashCode() {
		return this.getHandleIdentifier().hashCode();
	}
	
	/**
	 * @return the url of the hit without any additional login information etc.
	 */
	public String getUrl() {
		return "";
	}
	
	
}
