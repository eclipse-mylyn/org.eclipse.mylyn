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
package org.eclipse.mylyn.internal.discovery.ui.wizards;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.equinox.internal.p2.metadata.InstallableUnit;
import org.eclipse.equinox.internal.provisional.p2.metadata.repository.IMetadataRepository;
import org.eclipse.equinox.internal.provisional.p2.query.Collector;
import org.eclipse.equinox.internal.provisional.p2.query.Query;
import org.eclipse.equinox.internal.provisional.p2.ui.operations.ProvisioningUtil;
import org.eclipse.mylyn.internal.discovery.core.model.ConnectorDescriptor;
import org.eclipse.mylyn.internal.discovery.ui.DiscoveryUi;
import org.eclipse.mylyn.internal.provisional.commons.ui.ICoreRunnable;
import org.osgi.framework.Version;

/**
 * A job that downloads and installs one or more {@link ConnectorDescriptor connectors}.
 * 
 * @author David Green
 */
public class InstallConnectorsJob implements ICoreRunnable {

	private final List<ConnectorDescriptor> installableConnectors;

	public InstallConnectorsJob(List<ConnectorDescriptor> installableConnectors) {
		if (installableConnectors == null || installableConnectors.isEmpty()) {
			throw new IllegalArgumentException();
		}
		this.installableConnectors = new ArrayList<ConnectorDescriptor>(installableConnectors);
	}

	public void run(IProgressMonitor monitor) throws CoreException {
		try {
			final int totalWork = installableConnectors.size() * 4;
			monitor.beginTask("Configuring installation selection", totalWork);

			// Tell p2 that it's okay to use these repositories
			Set<URL> updateSiteURLs = new HashSet<URL>();
			for (ConnectorDescriptor descriptor : installableConnectors) {
				URL url = new URL(descriptor.getSiteUrl());
				if (updateSiteURLs.add(url)) {
					if (monitor.isCanceled()) {
						return;
					}
					ProvisioningUtil.addMetadataRepository(url);
					ProvisioningUtil.addArtifactRepository(url);
					ProvisioningUtil.setColocatedRepositoryEnablement(url, true);
				}
				monitor.worked(1);
			}
			if (updateSiteURLs.isEmpty()) {
				// should never happen
				throw new IllegalStateException();
			}
			// Fetch p2's metadata for these repositories
			List<IMetadataRepository> repositories = new ArrayList<IMetadataRepository>();
			{
				int unit = installableConnectors.size() / updateSiteURLs.size();
				for (URL updateSiteUrl : updateSiteURLs) {
					if (monitor.isCanceled()) {
						return;
					}
					repositories.add(ProvisioningUtil.loadMetadataRepository(updateSiteUrl, new SubProgressMonitor(
							monitor, unit)));
				}
			}
			// Perform a query to get the installable units
			List<InstallableUnit> installableUnits = new ArrayList<InstallableUnit>();
			{
				int unit = installableConnectors.size() / repositories.size();

				for (IMetadataRepository repository : repositories) {
					Collector collector = new Collector();
					Query query = new Query() {
						@Override
						public boolean isMatch(Object candidate) {
							// TODO Auto-generated method stub
							return false;
						}
					};
					repository.query(query, collector, new SubProgressMonitor(monitor, unit));
				}
			}

			// TODO: filter those installable units that have a duplicate in the list with a higher version number
			{
				Map<String, Version> symbolicNameToVersion = new HashMap<String, Version>();
				for (InstallableUnit unit : new ArrayList<InstallableUnit>(installableUnits)) {
					Version version = symbolicNameToVersion.get(unit.getId());
					if (version == null || version.compareTo(unit.getVersion()) == -1) {
						symbolicNameToVersion.put(unit.getId(), unit.getVersion());
					}
				}
			}

			// TODO: do the install
//			Display.getDefault().asyncExec(new Runnable() {
//
//				public void run() {
//					IAction installAction = new InstallAction(selectionProvider, profileId, null,
//							ProvPolicies.getDefault(), shell);
//
//					installAction.run();
//				}
//			});

			monitor.done();
		} catch (MalformedURLException e) {
			// should never happen, since we already validated URLs.
			throw new CoreException(new Status(IStatus.ERROR, DiscoveryUi.BUNDLE_ID,
					"Unexpected error handling repository URL", e));
		}
	}

}
