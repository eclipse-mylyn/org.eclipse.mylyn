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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.versions.core.ChangeSet;
import org.eclipse.mylyn.versions.core.ScmCore;
import org.eclipse.mylyn.versions.core.ScmRepository;
import org.eclipse.mylyn.versions.core.spi.ScmConnector;
import org.eclipse.mylyn.versions.tasks.core.IChangeSetMapping;
import org.eclipse.mylyn.versions.tasks.ui.AbstractChangesetMappingProvider;

/**
 * 
 * @author Kilian Matt
 *
 */
public class GenericTaskChangesetMapper extends
		AbstractChangesetMappingProvider {

	private IConfiguration configuration;

	public GenericTaskChangesetMapper() {
		this.configuration = new EclipsePluginConfiguration();

	}

	public GenericTaskChangesetMapper(IConfiguration configuration) {
		this.configuration = configuration;
	}

	public void getChangesetsForTask(IChangeSetMapping mapping,
			IProgressMonitor monitor) throws CoreException {
		ITask task = mapping.getTask();
		if (task == null)
			throw new IllegalArgumentException("task must not be null");

		List<ScmRepository> repos = getRepositoriesFor(task);
		for (ScmRepository repo : repos) {

			List<ChangeSet> allChangeSets = repo.getConnector().getChangeSets(
					repo, new NullProgressMonitor());
			for (ChangeSet cs : allChangeSets) {
				if (changeSetMatches(cs, task)) {
					mapping.addChangeSet(cs);
				}
			}
		}
	}

	private boolean changeSetMatches(ChangeSet cs, ITask task) {
		// FIXME better detection
		return cs.getMessage().contains(task.getTaskKey())
				|| cs.getMessage().contains(task.getUrl());
	}

	private List<ScmRepository> getRepositoriesFor(ITask task)
			throws CoreException {

		Set<ScmRepository> repos = new HashSet<ScmRepository>();

		List<IProject> projects = configuration.getProjectsForTaskRepository(
				task.getConnectorKind(), task.getRepositoryUrl());
		for (IProject p : projects) {
			ScmRepository repository = getRepositoryForProject(p);
			repos.add(repository);
		}
		return new ArrayList<ScmRepository>(repos);
	}

	private ScmRepository getRepositoryForProject(IProject p)
			throws CoreException {
		ScmConnector connector = ScmCore.getConnector(p);
		ScmRepository repository = connector.getRepository(p,
				new NullProgressMonitor());
		return repository;
	}

	public int getScoreFor(ITask task) {
		return 0;
	}

}
