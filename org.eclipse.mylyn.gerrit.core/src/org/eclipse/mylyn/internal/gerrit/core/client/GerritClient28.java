/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.httpclient.HttpMethodBase;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.gerrit.core.GerritCorePlugin;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritHttpClient.ErrorHandler;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.ChangeDetailX;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.BranchInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.BranchInput;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.ChangeInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.ChangeInfo28;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.CherryPickInput;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.CommitInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.RevisionInfo;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.Version;

import com.google.common.base.Function;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.gerrit.common.data.ChangeDetail;
import com.google.gerrit.common.data.ToggleStarRequest;
import com.google.gerrit.reviewdb.Change;
import com.google.gerrit.reviewdb.PatchSet;
import com.google.gerrit.reviewdb.Project;
import com.google.gwtjsonrpc.client.VoidResult;

public class GerritClient28 extends GerritClient27 {

	private final Cache<Project.NameKey, Set<String>> projectBranchMap = CacheBuilder.newBuilder().build();

	protected GerritClient28(TaskRepository repository, Version version) {
		super(repository, version);
	}

	@Override
	public VoidResult setStarred(final String reviewId, final boolean starred, IProgressMonitor monitor)
			throws GerritException {
		final Change.Id id = new Change.Id(id(reviewId));
		final ToggleStarRequest req = new ToggleStarRequest();
		req.toggle(id, starred);
		final String uri = "/a/accounts/self/starred.changes/" + id.get(); //$NON-NLS-1$

		return execute(monitor, new Operation<VoidResult>() {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {

				if (starred) {
					executePutRestRequest(uri, req, ToggleStarRequest.class, createErrorHandler(), monitor);
				} else {
					executeDeleteRestRequest(uri, req, ToggleStarRequest.class, createErrorHandler(), monitor);
				}
			}
		});
	}

	@Override
	public ChangeDetailX getChangeDetail(int reviewId, IProgressMonitor monitor) throws GerritException {
		ChangeDetailX changeDetail = super.getChangeDetail(reviewId, monitor);
		ChangeInfo28 changeInfo = getAdditionalChangeInfo(reviewId, monitor);
		if (changeInfo != null) {
			setRevisionParentCommit(changeInfo, changeDetail);
		}
		return changeDetail;
	}

	protected void setRevisionParentCommit(ChangeInfo changeInfo, ChangeDetailX changeDetail) {
		if (changeInfo.getRevisions() != null) {
			for (Entry<String, RevisionInfo> revisions : changeInfo.getRevisions().entrySet()) {
				RevisionInfo revision = revisions.getValue();
				if (revision.getCommit() != null) {
					CommitInfo commit = revision.getCommit();
					if (commit.getParents().length >= 1) {
						if (changeDetail.getParents() == null) {
							changeDetail.setParents(new HashMap<Integer, CommitInfo[]>());
						}
						changeDetail.getParents().put(revision.getNumber(), commit.getParents());
					}
				}
			}
		}
	}

	/**
	 * Contains different information than the ChangeInfo returned by {@link #getChangeInfo(int, IProgressMonitor)}.
	 * Introduced in 2.8.
	 */
	protected ChangeInfo28 getAdditionalChangeInfo(int reviewId, IProgressMonitor monitor) {
		ChangeInfo28 changeInfo28 = null;
		try {
			changeInfo28 = executeGetRestRequest("/changes/" + Integer.toString(reviewId) //$NON-NLS-1$
					+ "/?o=ALL_REVISIONS&o=CURRENT_ACTIONS&o=ALL_COMMITS", ChangeInfo28.class, monitor); //$NON-NLS-1$
		} catch (GerritException e) {
			StatusHandler.log(new Status(IStatus.ERROR, GerritCorePlugin.PLUGIN_ID, e.getMessage(), e));
		}
		return changeInfo28;
	}

	@Override
	public ChangeDetail cherryPick(String reviewId, int patchSetId, final String message, final String destBranch,
			IProgressMonitor monitor) throws GerritException {
		final PatchSet.Id id = new PatchSet.Id(new Change.Id(id(reviewId)), patchSetId);
		String url = "/changes/" + id.getParentKey() + "/revisions/" + id.get() + "/cherrypick"; //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		CherryPickInput input = new CherryPickInput(message, destBranch);
		ChangeInfo result = executePostRestRequest(url, input, ChangeInfo.class, new ErrorHandler() {
			@Override
			public void handleError(HttpMethodBase method) throws GerritException {
				String errorMsg = getResponseBodyAsString(method);
				if (isNotPermitted(method, errorMsg)) {
					errorMsg = NLS.bind("Cannot cherry pick: {0}", errorMsg); //$NON-NLS-1$
				} else if (isConflict(method)) {
					errorMsg = NLS.bind("Request Conflict: {0}", errorMsg); //$NON-NLS-1$
				} else if (isBadRequest(method)) {
					errorMsg = NLS.bind("Bad Request: {0}", errorMsg); //$NON-NLS-1$
				}
				throw new GerritException(errorMsg);
			}

			private String getResponseBodyAsString(HttpMethodBase method) {
				try {
					return method.getResponseBodyAsString();
				} catch (IOException e) {
					return null;
				}
			}

			private boolean isNotPermitted(HttpMethodBase method, String msg) {
				return method.getStatusCode() == HttpURLConnection.HTTP_FORBIDDEN
						&& msg.toLowerCase().startsWith("cherry pick not permitted"); //$NON-NLS-1$
			}

			private boolean isConflict(HttpMethodBase method) {
				return method.getStatusCode() == HttpURLConnection.HTTP_CONFLICT;
			}

			private boolean isBadRequest(HttpMethodBase method) {
				return method.getStatusCode() == HttpURLConnection.HTTP_BAD_REQUEST;
			}
		}, monitor);

		return getChangeDetail(result.getNumber(), monitor);
	}

	@Override
	public GerritConfiguration refreshConfigOnce(Project.NameKey project, IProgressMonitor monitor)
			throws GerritException {
		GerritConfiguration config = super.refreshConfigOnce(project, monitor);
		// the order is important here: calling super first ensures that we won't fetch the branches twice
		if (project != null && getCachedBranches(project) == null) {
			cacheBranches(project, monitor);
		}
		return config;
	}

	@Override
	public GerritConfiguration refreshConfig(IProgressMonitor monitor) throws GerritException {
		refreshAllCachedProjectBranches(monitor);
		return super.refreshConfig(monitor);
	}

	private void refreshAllCachedProjectBranches(IProgressMonitor monitor) throws GerritException {
		Set<Project.NameKey> projects = projectBranchMap.asMap().keySet();
		for (Project.NameKey project : projects) {
			cacheBranches(project, monitor);
		}
	}

	private void cacheBranches(Project.NameKey project, IProgressMonitor monitor) throws GerritException {
		Set<String> branchNames = getBranchNames(project, monitor);
		projectBranchMap.put(project, branchNames);
	}

	private ImmutableSet<String> getBranchNames(Project.NameKey project, IProgressMonitor monitor)
			throws GerritException {
		return FluentIterable.from(Arrays.asList(getRemoteProjectBranches(project.get(), monitor)))
				.transform(new Function<BranchInfo, String>() {
					public String apply(BranchInfo input) {
						return input.getRef();
					}
				})
				.toSet();
	}

	@Override
	public boolean supportsBranchCreation() throws GerritException {
		return true;
	}

	@Override
	public BranchInfo[] getRemoteProjectBranches(String projectName, IProgressMonitor monitor) throws GerritException {
		String url = getProjectBranchesUrl(projectName);
		return executeGetRestRequest(url, BranchInfo[].class, monitor);
	}

	@Override
	public void createRemoteBranch(String projectName, String branchName, String revision, IProgressMonitor monitor)
			throws GerritException {
		String url = getProjectBranchesUrl(projectName) + branchName;
		BranchInput input = new BranchInput(branchName, revision);
		executePutRestRequest(url, input, BranchInput.class, createErrorHandler(), monitor);
	}

	@Override
	public void deleteRemoteBranch(String projectName, String branchName, String revision, IProgressMonitor monitor)
			throws GerritException {
		String url = getProjectBranchesUrl(projectName) + branchName;
		BranchInput input = new BranchInput(branchName, revision);
		executeDeleteRestRequest(url, input, BranchInput.class, createErrorHandler(), monitor);
	}

	@Override
	public Set<String> getCachedBranches(Project.NameKey project) {
		return projectBranchMap.getIfPresent(project);
	}

	private String getProjectBranchesUrl(String projectName) {
		return "/projects/" + projectName + "/branches/"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void clearCachedBranches(Project.NameKey project) {
		projectBranchMap.invalidate(project);
	}

}
