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
import java.util.List;
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
 * 
 * @author Kilian Matt
 *
 */
public class EclipseWorkspaceRepositorySource implements IChangeSetSource {
	public void fetchAllChangesets(IProgressMonitor monitor,
			IChangeSetIndexer indexer) throws CoreException {
		Set<ScmRepository> repositories = new HashSet<ScmRepository>();

		for (IProject project : ResourcesPlugin.getWorkspace().getRoot()
				.getProjects()) {
			ScmConnector connector = ScmCore.getConnector(project);
			if(connector!=null) {
				repositories.add(connector.getRepository(project, monitor));
			}
		}

		for (ScmRepository repo : repositories) {
			List<ChangeSet> changesets;
			changesets = repo.getConnector().getChangeSets(repo, monitor);
			for (ChangeSet cs : changesets) {
				indexer.index(cs);
			}
		}

	}
}