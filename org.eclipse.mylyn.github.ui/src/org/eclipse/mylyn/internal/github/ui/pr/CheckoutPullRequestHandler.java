/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.mylyn.internal.github.ui.pr;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Collections;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.HandlerEvent;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.egit.core.op.CreateLocalBranchOperation;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.ui.internal.branch.BranchOperationUI;
import org.eclipse.egit.ui.internal.pull.PullOperationUI;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.mylyn.internal.github.core.pr.PullRequestComposite;
import org.eclipse.mylyn.internal.github.core.pr.PullRequestConnector;
import org.eclipse.mylyn.internal.github.core.pr.PullRequestUtils;
import org.eclipse.mylyn.internal.github.ui.GitHubUi;
import org.eclipse.mylyn.internal.github.ui.TaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * Checkout pull request handler
 */
public class CheckoutPullRequestHandler extends TaskDataHandler {

	/**
	 * ID
	 */
	public static final String ID = "org.eclipse.mylyn.github.ui.command.checkoutPullRequest"; //$NON-NLS-1$

	/**
	 * Create checkout pull request handler
	 */
	public CheckoutPullRequestHandler() {
	}

	private RevCommit getBase(Repository repo, PullRequest request)
			throws IOException {
		RevWalk walk = new RevWalk(repo);
		try {
			return walk.parseCommit(repo.resolve(request.getHead().getSha()));
		} catch (IOException e) {
			// Fall back to head if head of pull request cannot be found
			return walk.parseCommit(repo.resolve(Constants.HEAD));
		} finally {
			walk.release();
		}
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		final TaskData data = getTaskData(event);
		if (data == null)
			return null;

		Job job = new Job(MessageFormat.format(
				Messages.CheckoutPullRequestHandler_JobName, data.getTaskId())) {

			protected IStatus run(IProgressMonitor monitor) {
				SubProgressMonitor sub;
				try {
					PullRequestComposite prComp = PullRequestConnector
							.getPullRequest(data);
					if (prComp == null)
						return Status.CANCEL_STATUS;
					PullRequest request = prComp.getRequest();
					Repository repo = PullRequestUtils.getRepository(request);
					if (repo == null)
						return Status.CANCEL_STATUS;
					String branchName = PullRequestUtils.getBranchName(request);
					RevCommit base = getBase(repo, request);

					boolean pull = false;
					Ref branchRef = repo.getRef(branchName);
					if (branchRef == null) {
						monitor.beginTask("", 4); //$NON-NLS-1$
						sub = new SubProgressMonitor(monitor, 1);
						sub.subTask(MessageFormat
								.format(Messages.CheckoutPullRequestHandler_TaskCreateBranch,
										branchName));
						CreateLocalBranchOperation createOp = new CreateLocalBranchOperation(
								repo, branchName, base);
						createOp.execute(sub);
						sub.done();
						monitor.subTask(MessageFormat
								.format(Messages.CheckoutPullRequestHandler_TaskAddRemote,
										request.getHead().getRepository()
												.getOwner()));
						PullRequestUtils.addRemote(repo, request);
						PullRequestUtils.configureTopicBranch(repo, request);
						pull = true;
						branchRef = repo.getRef(branchName);
						monitor.worked(1);
					} else
						monitor.beginTask("", 2); //$NON-NLS-1$
					if (branchRef != null) {
						sub = new SubProgressMonitor(monitor, 1);
						sub.subTask(MessageFormat
								.format(Messages.CheckoutPullRequestHandler_TaskCheckoutBranch,
										branchName));
						BranchOperationUI.checkout(repo, branchRef.getName())
								.run(sub);
						sub.done();
					} else
						monitor.worked(1);
					if (pull) {
						sub = new SubProgressMonitor(monitor, 1);
						sub.subTask(MessageFormat
								.format(Messages.CheckoutPullRequestHandler_TaskPullChanges,
										request.getHead().getLabel()));
						new PullOperationUI(Collections.singleton(repo))
								.execute(sub);
						sub.done();
					} else
						monitor.worked(1);
					monitor.done();
				} catch (IOException e) {
					GitHubUi.logError(e);
				} catch (CoreException e) {
					GitHubUi.logError(e);
				} catch (URISyntaxException e) {
					GitHubUi.logError(e);
				} finally {
					fireHandlerChanged(new HandlerEvent(
							CheckoutPullRequestHandler.this, true, false));
				}
				return Status.OK_STATUS;
			}
		};
		schedule(job, event);
		return null;
	}
}
