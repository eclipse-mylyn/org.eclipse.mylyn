/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.mylyn.internal.github.ui.pr;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.MessageFormat;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.ui.Activator;
import org.eclipse.egit.ui.UIPreferences;
import org.eclipse.egit.ui.internal.fetch.FetchOperationUI;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.mylyn.internal.github.core.pr.PullRequestComposite;
import org.eclipse.mylyn.internal.github.core.pr.PullRequestConnector;
import org.eclipse.mylyn.internal.github.core.pr.PullRequestUtils;
import org.eclipse.mylyn.internal.github.ui.GitHubUi;
import org.eclipse.mylyn.internal.github.ui.TaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * Handler class that fetches changes from a selected pull request
 */
public class FetchPullRequestHandler extends TaskDataHandler {

	/**
	 * ID
	 */
	public static final String ID = "org.eclipse.mylyn.github.ui.command.fetchPullRequest"; //$NON-NLS-1$

	/**
	 * Create handler to fetch changes from a pull request
	 */
	public FetchPullRequestHandler() {

	}

	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final TaskData data = getTaskData(event);
		if (data == null)
			return null;

		Job job = new Job(MessageFormat.format(
				Messages.FetchPullRequestHandler_JobName, data.getTaskId())) {

			protected IStatus run(IProgressMonitor monitor) {
				try {
					PullRequestComposite prComp = PullRequestConnector
							.getPullRequest(data);
					if (prComp == null)
						return Status.CANCEL_STATUS;
					PullRequest request = prComp.getRequest();
					Repository repo = PullRequestUtils.getRepository(request);
					if (repo == null) {
						PullRequestConnectorUi.showNoRepositoryDialog(request);
						return Status.CANCEL_STATUS;
					}
					RemoteConfig remote = PullRequestUtils.addRemote(repo,
							request);
					new FetchOperationUI(repo, remote, Activator.getDefault()
							.getPreferenceStore()
							.getInt(UIPreferences.REMOTE_CONNECTION_TIMEOUT),
							false).execute(monitor);
					executeCallback(event);
				} catch (IOException e) {
					GitHubUi.logError(e);
				} catch (URISyntaxException e) {
					GitHubUi.logError(e);
				} catch (CoreException e) {
					GitHubUi.logError(e);
				}
				return Status.OK_STATUS;
			}
		};
		schedule(job, event);
		return null;
	}
}
