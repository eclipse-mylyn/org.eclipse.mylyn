/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.versions.core.spi;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.versions.core.ChangeSet;
import org.eclipse.mylyn.versions.core.ScmArtifact;
import org.eclipse.mylyn.versions.core.ScmRepository;
import org.eclipse.team.core.history.IFileRevision;

/**
 * @author Steffen Pingel
 */
public abstract class ScmConnector {

	/**
	 * Lookup a local resource.
	 */
	public abstract ScmArtifact getArtifact(IResource resource, String revision) throws CoreException;

	public ScmArtifact getArtifact(IResource resource) throws CoreException {
		return getArtifact(resource, null);
	}

	public abstract ChangeSet getChangeSet(ScmRepository repository, IFileRevision revision, IProgressMonitor monitor)
			throws CoreException;

	public abstract List<ChangeSet> getChangeSets(ScmRepository repository, IProgressMonitor monitor)
			throws CoreException;

	public Iterator<ChangeSet> getChangeSetsIterator(ScmRepository repository, IProgressMonitor monitor) {
		throw new UnsupportedOperationException();
	}

	public abstract String getProviderId();

	public abstract List<ScmRepository> getRepositories(IProgressMonitor monitor) throws CoreException;

	public abstract ScmRepository getRepository(IResource resource, IProgressMonitor monitor) throws CoreException;

}
