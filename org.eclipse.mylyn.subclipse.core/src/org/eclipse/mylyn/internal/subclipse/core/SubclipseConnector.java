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
import java.net.URI;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.versions.core.Change;
import org.eclipse.mylyn.versions.core.ChangeSet;
import org.eclipse.mylyn.versions.core.ChangeType;
import org.eclipse.mylyn.versions.core.ScmArtifact;
import org.eclipse.mylyn.versions.core.ScmRepository;
import org.eclipse.mylyn.versions.core.ScmUser;
import org.eclipse.mylyn.versions.core.spi.ScmConnector;
import org.eclipse.mylyn.versions.core.spi.ScmResourceUtils;
import org.eclipse.team.core.RepositoryProvider;
import org.eclipse.team.core.history.IFileRevision;
import org.tigris.subversion.subclipse.core.ISVNRemoteResource;
import org.tigris.subversion.subclipse.core.ISVNRepositoryLocation;
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

	private final ILog logger = SubclipseCorePlugin.getDefault().getLog();

	/**
	 * allow mapping from local url project folders to works space project
	 */
	private final Map<String, IProject> mapSvnFolderToProject = new HashMap<String, IProject>();

	@Override
	public ScmArtifact getArtifact(IResource resource, String revision) {
		ISVNRemoteResource svnResource = null;

		SubclipseRepository repo = null;
		try {
			svnResource = SVNWorkspaceRoot.getBaseResourceFor(resource);
			repo = (SubclipseRepository) getRepository(resource, null);
		} catch (SVNException e) {
			// TODO: implement a plug-in logger besides the one in the work space
			// e.printStackTrace();
			return null;
		} catch (CoreException e) {
			// TODO implement a plug-in logger besides the one in the work space
			// e.printStackTrace();
			return null;
		}

		SVNRevision SubCRevision = svnResource.getRevision();
		String id = String.valueOf(SubCRevision.getKind());
		SubclipseArtifact artifact = new SubclipseArtifact(id, svnResource.getRepositoryRelativePath(), repo);

		//Assign the resource information
		artifact.setProjectName(resource.getProject().getName());
		artifact.setProjectRelativePath(resource.getProjectRelativePath().toPortableString());
		artifact.setRemoteResource(svnResource);

		return artifact;
	}

	@Override
	public ChangeSet getChangeSet(ScmRepository repository, IFileRevision revision, IProgressMonitor monitor)
			throws CoreException {

		//Cast to Access to the package implementation api
		SubclipseRepository repo = (SubclipseRepository) repository;

		//resolve the revision to SVNRevision
		SVNRevision sRevision = resolveSvnRevision(revision);

		//Get the commit message data for the single revision provided 
		SVNUrl repoLocationUrl = repo.getProjectSVNFolder();
		boolean fetchChangePaths = true;
		ISVNLogMessage[] messages = resolveChangeSets(repo, repoLocationUrl, sRevision, sRevision, fetchChangePaths,
				null);

		if (messages == null) {
			return null;
		}

		//Only one message is expected as the start and end revisions provided are the same
		assert (messages.length == 1);

		ISVNLogMessage isvnLogMessage = messages[0];

		//Prepare the list of changes adapted from ISVNLogMessageChangePath
		ISVNLogMessageChangePath[] changePaths = isvnLogMessage.getChangedPaths();
		List<Change> changes = new ArrayList<Change>();

		//One Change instance created per changePath, needs to resolve base commit for each path
		String id = String.valueOf(isvnLogMessage.getRevision().getNumber());
		for (ISVNLogMessageChangePath isvnLogMessageChangePath : changePaths) {
			//Resolve change type
			ChangeType ctype = mapChangeType(isvnLogMessageChangePath);

			//Initialise target and base artifact
			SubclipseArtifact newArtifact = getArtifact(repo, isvnLogMessageChangePath, id);
			SubclipseArtifact oldArtifact = null;

			//TODO: Implement the resolution of the replaced version
			if (ctype == ChangeType.ADDED || ctype == ChangeType.REPLACED) {
				changes.add(new Change(oldArtifact, newArtifact, ctype));
				continue;
			}

			//Resolve the base artifact
			try {
				oldArtifact = resolveBaseArtifact(repo, newArtifact.getRepositoryURL(), sRevision,
						isvnLogMessageChangePath);
			} catch (MalformedURLException e) {
				logger.log(new Status(IStatus.ERROR, SubclipseCorePlugin.PLUGIN_ID, "Error resolving an artifact url" //$NON-NLS-1$
						+ isvnLogMessageChangePath.getPath(), e));
			}

			if (newArtifact != null || oldArtifact != null) {
				changes.add(new Change(oldArtifact, newArtifact, ctype));
			}

		}

		return changeSet(repo, isvnLogMessage, changes);
	}

	@Override
	public List<ChangeSet> getChangeSets(ScmRepository repository, IProgressMonitor monitor) throws CoreException {
		SubclipseRepository repo = (SubclipseRepository) repository;

		SVNRevision firstRevision = new SVNRevision.Number(1L);

		//TODO: retrieving all revisions per project repository can take really long time, 
		//need to discuss API to narrow down the list e.g. return an iterator
		//For the time being we limit the possibility to review commits within the last 20
		ISVNLogMessage[] messages = resolveChangeSets(repo, repo.getProjectSVNFolder(), SVNRevision.HEAD,
				firstRevision, false, 20L);

		//Convert the messages to ChangeSet
		List<ChangeSet> changeSets = new ArrayList<ChangeSet>(messages.length);
		//No detailed changes provided at this point, but when requesting specific changeSet per revision
		List<Change> changes = new ArrayList<Change>();

		//adapt to ChageSet
		for (ISVNLogMessage message : messages) {
			changeSets.add(changeSet(repo, message, changes));
		}

		//resolve and keep references to all workspace subclipse project URLs
		resolveSubclipseProjects();

		return changeSets;
	}

	@Override
	public String getProviderId() {
		return SVNProviderPlugin.getTypeId();
	}

	protected SubclipseRepository getRepository(ISVNRepositoryLocation location, IProject project) {
		SubclipseRepository repository = new SubclipseRepository(this, location, project);
		return repository;
	}

	@Override
	public ScmRepository getRepository(IResource resource, IProgressMonitor monitor) throws CoreException {
		IProject project = resource.getProject();

		ISVNRepositoryLocation location = SVNWorkspaceRoot.getRepositoryFor(project.getLocation());

		return getRepository(location, project);
	}

	@Override
	public List<ScmRepository> getRepositories(IProgressMonitor monitor) throws CoreException {
		//We consider one repository per project in the workspace in order to limit the resolution of 
		//changesets to the ones associated to the selected project
		if (mapSvnFolderToProject.size() == 0) {
			resolveSubclipseProjects();
		}

		List<ScmRepository> repositories = new ArrayList<ScmRepository>(mapSvnFolderToProject.size());
		for (IProject project : mapSvnFolderToProject.values()) {
			ScmRepository repository = getRepository(project, monitor);
			repositories.add(repository);
		}

		return repositories;
	}

	private SubclipseArtifact getArtifact(SubclipseRepository repo, ISVNLogMessageChangePath changePath, String id) {
		SubclipseArtifact artifact = new SubclipseArtifact(id, changePath.getPath(), repo);

		//ATTEMPT TO RESOLVE IT TO THE WORK SPACE
		String artifactRepoURLStr = null;
		try {
			artifactRepoURLStr = artifact.getRepositoryURL().toString();
		} catch (MalformedURLException e) {
			logger.log(new Status(IStatus.ERROR, SubclipseCorePlugin.PLUGIN_ID, "Unable to resolve URL", e)); //$NON-NLS-1$
			return null;
		}

		if (mapSvnFolderToProject.size() == 0) {
			resolveSubclipseProjects();
		}

		//Resolve an associated project in the work space
		Set<String> projectFolders = mapSvnFolderToProject.keySet();
		for (String projURLStr : projectFolders) {
			if (artifactRepoURLStr.startsWith(projURLStr)) {
				//work space container project possibly found
				//resolve the local file path of the artifact by replacing the project URL string in the artifact URL with the absolute path
				IProject project = mapSvnFolderToProject.get(projURLStr);
				String projectAbsPathStr = project.getLocation().toString();
				String artifactWSLocation = artifactRepoURLStr.replace(projURLStr, projectAbsPathStr);
				//Attempt to resolve the IResource for it
				URI absURI = URIUtil.toURI(new Path(artifactWSLocation));
				IFile ifile = ScmResourceUtils.getWorkSpaceFile(absURI, project);
				if (ifile != null) {
					//Artifact found in the work space
					//Assign the eclipse project name and project relative path to the Artifact 
					artifact.setProjectName(project.getName());
					artifact.setProjectRelativePath(ifile.getProjectRelativePath().toPortableString());
					break;
				}
			}
		}

		return artifact;
	}

	private SubclipseArtifact resolveBaseArtifact(SubclipseRepository repo, SVNUrl pathUrl, SVNRevision sRevision,
			ISVNLogMessageChangePath targetMessageChangePath) throws CoreException {

		SubclipseArtifact oldArtifact = null;

		//Initialise the end revision to 1, although it will be limited to 2 entries to resolve the predecessor
		SVNRevision eRevision = null;
		try {
			eRevision = SVNRevision.getRevision("1"); //$NON-NLS-1$
		} catch (ParseException e) {
			//Should not happen for a constant value 
			e.printStackTrace();
		}

		//Resolve the previous commit where this file took part
		ISVNLogMessage[] filePreviousCommits = null;
		boolean fetchChangePaths = true;
		filePreviousCommits = resolveChangeSets(repo, pathUrl, sRevision, eRevision, fetchChangePaths, Long.valueOf(2L));

		String revisionId = null;
		if (filePreviousCommits != null && filePreviousCommits.length > 1) {
			//Position 0 is for the requested target, position one shall carry the base log message for the file
			ISVNLogMessage aBaseCommitMessage = filePreviousCommits[1];
			revisionId = aBaseCommitMessage.getRevision().toString();
			assert (revisionId != null);

			oldArtifact = getArtifact(repo, targetMessageChangePath, revisionId);

			//The Validation below shall not be needed, kept as comment for troubleshooting in initial version
			//			ISVNLogMessageChangePath[] abaseCommitchangePaths = aBaseCommitMessage.getChangedPaths();
			//			for (ISVNLogMessageChangePath potentialBasePath : abaseCommitchangePaths) {
			//				String potentialPath = potentialBasePath.getPath();
			//				if (potentialPath.equals(targetMessageChangePath.getPath())) {
			//					//The base path was properly resolved in the base commit for this artifact.
			//					oldArtifact = getArtifact(repo, targetMessageChangePath, revisionId);
			//					break;
			//				}
			//			}
		}

		return oldArtifact;
	}

	private SVNRevision resolveSvnRevision(IFileRevision revision) throws CoreException {
		String versionId = revision.getContentIdentifier();
		SVNRevision sRevision = null;
		try {
			sRevision = SVNRevision.getRevision(versionId);
		} catch (ParseException e1) {
			IStatus status = new Status(IStatus.ERROR, SubclipseCorePlugin.PLUGIN_ID, "Unable to resolve VersionId " //$NON-NLS-1$
					+ versionId, e1);
			throw new CoreException(status);
		}

		return sRevision;
	}

	private ChangeType mapChangeType(ISVNLogMessageChangePath isvnLogMessageChangePath) {
		ChangeType changetype = null;
		char action = isvnLogMessageChangePath.getAction();

		switch (action) {
		case 'M':
			changetype = ChangeType.MODIFIED;
			break;
		case 'A':
			changetype = ChangeType.ADDED;
			break;
		case 'D':
			changetype = ChangeType.DELETED;
			break;
		case 'R':
			changetype = ChangeType.REPLACED;
			break;
		}

		return changetype;
	}

	private ISVNLogMessage[] resolveChangeSets(SubclipseRepository repository, SVNUrl urlLocation, SVNRevision start,
			SVNRevision end, boolean fetchChangePath, Long limit) throws CoreException {

		ISVNRepositoryLocation location = repository.getLocation();

		ISVNLogMessage[] messages = null;
		try {
			ISVNClientAdapter adapter = location.getSVNClient();

			if (limit == null || limit < 1) {
				messages = adapter.getLogMessages(urlLocation, start, end, fetchChangePath);
			} else {
				messages = adapter.getLogMessages(urlLocation, start, start, end, false, fetchChangePath, limit);
			}

		} catch (SVNClientException e) {
			StringBuilder sb = new StringBuilder("Unable to resolve ChangeSets for location"); //$NON-NLS-1$
			if (urlLocation != null) {
				sb.append(": " + urlLocation.toString()); //$NON-NLS-1$
			}
			IStatus status = new Status(IStatus.ERROR, SubclipseCorePlugin.PLUGIN_ID, sb.toString(), e);
			throw new CoreException(status);
		}
		return messages;
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

	private void resolveSubclipseProjects() {
		//refresh the list to allow for updates in the work space
		mapSvnFolderToProject.clear();
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (IProject iProject : projects) {
			RepositoryProvider provider = RepositoryProvider.getProvider(iProject);
			if (getProviderId().equals(provider.getID())) {
				//found a subclipse project in the work space
				String folderUrlStr = SVNWorkspaceRoot.getSVNFolderFor(iProject).getUrl().toString();
				mapSvnFolderToProject.put(folderUrlStr, iProject);
			}
		}
	}

}
