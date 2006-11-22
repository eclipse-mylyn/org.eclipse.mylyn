/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.trac.core;

import java.net.Proxy;
import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylar.internal.trac.core.model.TracComponent;
import org.eclipse.mylar.internal.trac.core.model.TracMilestone;
import org.eclipse.mylar.internal.trac.core.model.TracPriority;
import org.eclipse.mylar.internal.trac.core.model.TracSeverity;
import org.eclipse.mylar.internal.trac.core.model.TracTicketResolution;
import org.eclipse.mylar.internal.trac.core.model.TracTicketStatus;
import org.eclipse.mylar.internal.trac.core.model.TracTicketType;
import org.eclipse.mylar.internal.trac.core.model.TracVersion;

/**
 * @author Steffen Pingel
 */
public abstract class AbstractTracClient implements ITracClient {

	protected String username;

	protected String password;

	protected URL repositoryUrl;

	protected Version version;

	protected TracClientData data;

	protected Proxy proxy;
	
	public AbstractTracClient(URL repositoryUrl, Version version, String username, String password, Proxy proxy) {
		this.repositoryUrl = repositoryUrl;
		this.version = version;
		this.username = username;
		this.password = password;
		this.proxy = proxy;
		
		this.data = new TracClientData();
	}

	public Version getVersion() {
		return version;
	}

	protected boolean hasAuthenticationCredentials() {
		return username != null && username.length() > 0;
	}

	public TracComponent[] getComponents() {
		return (data.components != null) ? data.components.toArray(new TracComponent[0]) : null;
	}

	public TracMilestone[] getMilestones() {
		return (data.milestones != null) ? data.milestones.toArray(new TracMilestone[0]) : null;
	}

	public TracPriority[] getPriorities() {
		return (data.priorities != null) ? data.priorities.toArray(new TracPriority[0]) : null;
	}

	public TracSeverity[] getSeverities() {
		return (data.severities != null) ? data.severities.toArray(new TracSeverity[0]) : null;
	}
	
	public TracTicketResolution[] getTicketResolutions() {
		return (data.ticketResolutions != null) ? data.ticketResolutions.toArray(new TracTicketResolution[0]) : null;
	}

	public TracTicketStatus[] getTicketStatus() {
		return (data.ticketStatus != null) ? data.ticketStatus.toArray(new TracTicketStatus[0]) : null;
	}

	public TracTicketType[] getTicketTypes() {
		return (data.ticketTypes != null) ? data.ticketTypes.toArray(new TracTicketType[0]) : null;
	}

	public TracVersion[] getVersions() {
		return (data.versions != null) ? data.versions.toArray(new TracVersion[0]) : null;
	}

	public boolean hasAttributes() {
		return (data.lastUpdate != 0);
	}
	
	public void updateAttributes(IProgressMonitor monitor, boolean force) throws TracException {
		if (!hasAttributes() || force) {
			updateAttributes(monitor);
			data.lastUpdate = System.currentTimeMillis();
		}
	}
	
	public abstract void updateAttributes(IProgressMonitor monitor) throws TracException;

	public void setData(TracClientData data) {
		this.data = data;
	}
	
	public String[] getDefaultTicketResolutions() {
		return new String[] { "fixed", "invalid", "wontfix", "duplicate", "worksforme" }; 
	}
	
	public String[] getDefaultTicketActions(String status) {
		if ("new".equals(status)) {
			return new String[] { "leave", "resolve", "reassign", "accept" };
		} else if ("assigned".equals(status)) {
			return new String[] { "leave", "resolve", "reassign" };
		} else if ("reopened".equals(status)) {
			return new String[] { "leave", "resolve", "reassign" };
		} else if ("closed".equals(status)) {
			return new String[] { "leave", "reopen" };
		}
		return null;
	}

	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}
	
	public Proxy getProxy() {
		return proxy;
	}
	
}
