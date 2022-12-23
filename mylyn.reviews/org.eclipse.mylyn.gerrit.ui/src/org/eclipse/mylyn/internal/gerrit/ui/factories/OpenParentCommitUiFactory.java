/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.factories;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.egit.ui.internal.commit.CommitEditor;
import org.eclipse.egit.ui.internal.commit.RepositoryCommit;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.mylyn.internal.gerrit.core.egit.GerritToGitMapping;
import org.eclipse.mylyn.internal.gerrit.ui.GerritUiPlugin;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.ui.spi.factories.IUiContext;
import org.eclipse.swt.widgets.Display;

public class OpenParentCommitUiFactory extends OpenCommitUiFactory {

	private final String commitId;

	public OpenParentCommitUiFactory(IUiContext context, IReviewItemSet set, String commitId) {
		super(context, set);
		this.commitId = commitId;
	}

	@Override
	public void execute() {
		GerritToGitMapping mapping = getGitRepository(true);
		if (mapping != null) {
			final ParentCommitJob job = new ParentCommitJob(Messages.OpenCommitUiFactory_Opening_Commit_Viewer,
					mapping.getRepository(), commitId);
			job.schedule();
			job.addJobChangeListener(new JobChangeAdapter() {
				@Override
				public void done(IJobChangeEvent event) {
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							CommitEditor.openQuiet(job.getCommit());
						}
					});
				}
			});
		}

	}

	public static class ParentCommitJob extends Job {

		private final Repository repository;

		private RepositoryCommit commit;

		private final String commitId;

		public ParentCommitJob(String name, Repository repository, String commitId) {
			super(name);
			this.repository = repository;
			this.commitId = commitId;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			try {
				RevCommit revCommit = getRevCommit();
				commit = new RepositoryCommit(repository, revCommit);
			} catch (Exception e) {
				return new Status(IStatus.ERROR, GerritUiPlugin.PLUGIN_ID, "Patch set retrieval failed", e); //$NON-NLS-1$
			}
			return Status.OK_STATUS;
		}

		public RepositoryCommit getCommit() {
			return commit;
		}

		private RevCommit getRevCommit() throws AmbiguousObjectException, IOException {
			ObjectId ref = repository.resolve(commitId);
			try (RevWalk walker = new RevWalk(repository)) {
				return walker.parseCommit(ref);
			}
		}

	}
}
