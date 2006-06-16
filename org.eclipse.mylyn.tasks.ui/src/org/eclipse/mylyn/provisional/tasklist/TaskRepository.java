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

package org.eclipse.mylar.provisional.tasklist;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;

/**
 * @author Mik Kersten
 */
public class TaskRepository {

	public static final String DEFAULT_CHARACTER_ENCODING = "UTF-8";
	
	public static final String AUTH_PASSWORD = "org.eclipse.mylar.tasklist.repositories.password"; //$NON-NLS-1$ 

	public static final String AUTH_USERNAME = "org.eclipse.mylar.tasklist.repositories.username"; //$NON-NLS-1$ 

	public static final String NO_VERSION_SPECIFIED = "";
	
	private static final String AUTH_SCHEME = "Basic";

	private static final String AUTH_REALM = "";

    private static final URL DEFAULT_URL;
    
    static {
      URL u = null;
      try {
        u = new URL("http://eclipse.org/mylar");
      } catch(Exception ex) {
        // TODO ?
      }
      DEFAULT_URL = u; 
    }

	private String kind;

	private String serverUrl;
	
	private Date syncTime = new Date(0); 
    
    private Map<String, String> properties = new HashMap<String, String>();
	
	/**
	 * for testing purposes
	 */
	public TaskRepository(String kind, String serverUrl) {
		this(kind, serverUrl, NO_VERSION_SPECIFIED);
	}
	 
	/**
	 * for testing purposes
	 * sets repository time zone to local default time zone
	 * sets character encoding to DEFAULT_CHARACTER_ENCODING
	 */ 
	public TaskRepository(String kind, String serverUrl, String version) {
		this(kind, serverUrl, version, DEFAULT_CHARACTER_ENCODING, TimeZone.getDefault().getID());		
	}
	
	public TaskRepository(String kind, String serverUrl, String version, String encoding, String timeZoneId) {
		this.kind = kind;
		this.serverUrl = serverUrl;
		this.properties.put(TaskRepositoryManager.PROPERTY_VERSION, version);
		this.properties.put(TaskRepositoryManager.PROPERTY_ENCODING, encoding);
		this.properties.put(TaskRepositoryManager.PROPERTY_TIMEZONE, timeZoneId);
	}
	
	public TaskRepository(String kind, String serverUrl, Map<String, String> properties) {
		this.kind = kind;
		this.serverUrl = serverUrl;
		this.properties.putAll(properties);
	}

	public String getUrl() {
		return this.serverUrl;
	}
	
	public boolean hasCredentials() {
		String username = getUserName();
		String password = getPassword();
		//return username != null && !username.equals("") && password != null && !password.equals("");
		return username != null && password != null;
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

	private Map getAuthInfo() {
		try {
			return Platform.getAuthorizationInfo(new URL(getUrl()), AUTH_REALM, AUTH_SCHEME);
		} catch(MalformedURLException ex) {
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
		if (serverUrl != null) {
			return serverUrl.hashCode();
		} else {
			return super.hashCode();
		}
	}

	public String toString() {
		return serverUrl;
	}

	public String getKind() {
		return kind;
	}

	
	public String getVersion() {
		return properties.get(TaskRepositoryManager.PROPERTY_VERSION);
	}
	
	/**
	 * for testing purposes
	 */
	void setVersion(String ver) {
		properties.put(TaskRepositoryManager.PROPERTY_VERSION, ver == null ? NO_VERSION_SPECIFIED : ver);
	}

	
	public String getCharacterEncoding() {
		final String encoding = properties.get(TaskRepositoryManager.PROPERTY_ENCODING);
		return encoding==null || "".equals(encoding) ? DEFAULT_CHARACTER_ENCODING : encoding;
	}
	
	/**
	 * for testing purposes
	 */
	void setCharacterEncoding(String characterEncoding) {
		properties.put(TaskRepositoryManager.PROPERTY_ENCODING, characterEncoding);
	}
	
	public String getTimeZoneId() {
		final String timeZoneId = properties.get(TaskRepositoryManager.PROPERTY_TIMEZONE);
		return timeZoneId==null || "".equals(timeZoneId) ? TimeZone.getDefault().getID() : timeZoneId;
	}
	
	/**
	 * for testing purposes
	 */
	public void setTimeZoneId(String timeZoneId) {
		properties.put(TaskRepositoryManager.PROPERTY_TIMEZONE, timeZoneId);
	}

	public Date getSyncTime() {
		return syncTime;
	}

	/**
	 * ONLY for use by TaskRepositoryManager.
	 * To set the sync time call TaskRepositoryManager.setSyncTime(repository, date);
	 */
	protected void setSyncTime(Date syncTime) {
		this.syncTime = syncTime;
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

}
