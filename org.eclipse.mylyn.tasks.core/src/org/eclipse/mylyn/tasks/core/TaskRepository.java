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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.mylar.context.core.MylarStatusHandler;

/**
 * Note that task repositories use Strings for storing time stamps because using
 * Date objects led to the following problems:
 * <ul>
 * <li>Often we are unable to get the time zone of the repository so
 * interpreting the date string correctly doesn't work.</li>
 * <li>Even if we do know the time zone information the local clock may be
 * wrong. This can cause lost incoming when asking the repository for all
 * changes since date X.</li>
 * <li>The solution we have come up with thus far is not to interpret the date
 * as a DATE object but rather simply use the date string given to us by the
 * repository itself.</li>
 * </ul>
 * 
 * @author Mik Kersten
 * @author Rob Elves
 */
public class TaskRepository {

	public static final String DEFAULT_CHARACTER_ENCODING = "UTF-8";

	public static final String AUTH_PASSWORD = "org.eclipse.mylar.tasklist.repositories.password"; //$NON-NLS-1$ 

	public static final String AUTH_USERNAME = "org.eclipse.mylar.tasklist.repositories.username"; //$NON-NLS-1$ 

	public static final String AUTH_HTTP_PASSWORD = "org.eclipse.mylar.tasklist.repositories.httpauth.password"; //$NON-NLS-1$ 

	public static final String AUTH_HTTP_USERNAME = "org.eclipse.mylar.tasklist.repositories.httpauth.username"; //$NON-NLS-1$ 

	public static final String NO_VERSION_SPECIFIED = "unknown";

	private static final String AUTH_SCHEME = "Basic";

	private static final String AUTH_REALM = "";

	private static final URL DEFAULT_URL;

	static {
		URL u = null;
		try {
			u = new URL("http://eclipse.org/mylar");
		} catch (Exception ex) {
			// TODO ?
		}
		DEFAULT_URL = u;
	}

	private Map<String, String> properties = new HashMap<String, String>();

	/**
	 * for testing purposes
	 */
	public TaskRepository(String kind, String serverUrl) {
		this(kind, serverUrl, NO_VERSION_SPECIFIED);
	}

	/**
	 * for testing purposes sets repository time zone to local default time zone
	 * sets character encoding to DEFAULT_CHARACTER_ENCODING
	 */
	public TaskRepository(String kind, String serverUrl, String version) {
		this(kind, serverUrl, version, DEFAULT_CHARACTER_ENCODING, TimeZone.getDefault().getID());
	}

	public TaskRepository(String kind, String serverUrl, String version, String encoding, String timeZoneId) {
		this.properties.put(IRepositoryConstants.PROPERTY_KIND, kind);
		this.properties.put(IRepositoryConstants.PROPERTY_URL, serverUrl);
		this.properties.put(IRepositoryConstants.PROPERTY_VERSION, version);
		this.properties.put(IRepositoryConstants.PROPERTY_ENCODING, encoding);
		this.properties.put(IRepositoryConstants.PROPERTY_TIMEZONE, timeZoneId);
	}

	public TaskRepository(String kind, String serverUrl, Map<String, String> properties) {
		this.properties.put(IRepositoryConstants.PROPERTY_KIND, kind);
		this.properties.put(IRepositoryConstants.PROPERTY_URL, serverUrl);
		this.properties.putAll(properties);
	}

	public String getUrl() {
		return properties.get(IRepositoryConstants.PROPERTY_URL);
	}

	public void setUrl(String newUrl) {
		properties.put(IRepositoryConstants.PROPERTY_URL, newUrl);
	}

	public boolean hasCredentials() {
		String username = getUserName();
		String password = getPassword();
		return username != null && username.length() > 0 && password != null && password.length() > 0;
	}

	@SuppressWarnings("unchecked")
	public String getUserName() {
		Map<String, String> map = getAuthInfo();
		if (map != null && map.containsKey(AUTH_USERNAME)) {
			return map.get(AUTH_USERNAME);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public String getPassword() {
		Map<String, String> map = getAuthInfo();
		if (map != null && map.containsKey(AUTH_PASSWORD)) {
			return map.get(AUTH_PASSWORD);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public void setAuthenticationCredentials(String username, String password) {
		Map<String, String> map = getAuthInfo();

		if (map == null) {
			map = new java.util.HashMap<String, String>();
		}

		if (username != null) {
			map.put(AUTH_USERNAME, username);
		}
		if (password != null) {
			map.put(AUTH_PASSWORD, password);
		}
		try {
			// write the map to the keyring
			try {
				Platform.addAuthorizationInfo(new URL(getUrl()), AUTH_REALM, AUTH_SCHEME, map);
			} catch (MalformedURLException ex) {
				Platform.addAuthorizationInfo(DEFAULT_URL, getUrl(), AUTH_SCHEME, map);
			}
		} catch (CoreException e) {
			MylarStatusHandler.fail(e, "could not set authorization", true);
		}
	}

	public void flushAuthenticationCredentials() {
		try {
			try {
				Platform.flushAuthorizationInfo(new URL(getUrl()), AUTH_REALM, AUTH_SCHEME);
			} catch (MalformedURLException ex) {
				Platform.flushAuthorizationInfo(DEFAULT_URL, getUrl(), AUTH_SCHEME);
			}
		} catch (CoreException e) {
			MylarStatusHandler.fail(e, "could not set authorization", true);
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, String> getAuthInfo() {
		try {
			return Platform.getAuthorizationInfo(new URL(getUrl()), AUTH_REALM, AUTH_SCHEME);
		} catch (MalformedURLException ex) {
			return Platform.getAuthorizationInfo(DEFAULT_URL, getUrl(), AUTH_SCHEME);
		}
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof TaskRepository && getUrl() != null) {
			return getUrl().equals(((TaskRepository) object).getUrl());
		} else {
			return super.equals(object);
		}
	}

	@Override
	public int hashCode() {
		if (getUrl() != null) {
			return getUrl().hashCode();
		} else {
			return super.hashCode();
		}
	}

	public String toString() {
		return getUrl();
	}

	public String getKind() {
		return properties.get(IRepositoryConstants.PROPERTY_KIND);
	}

	public String getVersion() {
		final String version = properties.get(IRepositoryConstants.PROPERTY_VERSION);
		return version == null || "".equals(version) ? NO_VERSION_SPECIFIED : version;
	}

	public void setVersion(String ver) {
		properties.put(IRepositoryConstants.PROPERTY_VERSION, ver == null ? NO_VERSION_SPECIFIED : ver);
	}

	public String getCharacterEncoding() {
		final String encoding = properties.get(IRepositoryConstants.PROPERTY_ENCODING);
		return encoding == null || "".equals(encoding) ? DEFAULT_CHARACTER_ENCODING : encoding;
	}

	/**
	 * for testing purposes
	 */
	public void setCharacterEncoding(String characterEncoding) {
		properties.put(IRepositoryConstants.PROPERTY_ENCODING, characterEncoding == null ? DEFAULT_CHARACTER_ENCODING
				: characterEncoding);
	}

	public String getTimeZoneId() {
		final String timeZoneId = properties.get(IRepositoryConstants.PROPERTY_TIMEZONE);
		return timeZoneId == null || "".equals(timeZoneId) ? TimeZone.getDefault().getID() : timeZoneId;
	}

	public void setTimeZoneId(String timeZoneId) {
		this.properties.put(IRepositoryConstants.PROPERTY_TIMEZONE, timeZoneId == null ? TimeZone.getDefault().getID()
				: timeZoneId);
	}

	public String getSyncTimeStamp() {
		return this.properties.get(IRepositoryConstants.PROPERTY_SYNCTIMESTAMP);
	}

	/**
	 * ONLY for use by IRepositoryConstants. To set the sync time call
	 * IRepositoryConstants.setSyncTime(repository, date);
	 */
	public void setSyncTimeStamp(String syncTime) {
		this.properties.put(IRepositoryConstants.PROPERTY_SYNCTIMESTAMP, syncTime);
	}

	public void setRepositoryLabel(String repositoryLabel) {
		this.properties.put(IRepositoryConstants.PROPERTY_LABEL, repositoryLabel);
	}

	public String getRepositoryLabel() {
		return this.properties.get(IRepositoryConstants.PROPERTY_LABEL);
	}

	public Map<String, String> getProperties() {
		return this.properties;
	}

	public String getProperty(String name) {
		return this.properties.get(name);
	}

	public void setProperty(String name, String value) {
		this.properties.put(name, value);
	}

	public boolean hasProperty(String name) {
		String value = getProperty(name);
		return value != null && value.trim().length() > 0;
	}
}
