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
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.jenkins.ui;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.builds.ui.BuildsUi;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.osgi.util.NLS;

/**
 * This class implements a mechanism for discovering Jenkins servers through the use of Multicast DNS (mDNS) using
 * only JDK functionality. Jenkins announces itself as <code>_jenkins._tcp.local</code> service and publishes its
 * properties (<code>url</code>, <code>server-id</code>) in a TXT record.
 *
 * @author Torkild U. Resheim, Itema AS
 * @author Steffen Pingel
 */
public class JenkinsDiscovery {

	private static final String MDNS_ADDRESS = "224.0.0.251"; //$NON-NLS-1$

	private static final int MDNS_PORT = 5353;

	/** Interval after which the services are queried again in case the initial query got lost. */
	private static final int REQUERY_INTERVAL_MS = 60_000;

	private static final String URL_PROPERTY = "url"; //$NON-NLS-1$

	/** Server id property name (Jenkins only). */
	private static final String SERVER_ID_PROPERTY = "server-id"; //$NON-NLS-1$

	private final Set<String> reportedHostsWithoutUrl = new HashSet<>();

	private volatile boolean running;

	private volatile MulticastSocket socket;

	private Thread discoveryThread;

	public JenkinsDiscovery() {
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

	public synchronized void start() {
		if (running) {
			return;
		}
		running = true;
		discoveryThread = new Thread(this::runDiscovery, "Jenkins mDNS Discovery"); //$NON-NLS-1$
		discoveryThread.setDaemon(true);
		discoveryThread.start();
	}

	public synchronized void stop() {
		running = false;
		MulticastSocket current = socket;
		if (current != null) {
			// unblocks the receive call
			current.close();
			socket = null;
		}
		discoveryThread = null;
	}

	private void runDiscovery() {
		try (MulticastSocket mcSocket = new MulticastSocket(MDNS_PORT)) {
			socket = mcSocket;
			List<NetworkInterface> interfaces = joinGroup(mcSocket);
			if (!running) {
				return;
			}
			mcSocket.setSoTimeout(REQUERY_INTERVAL_MS);
			byte[] buffer = new byte[9000];
			sendQuery(mcSocket, interfaces);
			while (running) {
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				try {
					mcSocket.receive(packet);
					for (Map<String, String> service : JenkinsMdnsMessage.parseServices(packet.getData(),
							packet.getLength())) {
						handleService(packet.getAddress(), service);
					}
				} catch (SocketTimeoutException e) {
					sendQuery(mcSocket, interfaces);
				}
			}
		} catch (IOException e) {
			if (running) {
				StatusHandler.log(new Status(IStatus.WARNING, JenkinsConnectorUi.ID_PLUGIN,
						Messages.Discovery_CouldNotStartService, e));
			}
		} finally {
			socket = null;
		}
	}

	private List<NetworkInterface> joinGroup(MulticastSocket mcSocket) throws IOException {
		InetSocketAddress group = new InetSocketAddress(InetAddress.getByName(MDNS_ADDRESS), MDNS_PORT);
		List<NetworkInterface> joined = new ArrayList<>();
		var interfaces = NetworkInterface.getNetworkInterfaces();
		if (interfaces != null) {
			for (NetworkInterface ni : Collections.list(interfaces)) {
				try {
					if (ni.isUp() && ni.supportsMulticast() && !ni.isLoopback()) {
						mcSocket.joinGroup(group, ni);
						joined.add(ni);
					}
				} catch (IOException e) {
					// interface does not support this group, try the others
				}
			}
		}
		return joined;
	}

	/**
	 * Asks all Jenkins servers to announce themselves, as they would otherwise only do so when they start.
	 */
	private void sendQuery(MulticastSocket mcSocket, List<NetworkInterface> interfaces) {
		byte[] query = JenkinsMdnsMessage.createQuery();
		try {
			InetAddress group = InetAddress.getByName(MDNS_ADDRESS);
			for (NetworkInterface ni : interfaces) {
				mcSocket.setNetworkInterface(ni);
				mcSocket.send(new DatagramPacket(query, query.length, group, MDNS_PORT));
			}
		} catch (IOException e) {
			// not fatal, announcements are still received and the query is repeated
		}
	}

	private void handleService(InetAddress source, Map<String, String> properties) {
		String url = properties.get(URL_PROPERTY);
		if (url == null) {
			String host = source.getHostAddress();
			boolean firstTime;
			synchronized (reportedHostsWithoutUrl) {
				firstTime = reportedHostsWithoutUrl.add(host);
			}
			if (firstTime) {
				notifyMessage(Messages.JenkinsDiscovery_MessageTitle,
						NLS.bind(Messages.JenkinsDiscovery_MissingURL, host));
			}
			return;
		}
		String id = properties.get(SERVER_ID_PROPERTY);
		if (id == null) {
			id = UUID.randomUUID().toString();
		}
		if (isNew(url, id)) {
			// Change the first segment (org.eclipse.mylyn.hudson) to the id of
			// the new repository type when we start differentiation between the two
			notifyMessage(Messages.JenkinsDiscovery_MessageTitle, NLS.bind(Messages.JenkinsDiscovery_MessageText,
					url, Messages.JenkinsDiscovery_ServerName, url, id));
		}
	}

	private void notifyMessage(String title, String description) {
		BuildsUi.serverDiscovered(title, description);
	}

}
