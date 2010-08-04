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

package org.eclipse.mylyn.internal.builds.core.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.repositories.RepositoryLocation;
import org.eclipse.mylyn.internal.builds.core.BuildModel;
import org.eclipse.mylyn.internal.builds.core.BuildPackage;
import org.eclipse.mylyn.internal.builds.core.BuildServer;
import org.eclipse.mylyn.internal.builds.core.BuildsCorePlugin;
import org.eclipse.mylyn.internal.builds.core.IBuildLoader;
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
						EMap<String, String> properties = ((BuildServer) server).getAttributes();
						if (properties.size() > 0) {
							((BuildServer) server).setLocation(new RepositoryLocation(properties.map()));
						}
					}
				} else {
					StatusHandler.log(new Status(IStatus.ERROR, BuildsCorePlugin.ID_PLUGIN, NLS.bind(
							"Unexpected content while loading builds from ''{0}''", file.getAbsolutePath())));
				}
			} catch (RuntimeException e) {
				StatusHandler.log(new Status(IStatus.ERROR, BuildsCorePlugin.ID_PLUGIN, NLS.bind(
						"Unexpected error while loading builds from ''{0}''", file.getAbsolutePath()), e));
			}
		}
		if (model == null) {
			resource = resourceSet.createResource(uri);
			model = BuildPackage.eINSTANCE.getBuildFactory().createBuildModel();
			resource.getContents().clear();
			resource.getContents().add(model);
		}
		this.model = model;
		this.resource = resource;
	}

	public void save() throws IOException {
		Map<Object, Object> options = new HashMap<Object, Object>();
//		options.put(XMLResource.OPTION_ENCODING, "UTF-8");
		resource.save(options);
	}

	public BuildModel getModel() {
		return model;
	}

	public IBuildServer createServer(String connectorKind, RepositoryLocation location) {
		BuildServer server = BuildPackage.eINSTANCE.getBuildFactory().createBuildServer();
		server.setConnectorKind(connectorKind);
		server.setLocation(location);
		server.setLoader(buildLoader);
		return server;
	}

}
