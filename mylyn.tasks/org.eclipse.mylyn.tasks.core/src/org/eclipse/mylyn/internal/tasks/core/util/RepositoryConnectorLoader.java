/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.util;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.internal.tasks.core.RepositoryTemplateManager;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.core.externalization.TaskListExternalizer;
import org.eclipse.mylyn.tasks.core.spi.RepositoryConnectorDescriptor;

public class RepositoryConnectorLoader {

	private static final String EXTENSION_REPOSITORIES = "org.eclipse.mylyn.tasks.ui.repositories"; //$NON-NLS-1$

	/**
	 * Plug-in ids of connector extensions that failed to load.
	 */
	private final ContributorBlackList blackList = new ContributorBlackList();

	private final Set<RepositoryConnectorDescriptor> descriptors = new HashSet<RepositoryConnectorDescriptor>();

	public ContributorBlackList getBlackList() {
		return blackList;
	}

	public void registerConnectors(TaskRepositoryManager repositoryManager, TaskListExternalizer taskListExternalizer) {
		IExtensionRegistry registry = Platform.getExtensionRegistry();

		// NOTE: has to be read first, consider improving
		RepositoryConnectorExtensionReader reader = new RepositoryConnectorExtensionReader(taskListExternalizer,
				repositoryManager);
		// load core extension point
		reader.loadConnectorsFromRepositoriesExtension();
		// load legacy ui extension point
		reader.loadConnectors(registry.getExtensionPoint(EXTENSION_REPOSITORIES));
		// load connectors contributed at runtime
		reader.loadConnectorsFromContributors();
		reader.registerConnectors();
		descriptors.addAll(reader.getDescriptors());
		blackList.merge(reader.getBlackList());
	}

	public void registerTemplates(TaskRepositoryManager repositoryManager,
			RepositoryTemplateManager repositoryTemplateManager) {
		RepositoryTemplateExtensionReader templateExtensionReader = new RepositoryTemplateExtensionReader(
				repositoryManager, repositoryTemplateManager);
		templateExtensionReader.loadExtensions(blackList);
	}

}
