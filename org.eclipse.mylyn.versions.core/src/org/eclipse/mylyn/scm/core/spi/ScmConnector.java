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

package org.eclipse.mylyn.scm.core.spi;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.scm.core.Artifact;
import org.eclipse.mylyn.scm.core.ArtifactInfo;
import org.eclipse.mylyn.scm.core.ChangeSet;
import org.eclipse.mylyn.scm.core.ScmRepository;
import org.eclipse.team.core.RepositoryProvider;
import org.eclipse.team.core.history.IFileRevision;

public abstract class ScmConnector {

	/**
	 * Lookup a remote or local resource.
	 */
	public abstract Artifact getArtifact(ArtifactInfo resource, IProgressMonitor monitor) throws CoreException;

	/**
	 * Lookup a local resource.
	 */
	public abstract Artifact getArtifact(IResource resource, IProgressMonitor monitor) throws CoreException;

	public abstract ChangeSet getChangeset(ScmRepository repository, IFileRevision revision, IProgressMonitor monitor)
			throws CoreException;

	public abstract List<ChangeSet> getChangeSets(ScmRepository repository, IProgressMonitor monitor)
			throws CoreException;

	public abstract RepositoryProvider getProvider();

	public abstract List<ScmRepository> getRepositories(IProgressMonitor monitor) throws CoreException;

	public abstract ScmRepository getRepository(IResource resource, IProgressMonitor monitor) throws CoreException;

}
