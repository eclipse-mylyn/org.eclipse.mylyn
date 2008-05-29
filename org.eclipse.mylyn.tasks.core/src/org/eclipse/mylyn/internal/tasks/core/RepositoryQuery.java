/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;

/**
 * @author Mik Kersten
 * @author Eugene Kuleshov
 * @author Rob Elves
 */
public class RepositoryQuery extends AbstractTaskContainer implements IRepositoryQuery {

	private final String connectorKind;

	protected String lastSynchronizedStamp = "<never>";

	protected String repositoryUrl;

	protected IStatus status;

	private boolean synchronizing;

	private String summary;

	private Map<String, String> attributes;

	@Deprecated
	public RepositoryQuery(String description) {
		this("", description);
	}

	public RepositoryQuery(String connectorKind, String handle) {
		super(handle);
		this.connectorKind = connectorKind;
		setSummary(handle);
	}

	/**
	 * @since 3.0
	 */
	public String getConnectorKind() {
		return connectorKind;
	}

	public String getLastSynchronizedTimeStamp() {
		return lastSynchronizedStamp;
	}

	@Override
	public String getPriority() {
		if (super.isEmpty()) {
			return PriorityLevel.P1.toString();
		}
		String highestPriority = PriorityLevel.P5.toString();
		for (ITask hit : getChildren()) {
			if (highestPriority.compareTo(hit.getPriority()) > 0) {
				highestPriority = hit.getPriority();
			}
		}
		return highestPriority;
	}

	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	public IStatus getStatus() {
		return status;
	}

	// TODO: move higher up and merge with AbstractTask
	public boolean isSynchronizing() {
		return synchronizing;
	}

	public void setLastSynchronizedStamp(String lastRefreshTimeStamp) {
		this.lastSynchronizedStamp = lastRefreshTimeStamp;
	}

	public void setRepositoryUrl(String newRepositoryUrl) {
		if (repositoryUrl != null && url != null) {
			// the repository url has changed, so change corresponding part of
			// query URL
			this.url = newRepositoryUrl + url.substring(repositoryUrl.length());
		}
		this.repositoryUrl = newRepositoryUrl;
	}

	public void setStatus(IStatus status) {
		this.status = status;
	}

	public void setSynchronizing(boolean synchronizing) {
		this.synchronizing = synchronizing;
	}

	@Override
	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public synchronized String getAttribute(String key) {
		return (attributes != null) ? attributes.get(key) : null;
	}

	public synchronized Map<String, String> getAttributes() {
		if (attributes != null) {
			return new HashMap<String, String>(attributes);
		} else {
			return Collections.emptyMap();
		}
	}

	public synchronized void setAttribute(String key, String value) {
		Assert.isNotNull(key);
		if (attributes == null) {
			attributes = new HashMap<String, String>();
		}
		attributes.put(key, value);
	}

}
