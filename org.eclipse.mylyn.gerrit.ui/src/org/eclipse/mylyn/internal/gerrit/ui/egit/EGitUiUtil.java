/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.egit;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.egit.ui.Activator;
import org.eclipse.egit.ui.UIPreferences;
import org.eclipse.egit.ui.internal.fetch.FetchOperationUI;
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
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.gerrit.ui.GerritUiPlugin;

import com.google.gerrit.reviewdb.PatchSet;

/**
 * Provides common UI utilities.
 * 
 * @author Steffen Pingel
 */
public class EGitUiUtil {

	private static boolean credentialsProviderWarningLogged;

	public static void setCredentialsProvider(FetchOperationUI op) {
		// TODO EGit1.1 replace with op.setCredentialsProvider(new EGitCredentialsProvider())
		try {
			Class clazz = Class.forName("org.eclipse.egit.ui.internal.credentials.EGitCredentialsProvider");
			Method method = FetchOperationUI.class.getDeclaredMethod("setCredentialsProvider", clazz);
			method.invoke(op, clazz.newInstance());
		} catch (Exception e) {
			if (!credentialsProviderWarningLogged) {
				credentialsProviderWarningLogged = true;
				StatusHandler.log(new Status(
						IStatus.WARNING,
						GerritUiPlugin.PLUGIN_ID,
						"Fetch operation may fail: EGit credentials provider not available. EGit 1.1 or later is required.",
						e));
			}
		}
	}

	public static RevCommit getRevCommit(Repository repository, PatchSet target) throws AmbiguousObjectException,
			IOException, MissingObjectException, IncorrectObjectTypeException {
		ObjectId ref = repository.resolve(target.getRevision().get());
		RevWalk walker = new RevWalk(repository);
		RevCommit targetCommit = walker.parseCommit(ref);
		return targetCommit;
	}

	private static RevCommit fetchRefSpec(IProgressMonitor monitor, Repository repository, RemoteConfig remote,
			RefSpec refSpec) throws URISyntaxException, CoreException, MissingObjectException,
			IncorrectObjectTypeException, IOException {
		List<RefSpec> refSpecs = Collections.singletonList(refSpec);
		FetchOperationUI op = new FetchOperationUI(repository, remote.getURIs().get(0), refSpecs,
				Activator.getDefault().getPreferenceStore().getInt(UIPreferences.REMOTE_CONNECTION_TIMEOUT), false);
		EGitUiUtil.setCredentialsProvider(op);
		FetchResult result = op.execute(monitor);
		ObjectId resultRef = result.getAdvertisedRef(refSpec.getSource()).getObjectId();
		return new RevWalk(repository).parseCommit(resultRef);
	}

	public static RevCommit fetchPatchSet(IProgressMonitor monitor, Repository repository, RemoteConfig remote,
			PatchSet patchSet) throws IOException, CoreException, URISyntaxException {
		try {
			// commit was already fetched
			return EGitUiUtil.getRevCommit(repository, patchSet);
		} catch (MissingObjectException e) {
			// need to getch it
			RefSpec refSpec = new RefSpec(patchSet.getRefName() + ":FETCH_HEAD"); //$NON-NLS-1$
			return fetchRefSpec(monitor, repository, remote, refSpec);
		}
	}

}
