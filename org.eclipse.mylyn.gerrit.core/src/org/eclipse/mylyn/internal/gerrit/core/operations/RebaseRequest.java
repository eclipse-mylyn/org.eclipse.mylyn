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

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritException;

import com.google.gerrit.common.data.ChangeDetail;

/**
 * @author Steffen Pingel
 */
public class RebaseRequest extends AbstractRequest<ChangeDetail> {

	int patchSetId;

	String reviewId;

	public RebaseRequest(String reviewId, int patchSetId) {
		Assert.isNotNull(reviewId);
		this.reviewId = reviewId;
		this.patchSetId = patchSetId;
	}

	public int getPatchSetId() {
		return patchSetId;
	}

	public String getReviewId() {
		return reviewId;
	}

	@Override
	protected ChangeDetail execute(GerritClient client, IProgressMonitor monitor) throws GerritException {
		return client.rebase(getReviewId(), getPatchSetId(), monitor);
	}

}
