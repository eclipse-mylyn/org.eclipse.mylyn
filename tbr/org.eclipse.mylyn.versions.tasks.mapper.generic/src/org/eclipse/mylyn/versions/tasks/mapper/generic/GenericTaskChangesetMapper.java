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

import java.io.File;
import java.io.NotSerializableException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.versions.core.ChangeSet;
import org.eclipse.mylyn.versions.core.ScmCore;
import org.eclipse.mylyn.versions.core.ScmRepository;
import org.eclipse.mylyn.versions.core.spi.ScmConnector;
import org.eclipse.mylyn.versions.tasks.core.IChangeSetMapping;
import org.eclipse.mylyn.versions.tasks.mapper.internal.ChangeSetIndexer;
import org.eclipse.mylyn.versions.tasks.mapper.internal.RepositoryIndexerPlugin;
import org.eclipse.mylyn.versions.tasks.ui.AbstractChangesetMappingProvider;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.core.history.ITag;
import org.eclipse.team.core.history.provider.FileRevision;

/**
 * 
 * @author Kilian Matt
 *
 */
public class GenericTaskChangesetMapper extends
		AbstractChangesetMappingProvider {

	private IConfiguration configuration;
	private IChangeSetIndexSearcher indexSearch;

	public GenericTaskChangesetMapper() {
		this.configuration = new EclipsePluginConfiguration();
		this.indexSearch = RepositoryIndexerPlugin.getDefault().getIndexer();
	}

	public GenericTaskChangesetMapper(IConfiguration configuration) {
		this.configuration = configuration;
	}

	public void getChangesetsForTask(final IChangeSetMapping mapping,
			final IProgressMonitor monitor) throws CoreException {
		ITask task = mapping.getTask();
		if (task == null)
			throw new IllegalArgumentException("task must not be null");

		
		List<ScmRepository> repos = getRepositoriesFor(task);
		for (final ScmRepository repo : repos) {
				indexSearch.search(task, repo.getUrl(), 10, new IChangeSetCollector() {
					
					public void collect(String revision, String repositoryUrl) throws CoreException {
						mapping.addChangeSet(getChangeset(revision, repo,monitor));				
					}
				});
		}
	}

	class FileRevision implements IFileRevision{
		private String contentIdentifier;

		public FileRevision(String contentIdentifier){
			this.contentIdentifier=contentIdentifier;
		}

		public IStorage getStorage(IProgressMonitor monitor)
				throws CoreException {
			throw new UnsupportedOperationException();
		}

		public String getName() {
			throw new UnsupportedOperationException();
		}

		public URI getURI() {
			throw new UnsupportedOperationException();
		}

		public long getTimestamp() {
			throw new UnsupportedOperationException();
		}

		public boolean exists() {
			throw new UnsupportedOperationException();
		}

		public String getContentIdentifier() {
			return contentIdentifier;
		}

		public String getAuthor() {
			throw new UnsupportedOperationException();
		}

		public String getComment() {
			throw new UnsupportedOperationException();
		}

		public ITag[] getBranches() {
			throw new UnsupportedOperationException();
		}

		public ITag[] getTags() {
			throw new UnsupportedOperationException();
		}

		public boolean isPropertyMissing() {
			throw new UnsupportedOperationException();
		}

		public IFileRevision withAllProperties(IProgressMonitor monitor)
				throws CoreException {
			throw new UnsupportedOperationException();
		}}
	
	protected ChangeSet getChangeset(String revision, ScmRepository repo,IProgressMonitor monitor) throws CoreException {
		return repo.getConnector().getChangeSet(repo, new FileRevision(revision), monitor);
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
