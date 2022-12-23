/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Research Group for Industrial Software (INSO), Vienna University of Technology - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.versions.tasks.mapper.generic;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.versions.core.ScmRepository;
import org.eclipse.mylyn.versions.tasks.core.IChangeSetMapping;
import org.eclipse.mylyn.versions.tasks.mapper.internal.ChangeSetProvider;
import org.eclipse.mylyn.versions.tasks.mapper.internal.EclipsePluginConfiguration;
import org.eclipse.mylyn.versions.tasks.mapper.internal.MappingChangeSetCollector;
import org.eclipse.mylyn.versions.tasks.mapper.internal.RepositoryIndexerPlugin;
import org.eclipse.mylyn.versions.tasks.ui.AbstractChangesetMappingProvider;

/**
 *
 * @author Kilian Matt
 *
 */
public class GenericTaskChangesetMapper extends AbstractChangesetMappingProvider {

	private IConfiguration configuration;
	private IChangeSetIndexSearcher indexSearch;

	public GenericTaskChangesetMapper() {
		this.configuration = new EclipsePluginConfiguration();
		this.indexSearch = RepositoryIndexerPlugin.getDefault().getIndexer();
	}

	public GenericTaskChangesetMapper(IConfiguration configuration, IChangeSetIndexSearcher indexSearch) {
		this.configuration = configuration;
		this.indexSearch=indexSearch;
	}

	public void getChangesetsForTask(final IChangeSetMapping mapping,
			final IProgressMonitor monitor) throws CoreException {
		ITask task = mapping.getTask();
		if (task == null)
			throw new IllegalArgumentException("task must not be null");

		for (final ScmRepository repo : configuration.getRepositoriesFor(task)) {
			ChangeSetProvider provider = new ChangeSetProvider(repo);
			indexSearch.search(task, repo.getUrl(), 10,
					new MappingChangeSetCollector(monitor, mapping, provider));
		}
	}



	public int getScoreFor(ITask task) {
		return 0;
	}

}
