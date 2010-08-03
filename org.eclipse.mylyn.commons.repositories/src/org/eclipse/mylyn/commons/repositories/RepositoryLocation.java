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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.mylyn.commons.repositories.auth.AuthenticationCredentials;
import org.eclipse.mylyn.commons.repositories.auth.AuthenticationType;
import org.eclipse.mylyn.commons.repositories.auth.ICredentialsStore;
import org.eclipse.mylyn.internal.commons.repositories.CredentialsFactory;
import org.eclipse.mylyn.internal.commons.repositories.LocationService;

/**
 * @author Steffen Pingel
 */
public class RepositoryLocation extends PlatformObject {

	private static final String AUTH_HTTP = "org.eclipse.mylyn.tasklist.repositories.httpauth"; //$NON-NLS-1$

	private static final String AUTH_PROXY = "org.eclipse.mylyn.tasklist.repositories.proxy"; //$NON-NLS-1$

	private static final String AUTH_REPOSITORY = "org.eclipse.mylyn.tasklist.repositories"; //$NON-NLS-1$

	private static final String ENABLED = ".enabled"; //$NON-NLS-1$

	private static final String ID_PLUGIN = "org.eclipse.mylyn.commons.repository";

	public static final String PROPERTY_CATEGORY = "category"; //$NON-NLS-1$

	public static final String PROPERTY_ENCODING = "encoding"; //$NON-NLS-1$

	public static final String PROPERTY_ID = "id"; //$NON-NLS-1$

	public static final String PROPERTY_LABEL = "label"; //$NON-NLS-1$

	public static final String PROPERTY_OFFLINE = "org.eclipse.mylyn.tasklist.repositories.offline"; //$NON-NLS-1$

	public static final String PROPERTY_TIMEZONE = "timezone"; //$NON-NLS-1$

	public static final String PROPERTY_URL = "url"; //$NON-NLS-1$

	public static final String PROPERTY_USERNAME = "org.eclipse.mylyn.repositories.username"; //$NON-NLS-1$

	public static final String PROXY_HOSTNAME = "org.eclipse.mylyn.tasklist.repositories.proxy.hostname"; //$NON-NLS-1$

	public static final String PROXY_PORT = "org.eclipse.mylyn.tasklist.repositories.proxy.port"; //$NON-NLS-1$

	public static final String PROXY_USEDEFAULT = "org.eclipse.mylyn.tasklist.repositories.proxy.usedefault"; //$NON-NLS-1$

	private static final String SAVE_PASSWORD = ".savePassword"; //$NON-NLS-1$

	private static final String USERNAME = ".username"; //$NON-NLS-1$

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

	private ICredentialsStore credentialsStore;

	// transient
	private IStatus errorStatus = null;

	private boolean isCachedUserName;

	private final Map<String, String> properties = new LinkedHashMap<String, String>();

	private final Set<PropertyChangeListener> propertyChangeListeners = new HashSet<PropertyChangeListener>();

	private ILocationService service;

	private boolean workingCopy;

	public RepositoryLocation() {
		this.service = LocationService.getDefault();
	}

	public RepositoryLocation(Map<String, String> properties) {
		this.properties.putAll(properties);
		this.workingCopy = true;
		this.service = LocationService.getDefault();
	}

	public RepositoryLocation(RepositoryLocation source) {
		this.properties.putAll(source.properties);
		this.workingCopy = true;
		this.service = source.getService();
	}

	public void addChangeListener(PropertyChangeListener listener) {
		propertyChangeListeners.add(listener);
	}

	public void clearCredentials() {
		getCredentialsStore().clear();
	}

	public <T extends AuthenticationCredentials> T getCredentials(AuthenticationType authType, Class<T> credentialsKind) {
		String prefix = getKeyPrefix(authType);

		String enabled = getProperty(prefix + ENABLED);
		if (enabled == null || "true".equals(enabled)) { //$NON-NLS-1$
			try {
				return CredentialsFactory.create(credentialsKind, getCredentialsStore(), prefix);
			} catch (StorageException e) {
				// FIXME
			}
		}
		return null;
	}

	public ICredentialsStore getCredentialsStore() {
		if (credentialsStore == null) {
			return getService().getCredentialsStore(getId());
		}
		return credentialsStore;
	}

	public String getId() {
		String id = getProperty(PROPERTY_ID);
		if (id == null) {
			throw new IllegalStateException("Repository ID is not set"); //$NON-NLS-1$
		}
		return id;
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
			return getUrl();
		}
	}

	/**
	 * @since 3.0
	 */
	public boolean getSavePassword(AuthenticationType authType) {
		String value = getProperty(getKeyPrefix(authType) + SAVE_PASSWORD);
		return value != null && "true".equals(value); //$NON-NLS-1$
	}

	public ILocationService getService() {
		return service;
	}

	/**
	 * @since 3.0
	 */
	public IStatus getStatus() {
		return errorStatus;
	}

	public String getUrl() {
		return getProperty(PROPERTY_URL);
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

	private void handlePropertyChange(String key, Object old, Object value) {
		if (PROPERTY_ID.equals(key)) {
			// FIXME migrate credentials
		}

		PropertyChangeEvent event = new PropertyChangeEvent(this, key, old, value);
		for (PropertyChangeListener listener : propertyChangeListeners) {
			listener.propertyChange(event);
		}
	}

	private boolean hasChanged(Object oldValue, Object newValue) {
		return oldValue != null && !oldValue.equals(newValue) || oldValue == null && newValue != null;
	}

	public boolean hasProperty(String name) {
		String value = getProperty(name);
		return value != null && value.trim().length() > 0;
	}

	public boolean isOffline() {
		return Boolean.parseBoolean(getProperty(PROPERTY_OFFLINE));
	}

	public boolean isWorkingCopy() {
		return workingCopy;
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

	public <T extends AuthenticationCredentials> void setCredentials(AuthenticationType authType, T credentials) {
		String prefix = getKeyPrefix(authType);

		if (credentials == null) {
			setProperty(prefix + ENABLED, String.valueOf(false));
		} else {
			setProperty(prefix + ENABLED, String.valueOf(true));
			try {
				credentials.save(getCredentialsStore(), prefix);
			} catch (StorageException e) {
				// FIXME
			}
		}
	}

	public void setCredentialsStore(ICredentialsStore credentialsStore) {
		this.credentialsStore = credentialsStore;
	}

	public void setLabel(String label) {
		setProperty(PROPERTY_LABEL, label);
	}

	public void setOffline(boolean offline) {
		properties.put(PROPERTY_OFFLINE, String.valueOf(offline));
	}

	public void setProperty(String key, String newValue) {
		Assert.isNotNull(key);
		String oldValue = this.properties.get(key);
		if (hasChanged(oldValue, newValue)) {
			this.properties.put(key.intern(), (newValue != null) ? newValue.intern() : null);
			handlePropertyChange(key, oldValue, newValue);
		}
	}

	public void setService(ILocationService service) {
		this.service = service;
	}

	public void setStatus(IStatus errorStatus) {
		this.errorStatus = errorStatus;
	}

	@Override
	public String toString() {
		return getRepositoryLabel();
	}

}
