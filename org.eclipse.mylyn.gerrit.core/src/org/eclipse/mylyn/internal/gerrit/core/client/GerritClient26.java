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

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.ChangeDetailX;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.PatchSetPublishDetailX;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.ChangeInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.ReviewerInfo;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.osgi.framework.Version;

import com.google.gerrit.reviewdb.PatchSet.Id;

public class GerritClient26 extends GerritClient {

	protected GerritClient26(TaskRepository repository, Version version) {
		super(repository, version);
	}

	/**
	 * Returns the details for a specific review.
	 */
	@Override
	public ChangeDetailX getChangeDetail(int reviewId, IProgressMonitor monitor) throws GerritException {
		ChangeDetailX changeDetail = super.getChangeDetail(reviewId, monitor);
		if (changeDetail.getApprovals() == null) {
			ChangeInfo changeInfo = getChangeInfo(reviewId, monitor);
			changeDetail.setApprovals(changeInfo.convertToApprovalDetails());
			changeDetail.setApprovalTypes(changeInfo.convertToApprovalTypes());
		}
		List<ReviewerInfo> reviewers = listReviewers(reviewId, monitor);
		if (!hasAllReviewers(changeDetail.getAccounts(), reviewers)) {
			merge(changeDetail.getAccounts(), reviewers);
		}
		return changeDetail;
	}

	@Override
	public PatchSetPublishDetailX getPatchSetPublishDetail(Id id, IProgressMonitor monitor) throws GerritException {
		PatchSetPublishDetailX publishDetail = super.getPatchSetPublishDetail(id, monitor);
		if (publishDetail.getLabels() == null) {
			ChangeInfo changeInfo = getChangeInfo(id.getParentKey().get(), monitor);
			publishDetail.setLabels(changeInfo.convertToPermissionLabels());
			if (publishDetail.getGiven() == null) {
				publishDetail.setGiven(changeInfo.convertToPatchSetApprovals(id, getAccount(monitor)));
			}
		}

		return publishDetail;
	}
}
