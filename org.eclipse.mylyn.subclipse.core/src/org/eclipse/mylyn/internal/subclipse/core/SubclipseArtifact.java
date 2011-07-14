/*******************************************************************************
 * Copyright (c) 2011 Ericsson AB and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Ericsson AB - Initial API and Implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.subclipse.core;

import java.net.MalformedURLException;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.mylyn.versions.core.ScmArtifact;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.core.history.provider.FileRevision;
import org.tigris.subversion.subclipse.core.ISVNRemoteResource;
import org.tigris.subversion.subclipse.core.ISVNRepositoryLocation;
import org.tigris.subversion.svnclientadapter.SVNUrl;

/**
 * @author Alvaro Sanchez-Leon
 */
@SuppressWarnings("restriction")
public class SubclipseArtifact extends ScmArtifact {

	private final SubclipseRepository repository;

	private static ILog logger = SubclipseCorePlugin.getDefault().getLog();

	private ISVNRemoteResource svnRemResource = null;

	public SubclipseArtifact(String id, String path, SubclipseRepository repository) {
		super(id, path);
		this.repository = repository;
	}

	@Override
	public IFileRevision getFileRevision(IProgressMonitor monitor) {
		try {
			final IPath path = Path.fromPortableString(getPath());
			return new FileRevision() {

				public IFileRevision withAllProperties(IProgressMonitor monitor) throws CoreException {
					return this;
				}

				public boolean isPropertyMissing() {
					return false;
				}

				public IStorage getStorage(IProgressMonitor monitor) throws CoreException {
					//If base revision e.g. remote resource is available
					if (svnRemResource != null) {
						return svnRemResource.getStorage(monitor);
					}

					Long revisionNum = Long.decode(getId());
					return resolveStorage(monitor, revisionNum, repository.getLocation(), getPath());
				}

				public String getName() {
					return path.lastSegment();
				}
			};

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public IFileRevision[] getContributors(IProgressMonitor monitor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IFileRevision[] getTargets(IProgressMonitor monitor) {
		throw new UnsupportedOperationException();
	}

	public SVNUrl getRepositoryURL() throws MalformedURLException {
		return new SVNUrl(appendToPath(repository.getLocation().getLocation(), getPath()));
	}

	public void setRemoteResource(ISVNRemoteResource remoteResource) {
		svnRemResource = remoteResource;
	}

	/**
	 * Specific to svn paths as the separator is assumed to be the same for multiple OS
	 * 
	 * @param aBase
	 * @param aTail
	 * @return
	 */
	private String appendToPath(String aBase, String aTail) {
		//initialise with most likely layout
		String dBase = aBase;
		String dTail = aTail;

		if (!aBase.endsWith("/")) { //$NON-NLS-1$
			dBase = aBase + "/"; //$NON-NLS-1$
		}

		if (aTail.startsWith("/")) { //$NON-NLS-1$
			dTail = aTail.substring(1);
		}

		return dBase + dTail;
	}

	private IStorage resolveStorage(IProgressMonitor monitor, Long revNo, ISVNRepositoryLocation location, String path)
			throws CoreException {

		//TODO: Implement me
		throw new UnsupportedOperationException();
	}
}
