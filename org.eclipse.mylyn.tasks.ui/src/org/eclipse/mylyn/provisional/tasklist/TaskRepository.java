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

import java.net.URL;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;

/**
 * @author Mik Kersten
 */
public class TaskRepository {

	public static final String AUTH_PASSWORD = "org.eclipse.mylar.tasklist.repositories.password"; //$NON-NLS-1$ 

	public static final String AUTH_USERNAME = "org.eclipse.mylar.tasklist.repositories.username"; //$NON-NLS-1$ 

	public static final String NO_VERSION_SPECIFIED = "";
	
	private static final String AUTH_SCHEME = "Basic";

	private static final String AUTH_REALM = "";

	private URL serverUrl;

	private String kind;
	
	private String version = NO_VERSION_SPECIFIED;

	public TaskRepository(String kind, URL serverUrl) {
		this.serverUrl = serverUrl;
		this.kind = kind;
	}

	public TaskRepository(String kind, URL serverUrl, String version) {
		this(kind, serverUrl);
		this.version = version;
	}
	
	public URL getUrl() {
		return serverUrl;
	}

	public boolean hasCredentials() {
		String username = getUserName();
		String password = getPassword();
		return username != null && !username.equals("") && password != null && !password.equals("");
	}

	@SuppressWarnings("unchecked")
	public String getUserName() {
		Map<String, String> map = Platform.getAuthorizationInfo(serverUrl, AUTH_REALM, AUTH_SCHEME);
		if (map != null && map.containsKey(AUTH_USERNAME)) {
			return map.get(AUTH_USERNAME);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public String getPassword() {
		Map<String, String> map = Platform.getAuthorizationInfo(serverUrl, AUTH_REALM, AUTH_SCHEME);
		if (map != null && map.containsKey(AUTH_PASSWORD)) {
			return map.get(AUTH_PASSWORD);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public void setAuthenticationCredentials(String username, String password) {
		Map<String, String> map = Platform.getAuthorizationInfo(serverUrl, AUTH_REALM, AUTH_SCHEME);

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
			Platform.addAuthorizationInfo(serverUrl, AUTH_REALM, AUTH_SCHEME, map);
		} catch (CoreException e) {
			MylarStatusHandler.fail(e, "could not set authorization", true);
		}
	}

	@Override
	public boolean equals(Object object) {
		if (serverUrl != null && object instanceof TaskRepository) {
			return serverUrl.equals(((TaskRepository) object).getUrl());
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
		return serverUrl.toExternalForm();
	}

	public String getKind() {
		return kind;
	}

	
	public String getVersion() {
		return version;
	}
	
	void setVersion(String ver) {
		if(ver == null) {
			version = NO_VERSION_SPECIFIED;
		} else {
			version = ver;
		}
	}
}
