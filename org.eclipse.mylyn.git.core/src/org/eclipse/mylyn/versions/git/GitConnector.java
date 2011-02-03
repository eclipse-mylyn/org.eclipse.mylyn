/*******************************************************************************
 * Copyright (c) 2011 Research Group for Industrial Software (INSO), Vienna University of Technology
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.versions.git;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.egit.core.RepositoryCache;
import org.eclipse.egit.core.project.RepositoryMapping;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.mylyn.versions.core.Change;
import org.eclipse.mylyn.versions.core.ChangeSet;
import org.eclipse.mylyn.versions.core.ChangeType;
import org.eclipse.mylyn.versions.core.ScmArtifact;
import org.eclipse.mylyn.versions.core.ScmArtifactInfo;
import org.eclipse.mylyn.versions.core.ScmRepository;
import org.eclipse.mylyn.versions.core.ScmUser;
import org.eclipse.mylyn.versions.core.spi.ScmConnector;
import org.eclipse.team.core.history.IFileRevision;
/**
 * Git Connector implementation
 * @author mattk
 *
 */
public class GitConnector extends ScmConnector {

	static final String ID = "org.eclipse.mylyn.versions.git";

	@Override
	public String getProviderId() {
		return ID;
	}

	@Override
	public ScmArtifact getArtifact(ScmArtifactInfo resource,
			IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScmArtifact getArtifact(IResource resource) throws CoreException {
		GitRepository repository = (GitRepository) this.getRepository(resource,
				new NullProgressMonitor());

		return new GitArtifact(repository.getWorkspaceRevision(resource),
				repository.convertWorkspacePath(resource), repository);
	}

	@Override
	public ChangeSet getChangeset(ScmRepository repository,
			IFileRevision revision, IProgressMonitor monitor)
			throws CoreException {
		Repository repository2 = ((GitRepository) repository).getRepository();
		RevWalk walk = new RevWalk(repository2);
		try {
			RevCommit commit;
			commit = walk.parseCommit(ObjectId.fromString(revision
					.getContentIdentifier()));
			TreeWalk treeWalk = new TreeWalk(repository2);
			for(RevCommit p: commit.getParents()) {
				walk.parseHeaders(p);
				walk.parseBody(p);
				treeWalk.addTree(p.getTree());
			}
			treeWalk.addTree(commit.getTree());
			treeWalk.setRecursive(true);

			List<DiffEntry> entries= DiffEntry.scan(treeWalk);
			List<Change> changes = new ArrayList<Change>();
			for (DiffEntry d : entries) {
				// FIXME - could not work for renaming
				if(!d.getChangeType().equals(org.eclipse.jgit.diff.DiffEntry.ChangeType.RENAME) &&d.getOldId().equals(d.getNewId())) continue;
				
				changes.add(new Change(new GitArtifact(d.getOldId().name(),
						d.getOldPath(), (GitRepository) repository),
						new GitArtifact(d.getNewId().name(), d.getNewPath(),
								(GitRepository) repository),mapChangeType(d.getChangeType())));

			}
			return changeSet(commit, repository, changes);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CoreException(new Status(IStatus.ERROR, GitConnector.ID,
					e.getMessage()));
		}

	}

	private ChangeType mapChangeType(
			org.eclipse.jgit.diff.DiffEntry.ChangeType change) {
		switch(change) {
		case ADD:
		case  COPY:
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
	public List<ChangeSet> getChangeSets(ScmRepository repository,
			IProgressMonitor monitor) throws CoreException {
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

	private ChangeSet changeSet(RevCommit r, ScmRepository repository,
			List<Change> changes) {
		ChangeSet changeSet = new ChangeSet(getScmUser(r.getCommitterIdent()),
				new Date(r.getCommitTime() * 1000), r.name(),
				r.getFullMessage(), repository, changes);
		return changeSet;
	}

	private ScmUser getScmUser(PersonIdent person) {
		return new ScmUser(person.getEmailAddress(), person.getName(),
				person.getEmailAddress());
	}

	@Override
	public List<ScmRepository> getRepositories(IProgressMonitor monitor)
			throws CoreException {
		ArrayList<ScmRepository> repos = new ArrayList<ScmRepository>();
		for (IProject project : ResourcesPlugin.getWorkspace().getRoot()
				.getProjects()) {
			GitRepository repository = (GitRepository) this.getRepository(
					project, monitor);
			if (repository != null)
				repos.add(repository);
		}
		return repos;
	}

	@Override
	public ScmRepository getRepository(IResource resource,
			IProgressMonitor monitor) throws CoreException {
		RepositoryMapping mapping = RepositoryMapping.getMapping(resource);
		if (mapping == null)
			return null;
		return new GitRepository(mapping);
	}

	protected RepositoryCache getRepositoryCache() {
		return org.eclipse.egit.core.Activator.getDefault()
				.getRepositoryCache();
	}

	@Override
	public ScmArtifact getArtifact(IResource resource, String revision)
			throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}
}
