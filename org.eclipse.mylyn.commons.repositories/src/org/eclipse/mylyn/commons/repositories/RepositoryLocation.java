/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.repositories;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.security.storage.EncodingUtils;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.repositories.auth.AuthenticationType;
import org.eclipse.mylyn.commons.repositories.auth.UsernamePasswordCredentials;

/**
 * @author Steffen Pingel
 */
public class RepositoryLocation extends PlatformObject {

	private static final String AUTH_HTTP = "org.eclipse.mylyn.tasklist.repositories.httpauth"; //$NON-NLS-1$

	private static final String AUTH_PROXY = "org.eclipse.mylyn.tasklist.repositories.proxy"; //$NON-NLS-1$

	private static final String AUTH_REALM = ""; //$NON-NLS-1$

	private static final String AUTH_REPOSITORY = "org.eclipse.mylyn.tasklist.repositories"; //$NON-NLS-1$

	private static final String AUTH_SCHEME = "Basic"; //$NON-NLS-1$

	private static Map<String, Map<String, String>> credentials = new HashMap<String, Map<String, String>>();

	public static final String DEFAULT_CHARACTER_ENCODING = "UTF-8"; //$NON-NLS-1$

	private static final String ENABLED = ".enabled"; //$NON-NLS-1$

	private static final String ID_PLUGIN = "org.eclipse.mylyn.commons.repository";

	public static final String OFFLINE = "org.eclipse.mylyn.tasklist.repositories.offline"; //$NON-NLS-1$

	private static final String PASSWORD = ".password"; //$NON-NLS-1$

	public static final String PROPERTY_CATEGORY = "category"; //$NON-NLS-1$

	private static final String PROPERTY_CONFIG_TIMESTAMP = "org.eclipse.mylyn.tasklist.repositories.configuration.timestamp"; //$NON-NLS-1$

	public static final String PROPERTY_CONNECTOR_KIND = "kind"; //$NON-NLS-1$

	public static final String PROPERTY_ENCODING = "encoding"; //$NON-NLS-1$

	public static final String PROPERTY_LABEL = "label"; //$NON-NLS-1$

	public static final String PROPERTY_TIMEZONE = "timezone"; //$NON-NLS-1$

	public static final String PROPERTY_URL = "url"; //$NON-NLS-1$

	public static final String PROXY_HOSTNAME = "org.eclipse.mylyn.tasklist.repositories.proxy.hostname"; //$NON-NLS-1$

	public static final String PROXY_PORT = "org.eclipse.mylyn.tasklist.repositories.proxy.port"; //$NON-NLS-1$

	public static final String PROXY_USEDEFAULT = "org.eclipse.mylyn.tasklist.repositories.proxy.usedefault"; //$NON-NLS-1$

	private static final String SAVE_PASSWORD = ".savePassword"; //$NON-NLS-1$

	private static final String USERNAME = ".username"; //$NON-NLS-1$

	private final Map<String, String> transientProperties = new HashMap<String, String>();

	private static String getKeyPrefix(AuthenticationType type) {
		switch (type) {
		case HTTP:
			return AUTH_HTTP;
		case PROXY:
			return AUTH_PROXY;
		case REPOSITORY:
			return AUTH_REPOSITORY;
		}
		throw new IllegalArgumentException("Unknown authentication type: " + type); //$NON-NLS-1$
	}

	private String cachedUserName;

	// transient
	private IStatus errorStatus = null;

	private boolean isCachedUserName;

	private ILocationService locationService;

	private final Object LOCK = new Object();

	private final Map<String, String> properties = new LinkedHashMap<String, String>();

	private final Set<PropertyChangeListener> propertyChangeListeners = new HashSet<PropertyChangeListener>();

	private URI uri;

	private boolean workingCopy;

	public RepositoryLocation(URI uri) {
		this.uri = uri;
	}

	public RepositoryLocation(Map<String, String> properties) {
		this.properties.putAll(properties);
		this.workingCopy = true;
	}

	public boolean isWorkingCopy() {
		return workingCopy;
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	private void addAuthInfo(String username, String password, String userProperty, String passwordProperty) {
		if (Platform.isRunning()) {
			try {
				ISecurePreferences securePreferences = getSecurePreferences();
				if (userProperty.equals(getKeyPrefix(AuthenticationType.REPOSITORY) + USERNAME)) {
					this.setProperty(userProperty, username);
				} else {
					securePreferences.put(userProperty, username, false);
				}
				securePreferences.put(passwordProperty, password, true);
			} catch (StorageException e) {
				StatusHandler.log(new Status(IStatus.ERROR, ID_PLUGIN, "Could not store authorization credentials", e)); //$NON-NLS-1$
			}
		} else {
			synchronized (LOCK) {
				Map<String, String> headlessCreds = credentials.get(getRepositoryUrl());
				if (headlessCreds == null) {
					headlessCreds = new HashMap<String, String>();
					credentials.put(getRepositoryUrl(), headlessCreds);
				}
				headlessCreds.put(userProperty, username);
				headlessCreds.put(passwordProperty, password);
			}
		}
	}

	private String getRepositoryUrl() {
		return getUri().toString();
	}

	public void addChangeListener(PropertyChangeListener listener) {
		propertyChangeListeners.add(listener);
	}

	public void flushAuthenticationCredentials() {
		synchronized (this) {
			transientProperties.clear();
			isCachedUserName = false;
		}

		synchronized (LOCK) {
			if (Platform.isRunning()) {
				ISecurePreferences securePreferences = getSecurePreferences();
				securePreferences.removeNode();
				this.setProperty(AuthenticationType.REPOSITORY + USERNAME, ""); //$NON-NLS-1$
			} else {
				Map<String, String> headlessCreds = credentials.get(getRepositoryUrl());
				if (headlessCreds != null) {
					headlessCreds.clear();
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private String getAuthInfo(String property) {
		if (Platform.isRunning()) {
			String propertyValue = null;
			if (property.equals(getKeyPrefix(AuthenticationType.REPOSITORY) + USERNAME)) {
				propertyValue = this.getProperty(property);
			} else {
				try {
					ISecurePreferences securePreferences = getSecurePreferences();
					propertyValue = securePreferences.get(property, null);
				} catch (StorageException e) {
					StatusHandler.log(new Status(IStatus.ERROR, ID_PLUGIN,
							"Could not retrieve authorization credentials", e)); //$NON-NLS-1$
				}
			}
			return propertyValue;
		} else {
			synchronized (LOCK) {
				Map<String, String> headlessCreds = credentials.get(getRepositoryUrl());
				if (headlessCreds == null) {
					headlessCreds = new HashMap<String, String>();
					credentials.put(getRepositoryUrl(), headlessCreds);
				}
				return headlessCreds.get(property);
			}
		}
	}

	/**
	 * Returns the credentials for an authentication type.
	 * 
	 * @param authType
	 *            the type of authentication
	 * @return null, if no credentials are set for <code>authType</code>
	 * @since 3.0
	 */
	public synchronized UsernamePasswordCredentials getCredentials(AuthenticationType authType) {
		String key = getKeyPrefix(authType);

		String enabled = getProperty(key + ENABLED);
		if (enabled == null || "true".equals(enabled)) { //$NON-NLS-1$
			String userName = getAuthInfo(key + USERNAME);
			String password;

			String savePassword = getProperty(key + SAVE_PASSWORD);
			if (savePassword != null && "true".equals(savePassword)) { //$NON-NLS-1$
				password = getAuthInfo(key + PASSWORD);
			} else {
				password = transientProperties.get(key + PASSWORD);
			}

			if (userName == null) {
				userName = ""; //$NON-NLS-1$
			}
			if (password == null) {
				password = ""; //$NON-NLS-1$
			}

			if (enabled == null && userName.length() == 0) {
				// API30: legacy support for versions prior to 2.2 that did not set the enable flag, remove for 3.0
				return null;
			}

			return new UsernamePasswordCredentials(userName, password);
		} else {
			return null;
		}
	}

	public Map<String, String> getProperties() {
		return new LinkedHashMap<String, String>(this.properties);
	}

	public String getProperty(String name) {
		return this.properties.get(name);
	}

	/**
	 * @return the URL if the label property is not set
	 */
	public String getRepositoryLabel() {
		String label = properties.get(PROPERTY_LABEL);
		if (label != null && label.length() > 0) {
			return label;
		} else {
			return getUri().toString();
		}
	}

	/**
	 * @since 3.0
	 */
	public boolean getSavePassword(AuthenticationType authType) {
		String value = getProperty(getKeyPrefix(authType) + SAVE_PASSWORD);
		return value != null && "true".equals(value); //$NON-NLS-1$
	}

	private ISecurePreferences getSecurePreferences() {
		ISecurePreferences securePreferences = SecurePreferencesFactory.getDefault().node(ID_PLUGIN);
		securePreferences = securePreferences.node(EncodingUtils.encodeSlashes(getUri().toString()));
		return securePreferences;
	}

	/**
	 * @since 3.0
	 */
	public IStatus getStatus() {
		return errorStatus;
	}

	/**
	 * The username is cached since it needs to be retrieved frequently (e.g. for Task List decoration).
	 */
	public String getUserName() {
		// NOTE: if anonymous, user name is "" string so we won't go to keyring
		if (!isCachedUserName) {
			// do not open secure store for username to avoid prompting user for password during initialization 
			cachedUserName = getProperty(getKeyPrefix(AuthenticationType.REPOSITORY) + USERNAME);
			isCachedUserName = true;
		}
		return cachedUserName;
	}

	public boolean hasProperty(String name) {
		String value = getProperty(name);
		return value != null && value.trim().length() > 0;
	}

	public boolean isOffline() {
		return Boolean.parseBoolean(getProperty(OFFLINE));
	}

	private void notifyChangeListeners(String key, String old, String value) {
		PropertyChangeEvent event = new PropertyChangeEvent(this, key, old, value);
		for (PropertyChangeListener listener : propertyChangeListeners) {
			listener.propertyChange(event);
		}
	}

	/**
	 * @since 3.0
	 */
	public void removeChangeListener(PropertyChangeListener listener) {
		propertyChangeListeners.remove(listener);
	}

	public void removeProperty(String key) {
		setProperty(key, null);
	}

	/**
	 * Sets the credentials for <code>authType</code>.
	 * 
	 * @param authType
	 *            the type of authentication
	 * @param credentials
	 *            the credentials, if null, the credentials for <code>authType</code> will be flushed
	 * @param savePassword
	 *            if true, the password will be persisted in the platform key ring; otherwise it will be stored in
	 *            memory only
	 */
	public synchronized void setCredentials(AuthenticationType authType, UsernamePasswordCredentials credentials,
			boolean savePassword) {
		String key = getKeyPrefix(authType);

		setProperty(key + SAVE_PASSWORD, String.valueOf(savePassword));

		if (credentials == null) {
			setProperty(key + ENABLED, String.valueOf(false));
			transientProperties.remove(key + PASSWORD);
			addAuthInfo("", "", key + USERNAME, key + PASSWORD); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			setProperty(key + ENABLED, String.valueOf(true));
			if (savePassword) {
				addAuthInfo(credentials.getUserName(), credentials.getPassword(), key + USERNAME, key + PASSWORD);
				transientProperties.remove(key + PASSWORD);
			} else {
				addAuthInfo(credentials.getUserName(), "", key + USERNAME, key + PASSWORD); //$NON-NLS-1$
				transientProperties.put(key + PASSWORD, credentials.getPassword());
			}
		}

		if (authType == AuthenticationType.REPOSITORY) {
			if (credentials == null) {
				this.cachedUserName = null;
				this.isCachedUserName = false;
			} else {
				this.cachedUserName = credentials.getUserName();
				this.isCachedUserName = true;
			}
		}
	}

	public void setLabel(String label) {
		setProperty(PROPERTY_LABEL, label);
	}

	public void setOffline(boolean offline) {
		properties.put(OFFLINE, String.valueOf(offline));
	}

	public void setProperty(String key, String newValue) {
		Assert.isNotNull(key);
		String oldValue = this.properties.get(key);
		if ((oldValue != null && !oldValue.equals(newValue)) || (oldValue == null && newValue != null)) {
			this.properties.put(key.intern(), (newValue != null) ? newValue.intern() : null);
			notifyChangeListeners(key, oldValue, newValue);
		}
	}

	public void setStatus(IStatus errorStatus) {
		this.errorStatus = errorStatus;
	}

	@Override
	public String toString() {
		return getRepositoryLabel();
	}

}
