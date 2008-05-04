/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskAttribute;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData;

/**
 * @author Rob Elves
 */
public class TaskDataState {

	private RepositoryTaskData newTaskData;

	private RepositoryTaskData oldTaskData;

	private Set<RepositoryTaskAttribute> edits = new CopyOnWriteArraySet<RepositoryTaskAttribute>();

	private final String url;

	private final String id;

	public TaskDataState(String repositoryUrl, String id) {
		this.url = repositoryUrl;
		this.id = id;
	}

	public RepositoryTaskData getNewTaskData() {
		return newTaskData;
	}

	public void setNewTaskData(RepositoryTaskData newTaskData) {
		this.newTaskData = newTaskData;
	}

	public RepositoryTaskData getOldTaskData() {
		return oldTaskData;
	}

	public void setOldTaskData(RepositoryTaskData oldTaskData) {
		this.oldTaskData = oldTaskData;
	}

	public Set<RepositoryTaskAttribute> getEdits() {
		return edits;
	}

	public void setEdits(Set<RepositoryTaskAttribute> edits) {
		if (edits == null) {
			edits = new HashSet<RepositoryTaskAttribute>();
		} else {
			this.edits = edits;
		}
	}

	public void discardEdits() {
		if (edits != null) {
			this.edits.clear();
		} else {
			setEdits(null);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final TaskDataState other = (TaskDataState) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (url == null) {
			if (other.url != null) {
				return false;
			}
		} else if (!url.equals(other.url)) {
			return false;
		}
		return true;
	}

	public String getUrl() {
		return url;
	}

	public String getId() {
		return id;
	}

// void discardEdits();	
//	   Set<RepositoryTaskAttribute> getChanged();
//	   isStateModified();
//	   hasIncomingChanges();
//	   hasChanged(RepositoryTaskAttribute attribute);
//	   public init();  // Perform constructor specific initialization (i.e. calc changed attributes etc)

}
