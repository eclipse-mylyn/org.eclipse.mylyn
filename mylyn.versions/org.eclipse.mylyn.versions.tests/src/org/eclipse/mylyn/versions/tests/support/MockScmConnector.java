/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.versions.tests.support;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.versions.core.ChangeSet;
import org.eclipse.mylyn.versions.core.ScmArtifact;
import org.eclipse.mylyn.versions.core.ScmRepository;
import org.eclipse.mylyn.versions.core.spi.ScmConnector;
import org.eclipse.team.core.history.IFileRevision;

/**
 * @author Steffen Pingel
 */
public class MockScmConnector extends ScmConnector {

	public MockScmConnector() {
	}

	@Override
	public ScmArtifact getArtifact(IResource resource, String revision) throws CoreException {
		// ignore
		return null;
	}

	@Override
	public ChangeSet getChangeSet(ScmRepository repository, IFileRevision revision, IProgressMonitor monitor)
			throws CoreException {
		// ignore
		return null;
	}

	@Override
	public List<ChangeSet> getChangeSets(ScmRepository repository, IProgressMonitor monitor) throws CoreException {
		return null;
	}

	@Override
	public String getProviderId() {
		return MockRepositoryProvider.ID;
	}

	@Override
	public List<ScmRepository> getRepositories(IProgressMonitor monitor) throws CoreException {
		return null;
	}

	@Override
	public ScmRepository getRepository(IResource resource, IProgressMonitor monitor) throws CoreException {
		// ignore
		return null;
	}

}
