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

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.versions.core.Change;
import org.eclipse.mylyn.versions.core.ChangeSet;
import org.eclipse.mylyn.versions.core.ScmArtifact;
import org.eclipse.mylyn.versions.core.ScmRepository;
import org.eclipse.mylyn.versions.core.ScmUser;
import org.eclipse.mylyn.versions.core.spi.ScmConnector;
import org.eclipse.team.core.history.IFileRevision;
import org.tigris.subversion.subclipse.core.ISVNRepositoryLocation;
import org.tigris.subversion.subclipse.core.SVNClientManager;
import org.tigris.subversion.subclipse.core.SVNException;
import org.tigris.subversion.subclipse.core.SVNProviderPlugin;
import org.tigris.subversion.subclipse.core.resources.SVNWorkspaceRoot;
import org.tigris.subversion.svnclientadapter.ISVNClientAdapter;
import org.tigris.subversion.svnclientadapter.ISVNLogMessage;
import org.tigris.subversion.svnclientadapter.ISVNLogMessageChangePath;
import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;

/**
 * @author Alvaro Sanchez-Leon
 */
@SuppressWarnings("restriction")
public class SubclipseConnector extends ScmConnector {

	ISVNClientAdapter clientAdapter = null;

	@Override
	public ScmArtifact getArtifact(IResource resource, String revision) {
		return new SubclipseArtifact(this, resource, revision);
	}

	@Override
	public ChangeSet getChangeSet(ScmRepository repository, IFileRevision revision, IProgressMonitor monitor)
			throws CoreException {
		SubclipseRepository repo = (SubclipseRepository) repository;

		String versionId = revision.getContentIdentifier();
		SVNRevision sRevision = null;
		try {
			sRevision = SVNRevision.getRevision(versionId);
		} catch (ParseException e1) {
			IStatus status = new Status(IStatus.ERROR, SubclipseCorePlugin.PLUGIN_ID, "Unable to resolve VersionId " //$NON-NLS-1$
					+ versionId, e1);
			throw new CoreException(status);
		}

		//fetch change paths = true
		ISVNLogMessage[] messages = resolveChangeSets(repo, sRevision, sRevision, true);

		if (messages == null) {
			return null;
		}

		//Only one message is expected as the start and end revision are the same
		assert (messages.length == 0);

		ISVNLogMessage isvnLogMessage = messages[0];
		List<Change> changes = new ArrayList<Change>();
		ChangeSet changeSet = changeSet(repo, isvnLogMessage, changes);

		//TODO: Work in progress
		//Build changes
		ISVNLogMessageChangePath[] changePaths = isvnLogMessage.getChangedPaths();
		for (ISVNLogMessageChangePath isvnLogMessageChangePath : changePaths) {
			isvnLogMessageChangePath.getAction();
//			isvnLogMessageChangePath.

		}

		throw new UnsupportedOperationException();
	}

	private ISVNLogMessage[] resolveChangeSets(SubclipseRepository repository, SVNRevision start, SVNRevision end,
			boolean fetchChangePath) throws CoreException {

		ISVNRepositoryLocation location = repository.getLocation();

		ISVNLogMessage[] messages = null;
		SVNUrl url = null;
		try {
			//getting specific revision
			url = location.getUrl();

			ISVNClientAdapter adapter = createClientAdapter();
			messages = adapter.getLogMessages(url, start, end, fetchChangePath);

		} catch (SVNClientException e) {
			StringBuilder sb = new StringBuilder("Unable to resolve ChangeSets for location"); //$NON-NLS-1$
			if (url != null) {
				sb.append(": " + url.toString()); //$NON-NLS-1$
			}
			IStatus status = new Status(IStatus.ERROR, SubclipseCorePlugin.PLUGIN_ID, sb.toString(), e);
			throw new CoreException(status);
		}
		return messages;
	}

	@Override
	public List<ChangeSet> getChangeSets(ScmRepository repository, IProgressMonitor monitor) throws CoreException {
		SubclipseRepository repo = (SubclipseRepository) repository;

		//TODO: retrieving all revisions per project repository does not scale, need to discuss API to narrow down list
		ISVNLogMessage[] messages = resolveChangeSets(repo, SVNRevision.START, SVNRevision.HEAD, false);

		//Convert the messages to ChangeSet
		List<ChangeSet> changeSets = new ArrayList<ChangeSet>(messages.length);
		//No detailed changes provided at this point, but when requesting specific changeSet per revision
		List<Change> changes = new ArrayList<Change>();
		for (ISVNLogMessage isvnLogMessage : messages) {
			changeSets.add(changeSet(repo, isvnLogMessage, changes));
		}

		return changeSets;
	}

	private ChangeSet changeSet(SubclipseRepository repository, ISVNLogMessage message, List<Change> changes) {
		ChangeSet changeSet = null;
		changeSet = new ChangeSet(getScmUser(message.getAuthor()), message.getDate(), message.getRevision().toString(),
				message.getMessage(), repository, changes);

		return changeSet;
	}

	private ScmUser getScmUser(String name) {
		return new ScmUser("", name, ""); //$NON-NLS-1$//$NON-NLS-2$
	}

	@Override
	public String getProviderId() {
		return SVNProviderPlugin.getTypeId();
	}

	@Override
	public List<ScmRepository> getRepositories(IProgressMonitor monitor) throws CoreException {
		ISVNRepositoryLocation[] locations = SVNProviderPlugin.getPlugin()
				.getRepositories()
				.getKnownRepositories(monitor);

		List<ScmRepository> repositories = new ArrayList<ScmRepository>(locations.length);
		for (ISVNRepositoryLocation location : locations) {
			ScmRepository repository = getRepository(location);
			repositories.add(repository);
		}
		return repositories;
	}

	protected SubclipseRepository getRepository(ISVNRepositoryLocation location) {
		SubclipseRepository repository = new SubclipseRepository(this, location);
		return repository;
	}

	@Override
	public ScmRepository getRepository(IResource resource, IProgressMonitor monitor) throws CoreException {
		IProject project = resource.getProject();

		ISVNRepositoryLocation location = SVNWorkspaceRoot.getRepositoryFor(project.getLocation());

		return getRepository(location);
	}

	private ISVNClientAdapter createClientAdapter() {
		//Only once instance needed
		if (clientAdapter != null) {
			return clientAdapter;
		}

		//Create references
		SVNClientManager clientManager = new SVNClientManager();
		ISVNClientAdapter svnClientAdapter = null;

		//Create the svn configuration directory
		File tmpSvnConfigDir;
		try {
			tmpSvnConfigDir = SubclipseCorePlugin.getDefault().getTmpDir();
		} catch (IOException e1) {
			ILog logger = SubclipseCorePlugin.getDefault().getLog();
			IStatus status = new Status(IStatus.ERROR, SubclipseCorePlugin.PLUGIN_ID,
					"Unable to create a temporary directory for the svn client adapter configuration", e1); //$NON-NLS-1$
			logger.log(status);
			return null;
		}

		//setup manager
		clientManager.setConfigDir(tmpSvnConfigDir);
		String svnClientInterface = "org.eclipse.mylyn.subclipse.core"; //$NON-NLS-1$
		clientManager.setSvnClientInterface(svnClientInterface);
		try {
			clientManager.startup(null);
		} catch (CoreException e3) {
			ILog logger = SubclipseCorePlugin.getDefault().getLog();
			IStatus status = new Status(IStatus.ERROR, SubclipseCorePlugin.PLUGIN_ID,
					"Unable to start the svn client manager", e3); //$NON-NLS-1$
			logger.log(status);
			return null;
		}

		//create adapter and configuration files
		try {
			svnClientAdapter = clientManager.createSVNClient();
		} catch (SVNException e) {
			ILog logger = SubclipseCorePlugin.getDefault().getLog();
			IStatus status = new Status(IStatus.ERROR, SubclipseCorePlugin.PLUGIN_ID,
					"Failed to create the svn client adapter", e); //$NON-NLS-1$
			logger.log(status);
			return null;
		}

		return svnClientAdapter;
	}

}
