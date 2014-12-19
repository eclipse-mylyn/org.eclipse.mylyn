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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.ChangeDetailX;
import org.eclipse.mylyn.internal.gerrit.core.client.data.GerritQueryResult;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.osgi.framework.Version;

import com.google.gerrit.common.data.AccountDashboardInfo;
import com.google.gerrit.common.data.ChangeDetail;
import com.google.gerrit.common.data.ReviewerResult;
import com.google.gerrit.common.data.SingleListChangeInfo;
import com.google.gerrit.reviewdb.Account;
import com.google.gerrit.reviewdb.ApprovalCategoryValue.Id;
import com.google.gerrit.reviewdb.Change;
import com.google.gerrit.reviewdb.PatchSet;
import com.google.gwtjsonrpc.client.VoidResult;

public class GerritClient24 extends GerritClient {

	private boolean restQueryAPIEnabled;

	protected GerritClient24(TaskRepository repository, Version version) {
		super(repository, version);
	}

	@Override
	public ChangeDetailX getChangeDetail(int reviewId, IProgressMonitor monitor) throws GerritException {
		ChangeDetailX changeDetail = super.getChangeDetail(reviewId, monitor);
		changeDetail.convertSubmitRecordsToApprovalTypes(getGerritConfig().getApprovalTypes());
		return changeDetail;
	}

	@Override
	public ChangeDetail abandon(String reviewId, int patchSetId, final String message, IProgressMonitor monitor)
			throws GerritException {
		final PatchSet.Id id = new PatchSet.Id(new Change.Id(id(reviewId)), patchSetId);
		return execute(monitor, new Operation<ChangeDetail>() {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {
				getChangeManageService(monitor).abandonChange(id, message, this);
			}
		});
	}

	@Override
	public void publishComments(String reviewId, int patchSetId, final String message, final Set<Id> approvals,
			IProgressMonitor monitor) throws GerritException {
		final PatchSet.Id id = new PatchSet.Id(new Change.Id(id(reviewId)), patchSetId);
		execute(monitor, new Operation<VoidResult>() {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {
				getPatchDetailService(monitor).publishComments(id, message, approvals, this);
			}
		});
	}

	@Override
	public ReviewerResult addReviewers(String reviewId, final List<String> reviewers, IProgressMonitor monitor)
			throws GerritException {
		Assert.isLegal(reviewers != null, "reviewers cannot be null"); //$NON-NLS-1$
		final Change.Id id = new Change.Id(id(reviewId));
		try {
			return execute(monitor, new Operation<ReviewerResult>() {
				@Override
				public void execute(IProgressMonitor monitor) throws GerritException {
					getPatchDetailService(monitor).addReviewers(id, reviewers, this);
				}
			});
		} catch (GerritException e) {
			// Gerrit 2.2
			String message = e.getMessage();
			if (message != null && message.contains("Error parsing request")) { //$NON-NLS-1$
				return execute(monitor, new Operation<ReviewerResult>() {
					@Override
					public void execute(IProgressMonitor monitor) throws GerritException {
						getPatchDetailService(monitor).addReviewers(id, reviewers, false, this);
					}
				});
			} else {
				throw e;
			}
		}
	}

	private List<GerritQueryResult> convert(List<com.google.gerrit.common.data.ChangeInfo> changes) {
		List<GerritQueryResult> results = new ArrayList<GerritQueryResult>(changes.size());
		for (com.google.gerrit.common.data.ChangeInfo changeInfo : changes) {
			GerritQueryResult result = new GerritQueryResult(changeInfo);
			results.add(result);
		}
		return results;
	}

	/**
	 * Returns changes associated with the logged in user. This includes all open, closed and review requests for the
	 * user. On Gerrit 2.4 and earlier closed reviews are not included.
	 */
	@Override
	public List<GerritQueryResult> queryMyReviews(IProgressMonitor monitor) throws GerritException {
		if (!restQueryAPIEnabled) {
			try {
				final Account account = getAccount(monitor);
				AccountDashboardInfo ad = execute(monitor, new Operation<AccountDashboardInfo>() {
					@Override
					public void execute(IProgressMonitor monitor) throws GerritException {
						getChangeListService(monitor).forAccount(account.getId(), this);
					}
				});

				List<com.google.gerrit.common.data.ChangeInfo> allMyChanges = ad.getByOwner();
				allMyChanges.addAll(ad.getForReview());
				allMyChanges.addAll(ad.getClosed());
				return convert(allMyChanges);
			} catch (GerritException e) {
				if (isNoSuchServiceError(e)) {
					restQueryAPIEnabled = true;
				} else {
					throw e;
				}
			}
		}
		return super.queryMyReviews(monitor);
	}

	/**
	 * Sends a query for the changes visible to the caller to the gerrit server with the possibility of adding options
	 * to the query.
	 * 
	 * @param monitor
	 *            A progress monitor
	 * @param queryString
	 *            The specific gerrit change query
	 * @param optionString
	 *            Query options ("&o=" parameter). Only applicable for the REST API, ignored otherwise. May be null.
	 * @return a list of GerritQueryResults built from the parsed query result (ChangeInfo:s)
	 * @throws GerritException
	 */
	@Override
	public List<GerritQueryResult> executeQuery(IProgressMonitor monitor, final String queryString, String optionString)
			throws GerritException {
		if (!restQueryAPIEnabled) {
			try {
				SingleListChangeInfo sl = execute(monitor, new Operation<SingleListChangeInfo>() {
					@Override
					public void execute(IProgressMonitor monitor) throws GerritException {
						getChangeListService(monitor).allQueryNext(queryString, "z", -1, this); //$NON-NLS-1$
					}
				});
				return convert(sl.getChanges());
			} catch (GerritException e) {
				if (isNoSuchServiceError(e)) {
					restQueryAPIEnabled = true;
				} else {
					throw e;
				}
			}
		}
		return super.executeQuery(monitor, queryString);
	}

	@Override
	public ChangeDetail restore(String reviewId, int patchSetId, final String message, IProgressMonitor monitor)
			throws GerritException {
		final PatchSet.Id id = new PatchSet.Id(new Change.Id(id(reviewId)), patchSetId);
		return execute(monitor, new Operation<ChangeDetail>() {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {
				getChangeManageService(monitor).restoreChange(id, message, this);
			}
		});
	}

	@Override
	public ChangeDetail submit(String reviewId, int patchSetId, IProgressMonitor monitor) throws GerritException {
		final PatchSet.Id id = new PatchSet.Id(new Change.Id(id(reviewId)), patchSetId);
		return execute(monitor, new Operation<ChangeDetail>() {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {
				getChangeManageService(monitor).submit(id, this);
			}
		});
	}
}
