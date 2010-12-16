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

package org.eclipse.mylyn.internal.ccvs.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.versions.core.ChangeSet;
import org.eclipse.mylyn.versions.core.ScmArtifact;
import org.eclipse.mylyn.versions.core.ScmArtifactInfo;
import org.eclipse.mylyn.versions.core.ScmRepository;
import org.eclipse.mylyn.versions.core.spi.ScmConnector;
import org.eclipse.team.core.RepositoryProvider;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.internal.ccvs.core.CVSProviderPlugin;
import org.eclipse.team.internal.ccvs.core.CVSTeamProvider;
import org.eclipse.team.internal.ccvs.core.ICVSRepositoryLocation;

/**
 * @author Steffen Pingel
 */
public class CvsConnector extends ScmConnector {

	@Override
	public ScmArtifact getArtifact(ScmArtifactInfo resource, IProgressMonitor monitor) throws CoreException {
		return null;
	}

	@Override
	public ScmArtifact getArtifact(IResource resource) {
		return new CvsArtifact(this, resource);
	}

	@Override
	public ChangeSet getChangeset(ScmRepository repository, IFileRevision revision, IProgressMonitor monitor)
			throws CoreException {
		// ignore
		return null;
	}

	@Override
	public List<ChangeSet> getChangeSets(ScmRepository repository, IProgressMonitor monitor) throws CoreException {
		// ignore
		return null;
	}

	@Override
	public String getProviderId() {
		return CVSProviderPlugin.getTypeId();
	}

	@Override
	public List<ScmRepository> getRepositories(IProgressMonitor monitor) throws CoreException {
		ICVSRepositoryLocation[] locations = CVSProviderPlugin.getPlugin().getKnownRepositories();
		List<ScmRepository> repositories = new ArrayList<ScmRepository>(locations.length);
		for (ICVSRepositoryLocation location : locations) {
			ScmRepository repository = getRepository(location);
			repositories.add(repository);
		}
		return repositories;
	}

	protected CvsRepository getRepository(ICVSRepositoryLocation location) {
		CvsRepository repository = new CvsRepository(location);
		repository.setName(location.getLocation(true));
		repository.setUrl(location.getLocation(true));
		return repository;
	}

	@Override
	public ScmRepository getRepository(IResource resource, IProgressMonitor monitor) throws CoreException {
		RepositoryProvider provider = RepositoryProvider.getProvider(resource.getProject(), getProviderId());
		ICVSRepositoryLocation location = ((CVSTeamProvider) provider).getRemoteLocation();
		return getRepository(location);
	}

}
