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

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IBundleGroup;
import org.eclipse.core.runtime.IBundleGroupProvider;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.internal.discovery.core.DiscoveryCore;
import org.eclipse.mylyn.internal.discovery.core.util.WebUtil;
import org.eclipse.osgi.service.resolver.VersionRange;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.Bundle;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.Version;

/**
 * A means of discovering connectors.
 * 
 * @author David Green
 */
public class ConnectorDiscovery {

	private List<DiscoveryConnector> connectors = Collections.emptyList();

	private List<DiscoveryCategory> categories = Collections.emptyList();

	private List<DiscoveryCertification> certifications = Collections.emptyList();

	private List<DiscoveryConnector> filteredConnectors = Collections.emptyList();

	private final List<AbstractDiscoveryStrategy> discoveryStrategies = new ArrayList<AbstractDiscoveryStrategy>();

	private Dictionary<Object, Object> environment = System.getProperties();

	private boolean verifyUpdateSiteAvailability = false;

	private Map<String, Version> featureToVersion = null;

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
		filteredConnectors = new ArrayList<DiscoveryConnector>();
		categories = new ArrayList<DiscoveryCategory>();
		certifications = new ArrayList<DiscoveryCertification>();

		final int totalTicks = 100000;
		final int discoveryTicks = totalTicks - (totalTicks / 10);
		final int filterTicks = totalTicks - discoveryTicks;
		monitor.beginTask(Messages.ConnectorDiscovery_task_discovering_connectors, totalTicks);
		try {
			for (AbstractDiscoveryStrategy discoveryStrategy : discoveryStrategies) {
				discoveryStrategy.setCategories(categories);
				discoveryStrategy.setConnectors(connectors);
				discoveryStrategy.setCertifications(certifications);
				discoveryStrategy.performDiscovery(new SubProgressMonitor(monitor, discoveryTicks
						/ discoveryStrategies.size()));
			}

			filterDescriptors();
			if (verifyUpdateSiteAvailability) {
				verifySiteAvailability(new SubProgressMonitor(monitor, filterTicks));
			}
			connectCategoriesToDescriptors();
			connectCertificationsToDescriptors();
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
	 * get the connectors that were discovered and not filtered
	 * 
	 * @return the connectors, or an empty list if there are none.
	 */
	public List<DiscoveryConnector> getConnectors() {
		return connectors;
	}

	/**
	 * get the connectors that were discovered but filtered
	 * 
	 * @return the filtered connectors, or an empty list if there were none.
	 */
	public List<DiscoveryConnector> getFilteredConnectors() {
		return filteredConnectors;
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
	 * 
	 * @see DiscoveryConnector#getAvailable()
	 * @see #verifySiteAvailability(IProgressMonitor)
	 */
	public boolean isVerifyUpdateSiteAvailability() {
		return verifyUpdateSiteAvailability;
	}

	/**
	 * indicate if update site availability should be verified. The default is false.
	 * 
	 * @see DiscoveryConnector#getAvailable()
	 * @see #verifySiteAvailability(IProgressMonitor)
	 */
	public void setVerifyUpdateSiteAvailability(boolean verifyUpdateSiteAvailability) {
		this.verifyUpdateSiteAvailability = verifyUpdateSiteAvailability;
	}

	/**
	 * <em>not for general use: public for testing purposes only</em> A map of installed features to their version. Used
	 * to resolve {@link ConnectorDescriptor#getFeatureFilter() feature filters}.
	 */
	public Map<String, Version> getFeatureToVersion() {
		return featureToVersion;
	}

	/**
	 * <em>not for general use: public for testing purposes only</em> A map of installed features to their version. Used
	 * to resolve {@link ConnectorDescriptor#getFeatureFilter() feature filters}.
	 */
	public void setFeatureToVersion(Map<String, Version> featureToVersion) {
		this.featureToVersion = featureToVersion;
	}

	private void connectCertificationsToDescriptors() {
		Map<String, DiscoveryCertification> idToCertification = new HashMap<String, DiscoveryCertification>();
		for (DiscoveryCertification certification : certifications) {
			DiscoveryCertification previous = idToCertification.put(certification.getId(), certification);
			if (previous != null) {
				StatusHandler.log(new Status(IStatus.ERROR, DiscoveryCore.ID_PLUGIN, NLS.bind(
						"Duplicate certification id ''{0}'': declaring sources: {1}, {2}", //$NON-NLS-1$
						new Object[] { certification.getId(), certification.getSource().getId(),
								previous.getSource().getId() })));
			}
		}

		for (DiscoveryConnector connector : connectors) {
			if (connector.getCertificationId() != null) {
				DiscoveryCertification certification = idToCertification.get(connector.getCertificationId());
				if (certification != null) {
					connector.setCertification(certification);
				} else {
					StatusHandler.log(new Status(IStatus.ERROR, DiscoveryCore.ID_PLUGIN, NLS.bind(
							"Unknown category ''{0}'' referenced by connector ''{1}'' declared in {2}", new Object[] { //$NON-NLS-1$
							connector.getCertificationId(), connector.getId(), connector.getSource().getId() })));
				}
			}
		}
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
					filteredConnectors.add(connector);
				}
			}
			for (FeatureFilter featureFilter : connector.getFeatureFilter()) {
				if (featureToVersion == null) {
					featureToVersion = computeFeatureToVersion();
				}
				boolean match = false;
				Version version = featureToVersion.get(featureFilter.getFeatureId());
				if (version != null) {
					VersionRange versionRange = new VersionRange(featureFilter.getVersion());
					if (versionRange.isIncluded(version)) {
						match = true;
					}
				}
				if (!match) {
					connectors.remove(connector);
					filteredConnectors.add(connector);
					break;
				}
			}
		}
	}

	private Map<String, Version> computeFeatureToVersion() {
		Map<String, Version> featureToVersion = new HashMap<String, Version>();
		for (IBundleGroupProvider provider : Platform.getBundleGroupProviders()) {
			for (IBundleGroup bundleGroup : provider.getBundleGroups()) {
				for (Bundle bundle : bundleGroup.getBundles()) {
					featureToVersion.put(bundle.getSymbolicName(), bundle.getVersion());
				}
			}
		}
		return featureToVersion;
	}

	/**
	 * Determine update site availability. This may be performed automatically as part of discovery when
	 * {@link #isVerifyUpdateSiteAvailability()} is true, or it may be invoked later by calling this method.
	 */
	public void verifySiteAvailability(IProgressMonitor monitor) {
		// NOTE: we don't put java.net.URLs in the map since it involves DNS activity when
		//       computing the hash code.
		Map<String, Collection<DiscoveryConnector>> urlToDescriptors = new HashMap<String, Collection<DiscoveryConnector>>();

		for (DiscoveryConnector descriptor : connectors) {
			String url = descriptor.getSiteUrl();
			if (!url.endsWith("/")) { //$NON-NLS-1$
				url += "/"; //$NON-NLS-1$
			}
			Collection<DiscoveryConnector> collection = urlToDescriptors.get(url);
			if (collection == null) {
				collection = new ArrayList<DiscoveryConnector>();
				urlToDescriptors.put(url, collection);
			}
			collection.add(descriptor);
		}
		final int totalTicks = urlToDescriptors.size();
		monitor.beginTask(Messages.ConnectorDiscovery_task_verifyingAvailability, totalTicks);
		try {
			if (!urlToDescriptors.isEmpty()) {
				ExecutorService executorService = Executors.newFixedThreadPool(Math.min(urlToDescriptors.size(), 4));
				try {
					List<Future<VerifyUpdateSiteJob>> futures = new ArrayList<Future<VerifyUpdateSiteJob>>(
							urlToDescriptors.size());
					for (String url : urlToDescriptors.keySet()) {
						futures.add(executorService.submit(new VerifyUpdateSiteJob(url)));
					}
					for (Future<VerifyUpdateSiteJob> jobFuture : futures) {
						try {
							for (;;) {
								try {
									VerifyUpdateSiteJob job = jobFuture.get(1L, TimeUnit.SECONDS);

									Collection<DiscoveryConnector> descriptors = urlToDescriptors.get(job.url);
									for (DiscoveryConnector descriptor : descriptors) {
										descriptor.setAvailable(job.ok);
									}
									break;
								} catch (TimeoutException e) {
									if (monitor.isCanceled()) {
										return;
									}
								}
							}
						} catch (InterruptedException e) {
							monitor.setCanceled(true);
							return;
						} catch (ExecutionException e) {
							if (e.getCause() instanceof OperationCanceledException) {
								monitor.setCanceled(true);
								return;
							}
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
		} finally {
			monitor.done();
		}
	}

	private static class VerifyUpdateSiteJob implements Callable<VerifyUpdateSiteJob> {

		private final String url;

		private boolean ok = false;

		public VerifyUpdateSiteJob(String url) {
			this.url = url;
		}

		public VerifyUpdateSiteJob call() throws Exception {
			URL baseUrl = new URL(url);
			List<WebLocation> locations = new ArrayList<WebLocation>();
			for (String location : new String[] { "content.jar", "content.xml", "site.xml" }) { //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
				locations.add(new WebLocation(new URL(baseUrl, location).toExternalForm()));
			}
			ok = WebUtil.verifyAvailability(locations, true, new NullProgressMonitor());
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
