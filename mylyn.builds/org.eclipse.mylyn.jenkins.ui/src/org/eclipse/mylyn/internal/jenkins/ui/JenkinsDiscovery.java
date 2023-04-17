/*******************************************************************************
 * Copyright (c) 2010, 2014 Itema AS and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Torkild U. Resheim - initial API and implementation
 *     Torkild U. Resheim - Uniquely identify Jenkins servers, bug 341725
 *     Torkild U. Resheim - Distinguish between Hudson and Jenkins, bug 353861
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.jenkins.ui;

import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.discovery.IDiscoveryLocator;
import org.eclipse.ecf.discovery.IServiceEvent;
import org.eclipse.ecf.discovery.IServiceInfo;
import org.eclipse.ecf.discovery.IServiceListener;
import org.eclipse.ecf.discovery.IServiceProperties;
import org.eclipse.ecf.discovery.identity.IServiceID;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;
import org.eclipse.mylyn.builds.ui.BuildsUi;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.osgi.util.NLS;

/**
 * This class implements a mechanism for discovering Jenkins servers through the use of Multicast DNS (MDNS).
 *
 * @author Torkild U. Resheim, Itema AS
 * @author Steffen Pingel
 */
public class JenkinsDiscovery {
	/**
	 * This class works around a source incompatibility between the org.eclipse.ecf.discovery version in Luna and
	 * earlier versions. Version 5.0 added the triggerDiscovery method to IServiceListener. This class can be extended
	 * in order to implement this method without the @Overide annotation causing compilation to fail against earlier
	 * versions (e.g. Kepler).
	 */
	private static abstract class AbstractServiceListener {
		public abstract boolean triggerDiscovery();
	}

	private final class JenkinsServiceListener extends AbstractServiceListener implements IServiceListener {
		public void serviceDiscovered(IServiceEvent anEvent) {
			IServiceInfo serviceInfo = anEvent.getServiceInfo();
			IServiceID serviceId = serviceInfo.getServiceID();
			IServiceTypeID serviceTypeId = serviceId.getServiceTypeID();
			// Note that Jenkins will claim that it's both Jenkins and
			// Hudson for backward compatibility.
			if (serviceTypeId.getName().equals(JENKINS_MDNS_ID)) {
				IServiceProperties properties = serviceInfo.getServiceProperties();
				try {
					if (properties.getProperty(URL_PROPERTY) == null) {
						notifyMessage(Messages.JenkinsDiscovery_MessageTitle,
								NLS.bind(Messages.JenkinsDiscovery_MissingURL,
										new Object[] { serviceInfo.getLocation().getHost() }));
					} else {
						issueJenkinsNotification(properties);
					}
				} catch (URISyntaxException e) {
					StatusHandler.log(new Status(IStatus.ERROR, JenkinsConnectorUi.ID_PLUGIN,
							NLS.bind(Messages.Discovery_IncorrectURI,
									new Object[] { properties.getProperty(URL_PROPERTY).toString() }),
							e));
				}
			}
		}

		public void serviceUndiscovered(IServiceEvent anEvent) {
			// Ignore this for now
		}

		@Override
		public boolean triggerDiscovery() {
			return false;
		}
	}

	private static final String ECF_DISCOVERY_JMDNS = "ecf.discovery.jmdns"; //$NON-NLS-1$

	private static final String HUDSON_MDNS_ID = "_hudson._tcp.local._iana"; //$NON-NLS-1$

	private static final String JENKINS_MDNS_ID = "_jenkins._tcp.local._iana"; //$NON-NLS-1$

	private static final String URL_PROPERTY = "url"; //$NON-NLS-1$

	/** Server id property name (Jenkins only). */
	private static final String SERVER_ID_PROPERTY = "server-id"; //$NON-NLS-1$

	private IContainer container;

	public JenkinsDiscovery() {
	}

	protected IContainer getContainer() throws ContainerCreateException {
		return ContainerFactory.getDefault().createContainer(ECF_DISCOVERY_JMDNS);
	}

	/**
	 * Determines whether or not the detected server is a new server or not.
	 *
	 * @param url
	 *            the server URL
	 * @param id
	 *            the server identifier
	 * @return <code>true</code> if the detected server is new.
	 */
	private boolean isNew(String url, String id) {
		if (url == null) {
			return false;
		}
		List<RepositoryLocation> locations = BuildsUi.getServerLocations();
		for (RepositoryLocation location : locations) {
			if (location.hasUrl(url) || location.getId().equals(id)) {
				return false;
			}
		}
		return true;
	}

	public void start() {
		try {
			container = getContainer();
			final IDiscoveryLocator adapter = container.getAdapter(IDiscoveryLocator.class);
			adapter.addServiceListener(new JenkinsServiceListener());
			container.connect(null, null);

		} catch (ContainerCreateException e) {
			StatusHandler.log(new Status(IStatus.WARNING, JenkinsConnectorUi.ID_PLUGIN,
					Messages.Discovery_CouldNotStartService, e));
		} catch (ContainerConnectException e) {
			StatusHandler.log(new Status(IStatus.WARNING, JenkinsConnectorUi.ID_PLUGIN,
					Messages.Discovery_CouldNotStartService, e));
		}
	}

	public void stop() {
		if (container != null) {
			container.disconnect();
			container = null;
		}
	}

	private void notifyMessage(String title, String description) {
		BuildsUi.serverDiscovered(title, description);
	}

	private void issueJenkinsNotification(IServiceProperties properties) throws URISyntaxException {
		String url = properties.getProperty(URL_PROPERTY).toString();
		String id = getId(properties);
		if (isNew(url, id)) {
			// Change the first segment (org.eclipse.mylyn.hudson) to the id of
			// the new repository type when we start differentiation between the two
			notifyMessage(Messages.JenkinsDiscovery_MessageTitle, NLS.bind(Messages.JenkinsDiscovery_MessageText,
					new Object[] { url, Messages.JenkinsDiscovery_ServerName, url, id }));
		}
	}

	private String getId(IServiceProperties properties) {
		String id = (String) properties.getProperty(SERVER_ID_PROPERTY);
		if (id == null) {
			id = UUID.randomUUID().toString();
		}
		return id;
	}

}
