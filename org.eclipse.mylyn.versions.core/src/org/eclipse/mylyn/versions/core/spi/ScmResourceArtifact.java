/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.versions.core.spi;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.versions.core.ScmArtifact;
import org.eclipse.team.core.RepositoryProvider;
import org.eclipse.team.core.history.IFileHistory;
import org.eclipse.team.core.history.IFileHistoryProvider;
import org.eclipse.team.core.history.IFileRevision;

/**
 * @author Steffen Pingel
 */
public class ScmResourceArtifact extends ScmArtifact {

	private final IResource resource;

	private final ScmConnector connector;

	public ScmResourceArtifact(ScmConnector connector, IResource resource, String id, String path) {
		super(id, path);
		this.connector = connector;
		this.resource = resource;
		setProjectName(resource.getProject().getName());
		setProjectRelativePath(resource.getProjectRelativePath().toPortableString());
	}

	public ScmResourceArtifact(ScmConnector connector, IResource resource, String id) {
		this(connector, resource, id, resource.getFullPath().toString());
	}

	@Override
	public IFileRevision getFileRevision(IProgressMonitor monitor) {
		return getFileHistory(monitor).getFileRevision(getId());
	}

	public IFileHistory getFileHistory(IProgressMonitor monitor) {
		RepositoryProvider provider = RepositoryProvider.getProvider(resource.getProject(), connector.getProviderId());
		IFileHistory history = provider.getFileHistoryProvider().getFileHistoryFor(resource, IFileHistoryProvider.NONE,
				monitor);
		return history;
	}

	@Override
	public IFileRevision[] getContributors(IProgressMonitor monitor) {
		IFileHistory history = getFileHistory(monitor);
		IFileRevision fileRevision = history.getFileRevision(getId());
		return (fileRevision != null) ? history.getContributors(fileRevision) : null;
	}

	@Override
	public IFileRevision[] getTargets(IProgressMonitor monitor) {
		IFileHistory history = getFileHistory(monitor);
		IFileRevision fileRevision = history.getFileRevision(getId());
		return (fileRevision != null) ? history.getTargets(fileRevision) : null;
	}

}
