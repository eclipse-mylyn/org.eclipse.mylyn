/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.discovery.core.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.internal.discovery.core.DiscoveryCore;
import org.eclipse.mylyn.internal.discovery.core.util.WebUtil;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;

/**
 * A means of discovering connectors.
 * 
 * @author David Green
 */
public class ConnectorDiscovery {

	private List<DiscoveryConnector> connectors = Collections.emptyList();

	private List<DiscoveryCategory> categories = Collections.emptyList();

	private final List<AbstractDiscoveryStrategy> discoveryStrategies = new ArrayList<AbstractDiscoveryStrategy>();

	private Dictionary<Object, Object> environment = System.getProperties();

	private boolean verifyUpdateSiteAvailability = false;

	public ConnectorDiscovery() {
	}

	/**
	 * get the discovery strategies to use.
	 */
	public List<AbstractDiscoveryStrategy> getDiscoveryStrategies() {
		return discoveryStrategies;
	}

	/**
	 * Initialize this by performing discovery. Discovery may take a long time as it involves network access.
	 * PRECONDITION: must add at least one {@link #getDiscoveryStrategies() discovery strategy} prior to calling.
	 */
	public void performDiscovery(IProgressMonitor monitor) throws CoreException {
		if (discoveryStrategies.isEmpty()) {
			throw new IllegalStateException();
		}
		connectors = new ArrayList<DiscoveryConnector>();
		categories = new ArrayList<DiscoveryCategory>();

		final int totalTicks = 100000;
		final int discoveryTicks = totalTicks - (totalTicks / 10);
		final int filterTicks = totalTicks - discoveryTicks;
		monitor.beginTask(Messages.ConnectorDiscovery_task_discovering_connectors, totalTicks);
		try {
			for (AbstractDiscoveryStrategy discoveryStrategy : discoveryStrategies) {
				discoveryStrategy.setCategories(categories);
				discoveryStrategy.setConnectors(connectors);
				discoveryStrategy.performDiscovery(new SubProgressMonitor(monitor, discoveryTicks
						/ discoveryStrategies.size()));
			}

			filterDescriptors();
			if (verifyUpdateSiteAvailability) {
				filterUnavailableDescriptors(new SubProgressMonitor(monitor, filterTicks));
			}
			connectCategoriesToDescriptors();
		} finally {
			monitor.done();
		}
	}

	/**
	 * get the top-level categories
	 * 
	 * @return the categories, or an empty list if there are none.
	 */
	public List<DiscoveryCategory> getCategories() {
		return categories;
	}

	/**
	 * get the connectors
	 * 
	 * @return the connectors, or an empty list if there are none.
	 */
	public List<DiscoveryConnector> getConnectors() {
		return connectors;
	}

	/**
	 * The environment used to resolve {@link ConnectorDescriptor#getPlatformFilter() platform filters}. Defaults to the
	 * current environment.
	 */
	public Dictionary<Object, Object> getEnvironment() {
		return environment;
	}

	/**
	 * The environment used to resolve {@link ConnectorDescriptor#getPlatformFilter() platform filters}. Defaults to the
	 * current environment.
	 */
	public void setEnvironment(Dictionary<Object, Object> environment) {
		if (environment == null) {
			throw new IllegalArgumentException();
		}
		this.environment = environment;
	}

	/**
	 * indicate if update site availability should be verified. The default is false.
	 */
	public boolean isVerifyUpdateSiteAvailability() {
		return verifyUpdateSiteAvailability;
	}

	/**
	 * indicate if update site availability should be verified. The default is false.
	 */
	public void setVerifyUpdateSiteAvailability(boolean verifyUpdateSiteAvailability) {
		this.verifyUpdateSiteAvailability = verifyUpdateSiteAvailability;
	}

	private void connectCategoriesToDescriptors() {
		Map<String, DiscoveryCategory> idToCategory = new HashMap<String, DiscoveryCategory>();
		for (DiscoveryCategory category : categories) {
			DiscoveryCategory previous = idToCategory.put(category.getId(), category);
			if (previous != null) {
				StatusHandler.log(new Status(IStatus.ERROR, DiscoveryCore.ID_PLUGIN, NLS.bind(
						Messages.ConnectorDiscovery_duplicate_category_id, new Object[] { category.getId(),
								category.getSource().getId(), previous.getSource().getId() })));
			}
		}

		for (DiscoveryConnector connector : connectors) {
			DiscoveryCategory category = idToCategory.get(connector.getCategoryId());
			if (category != null) {
				category.getConnectors().add(connector);
				connector.setCategory(category);
			} else {
				StatusHandler.log(new Status(IStatus.ERROR, DiscoveryCore.ID_PLUGIN, NLS.bind(
						Messages.ConnectorDiscovery_bundle_references_unknown_category, new Object[] {
								connector.getCategoryId(), connector.getId(), connector.getSource().getId() })));
			}
		}
	}

	/**
	 * eliminate any connectors whose {@link ConnectorDescriptor#getPlatformFilter() platform filters} don't match
	 */
	private void filterDescriptors() {
		for (DiscoveryConnector connector : new ArrayList<DiscoveryConnector>(connectors)) {
			if (connector.getPlatformFilter() != null && connector.getPlatformFilter().trim().length() > 0) {
				boolean match = false;
				try {
					Filter filter = FrameworkUtil.createFilter(connector.getPlatformFilter());
					match = filter.match(environment);
				} catch (InvalidSyntaxException e) {
					StatusHandler.log(new Status(IStatus.ERROR, DiscoveryCore.ID_PLUGIN, NLS.bind(
							Messages.ConnectorDiscovery_illegal_filter_syntax, new Object[] {
									connector.getPlatformFilter(), connector.getId(), connector.getSource().getId() })));
				}
				if (!match) {
					connectors.remove(connector);
				}
			}
		}
	}

	/**
	 * filter connectors whose update site is not available
	 */
	private void filterUnavailableDescriptors(IProgressMonitor monitor) {
		Set<URL> urls = new HashSet<URL>();
		Set<URL> availableUrls = new HashSet<URL>();
		Map<ConnectorDescriptor, URL> descriptorToUrl = new HashMap<ConnectorDescriptor, URL>();

		for (ConnectorDescriptor descriptor : connectors) {
			try {
				String urlText = descriptor.getSiteUrl();
				if (!urlText.endsWith("/")) { //$NON-NLS-1$
					urlText += "/"; //$NON-NLS-1$
				}
				URL url = new URL(urlText);
				descriptorToUrl.put(descriptor, url);
				urls.add(url);
			} catch (MalformedURLException e) {
				// ignore
			}
		}
		final int totalTicks = urls.size();
		monitor.beginTask(Messages.ConnectorDiscovery_task_verifyingAvailability, totalTicks);
		try {
			if (!urls.isEmpty()) {
				ExecutorService executorService = Executors.newFixedThreadPool(Math.min(urls.size(), 4));
				try {
					List<Future<VerifyUpdateSiteJob>> futures = new ArrayList<Future<VerifyUpdateSiteJob>>(urls.size());
					for (URL url : urls) {
						futures.add(executorService.submit(new VerifyUpdateSiteJob(url)));
					}
					for (Future<VerifyUpdateSiteJob> jobFuture : futures) {
						try {
							VerifyUpdateSiteJob job = jobFuture.get();
							if (job.ok) {
								availableUrls.add(job.url);
							}
						} catch (InterruptedException e) {
							monitor.setCanceled(true);
							return;
						} catch (ExecutionException e) {
							IStatus status;
							if (e.getCause() instanceof CoreException) {
								status = ((CoreException) e.getCause()).getStatus();
							} else {
								status = new Status(IStatus.ERROR, DiscoveryCore.ID_PLUGIN,
										Messages.ConnectorDiscovery_unexpected_exception, e.getCause());
							}
							StatusHandler.log(status);
						}
						monitor.worked(1);
					}
				} finally {
					executorService.shutdownNow();
				}
			}
			for (ConnectorDescriptor descriptor : new ArrayList<ConnectorDescriptor>(connectors)) {
				URL url = descriptorToUrl.get(descriptor);
				if (!availableUrls.contains(url)) {
					connectors.remove(descriptor);
				}
			}
		} finally {
			monitor.done();
		}
	}

	private static class VerifyUpdateSiteJob implements Callable<VerifyUpdateSiteJob> {

		private final URL url;

		private boolean ok = false;

		public VerifyUpdateSiteJob(URL url) {
			this.url = url;
		}

		public VerifyUpdateSiteJob call() throws Exception {
			URL contentJarUrl = new URL(url, "content.jar"); //$NON-NLS-1$
			ok = WebUtil.verifyAvailability(new WebLocation(contentJarUrl.toExternalForm()), new NullProgressMonitor());
			return this;
		}

	}

	public void dispose() {
		for (final AbstractDiscoveryStrategy strategy : discoveryStrategies) {
			SafeRunner.run(new ISafeRunnable() {

				public void run() throws Exception {
					strategy.dispose();
				}

				public void handleException(Throwable exception) {
					StatusHandler.log(new Status(IStatus.ERROR, DiscoveryCore.ID_PLUGIN,
							Messages.ConnectorDiscovery_exception_disposing + strategy.getClass().getName(), exception));
				}
			});
		}
	}
}
