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

import java.net.URL;
import java.util.List;

import org.eclipse.mylar.internal.trac.model.TracComponent;
import org.eclipse.mylar.internal.trac.model.TracMilestone;
import org.eclipse.mylar.internal.trac.model.TracPriority;
import org.eclipse.mylar.internal.trac.model.TracSeverity;
import org.eclipse.mylar.internal.trac.model.TracTicketResolution;
import org.eclipse.mylar.internal.trac.model.TracTicketStatus;
import org.eclipse.mylar.internal.trac.model.TracTicketType;
import org.eclipse.mylar.internal.trac.model.TracVersion;

/**
 * @author Steffen Pingel
 */
public abstract class AbstractTracClient implements ITracClient {

	protected String username;

	protected String password;

	protected URL repositoryUrl;

	protected Version version;

	protected List<TracComponent> components;
	
	protected List<TracMilestone> milestones;
	
	protected List<TracPriority> priorities;
	
	protected List<TracSeverity> severities;
	
	protected List<TracTicketResolution> ticketResolutions;
	
	protected List<TracTicketStatus> ticketStatus;
	
	protected List<TracTicketType> ticketTypes;
	
	protected List<TracVersion> versions;
	
	public AbstractTracClient(URL repositoryUrl, Version version, String username, String password) {
		this.repositoryUrl = repositoryUrl;
		this.version = version;
		this.username = username;
		this.password = password;
	}

	public Version getVersion() {
		return version;
	}

	protected boolean hasAuthenticationCredentials() {
		return username != null && username.length() > 0;
	}


	public TracComponent[] getComponents() {
		return (components != null) ? components.toArray(new TracComponent[0]) : null;
	}

	public TracMilestone[] getMilestones() {
		return (milestones != null) ? milestones.toArray(new TracMilestone[0]) : null;
	}

	public TracPriority[] getPriorities() {
		return (priorities != null) ? priorities.toArray(new TracPriority[0]) : null;
	}

	public TracSeverity[] getSeverities() {
		return (severities != null) ? severities.toArray(new TracSeverity[0]) : null;
	}
	
	public TracTicketResolution[] getTicketResolutions() {
		return (ticketResolutions != null) ? ticketResolutions.toArray(new TracTicketResolution[0]) : null;
	}

	public TracTicketStatus[] getTicketStatus() {
		return (ticketStatus != null) ? ticketStatus.toArray(new TracTicketStatus[0]) : null;
	}

	public TracTicketType[] getTicketTypes() {
		return (ticketTypes != null) ? ticketTypes.toArray(new TracTicketType[0]) : null;
	}

	public TracVersion[] getVersions() {
		return (versions != null) ? versions.toArray(new TracVersion[0]) : null;
	}

}
