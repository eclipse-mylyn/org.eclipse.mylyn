/*******************************************************************************
 * Copyright (c) 2011 Ericsson AB and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Contributors:
 *   Alvaro Sanchez-Leon (Ericsson AB) - Initial API and Implementation
 *   Sebastien Dubois (Ericsson AB) - Improved getArtifact method to resolve remote resources versions
 *******************************************************************************/

package org.eclipse.mylyn.internal.subclipse.core;

import java.net.MalformedURLException;
import java.net.URI;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
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
import org.tigris.subversion.subclipse.core.commands.GetLogsCommand;
import org.tigris.subversion.subclipse.core.history.ILogEntry;
import org.tigris.subversion.subclipse.core.resources.SVNWorkspaceRoot;
import org.tigris.subversion.svnclientadapter.ISVNClientAdapter;
import org.tigris.subversion.svnclientadapter.ISVNLogMessage;
import org.tigris.subversion.svnclientadapter.ISVNLogMessageChangePath;
import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNRevision.Number;
import org.tigris.subversion.svnclientadapter.SVNUrl;

/**
 * @author Alvaro Sanchez-Leon
 */
@SuppressWarnings("restriction")
public class SubclipseConnector extends ScmConnector {

	private final ILog logger = SubclipseCorePlugin.getDefault().getLog();

	private final Map<IProject, SubclipseRepository> mapProjToRepo = new HashMap<IProject, SubclipseRepository>();

	private Integer threadBookNum = new Integer(0);

	/**
	 * allow mapping from local url project folders to works space project
	 */
	private final Map<String, IProject> mapSvnFolderToProject = new HashMap<String, IProject>();

	@Override
	public ScmArtifact getArtifact(IResource resource, String revision) {
		ISVNRemoteResource localResource = null;
		SubclipseRepository repo = null;
		ISVNRemoteResource resolvedResource = null;

		try {
			//First get local resource handle
			localResource = SVNWorkspaceRoot.getBaseResourceFor(resource);

			if (null == revision) {
				//We are looking for the local resource in the workspace
				resolvedResource = localResource;
			} else {
				//We are looking for a previous version.  Get it from the SVN remote repo using SVN logs.
				SVNRevision svnRevision = new SVNRevision.Number(Long.parseLong(revision));
				GetLogsCommand logCmd = new GetLogsCommand(localResource, null, svnRevision, svnRevision, false, 1L,
						null, true);
				try {
					logCmd.run(new NullProgressMonitor());
				} catch (SVNException e) {
					// TODO: implement a plug-in logger besides the one in the work space
					return null;
				}

				final ILogEntry[] entries = logCmd.getLogEntries();
				if (entries.length < 1) {
					//No version found
					return null;
				}
				resolvedResource = entries[entries.length - 1].getRemoteResource();
			}
			if (null == resolvedResource) {
				//No valid resource version found
				return null;
			}
			repo = (SubclipseRepository) getRepository(resource, null);
		} catch (SVNException e) {
			// TODO: implement a plug-in logger besides the one in the work space
			return null;
		} catch (CoreException e) {
			// TODO implement a plug-in logger besides the one in the work space
			return null;
		}

		String id = Long.toString(resolvedResource.getLastChangedRevision().getNumber());
		SubclipseArtifact artifact = new SubclipseArtifact(id, resolvedResource.getRepositoryRelativePath(), repo);

		//Assign the resource information
		artifact.setProjectName(resource.getProject().getName());
		artifact.setProjectRelativePath(resource.getProjectRelativePath().toPortableString());
		artifact.setRemoteResource(resolvedResource);

		return artifact;
	}

	@Override
	public ChangeSet getChangeSet(ScmRepository repository, IFileRevision revision, IProgressMonitor monitor)
			throws CoreException {

		//Cast to Access to the package implementation api
		SubclipseRepository repo = (SubclipseRepository) repository;

		//resolve the revision to SVNRevision
		SVNRevision sRevision = resolveSvnRevision(revision);

		return getChangeSet(repo, sRevision, monitor);
	}

	private ChangeSet getChangeSet(SubclipseRepository repository, SVNRevision sRevision, IProgressMonitor monitor)
			throws CoreException {
		//Get the commit message data for the single revision provided 
		SVNUrl repoLocationUrl = repository.getProjectSVNFolder();
		boolean fetchChangePaths = true;
		ISVNLogMessage[] messages = resolveChangeSets(repository, repoLocationUrl, sRevision, sRevision,
				fetchChangePaths, null);

		if (messages == null) {
			return null;
		}

		//Only one message is expected as the start and end revisions provided are the same
		assert (messages.length == 1);

		ISVNLogMessage isvnLogMessage = messages[0];

		List<Change> changes = buildChanges(repository, isvnLogMessage, true);

		return changeSet(repository, isvnLogMessage, changes);
	}

	/**
	 * @param repository
	 * @param isvnLogMessage
	 * @param withBaseVersions
	 *            - resolve base revisions
	 * @return
	 * @throws CoreException
	 */
	private List<Change> buildChanges(SubclipseRepository repository, ISVNLogMessage isvnLogMessage,
			boolean withBaseVersions) throws CoreException {
		//Prepare the list of changes adapted from ISVNLogMessageChangePath
		ISVNLogMessageChangePath[] changePaths = isvnLogMessage.getChangedPaths();
		List<Change> changes = new ArrayList<Change>();

		Number sRevision = isvnLogMessage.getRevision();
		//One Change instance created per changePath, needs to resolve base commit for each path
		String id = String.valueOf(sRevision.getNumber());
		for (ISVNLogMessageChangePath isvnLogMessageChangePath : changePaths) {
			//Resolve change type
			ChangeType ctype = mapChangeType(isvnLogMessageChangePath);

			//Initialise target and base artifact
			SubclipseArtifact newArtifact = getArtifact(repository, isvnLogMessageChangePath, id);
			SubclipseArtifact oldArtifact = null;

			//TODO: Implement the resolution of the replaced version
			if (ctype == ChangeType.ADDED || ctype == ChangeType.REPLACED) {
				changes.add(new Change(oldArtifact, newArtifact, ctype));
				continue;
			}

			//Deep parsing is only needed when the item has been selected by the user
			if (withBaseVersions) {
				//Resolve the base artifact
				try {
					oldArtifact = resolveBaseArtifact(repository, newArtifact.getRepositoryURL(), sRevision,
							isvnLogMessageChangePath, ctype);
				} catch (MalformedURLException e) {
					logger.log(
							new Status(IStatus.ERROR, SubclipseCorePlugin.PLUGIN_ID, "Error resolving an artifact url" //$NON-NLS-1$
									+ isvnLogMessageChangePath.getPath(), e));
				}

				//Only applicable when resolution of base is requested
				if (ctype == ChangeType.DELETED) {
					//Target element does not contain a valid revision where the deleted element existed. base version does
					newArtifact = null;
				}
			}

			if (newArtifact != null || oldArtifact != null) {
				changes.add(new Change(oldArtifact, newArtifact, ctype));
			}

		}
		return changes;
	}

	@Override
	public List<ChangeSet> getChangeSets(ScmRepository repository, IProgressMonitor monitor) throws CoreException {
		SubclipseRepository repo = (SubclipseRepository) repository;

		SVNRevision firstRevision = new SVNRevision.Number(1L);

		//TODO: retrieving all revisions per project repository can take really long time, 
		//need to discuss API to narrow down the list e.g. return an iterator
		//For the time being we limit the possibility to review commits within the last 20
		ISVNLogMessage[] messages = resolveChangeSets(repo, repo.getProjectSVNFolder(), SVNRevision.HEAD, firstRevision,
				false, 20L);

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
	public Iterator<ChangeSet> getChangeSetsIterator(ScmRepository repository, final IProgressMonitor monitor) {
		//resolve and keep references to all workspace subclipse project URLs
		resolveSubclipseProjects();

		final SubclipseRepository repo = (SubclipseRepository) repository;
		final ChangeSetsIterator niterator = scheduleIterator(monitor, repo);

		return niterator;
	}

	private ChangeSetsIterator scheduleIterator(final IProgressMonitor monitor, final SubclipseRepository repo) {
		final ChangeSetsIterator niterator = new ChangeSetsIterator(repo, monitor);
		Thread monitorThread = new Thread(new Runnable() {
			public void run() {
				//start potentially blocking task Using a blocking queue
				Thread thread = new Thread(niterator);
				String name = repo.getName() + "-" + ++threadBookNum; //$NON-NLS-1$
				thread.setName(name);
				thread.start();
				niterator.setRunnableThread(thread);

				try {
					//monitor cancellation to make sure we unblock 
					//thread to be able to process the cancellation flag
					while (thread.isAlive()) {
						if (monitor.isCanceled()) {
							thread.interrupt();
							break;
						}

						//Ugly. periodic poll for user cancellation
						//TODO: Discuss the definition of mylyn versions implementation of an IProgressMonitor
						//with an instance variable thread 
						//and Interrupt upon cancellation. Other options?
						Thread.sleep(100);
					}

					thread.join();
				} catch (InterruptedException e) {
					thread.interrupt();
				}
			}

		});

		monitorThread.start();
		return niterator;
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

		//check if it's cached
		SubclipseRepository repo = mapProjToRepo.get(project);

		if (repo == null) {
			ISVNRepositoryLocation location = SVNWorkspaceRoot.getRepositoryFor(project.getLocation());
			repo = getRepository(location, project);
			mapProjToRepo.put(project, repo);
		}

		return repo;
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

	private SubclipseArtifact resolveBaseArtifact(SubclipseRepository repo, SVNUrl pathUrl, Number sRevision,
			ISVNLogMessageChangePath targetMessageChangePath, ChangeType ctype) throws CoreException {

		SubclipseArtifact oldArtifact = null;

		if (ctype == ChangeType.DELETED) {
			//Deleted items can only resolve from previous version to deletion, as the item no longer exists
			sRevision = new Number(sRevision.getNumber() - 1);
		}

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
		filePreviousCommits = resolveChangeSets(repo, pathUrl, sRevision, eRevision, fetchChangePaths,
				Long.valueOf(2L));

		String revisionId = null;
		if (filePreviousCommits != null && filePreviousCommits.length > 1) {
			ISVNLogMessage aBaseCommitMessage;
			if (ctype != ChangeType.DELETED) {
				//Position 0 is for the requested target, position one shall carry the base log message for the file
				aBaseCommitMessage = filePreviousCommits[1];
			} else {
				//For deleted items the request started from target version -1
				aBaseCommitMessage = filePreviousCommits[0];
			}
			revisionId = aBaseCommitMessage.getRevision().toString();
			assert (revisionId != null);

			oldArtifact = getArtifact(repo, targetMessageChangePath, revisionId);
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
				messages = adapter.getLogMessages(urlLocation, start, start, end, true, fetchChangePath, limit);
			}

		} catch (SVNClientException e) {

			StringBuilder sb = new StringBuilder("Unable to resolve ChangeSet:" + start.toString() + " for location"); //$NON-NLS-1$ //$NON-NLS-2$
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
			if (provider != null && getProviderId().equals(provider.getID())) {
				//found a subclipse project in the work space
				String folderUrlStr = SVNWorkspaceRoot.getSVNFolderFor(iProject).getUrl().toString();
				mapSvnFolderToProject.put(folderUrlStr, iProject);
			}
		}
	}

	class ChangeSetsIterator implements Iterator<ChangeSet>, Runnable {
		private final int QUEUE_MAX = 40;

		private final Long CHUNKSIZE = 21L;

		private boolean dataProcessingStarted = false;

		private final SVNRevision earliestRevision = new SVNRevision.Number(1L);

		private final ArrayBlockingQueue<ChangeSet> changeSetQueue = new ArrayBlockingQueue<ChangeSet>(QUEUE_MAX);

		private final SubclipseRepository repo;

		private volatile AtomicBoolean done = new AtomicBoolean(false);

		private volatile AtomicBoolean cancelled = new AtomicBoolean(false);

		private Thread thread = null;

		private final IProgressMonitor monitor;

		private final String[] EXIT_ERROR_MESSAGES = new String[] { "connection refused", "Connection timed out" };

		public ChangeSetsIterator(SubclipseRepository repository, IProgressMonitor aMonitor) {
			this.repo = repository;
			this.monitor = aMonitor;
		}

		public void run() {
			ISVNLogMessage[] msgList = null;
			ISVNLogMessage messageBeingProcessed = null;
			Number headRevisionNum = new Number(Long.MAX_VALUE);
			int failedAttemptsCount = 0;

			//while head revision > earliest and ...
			while ((headRevisionNum.compareTo(earliestRevision) == 1) && !cancelled.get() && !done.get()) {
				messageBeingProcessed = null;
				SVNRevision startRevision = null;
				//initialise start Revision
				if (dataProcessingStarted) {
					startRevision = headRevisionNum;
				} else {
					startRevision = SVNRevision.HEAD;
				}

				try {
					//Resolve the changes for the max chunk size 
					msgList = resolveChangeSets(repo, repo.getProjectSVNFolder(), startRevision, earliestRevision, true,
							CHUNKSIZE);

					//adapt to ChangeSet
					int size = msgList.length;
					for (int i = 0; i < size && !cancelled.get() && !done.get(); i++) {
						messageBeingProcessed = msgList[i];

						if (i == CHUNKSIZE - 1) {
							//Don't add last entry to the queue as this will be the start of the next chunk
							continue;
						}

						ChangeSet changeset;
//						ChangeSet changeset = getChangeSet(repo, messageBeingProcessed.getRevision(), monitor);
						List<Change> changes = buildChanges(repo, messageBeingProcessed, false);
						changeset = changeSet(repo, messageBeingProcessed, changes);
						try {
							changeSetQueue.put(changeset);
						} catch (InterruptedException e) {
							//Exit
							cancelled.set(true);
							monitor.done();
							return;
						}

						// UI is done
						if (monitor.isCanceled()) {
							cancelled.set(true);
						}
					}

					//update chunk head revision
					headRevisionNum = updateProcessingHead(msgList[size - 1], headRevisionNum, false);

					//increment worked items
					monitor.worked(1);
					failedAttemptsCount = 0;

					if (size < CHUNKSIZE) {
						//no more items to fetch
						done.set(true);
					}
				} catch (CoreException e) {
					StringBuilder sb = new StringBuilder("Unable to resolve changeSets, ");
					String cause = e.getCause().getMessage();
					for (String errMessage : EXIT_ERROR_MESSAGES) {
						if (cause.contains(errMessage)) {
							//Network connection problems, exit
							cancelled.set(true);
							sb.append(cause);
						}
					}

					if (failedAttemptsCount == 0) {
						e.printStackTrace();
						logger.log(new Status(IStatus.ERROR, SubclipseCorePlugin.PLUGIN_ID, sb.toString(), e));
					}

					failedAttemptsCount++;

					if (!cancelled.get()) {
						//attempt to continue processing next message
						if (headRevisionNum != SVNRevision.HEAD && failedAttemptsCount < 11) {
							headRevisionNum = updateProcessingHead(messageBeingProcessed, headRevisionNum, true);
						} else {
							cancelled.set(true);
							done.set(true);
						}
					}
				}

				// UI is done
				if (monitor.isCanceled()) {
					cancelled.set(true);
				}
			}

			done.set(true);
			monitor.done();
		}

		private Number updateProcessingHead(ISVNLogMessage messageBeingProcessed, Number current, boolean next) {
			long nextRevisionValue;

			if (messageBeingProcessed != null) {
				//Take the latest processed message to start the new request
				nextRevisionValue = messageBeingProcessed.getRevision().getNumber();
			} else {
				//if the last processed message is invalid then use the current chunk start
				nextRevisionValue = current.getNumber();
			}

			if (next) {
				nextRevisionValue--;
			}

			dataProcessingStarted = true;
			return new Number(nextRevisionValue);
		}

		public boolean hasNext() {
			if (changeSetQueue.size() > 0) {
				return true;
			}

			if (cancelled.get() || done.get()) {
				return false;
			}

			return true;
		}

		public ChangeSet next() {
			try {
				return changeSetQueue.poll(60, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}

			return null;
		}

		public void remove() {
			changeSetQueue.poll();
		}

		public Thread getRunnableThread() {
			return thread;
		}

		public void setRunnableThread(Thread thread) {
			this.thread = thread;
		}

		public void setCancelled(boolean cancelled) {
			this.cancelled.set(cancelled);
		}

		public boolean isCancelled() {
			return cancelled.get();
		}
	}

}
