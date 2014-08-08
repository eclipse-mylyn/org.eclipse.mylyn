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

package org.eclipse.mylyn.internal.gerrit.core.operations;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritException;

import com.google.gerrit.reviewdb.ApprovalCategoryValue;

/**
 * @author Steffen Pingel
 */
public class PublishRequest extends AbstractRequest<Object> {

	int patchSetId;

	String reviewId;

	Set<ApprovalCategoryValue.Id> approvals;

	public PublishRequest(String reviewId, int patchSetId, Set<ApprovalCategoryValue.Id> approvals) {
		Assert.isNotNull(reviewId);
		this.reviewId = reviewId;
		this.patchSetId = patchSetId;
		this.approvals = Collections.unmodifiableSet(new HashSet<ApprovalCategoryValue.Id>(approvals));
	}

	public int getPatchSetId() {
		return patchSetId;
	}

	public String getReviewId() {
		return reviewId;
	}

	public Set<ApprovalCategoryValue.Id> getApprovals() {
		return approvals;
	}

	@Override
	protected Object execute(GerritClient client, IProgressMonitor monitor) throws GerritException {
		client.publishComments(getReviewId(), getPatchSetId(), getMessage(), getApprovals(), monitor);
		return null;
	}

	@Override
	public String getOperationName() {
		return Messages.GerritOperation_Publishing_Change;
	}

}
