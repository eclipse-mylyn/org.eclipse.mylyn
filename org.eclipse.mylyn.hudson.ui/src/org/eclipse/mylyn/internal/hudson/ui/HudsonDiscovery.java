/*******************************************************************************
 * Copyright (c) 2010 Itema AS and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Itema AS - initial API and implementation
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
 * This class implements a mechanism for discovering Hudson servers through the use of Multicast DNS (MDNS).
 * 
 * @author Torkild U. Resheim
 * @since 3.5
 */
public class HudsonDiscovery extends BuildsUiStartup {

	private static final String ECF_DISCOVERY_JMDNS = "ecf.discovery.jmdns"; //$NON-NLS-1$

	private static final String ECF_SINGLETON_DISCOVERY = "ecf.singleton.discovery"; //$NON-NLS-1$

	private static final String HUDSON_MDNS_ID = "_hudson._tcp.local._iana"; //$NON-NLS-1$

	private static final String HUDSON_URL_PROPERTY_ID = "url"; //$NON-NLS-1$

	protected IContainer getContainer() throws ContainerCreateException {
		return ContainerFactory.getDefault().createContainer(ECF_SINGLETON_DISCOVERY,
				new Object[] { ECF_DISCOVERY_JMDNS });
	}

	private boolean isNew(URI uri) {
		List<IBuildServer> servers = BuildsUi.getModel().getServers();
		for (IBuildServer server : servers) {
			try {
				if (server.getUrl().equalsIgnoreCase(uri.toURL().toExternalForm())) {
					return false;
				}
			} catch (MalformedURLException e) {
				StatusHandler.log(new Status(IStatus.ERROR, HudsonConnectorUi.ID_PLUGIN,
						Messages.HudsonDiscovery_CannotConvertURI, e));
			}
		}
		return true;
	}

	@Override
	public void lazyStartup() {
		try {
			final IContainer container = getContainer();
			final IDiscoveryLocator adapter = (IDiscoveryLocator) getContainer().getAdapter(IDiscoveryLocator.class);
			adapter.addServiceListener(new IServiceListener() {
				public void serviceDiscovered(IServiceEvent anEvent) {
					IServiceInfo serviceInfo = anEvent.getServiceInfo();
					IServiceID serviceId = serviceInfo.getServiceID();
					IServiceTypeID serviceTypeId = serviceId.getServiceTypeID();
					if (serviceTypeId.getName().equals(HUDSON_MDNS_ID)) {
						IServiceProperties properties = serviceInfo.getServiceProperties();
						try {
							if (properties.getProperty(HUDSON_URL_PROPERTY_ID) == null) {
								notifyMessage(Messages.HudsonDiscovery_MessageTitle, NLS.bind(
										Messages.HudsonDiscovery_MissingURL, new Object[] { serviceInfo.getLocation()
												.getHost() }));

							} else {
								URI uri = new URI(properties.getProperty(HUDSON_URL_PROPERTY_ID).toString());
								if (isNew(uri)) {
									notifyMessage(
											Messages.HudsonDiscovery_MessageTitle,
											NLS.bind(Messages.HudsonDiscovery_MessageText, new Object[] { uri,
													Messages.HudsonDiscovery_ServerName, uri.toString(),
													UUID.randomUUID().toString() }));
								}
							}
						} catch (URISyntaxException e) {
							StatusHandler.log(new Status(IStatus.ERROR, HudsonConnectorUi.ID_PLUGIN, NLS.bind(
									Messages.HudsonDiscovery_IncorrectURI,
									new Object[] { properties.getProperty(HUDSON_URL_PROPERTY_ID).toString() }), e));
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
					Messages.HudsonDiscovery_CouldNotStartService, e));
		} catch (ContainerConnectException e) {
			StatusHandler.log(new Status(IStatus.WARNING, HudsonConnectorUi.ID_PLUGIN,
					Messages.HudsonDiscovery_CouldNotStartService, e));
		}
	}

	private void notifyMessage(String title, String description) {
		BuildsUi.serverDiscovered(title, description);
	}

}
