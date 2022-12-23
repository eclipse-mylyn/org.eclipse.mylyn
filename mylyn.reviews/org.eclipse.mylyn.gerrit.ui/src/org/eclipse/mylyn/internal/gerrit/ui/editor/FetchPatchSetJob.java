/*******************************************************************************
 * Copyright (c) 2010, 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Sascha Scholz (SAP) - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.egit.ui.internal.commit.RepositoryCommit;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.mylyn.internal.gerrit.ui.GerritUiPlugin;
import org.eclipse.mylyn.internal.gerrit.ui.egit.EGitUiUtil;

import com.google.gerrit.reviewdb.PatchSet;

public class FetchPatchSetJob extends Job {

	private final Repository repository;

	private final RemoteConfig remote;

	private final PatchSet patchSet;

	private RepositoryCommit commit;

	public FetchPatchSetJob(String name, Repository repository, RemoteConfig remote, PatchSet patchSet) {
		super(name);
		this.repository = repository;
		this.remote = remote;
		this.patchSet = patchSet;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			SubMonitor subMonitor = SubMonitor.convert(monitor);
			RevCommit revCommit = EGitUiUtil.fetchPatchSet(subMonitor, repository, remote, patchSet);
			commit = new RepositoryCommit(repository, revCommit);
		} catch (Exception e) {
			return new Status(IStatus.ERROR, GerritUiPlugin.PLUGIN_ID, "Patch set retrieval failed", e); //$NON-NLS-1$
		}
		return Status.OK_STATUS;
	}

	public RepositoryCommit getCommit() {
		return commit;
	}

}
