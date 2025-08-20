/*******************************************************************************
 * Copyright (c) 2011 Research Group for Industrial Software (INSO), Vienna University of Technology
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.git.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.egit.core.project.RepositoryMapping;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.mylyn.versions.core.ScmRepository;

/**
 * ScmRepository implementation for git
 *
 * @author mattk
 */
public class GitRepository extends ScmRepository {

	private final RepositoryMapping mapping;

	private final IProject fMainWSProject;

	public GitRepository(GitConnector connector, RepositoryMapping mapping, IProject mainProject) {
		this.mapping = mapping;
		setConnector(connector);
		determineUrl();
		fMainWSProject = mainProject;
	}

	public Repository getRepository() {
		return mapping.getRepository();
	}

	public RepositoryMapping getMapping() {
		return mapping;
	}

	public String getWorkspaceRevision(IResource resource) {
		// TODO Auto-generated method stub
		return null;
	}

	public String convertWorkspacePath(IResource resource) {
		return mapping.getRepoRelativePath(resource);
	}

	private void determineUrl() {
		// FIXME - use a better approach and handle multiple remotes better
		String originUrl = getRepository().getConfig().getString("remote", "origin", "url"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		if (originUrl != null) {
			setUrl(originUrl);
		}
	}

	/**
	 * Returns the main work space project (if any) associated to this repository
	 *
	 * @return
	 */
	public IProject getMainWsProject() {
		return fMainWSProject;
	}

}
