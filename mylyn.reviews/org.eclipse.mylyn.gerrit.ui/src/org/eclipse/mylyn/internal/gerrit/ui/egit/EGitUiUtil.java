/*******************************************************************************
 * Copyright (c) 2011, 2013 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.gerrit.ui.egit;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.egit.core.internal.credentials.EGitCredentialsProvider;
import org.eclipse.egit.ui.internal.clone.AbstractGitCloneWizard;
import org.eclipse.egit.ui.internal.clone.GitCloneWizard;
import org.eclipse.egit.ui.internal.fetch.FetchOperationUI;
import org.eclipse.egit.ui.internal.provisional.wizards.GitRepositoryInfo;
import org.eclipse.egit.ui.internal.provisional.wizards.NoRepositoryInfoException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.mylyn.internal.gerrit.core.GerritCorePlugin;
import org.eclipse.mylyn.internal.gerrit.core.GerritUtil;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritConfiguration;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritException;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.swt.widgets.Shell;

import com.google.gerrit.reviewdb.AccountGeneralPreferences.DownloadScheme;
import com.google.gerrit.reviewdb.PatchSet;
import com.google.gerrit.reviewdb.Project;

/**
 * Provides common UI utilities.
 *
 * @author Steffen Pingel
 * @author Sascha Scholz
 */
public class EGitUiUtil {

	public static RevCommit getRevCommit(Repository repository, PatchSet target)
			throws AmbiguousObjectException, IOException, MissingObjectException, IncorrectObjectTypeException {
		ObjectId ref = repository.resolve(target.getRevision().get());
		try (RevWalk walker = new RevWalk(repository)) {
			return walker.parseCommit(ref);
		}
	}

	private static RevCommit fetchRefSpec(IProgressMonitor monitor, Repository repository, RemoteConfig remote,
			RefSpec refSpec) throws URISyntaxException, CoreException, MissingObjectException,
			IncorrectObjectTypeException, IOException {
		List<RefSpec> refSpecs = Collections.singletonList(refSpec);
		FetchOperationUI op = new FetchOperationUI(repository, remote.getURIs().get(0), refSpecs, false);
		op.setCredentialsProvider(new EGitCredentialsProvider());
		FetchResult result = op.execute(monitor);
		ObjectId resultRef = result.getAdvertisedRef(refSpec.getSource()).getObjectId();
		try (RevWalk walker = new RevWalk(repository)) {
			return walker.parseCommit(resultRef);
		}
	}

	public static RevCommit fetchPatchSet(IProgressMonitor monitor, Repository repository, RemoteConfig remote,
			PatchSet patchSet) throws IOException, CoreException, URISyntaxException {
		try {
			// commit was already fetched
			return EGitUiUtil.getRevCommit(repository, patchSet);
		} catch (MissingObjectException e) {
			// need to fetch it
			RefSpec refSpec = new RefSpec(patchSet.getRefName() + ":FETCH_HEAD"); //$NON-NLS-1$
			return fetchRefSpec(monitor, repository, remote, refSpec);
		}
	}

	public static int openCloneRepositoryWizard(Shell shell, final TaskRepository repository, final Project project) {
		AbstractGitCloneWizard cloneWizard = new GitCloneWizard(() -> {
			GitRepositoryInfo gitRepositoryInfo;
			try {
				GerritConfiguration config = GerritCorePlugin.getGerritClient(repository).refreshConfig(null);
				gitRepositoryInfo = new GitRepositoryInfo(getCloneUriForRepo(repository, config, project));
				return gitRepositoryInfo;
			} catch (GerritException e) {

			}
			return null;
		});
		WizardDialog dlg = new WizardDialog(shell, cloneWizard);
		dlg.setHelpAvailable(true);
		return dlg.open();
	}

	private static String getCloneUriForRepo(TaskRepository repository, GerritConfiguration config, Project project)
			throws NoRepositoryInfoException {
		try {
			Map<DownloadScheme, String> cloneUris = GerritUtil.getCloneUris(config, repository, project);
			if (cloneUris.containsKey(DownloadScheme.SSH)) {
				return cloneUris.get(DownloadScheme.SSH);
			}
			if (cloneUris.containsKey(DownloadScheme.HTTP)) {
				return cloneUris.get(DownloadScheme.HTTP);
			}
			for (DownloadScheme scheme : cloneUris.keySet()) {
				if (cloneUris.get(scheme) != null) {
					return cloneUris.get(scheme);
				}
			}
			return null;
		} catch (URISyntaxException e) {
			throw new NoRepositoryInfoException(e.getMessage(), e);
		}

	}

}
