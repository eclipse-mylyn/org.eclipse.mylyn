/*******************************************************************************
 * Copyright (c) 2013 Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Miles Parker, Tasktop Technologies - initial API and implementation
 *     Steffen Pingel, Tasktop Technologies - original GerritUtil implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.remote;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.core.model.IReviewsFactory;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.AbstractRemoteEmfFactory;
import org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage;
import org.eclipse.osgi.util.NLS;

import com.google.gerrit.common.data.PatchSetDetail;
import com.google.gerrit.reviewdb.PatchSet;

/**
 * Converts patch set details to review sets. Does not retrive actual patch set content. Does not require a remote
 * invocation, as the neccesary data is collected as part of {@link GerritReviewRemoteFactory} API call.
 * 
 * @author Miles Parker
 * @author Steffen Pingel
 */
public class PatchSetDetailRemoteFactory extends
		AbstractRemoteEmfFactory<IReview, IReviewItemSet, PatchSetDetail, PatchSetDetail, String> {

	public PatchSetDetailRemoteFactory(GerritRemoteFactoryProvider gerritRemoteFactoryProvider) {
		super(gerritRemoteFactoryProvider, ReviewsPackage.Literals.REVIEW__SETS,
				ReviewsPackage.Literals.REVIEW_ITEM__ID);
	}

	@Override
	public PatchSetDetail pull(IReview parent, PatchSetDetail remoteKey, IProgressMonitor monitor) throws CoreException {
		return remoteKey;
	}

	@Override
	public boolean isAsynchronous() {
		return false;
	}

	@Override
	public boolean isPullNeeded(IReview parent, IReviewItemSet object, PatchSetDetail remote) {
		return object != null;
	}

	@Override
	public IReviewItemSet createModel(IReview review, PatchSetDetail patchSetDetail) {
		PatchSet patchSet = patchSetDetail.getPatchSet();
		IReviewItemSet itemSet = IReviewsFactory.INSTANCE.createReviewItemSet();
		itemSet.setName(NLS.bind("Patch Set {0}", patchSet.getPatchSetId()));
		itemSet.setCreationDate(patchSet.getCreatedOn());
		itemSet.setId(patchSet.getPatchSetId() + "");
		itemSet.setReference(patchSet.getRefName());
		itemSet.setRevision(patchSet.getRevision().get());
		review.getSets().add(itemSet);
		return itemSet;
	}

	@Override
	public PatchSetDetail getRemoteKey(PatchSetDetail remoteObject) {
		return remoteObject;
	}

	@Override
	public String getLocalKeyForRemoteObject(PatchSetDetail remoteObject) {
		return remoteObject.getPatchSet().getPatchSetId() + "";
	}

	@Override
	public String getLocalKeyForRemoteKey(PatchSetDetail remoteKey) {
		return getLocalKeyForRemoteObject(remoteKey);
	}
}
