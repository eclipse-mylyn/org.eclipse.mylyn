/*******************************************************************************
 * Copyright (c) 2012 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Research Group for Industrial Software (INSO), Vienna University of Technology - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.versions.tasks.mapper.internal;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.versions.core.ChangeSet;
import org.eclipse.mylyn.versions.core.ScmCore;
import org.eclipse.mylyn.versions.core.ScmRepository;
import org.eclipse.mylyn.versions.core.spi.ScmConnector;
import org.eclipse.mylyn.versions.tasks.mapper.generic.IChangeSetIndexer;
import org.eclipse.mylyn.versions.tasks.mapper.generic.IChangeSetSource;

/**
 * ChangeSet Source, which provides the changesets of all (shared) projects in the Eclipse workspace.
 * 
 * @author Kilian Matt
 */
public class EclipseWorkspaceRepositorySource implements IChangeSetSource {
	public void fetchAllChangesets(IProgressMonitor monitor,
			IChangeSetIndexer indexer) throws CoreException {
		Set<ScmRepository> repositories = new HashSet<ScmRepository>();

		for (IProject project : ResourcesPlugin.getWorkspace().getRoot()
				.getProjects()) {
			ScmConnector connector = ScmCore.getConnector(project);
			if(connector!=null) {
				ScmRepository repository = connector.getRepository(project, monitor);
				repositories.add(repository);
			}
		}

		for (ScmRepository repo : repositories) {
			Iterator<ChangeSet> changesets = repo.getConnector().getChangeSetsIterator(repo, monitor);
			while (changesets.hasNext()) {
				ChangeSet cs =changesets.next();
				indexer.index(cs);
			}
			
		}

	}
	
	// TODO
	//notification on new (shared) projects, attn detect overlaps with existing repositories.
	// registering a listener for detecting new repository changes
	// 
	
}