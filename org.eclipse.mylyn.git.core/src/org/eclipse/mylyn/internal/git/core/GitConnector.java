/*******************************************************************************
 * Copyright (c) 2011 Research Group for Industrial Software (INSO), Vienna University of Technology
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *     Alvaro Sanchez-Leon - Resolve IResource information in generated artifacts
 *******************************************************************************/

package org.eclipse.mylyn.internal.git.core;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.egit.core.GitProvider;
import org.eclipse.egit.core.RepositoryCache;
import org.eclipse.egit.core.project.RepositoryMapping;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.mylyn.versions.core.Change;
import org.eclipse.mylyn.versions.core.ChangeSet;
import org.eclipse.mylyn.versions.core.ChangeType;
import org.eclipse.mylyn.versions.core.ScmArtifact;
import org.eclipse.mylyn.versions.core.ScmRepository;
import org.eclipse.mylyn.versions.core.ScmUser;
import org.eclipse.mylyn.versions.core.spi.ScmConnector;
import org.eclipse.mylyn.versions.core.spi.ScmResourceArtifact;
import org.eclipse.mylyn.versions.core.spi.ScmResourceUtils;
import org.eclipse.team.core.history.IFileRevision;

/**
 * Git Connector implementation
 * 
 * @author mattk
 */
public class GitConnector extends ScmConnector {

	static String PLUGIN_ID = "org.eclipse.mylyn.git.core"; //$NON-NLS-1$

	@Override
	public String getProviderId() {
		return GitProvider.class.getName();
	}

	@Override
	public ScmArtifact getArtifact(IResource resource) throws CoreException {
		//resolve revision associated to head
		FileRepository fileRepo = getFileRepository(resource);
		String resRepoRelPath = resolveRepoRelativePath(fileRepo, resource);
		String revision = null;
		try {
			revision = resolveObject(fileRepo, resRepoRelPath);
		} catch (Exception e) {
			//Not able to resolve revision
		}

		//Avoiding ScmResourceArtifact, see Bug 341733
		ScmArtifact artifact = null;
		if (revision != null) {
			GitRepository repo = getRepository(resource);
			artifact = new GitArtifact(revision, resRepoRelPath, repo);
			artifact.setProjectName(resource.getProject().getName());
			artifact.setProjectRelativePath(resource.getProjectRelativePath().toPortableString());
		} else {
			artifact = getArtifact(resource, null);
		}

		return artifact;
	}

	@Override
	public ScmArtifact getArtifact(IResource resource, String revision) throws CoreException {
		return new ScmResourceArtifact(this, resource, revision);
	}

	@Override
	public ChangeSet getChangeSet(ScmRepository repository, IFileRevision revision, IProgressMonitor monitor)
			throws CoreException {
		Repository repository2 = ((GitRepository) repository).getRepository();
		RevWalk walk = new RevWalk(repository2);
		try {
			RevCommit commit;
			commit = walk.parseCommit(ObjectId.fromString(revision.getContentIdentifier()));
			TreeWalk treeWalk = new TreeWalk(repository2);
			for (RevCommit p : commit.getParents()) {
				walk.parseHeaders(p);
				walk.parseBody(p);
				treeWalk.addTree(p.getTree());
				//we can compare with one parent only
				break;
			}
			treeWalk.addTree(commit.getTree());
			treeWalk.setRecursive(true);

			List<DiffEntry> entries = DiffEntry.scan(treeWalk);
			List<Change> changes = new ArrayList<Change>();
			File repoDir = repository2.getWorkTree().getAbsoluteFile();

			//define working area repo URI
			IPath repoWorkAreaPath = new Path(repoDir.getAbsolutePath()).addTrailingSeparator();

			for (DiffEntry d : entries) {
				// FIXME - could not work for renaming
				if (!d.getChangeType().equals(org.eclipse.jgit.diff.DiffEntry.ChangeType.RENAME)
						&& d.getOldId().equals(d.getNewId())) {
					continue;
				}

				//Create old and new artifacts with IResource information if available from the current workspace
				ScmArtifact newArtifact = getArtifact(repository, d, false, repoWorkAreaPath);
				ScmArtifact oldArtifact = getArtifact(repository, d, true, repoWorkAreaPath);

				changes.add(new Change(oldArtifact, newArtifact, mapChangeType(d.getChangeType())));
			}

			return changeSet(commit, repository, changes);

		} catch (Exception e) {
			e.printStackTrace();
			throw new CoreException(new Status(IStatus.ERROR, GitConnector.PLUGIN_ID, e.getMessage()));
		}

	}

	/**
	 * @param repository
	 * @param d
	 * @param old
	 *            - true => old entry, false => new entry
	 * @param repoWorkAreaPath
	 * @return
	 */
	private ScmArtifact getArtifact(ScmRepository repository, DiffEntry d, boolean old, IPath repoWorkAreaPath) {
		ScmArtifact artifact = null;
		String id = null;
		String path = null;

		if (old) {
			id = d.getOldId().name();
			path = d.getOldPath();
		} else { /* new */
			id = d.getNewId().name();
			path = d.getNewPath();
		}

		artifact = new GitArtifact(id, path, (GitRepository) repository);

		//Resolve path to workspace IFile
		IFile ifile = null;

		//resolve absolute path to artifact i.e. repo abs path + relative to resource
		IPath absPath = repoWorkAreaPath.append(path);
		URI absURI = URIUtil.toURI(absPath);
		IFile[] files = ScmResourceUtils.getWorkSpaceFiles(absURI);
		if (files != null && files.length > 0) {
			//if more than one project referring to the same file, pick the first one
			ifile = files[0];

			//Fill in the artifact with corresponding IResource information
			artifact.setProjectName(ifile.getProject().getName());
			artifact.setProjectRelativePath(ifile.getProjectRelativePath().toPortableString());
		}

		return artifact;
	}

	private ChangeType mapChangeType(org.eclipse.jgit.diff.DiffEntry.ChangeType change) {
		switch (change) {
		case ADD:
		case COPY:
			return ChangeType.ADDED;
		case DELETE:
			return ChangeType.DELETED;
		case MODIFY:
			return ChangeType.MODIFIED;
		case RENAME:
			return ChangeType.REPLACED;
		}
		return null;
	}

	@Override
	public List<ChangeSet> getChangeSets(ScmRepository repository, IProgressMonitor monitor) throws CoreException {
		List<ChangeSet> changeSets = new ArrayList<ChangeSet>();

		try {
			Git git = new Git(((GitRepository) repository).getRepository());
			Iterable<RevCommit> revs = git.log().call();
			for (RevCommit r : revs) {
				changeSets.add(changeSet(r, repository, new ArrayList<Change>()));
			}
		} catch (NoHeadException e) {
		}

		return changeSets;
	}

	private ChangeSet changeSet(RevCommit r, ScmRepository repository, List<Change> changes) {
		long adjTime = (long) r.getCommitTime() * 1000;
		ChangeSet changeSet = new ChangeSet(getScmUser(r.getCommitterIdent()), new Date(adjTime), r.name(),
				r.getFullMessage(), repository, changes);
		return changeSet;
	}

	private ScmUser getScmUser(PersonIdent person) {
		return new ScmUser(person.getEmailAddress(), person.getName(), person.getEmailAddress());
	}

	@Override
	public List<ScmRepository> getRepositories(IProgressMonitor monitor) throws CoreException {
		ArrayList<ScmRepository> repos = new ArrayList<ScmRepository>();
		for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
			GitRepository repository = this.getRepository(project, monitor);
			if (repository != null) {
				repos.add(repository);
			}
		}
		return repos;
	}

	@Override
	public GitRepository getRepository(IResource resource, IProgressMonitor monitor) throws CoreException {
		return getRepository(resource);
	}

	public GitRepository getRepository(IResource resource) {
		RepositoryMapping mapping = RepositoryMapping.getMapping(resource);
		if (mapping == null) {
			return null;
		}
		return new GitRepository(this, mapping);
	}

	protected RepositoryCache getRepositoryCache() {
		return org.eclipse.egit.core.Activator.getDefault().getRepositoryCache();
	}

	private FileRepository getFileRepository(IResource resource) {
		if (resource == null) {
			return null;
		}
		//Obtain repository path
		RepositoryMapping m = RepositoryMapping.getMapping(resource);
		try {
			return new FileRepository(m.getGitDirAbsolutePath().toFile());
		} catch (IOException e) {
			//Can not resolve id
		}

		return null;
	}

	private String resolveRepoRelativePath(FileRepository repo, IResource resource) {
		if (repo == null || resource == null) {
			return null;
		}
		//Obtain repository path
		IProject project = resource.getProject();
		RepositoryMapping m = RepositoryMapping.getMapping(resource);

		try {
			repo = new FileRepository(m.getGitDirAbsolutePath().toFile());
			File workTree = repo.getWorkTree();
			IPath workTreePath = Path.fromOSString(workTree.getAbsolutePath());
			if (workTreePath.isPrefixOf(project.getLocation())) {
				IPath makeRelativeTo = resource.getLocation().makeRelativeTo(workTreePath);
				String repoRelativePath = makeRelativeTo.toPortableString();
				return repoRelativePath;
			}
		} catch (IOException e) {
			//Can not resolve id
		}

		return null;
	}

	private String resolveObject(FileRepository repo, String repoRelativePath) throws AmbiguousObjectException,
			IOException {
		//Validate
		if (repo == null || repoRelativePath == null) {
			return null;
		}

		ObjectId headCommitId = repo.resolve(Constants.HEAD);
		String id = null;
		if (headCommitId != null) {
			// Not an empty repo
			RevWalk revWalk = new RevWalk(repo);
			RevCommit headCommit = revWalk.parseCommit(headCommitId);
			RevTree headTree = headCommit.getTree();
			TreeWalk resourceInRepo = TreeWalk.forPath(repo, repoRelativePath, headTree);
			if (resourceInRepo != null) {
				ObjectId objId = resourceInRepo.getObjectId(0);
				id = objId.getName();
			}
			revWalk.dispose();
		}

		return id;
	}

}
