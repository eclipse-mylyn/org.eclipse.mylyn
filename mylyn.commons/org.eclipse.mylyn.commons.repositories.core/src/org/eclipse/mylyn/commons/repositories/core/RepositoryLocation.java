/*******************************************************************************
 * Copyright (c) 2010, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.repositories.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.core.net.NetUtil;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocationChangeEvent.Type;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationCredentials;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationRequest;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationType;
import org.eclipse.mylyn.commons.repositories.core.auth.ICredentialsStore;
import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;
import org.eclipse.mylyn.internal.commons.repositories.core.CredentialsFactory;
import org.eclipse.mylyn.internal.commons.repositories.core.InMemoryCredentialsStore;
import org.eclipse.mylyn.internal.commons.repositories.core.LocationService;
import org.eclipse.mylyn.internal.commons.repositories.core.RepositoriesCoreInternal;

/**
 * @author Steffen Pingel
 */
// FIXME add synchronization
public class RepositoryLocation extends PlatformObject {

	private static boolean flushCredentialsErrorLogged;

	public static final String PROPERTY_ID = "id"; //$NON-NLS-1$

	public static final String PROPERTY_LABEL = "label"; //$NON-NLS-1$

	public static final String PROPERTY_OFFLINE = "org.eclipse.mylyn.tasklist.repositories.offline"; //$NON-NLS-1$

	public static final String PROPERTY_PROXY_HOST = "org.eclipse.mylyn.repositories.proxy.host"; //$NON-NLS-1$

	public static final String PROPERTY_PROXY_PORT = "org.eclipse.mylyn.repositories.proxy.port"; //$NON-NLS-1$

	public static final String PROPERTY_PROXY_USEDEFAULT = "org.eclipse.mylyn.repositories.proxy.usedefault"; //$NON-NLS-1$

	public static final String PROPERTY_URL = "url"; //$NON-NLS-1$

	public static final String PROPERTY_USERNAME = "org.eclipse.mylyn.repositories.username"; //$NON-NLS-1$

	private static final String ENABLED = ".enabled"; //$NON-NLS-1$

	private static Map<String, String> createDefaultProperties() {
		Map<String, String> defaultProperties = new HashMap<String, String>();
		defaultProperties.put(PROPERTY_PROXY_USEDEFAULT, Boolean.TRUE.toString());
		return defaultProperties;
	}

	private ICredentialsStore credentialsStore;

	// transient
	private IStatus errorStatus = null;

	private final Map<String, String> properties = new LinkedHashMap<String, String>();

	private final List<PropertyChangeListener> propertyChangeListeners = new CopyOnWriteArrayList<PropertyChangeListener>();

	private final List<IRepositoryLocationChangeListener> repositoryLocationChangeListeners = new CopyOnWriteArrayList<IRepositoryLocationChangeListener>();

	private ILocationService service;

	private final boolean workingCopy;

	public RepositoryLocation() {
		this(createDefaultProperties(), LocationService.getDefault(), false);
	}

	public RepositoryLocation(String url) {
		this();
		setUrl(url);
	}

	public RepositoryLocation(Map<String, String> properties) {
		this(properties, LocationService.getDefault(), true);
	}

	public RepositoryLocation(Map<String, String> properties, ILocationService service, boolean workingCopy) {
		this.properties.putAll(properties);
		this.service = service;
		this.workingCopy = workingCopy;
		if (this.properties.get(PROPERTY_ID) == null) {
			this.properties.put(RepositoryLocation.PROPERTY_ID, UUID.randomUUID().toString());
		}
	}

	public RepositoryLocation(RepositoryLocation source) {
		this(source.getProperties(), source.getService(), true);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeListeners.add(listener);
	}

	public void apply(RepositoryLocation location) {
		String oldId = getProperty(PROPERTY_ID);
		ICredentialsStore oldCredentialsStore = null;
		if (oldId != null) {
			oldCredentialsStore = getCredentialsStore();
		}

		// merge properties
		HashSet<String> removed = new HashSet<String>(properties.keySet());
		removed.removeAll(location.properties.keySet());
		for (Map.Entry<String, String> entry : location.properties.entrySet()) {
			setProperty(entry.getKey(), entry.getValue());
		}
		for (String key : removed) {
			setProperty(key, null);
		}

		String newId = getProperty(PROPERTY_ID);
		if (newId != null) {
			// migrate credentials if url has changed
			ICredentialsStore newCredentialsStore = getCredentialsStore();
			if (!newId.equals(oldId)) {
				if (oldCredentialsStore != null) {
					oldCredentialsStore.copyTo(newCredentialsStore);
					oldCredentialsStore.clear();
				}
			}

			// merge credentials
			if (location.getCredentialsStore() instanceof InMemoryCredentialsStore) {
				((InMemoryCredentialsStore) location.getCredentialsStore()).copyTo(newCredentialsStore);
			}

			// persist changes
			try {
				newCredentialsStore.flush();
			} catch (IOException e) {
				if (!flushCredentialsErrorLogged) {
					flushCredentialsErrorLogged = true;
					StatusHandler.log(new Status(IStatus.ERROR, RepositoriesCoreInternal.ID_PLUGIN,
							"Unexpected error occured while flushing credentials. Credentials may not have been saved.", //$NON-NLS-1$
							e));
				}
			}
		}

		fireRepositoryLocationChangeEvent(Type.ALL);
	}

	public void clearCredentials() {
		getCredentialsStore().clear();
	}

	public boolean getBooleanPropery(String key) {
		String value = getProperty(key);
		return value != null && Boolean.parseBoolean(value);
	}

	public <T extends AuthenticationCredentials> T getCredentials(AuthenticationType<T> authType) {
		return getCredentials(authType, true);
	}

	public <T extends AuthenticationCredentials> T getCredentials(AuthenticationType<T> authType, boolean loadSecrets) {
		String prefix = authType.getKey();
		if (getBooleanPropery(prefix + ENABLED)) {
			if (getId() == null) {
				// can't determine location of credentials
				return null;
			}
			return CredentialsFactory.create(authType.getCredentialsType(), getCredentialsStore(), prefix, loadSecrets);
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

	/**
	 * @return the URL if the label property is not set
	 */
	public String getLabel() {
		String label = properties.get(PROPERTY_LABEL);
		if (label != null && label.length() > 0) {
			return label;
		} else {
			return getUrl();
		}
	}

	public Map<String, String> getProperties() {
		return new LinkedHashMap<String, String>(this.properties);
	}

	public String getProperty(String name) {
		return this.properties.get(name);
	}

	public Proxy getProxy() {
		if (Boolean.parseBoolean(getProperty(PROPERTY_PROXY_USEDEFAULT))) {
			return null;
		}

		String proxyHost = getProperty(PROPERTY_PROXY_HOST);
		String proxyPort = getProperty(PROPERTY_PROXY_PORT);
		if (proxyHost != null && proxyHost.length() > 0 && proxyPort != null) {
			try {
				int proxyPortNum = Integer.parseInt(proxyPort);
				UserCredentials credentials = getCredentials(AuthenticationType.PROXY);
				if (credentials != null) {
					return NetUtil.createProxy(proxyHost, proxyPortNum, credentials.getUserName(),
							credentials.getPassword(), credentials.getDomain());
				} else {
					return NetUtil.createProxy(proxyHost, proxyPortNum);
				}
			} catch (NumberFormatException e) {
				StatusHandler.log(new Status(IStatus.ERROR, RepositoriesCoreInternal.ID_PLUGIN, 0,
						"Error occured while configuring proxy. Invalid port \"" //$NON-NLS-1$
								+ proxyPort + "\" specified.", //$NON-NLS-1$
						e));
			}
		}
		return null;
	}

	// FIXME e3.5 replace with 3.5 proxy API
	public Proxy getProxyForHost(String host, String proxyType) {
		Proxy proxy = getProxy();
		if (proxy != null) {
			return proxy;
		}
		return getService().getProxyForHost(host, proxyType);
	}

	public ILocationService getService() {
		return service;
	}

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
		return getProperty(PROPERTY_USERNAME);
	}

	public boolean hasProperty(String name) {
		String value = getProperty(name);
		return value != null && value.trim().length() > 0;
	}

	/**
	 * Returns true if a normalized form of <code>url</code> matches the URL of this location.
	 */
	public boolean hasUrl(String url) {
		Assert.isNotNull(url);
		String myUrl = getUrl();
		if (myUrl == null) {
			return false;
		}
		try {
			return new URI(url + "/").normalize().equals(new URI(myUrl + "/").normalize()); //$NON-NLS-1$//$NON-NLS-2$
		} catch (URISyntaxException e) {
			return false;
		}
	}

	public boolean isOffline() {
		return Boolean.parseBoolean(getProperty(PROPERTY_OFFLINE));
	}

	public boolean isWorkingCopy() {
		return workingCopy;
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeListeners.remove(listener);
	}

	public <T extends AuthenticationCredentials> void removeCredentials(AuthenticationType<T> authType, T credentials) {
		String prefix = authType.getKey();
		credentials.clear(getCredentialsStore(), prefix);
	}

	public void removeProperty(String key) {
		setProperty(key, null);
	}

	/**
	 * Requests credentials. This may block and prompt to enter credentials.
	 * 
	 * @param request
	 *            the authentication request
	 * @param monitor
	 *            the progress monitor
	 * @return the entered credentials
	 * @see ILocationService#requestCredentials(AuthenticationRequest, IProgressMonitor)
	 */
	public <T extends AuthenticationCredentials> T requestCredentials(
			AuthenticationRequest<AuthenticationType<T>> request, IProgressMonitor monitor) {
		return getService().requestCredentials(request, monitor);
	}

	public <T extends AuthenticationCredentials> void setCredentials(AuthenticationType<T> authType, T credentials) {
		String prefix = authType.getKey();
		if (credentials == null) {
			setProperty(prefix + ENABLED, String.valueOf(false));
		} else {
			setProperty(prefix + ENABLED, String.valueOf(true));
			credentials.save(getCredentialsStore(), prefix);
		}

		fireRepositoryLocationChangeEvent(Type.CREDENTIALS);
	}

	public void setCredentialsStore(ICredentialsStore credentialsStore) {
		this.credentialsStore = credentialsStore;
	}

	public void setIdPreservingCredentialsStore(String id) {
		Assert.isNotNull(id);
		ICredentialsStore store = getCredentialsStore();
		setProperty(RepositoryLocation.PROPERTY_ID, id);
		if (this.credentialsStore == null) {
			setCredentialsStore(store);
		}
	}

	public void setLabel(String label) {
		setProperty(PROPERTY_LABEL, label);
	}

	public void setOffline(boolean offline) {
		properties.put(PROPERTY_OFFLINE, String.valueOf(offline));
	}

	public void setProperty(String key, String newValue) {
		validatePropertyChange(key, newValue);
		String oldValue = this.properties.get(key);
		if (hasChanged(oldValue, newValue)) {
			this.properties.put(key.intern(), (newValue != null) ? newValue.intern() : null);
			handlePropertyChange(key, oldValue, newValue);
		}
	}

	public void validatePropertyChange(String key, String newValue) {
		Assert.isNotNull(key);
		if (key.equals(RepositoryLocation.PROPERTY_ID) && newValue == null) {
			throw new IllegalArgumentException("The ID property must not be null"); //$NON-NLS-1$
		}
	}

	public void setProxy(Proxy proxy) {
		if (proxy == null) {
			setProperty(PROPERTY_PROXY_USEDEFAULT, Boolean.toString(true));
		} else {
			SocketAddress address = proxy.address();
			if (address instanceof InetSocketAddress) {
				setProperty(PROPERTY_PROXY_HOST, ((InetSocketAddress) address).getHostName());
				setProperty(PROPERTY_PROXY_PORT, Integer.toString(((InetSocketAddress) address).getPort()));
				setProperty(PROPERTY_PROXY_USEDEFAULT, Boolean.toString(false));
			} else {
				throw new IllegalArgumentException("Invalid proxy address"); //$NON-NLS-1$
			}
		}

		fireRepositoryLocationChangeEvent(Type.PROYX);
	}

	public void setService(ILocationService service) {
		this.service = service;
	}

	public void setStatus(IStatus errorStatus) {
		this.errorStatus = errorStatus;
	}

	public void setUrl(String url) {
		setProperty(PROPERTY_URL, url);
	}

	public void setUserName(String userName) {
		setProperty(PROPERTY_USERNAME, userName);
	}

	@Override
	public String toString() {
		return getLabel();
	}

	private void handlePropertyChange(String key, Object old, Object value) {
		if (PROPERTY_ID.equals(key)) {
			credentialsStore = null;
		}

		firePropertyChangeEvent(key, old, value);
	}

	private void firePropertyChangeEvent(String key, Object old, Object value) {
		PropertyChangeEvent event = new PropertyChangeEvent(this, key, old, value);
		for (PropertyChangeListener listener : propertyChangeListeners) {
			listener.propertyChange(event);
		}
	}

	private void fireRepositoryLocationChangeEvent(RepositoryLocationChangeEvent.Type type) {
		RepositoryLocationChangeEvent event = new RepositoryLocationChangeEvent(this, type);
		for (IRepositoryLocationChangeListener listener : repositoryLocationChangeListeners) {
			listener.repositoryChanged(event);
		}
	}

	private boolean hasChanged(Object oldValue, Object newValue) {
		return oldValue != null && !oldValue.equals(newValue) || oldValue == null && newValue != null;
	}

}
