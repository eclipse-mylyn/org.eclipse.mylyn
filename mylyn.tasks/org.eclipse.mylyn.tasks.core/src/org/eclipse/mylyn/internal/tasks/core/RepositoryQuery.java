/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Eugene Kuleshov - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import java.util.Collections;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;

/**
 * @author Mik Kersten
 * @author Eugene Kuleshov
 * @author Rob Elves
 */
public class RepositoryQuery extends AbstractTaskContainer implements IRepositoryQuery, ITaskRepositoryElement {

	private final String connectorKind;

	protected String lastSynchronizedStamp = "<never>"; //$NON-NLS-1$

	protected String repositoryUrl;

	protected IStatus status;

	private boolean synchronizing;

	private String summary;

	private AttributeMap attributeMap;

	public RepositoryQuery(String connectorKind, String handle) {
		super(handle);
		this.connectorKind = connectorKind;
		setSummary(handle);
	}

	@Override
	public String getConnectorKind() {
		return connectorKind;
	}

	// TODO: should be a date
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

	@Override
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
		lastSynchronizedStamp = lastRefreshTimeStamp;
	}

	public void setRepositoryUrl(String newRepositoryUrl) {
		String url = getUrl();
		if (repositoryUrl != null && url != null && url.startsWith(repositoryUrl)) {
			// change corresponding part of the query URL
			setUrl(newRepositoryUrl + url.substring(repositoryUrl.length()));
		}
		repositoryUrl = newRepositoryUrl;
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

	@Override
	public void setSummary(String summary) {
		this.summary = summary;
	}

	@Override
	public synchronized String getAttribute(String key) {
		return attributeMap != null ? attributeMap.getAttribute(key) : null;
	}

	@Override
	public synchronized Map<String, String> getAttributes() {
		if (attributeMap != null) {
			return attributeMap.getAttributes();
		} else {
			return Collections.emptyMap();
		}
	}

	@Override
	public synchronized void setAttribute(String key, String value) {
		if (attributeMap == null) {
			attributeMap = new AttributeMap();
		}
		attributeMap.setAttribute(key, value);
	}

	public boolean getAutoUpdate() {
		String value = getAttribute(ITasksCoreConstants.ATTRIBUTE_AUTO_UPDATE);
		return value == null || Boolean.valueOf(value);
	}

	public void setAutoUpdate(boolean autoUpdate) {
		setAttribute(ITasksCoreConstants.ATTRIBUTE_AUTO_UPDATE, Boolean.toString(autoUpdate));
	}

}
