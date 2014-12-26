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

package org.eclipse.mylyn.internal.gerrit.core.operations;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritException;

import com.google.common.base.Strings;
import com.google.gerrit.common.data.ChangeDetail;

public class CherryPickRequest extends AbstractRequest<ChangeDetail> {

	private final int patchSetId;

	private final String reviewId;

	private String destination;

	public CherryPickRequest(String reviewId, int patchSetId, String destination) {
		Assert.isNotNull(reviewId);
		Assert.isLegal(!Strings.isNullOrEmpty(destination));
		this.reviewId = reviewId;
		this.patchSetId = patchSetId;
		this.destination = destination;
	}

	public int getPatchSetId() {
		return patchSetId;
	}

	public String getReviewId() {
		return reviewId;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	@Override
	ChangeDetail execute(GerritClient client, IProgressMonitor monitor) throws GerritException {
		return client.cherryPick(getReviewId(), getPatchSetId(), getMessage(), getDestination(), monitor);
	}

	@Override
	public String getOperationName() {
		return Messages.GerritOperation_Cherry_Picking;
	}
}
