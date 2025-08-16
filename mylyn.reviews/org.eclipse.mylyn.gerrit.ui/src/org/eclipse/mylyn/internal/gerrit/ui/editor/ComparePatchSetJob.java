/*******************************************************************************
 * Copyright (c) 2011, 2014 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.editor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.egit.core.synchronize.dto.GitSynchronizeData;
import org.eclipse.egit.ui.internal.synchronize.GitModelSynchronize;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.mylyn.internal.gerrit.ui.GerritUiPlugin;
import org.eclipse.mylyn.internal.gerrit.ui.egit.EGitUiUtil;

import com.google.gerrit.reviewdb.PatchSet;

public class ComparePatchSetJob extends Job {

	protected final Repository repository;

	protected final PatchSet target;

	private final PatchSet base;

	protected final RemoteConfig remote;

	public ComparePatchSetJob(Repository repository, RemoteConfig remote, PatchSet base, PatchSet target) {
		super(Messages.ComparePatchSetJob_Comparing_Patch_Set);
		this.repository = repository;
		this.remote = remote;
		this.base = base;
		this.target = target;
	}

	public void openSynchronization(String baseRef, String targetRef) throws IOException {
		GitSynchronizeData data = new GitSynchronizeData(repository, baseRef, targetRef, false);
		Set<IProject> projects = data.getProjects();
		GitModelSynchronize.launch(data, projects.toArray(new IResource[projects.size()]));
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			SubMonitor subMonitor = SubMonitor.convert(monitor);
			// fetch target first to retrieve parent commit
			String targetRef = EGitUiUtil.fetchPatchSet(subMonitor, repository, remote, target).getName();
			String baseRef;
			if (base != null) {
				baseRef = EGitUiUtil.fetchPatchSet(subMonitor, repository, remote, base).getName();
			} else {
				baseRef = fetchParent(target, subMonitor);
			}
			openSynchronization(baseRef, targetRef);
		} catch (Exception e) {
			return new Status(IStatus.ERROR, GerritUiPlugin.PLUGIN_ID, "Patch set retrieval failed", e); //$NON-NLS-1$
		}
		return Status.OK_STATUS;
	}

	private String fetchParent(PatchSet patchSet, IProgressMonitor monitor)
			throws URISyntaxException, CoreException, IOException {
		RevCommit targetCommit = EGitUiUtil.getRevCommit(repository, patchSet);
		RevCommit parentCommit = targetCommit.getParents()[0];
		return parentCommit.getName();
	}

}