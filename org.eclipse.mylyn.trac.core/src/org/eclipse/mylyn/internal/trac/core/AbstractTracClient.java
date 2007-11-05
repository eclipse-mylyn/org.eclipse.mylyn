/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.internal.trac.core.model.TracComponent;
import org.eclipse.mylyn.internal.trac.core.model.TracMilestone;
import org.eclipse.mylyn.internal.trac.core.model.TracPriority;
import org.eclipse.mylyn.internal.trac.core.model.TracSeverity;
import org.eclipse.mylyn.internal.trac.core.model.TracTicketField;
import org.eclipse.mylyn.internal.trac.core.model.TracTicketResolution;
import org.eclipse.mylyn.internal.trac.core.model.TracTicketStatus;
import org.eclipse.mylyn.internal.trac.core.model.TracTicketType;
import org.eclipse.mylyn.internal.trac.core.model.TracVersion;
import org.eclipse.mylyn.web.core.AbstractWebLocation;
import org.eclipse.mylyn.web.core.WebClientUtil;
import org.eclipse.mylyn.web.core.WebCredentials;

/**
 * @author Steffen Pingel
 */
public abstract class AbstractTracClient implements ITracClient {

	protected static final String USER_AGENT = "TracConnector";

	private static final String LOGIN_COOKIE_NAME = "trac_auth";

	protected static final IProgressMonitor DEFAULT_MONITOR = new NullProgressMonitor();
	
	protected final String repositoryUrl;

	protected final Version version;

	protected final AbstractWebLocation location;

	protected TracClientData data;

	public AbstractTracClient(URL repositoryUrl, Version version, String username, String password, Proxy proxy) {
		this.repositoryUrl = repositoryUrl.toString();
		this.version = version;

		this.location = null;
		
		this.data = new TracClientData();
	}

	public AbstractTracClient(AbstractWebLocation location, Version version) {
		this.location = location;
		this.version = version;
		this.repositoryUrl = location.getUrl();
		
		this.data = new TracClientData();
	}

	public Version getVersion() {
		return version;
	}

	protected boolean credentialsValid(WebCredentials credentials) {
		return credentials != null && credentials.getUserName().length() > 0;
	}

	protected void authenticateAccountManager(HttpClient httpClient, WebCredentials credentials) throws IOException, TracLoginException {
		PostMethod post = new PostMethod(WebClientUtil.getRequestPath(repositoryUrl + LOGIN_URL));
		post.setFollowRedirects(false);
		NameValuePair[] data = { new NameValuePair("referer", ""), new NameValuePair("user", credentials.getUserName()),
				new NameValuePair("password", credentials.getPassword()) };
		post.setRequestBody(data);
		try {
			int code = httpClient.executeMethod(post);
			// code should be a redirect in case of success  
			if (code == HttpURLConnection.HTTP_OK) {
				throw new TracLoginException();
			}
		} finally {
			post.releaseConnection();
		}
	}

	/**
	 * Check if authentication cookie has been set.
	 * 
	 * @throws TracLoginException
	 *             thrown if the cookie has not been set
	 */
	protected void validateAuthenticationState(HttpClient httpClient) throws TracLoginException {
		Cookie[] cookies = httpClient.getState().getCookies();
		for (Cookie cookie : cookies) {
			if (LOGIN_COOKIE_NAME.equals(cookie.getName())) {
				return;
			}
		}

		throw new TracLoginException();
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

	public TracTicketField[] getTicketFields() {
		return (data.ticketFields != null) ? data.ticketFields.toArray(new TracTicketField[0]) : null;
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

}
