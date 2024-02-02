/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.builds.internal.core.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.internal.core.BuildModel;
import org.eclipse.mylyn.builds.internal.core.BuildPackage;
import org.eclipse.mylyn.builds.internal.core.BuildServer;
import org.eclipse.mylyn.builds.internal.core.BuildsCorePlugin;
import org.eclipse.mylyn.builds.internal.core.IBuildLoader;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.osgi.util.NLS;

/**
 * @author Steffen Pingel
 */
public class BuildModelManager {

	private final Resource resource;

	private final BuildModel model;

	private final IBuildLoader buildLoader;

	public BuildModelManager(File file, IBuildLoader buildLoader) {
		this.buildLoader = buildLoader;

		Resource resource = null;
		BuildModel model = null;
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		URI uri = URI.createURI(file.toURI().toString());
		if (file.exists()) {
			try {
				resource = resourceSet.getResource(uri, true);
				Object root = resource.getContents().get(0);
				if (root instanceof BuildModel) {
					model = (BuildModel) root;
					for (IBuildServer server : model.getServers()) {
						((BuildServer) server).setLoader(buildLoader);
						Map<String, String> properties = ((BuildServer) server).getAttributes();
						if (properties.size() > 0) {
							((BuildServer) server).setLocation(new RepositoryLocation(properties));
						}
					}
				} else {
					StatusHandler.log(new Status(IStatus.ERROR, BuildsCorePlugin.ID_PLUGIN,
							NLS.bind("Unexpected content while loading builds from ''{0}''", file.getAbsolutePath())));
				}
			} catch (RuntimeException e) {
				StatusHandler.log(new Status(IStatus.ERROR, BuildsCorePlugin.ID_PLUGIN,
						NLS.bind("Unexpected error while loading builds from ''{0}''", file.getAbsolutePath()), e));
			}
		}
		if (model == null) {
			resource = resourceSet.createResource(uri);
			model = (BuildModel) BuildPackage.eINSTANCE.getBuildFactory().createBuildModel();
			resource.getContents().clear();
			resource.getContents().add(model);
		}
		this.model = model;
		this.resource = resource;
	}

	public void save() throws IOException {
		// ensure that model is a consistent state
		List<IBuildServer> servers = model.getServers();

		List<IBuild> builds = new ArrayList<>();

		List<IBuildPlan> danglingPlans = new ArrayList<>();
		List<IBuildPlan> plans = model.getPlans();
		for (IBuildPlan plan : plans) {
			if (!servers.contains(plan.getServer())) {
				danglingPlans.add(plan);
			} else if (plan.getLastBuild() != null) {
				builds.add(plan.getLastBuild());
			}
		}
		plans.removeAll(danglingPlans);

		model.getBuilds().clear();
		model.getBuilds().addAll(builds);

		Map<Object, Object> options = new HashMap<>();
		options.put(XMLResource.OPTION_ENCODING, "UTF-8"); //$NON-NLS-1$
		resource.save(options);
	}

	public BuildModel getModel() {
		return model;
	}

	public IBuildServer createServer(String connectorKind, RepositoryLocation location) {
		BuildServer server = (BuildServer) BuildPackage.eINSTANCE.getBuildFactory().createBuildServer();
		server.setConnectorKind(connectorKind);
		server.setLocation(location);
		server.setLoader(buildLoader);
		return server;
	}

}
