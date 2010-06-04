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
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.builds.core.BuildModel;
import org.eclipse.mylyn.internal.builds.core.BuildPackage;
import org.eclipse.mylyn.internal.builds.core.BuildServer;
import org.eclipse.mylyn.internal.builds.core.BuildsCorePlugin;
import org.eclipse.mylyn.internal.builds.core.tasks.BuildTaskConnector;
import org.eclipse.mylyn.internal.builds.core.tasks.IBuildLoader;
import org.eclipse.mylyn.tasks.core.IRepositoryListener;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.osgi.util.NLS;

/**
 * @author Steffen Pingel
 */
public class BuildModelManager {

	private class RepositoryListener implements IRepositoryListener {

		public void repositoryAdded(TaskRepository repository) {
			// ignore
		}

		public void repositoryRemoved(TaskRepository repository) {
			for (IBuildServer server : model.getServers()) {
				if (repository.equals(server.getRepository())) {
					model.getServers().remove(server);
					return;
				}
			}
		}

		public void repositorySettingsChanged(TaskRepository repository) {
			// ignore			
		}

		public void repositoryUrlChanged(TaskRepository repository, String oldUrl) {
			// ignore			
		}

	}

	private IRepositoryManager repositoryManager;

	private final Resource resource;

	private final BuildModel model;

	private final RepositoryListener repositoryListener;

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
		this.repositoryListener = new RepositoryListener();
	}

	public void save() throws IOException {
		Map<Object, Object> options = new HashMap<Object, Object>();
//		options.put(XMLResource.OPTION_ENCODING, "UTF-8");
		resource.save(options);
	}

	public BuildModel getModel() {
		return model;
	}

	public void setRepositoryManager(IRepositoryManager repositoryManager) {
		if (this.repositoryManager != null) {
			this.repositoryManager.removeListener(repositoryListener);
		}
		this.repositoryManager = repositoryManager;
		repositoryManager.addListener(repositoryListener);

		// hook repositories up to build model
		List<TaskRepository> repositories = repositoryManager.getAllRepositories();
		for (TaskRepository taskRepository : repositories) {
			if (BuildTaskConnector.CONNECTOR_KIND.equals(taskRepository.getConnectorKind())) {
				for (IBuildServer server : model.getServers()) {
					if (taskRepository.getRepositoryUrl().equals(server.getRepositoryUrl())) {
						((BuildServer) server).setRepository(taskRepository);
					}
				}
			}
		}
	}

	public IRepositoryManager getRepositoryManager() {
		return repositoryManager;
	}

	public IBuildServer createServer(TaskRepository repository) {
		BuildServer server = BuildPackage.eINSTANCE.getBuildFactory().createBuildServer();
		if (repository != null) {
			server.setConnectorKind(repository.getProperty(BuildTaskConnector.TASK_REPOSITORY_KEY_BUILD_CONNECTOR_KIND));
			server.setName(repository.getRepositoryLabel());
			server.setRepository(repository);
			server.setRepositoryUrl(repository.getRepositoryUrl());
			server.setUrl(repository.getRepositoryUrl());
		}
		server.setLoader(buildLoader);
		return server;
	}

}
