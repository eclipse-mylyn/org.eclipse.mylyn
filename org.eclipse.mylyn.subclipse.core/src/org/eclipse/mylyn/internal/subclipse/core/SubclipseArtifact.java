/*******************************************************************************
 * Copyright (c) 2011 Ericsson AB and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Contributors:
 *   Alvaro Sanchez-Leon (Ericsson AB) - Initial API and Implementation
 *   Sebastien Dubois (Ericsson AB) - Implemented getContributors method
 *******************************************************************************/

package org.eclipse.mylyn.internal.subclipse.core;

import java.net.MalformedURLException;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.versions.core.ScmArtifact;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.core.history.provider.FileRevision;
import org.tigris.subversion.subclipse.core.ISVNRemoteResource;
import org.tigris.subversion.subclipse.core.ISVNRepositoryLocation;
import org.tigris.subversion.subclipse.core.SVNException;
import org.tigris.subversion.subclipse.core.commands.GetLogsCommand;
import org.tigris.subversion.subclipse.core.commands.GetRemoteResourceCommand;
import org.tigris.subversion.subclipse.core.history.ILogEntry;
import org.tigris.subversion.subclipse.core.resources.RemoteFile;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;

/**
 * @author Alvaro Sanchez-Leon
 */
@SuppressWarnings("restriction")
public class SubclipseArtifact extends ScmArtifact {

	private final SubclipseRepository repository;

	private static final ILog logger = SubclipseCorePlugin.getDefault().getLog();

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

					final Long revisionNum = Long.decode(getId());
					return resolveStorage(monitor, revisionNum, repository.getLocation(), getPath());
				}

				@Override
				public String getContentIdentifier() {
					if (null != svnRemResource) {
						return svnRemResource.getLastChangedRevision().toString();
					}
					return null;
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
		//NOTE: Here we cannot use the ScmResourceArtifact to retrieve the contributors because Subclipse does not 
		//		implement IFileHistory and IFileRevision interfaces.  So we will assume that the only contributor is the
		//		previous version of the artifact.
		if (null == svnRemResource) {
			return null;
		}

		//First we have to get the previous revision using the previous ILogEntry.
		SVNRevision firstRevision = new SVNRevision.Number(1L);
		SVNRevision.Number previousRevision = new SVNRevision.Number(Long.parseLong(getId()));
		GetLogsCommand logCmd = new GetLogsCommand(svnRemResource, null, previousRevision, firstRevision, false, 2L,
				null, true);
		try {
			logCmd.run(monitor);
		} catch (SVNException e) {
			return null;
		}

		final ILogEntry[] entries = logCmd.getLogEntries();
		if (entries.length < 2) {
			//No base version found
			return null;
		}

		//Pick the previous revision of the artifact and return it as an IFileRevision
		final ISVNRemoteResource remoteResource = entries[entries.length - 1].getRemoteResource();
		final IFileRevision[] contributors = new IFileRevision[1];
		contributors[0] = new FileRevision() {
			public IFileRevision withAllProperties(IProgressMonitor monitor) throws CoreException {
				return this;
			}

			public boolean isPropertyMissing() {
				return false;
			}

			public IStorage getStorage(IProgressMonitor monitor) throws CoreException {
				if (null != remoteResource) {
					return remoteResource.getStorage(monitor);
				}
				return null;
			}

			@Override
			public String getContentIdentifier() {
				if (null != remoteResource) {
					return remoteResource.getLastChangedRevision().toString();
				}
				return null;
			}

			public String getName() {
				return remoteResource.getName();
			}
		};
		return contributors;
	}

	@Override
	public IFileRevision[] getTargets(IProgressMonitor monitor) {
		throw new UnsupportedOperationException();
	}

	public SVNUrl getRepositoryURL() throws MalformedURLException {
		return new SVNUrl(appendToPath(repository.getLocation().getRepositoryRoot().toString(), getPath()));
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

	private ISVNRemoteResource resolveRemoteResource(IProgressMonitor monitor, ISVNRepositoryLocation location,
			SVNRevision revision, SVNUrl url) throws CoreException {

		final GetRemoteResourceCommand command = new GetRemoteResourceCommand(location, url, revision);

		command.run(monitor);

		final ISVNRemoteResource resource = command.getRemoteResource();
		if (resource == null) {
			final String msg = "Unable to resolve remote resource for: " + url.toString(); //$NON-NLS-1$
			final Status status = new Status(IStatus.ERROR, SubclipseCorePlugin.PLUGIN_ID, msg);
			throw new CoreException(status);
		}

		return resource;
	}

	private IStorage resolveStorage(IProgressMonitor monitor, Long revNo, ISVNRepositoryLocation location,
			String path) {

		try {
			final SVNRevision revision = new SVNRevision.Number(revNo.longValue());

			final SVNUrl url = getRepositoryURL();

			final ISVNRemoteResource resource = resolveRemoteResource(monitor, location, revision, url);

			// check if the resource is a file
			if (resource.isFolder()) {
				throw new CoreException(new Status(IStatus.ERROR, SubclipseCorePlugin.PLUGIN_ID,
						"The path refers to a folder not to a file: " + path)); //$NON-NLS-1$
			}

			// create remote file with the correct peg revision
			final RemoteFile file = new RemoteFile(null, location, url, revision, (SVNRevision.Number) revision, null,
					null);
			file.setPegRevision(revision);
			file.fetchContents(monitor);

			return file.getStorage(monitor);

		} catch (Exception e) {
			logger.log(new Status(IStatus.WARNING, SubclipseCorePlugin.PLUGIN_ID, "Unable to resolve storage, " + revNo //$NON-NLS-1$
					+ ", " + path)); //$NON-NLS-1$
			return null;
		}
	}
}
