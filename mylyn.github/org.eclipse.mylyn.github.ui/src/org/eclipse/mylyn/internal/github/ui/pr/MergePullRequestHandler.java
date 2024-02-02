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
import java.text.MessageFormat;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.egit.core.op.MergeOperation;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.ui.internal.branch.BranchOperationUI;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.mylyn.internal.github.core.pr.PullRequestComposite;
import org.eclipse.mylyn.internal.github.core.pr.PullRequestConnector;
import org.eclipse.mylyn.internal.github.core.pr.PullRequestUtils;
import org.eclipse.mylyn.internal.github.ui.GitHubUi;
import org.eclipse.mylyn.internal.github.ui.TaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * Merges a pull request topic branch into the destination branch
 */
public class MergePullRequestHandler extends TaskDataHandler {

	/**
	 * ID
	 */
	public static final String ID = "org.eclipse.mylyn.github.ui.command.mergePullRequest"; //$NON-NLS-1$

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final TaskData data = getTaskData(event);
		if (data == null) {
			return null;
		}
		Job job = new Job(MessageFormat.format(
				Messages.MergePullRequestHandler_MergeJob, data.getTaskId())) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					PullRequestComposite prComp = PullRequestConnector.getPullRequest(data);
					if (prComp == null) {
						return Status.CANCEL_STATUS;
					}
					PullRequest request = prComp.getRequest();
					Repository repo = PullRequestUtils.getRepository(request);
					if (repo == null) {
						return Status.CANCEL_STATUS;
					}
					String target = request.getBase().getRef();
					String branchName = PullRequestUtils.getBranchName(request);
					try {
						Ref sourceRef = repo.findRef(branchName);
						if (sourceRef != null) {
							SubMonitor progress = SubMonitor.convert(monitor, 2);
							if (!PullRequestUtils.isCurrentBranch(target, repo)) {
								progress.subTask(MessageFormat.format(
										Messages.MergePullRequestHandler_TaskCheckout, target));
								BranchOperationUI.checkout(repo, target).run(progress.newChild(1));
							}
							progress.subTask(MessageFormat.format(
									Messages.MergePullRequestHandler_TaskMerge, branchName, target));
							new MergeOperation(repo, branchName).execute(progress.newChild(1));
							executeCallback(event);
						}
					} catch (IOException | CoreException e) {
						GitHubUi.logError(e);
					}
					return Status.OK_STATUS;
				} finally {
					if (monitor != null) {
						monitor.done();
					}
				}
			}
		};
		schedule(job, event);
		return null;
	}
}
