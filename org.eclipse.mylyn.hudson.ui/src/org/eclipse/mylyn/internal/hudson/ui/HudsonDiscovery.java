/*******************************************************************************
 * Copyright (c) 2010, 2011 Itema AS and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Torkild U. Resheim - initial API and implementation
 *     Torkild U. Resheim - Uniquely identify Jenkins servers, bug 341725
 *     Torkild U. Resheim - Distinguish between Hudson and Jenkins, bug 353861
 *******************************************************************************/

package org.eclipse.mylyn.internal.hudson.ui;

import java.net.MalformedURLException;
import java.net.URI;
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
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.ui.BuildsUi;
import org.eclipse.mylyn.builds.ui.BuildsUiStartup;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.osgi.util.NLS;

/**
 * This class implements a mechanism for discovering Hudson and Jenkins servers through the use of Multicast DNS (MDNS).
 * 
 * @author Torkild U. Resheim, Itema AS
 * @since 3.5
 */
public class HudsonDiscovery extends BuildsUiStartup {

	/** Jenkins server id property name */
	private static final String JENKINS_SERVER_ID_PROPERTY = "server-id"; //$NON-NLS-1$

	private static final String ECF_DISCOVERY_JMDNS = "ecf.discovery.jmdns"; //$NON-NLS-1$

	private static final String ECF_SINGLETON_DISCOVERY = "ecf.singleton.discovery"; //$NON-NLS-1$

	private static final String HUDSON_MDNS_ID = "_hudson._tcp.local._iana"; //$NON-NLS-1$

	private static final String JENKINS_MDNS_ID = "_jenkins._tcp.local._iana"; //$NON-NLS-1$

	private static final String URL_PROPERTY = "url"; //$NON-NLS-1$

	private static HudsonDiscovery instance;

	private IContainer container;

	public static HudsonDiscovery getInstance() {
		return instance;
	}

	public HudsonDiscovery() {
		instance = this;
	}

	protected IContainer getContainer() throws ContainerCreateException {
		return ContainerFactory.getDefault().createContainer(ECF_DISCOVERY_JMDNS);
	}

	/**
	 * Determines whether or not the detected server is a new server or not.
	 * 
	 * @param uri
	 *            the server URI
	 * @param id
	 *            the server identifier
	 * @return <code>true</code> if the detected server is new.
	 */
	private boolean isNew(URI uri, String id) {
		List<IBuildServer> servers = BuildsUi.getModel().getServers();
		for (IBuildServer server : servers) {

			try {
				if (server.getUrl().equalsIgnoreCase(uri.toURL().toExternalForm())) {
					return false;
				}
				if (server.getLocation().getId().equals(id)) {
					return false;
				}
			} catch (MalformedURLException e) {
				StatusHandler.log(new Status(IStatus.ERROR, HudsonConnectorUi.ID_PLUGIN,
						Messages.Discovery_CannotConvertURI, e));
			}
		}
		return true;
	}

	@Override
	public void lazyStartup() {
		try {
			container = getContainer();
			final IDiscoveryLocator adapter = (IDiscoveryLocator) container.getAdapter(IDiscoveryLocator.class);
			adapter.addServiceListener(new IServiceListener() {
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
								notifyMessage(Messages.JenkinsDiscovery_MessageTitle, NLS.bind(
										Messages.JenkinsDiscovery_MissingURL, new Object[] { serviceInfo.getLocation()
												.getHost() }));
							} else {
								issueJenkinsNotification(properties);
							}
						} catch (URISyntaxException e) {
							StatusHandler.log(new Status(IStatus.ERROR, HudsonConnectorUi.ID_PLUGIN, NLS.bind(
									Messages.Discovery_IncorrectURI,
									new Object[] { properties.getProperty(URL_PROPERTY).toString() }), e));
						}
					}
					if (serviceTypeId.getName().equals(HUDSON_MDNS_ID)) {
						IServiceProperties properties = serviceInfo.getServiceProperties();
						try {
							if (properties.getProperty(URL_PROPERTY) == null) {
								notifyMessage(Messages.HudsonDiscovery_MessageTitle, NLS.bind(
										Messages.HudsonDiscovery_MissingURL, new Object[] { serviceInfo.getLocation()
												.getHost() }));
							} else {
								issueHudsonNotification(properties);
							}
						} catch (URISyntaxException e) {
							StatusHandler.log(new Status(IStatus.ERROR, HudsonConnectorUi.ID_PLUGIN, NLS.bind(
									Messages.Discovery_IncorrectURI,
									new Object[] { properties.getProperty(URL_PROPERTY).toString() }), e));
						}
					}
				}

				public void serviceUndiscovered(IServiceEvent anEvent) {
					// Ignore this for now
				}
			});
			container.connect(null, null);

		} catch (ContainerCreateException e) {
			StatusHandler.log(new Status(IStatus.WARNING, HudsonConnectorUi.ID_PLUGIN,
					Messages.Discovery_CouldNotStartService, e));
		} catch (ContainerConnectException e) {
			StatusHandler.log(new Status(IStatus.WARNING, HudsonConnectorUi.ID_PLUGIN,
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

	private void issueHudsonNotification(IServiceProperties properties) throws URISyntaxException {
		String id = UUID.randomUUID().toString();
		URI uri = new URI(properties.getProperty(URL_PROPERTY).toString());
		if (isNew(uri, id)) {
			notifyMessage(
					Messages.HudsonDiscovery_MessageTitle,
					NLS.bind(Messages.HudsonDiscovery_MessageText, new Object[] { uri,
							Messages.HudsonDiscovery_ServerName, uri.toString(), id }));
		}
	}

	private void issueJenkinsNotification(IServiceProperties properties) throws URISyntaxException {
		String id = (String) properties.getProperty(JENKINS_SERVER_ID_PROPERTY);
		if (id == null) {
			id = UUID.randomUUID().toString();
		}
		URI uri = new URI(properties.getProperty(URL_PROPERTY).toString());
		if (isNew(uri, id)) {
			// Change the first segment (org.eclipse.mylyn.hudson) to the id of 
			// the new repository type when we start differentiation between the two
			notifyMessage(
					Messages.JenkinsDiscovery_MessageTitle,
					NLS.bind(Messages.JenkinsDiscovery_MessageText, new Object[] { uri,
							Messages.JenkinsDiscovery_ServerName, uri.toString(), id }));
		}
	}

}
