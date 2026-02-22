/*******************************************************************************
 * Copyright (c) 2013, 2014 Ericsson and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Miles Parker, Tasktop Technologies - initial API and implementation
 *     Steffen Pingel, Tasktop Technologies - original GerritUtil implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.remote;

import java.util.Date;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritChange;
import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.core.model.IReviewsFactory;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfConsumer;
import org.eclipse.mylyn.reviews.core.spi.remote.review.ReviewItemSetRemoteFactory;
import org.eclipse.osgi.util.NLS;

import com.google.gerrit.common.data.PatchSetDetail;
import com.google.gerrit.reviewdb.PatchSet;

/**
 * Converts patch set details to review sets. Does not retrive actual patch set content. Does not require a remote invocation, as the
 * neccesary data is collected as part of {@link GerritReviewRemoteFactory} API call.
 *
 * @author Miles Parker
 * @author Steffen Pingel
 */
public class PatchSetDetailRemoteFactory extends ReviewItemSetRemoteFactory<PatchSetDetail, PatchSetDetail> {

	public PatchSetDetailRemoteFactory(GerritRemoteFactoryProvider gerritRemoteFactoryProvider) {
		super(gerritRemoteFactoryProvider);
	}

	@Override
	public PatchSetDetail pull(IReview parent, PatchSetDetail remoteKey, IProgressMonitor monitor)
			throws CoreException {
		return remoteKey;
	}

	@Override
	public boolean isAsynchronous() {
		return false;
	}

	@Override
	public boolean isPullNeeded(IReview parent, IReviewItemSet object, PatchSetDetail remote) {
		return object == null || remote == null;
	}

	@Override
	public IReviewItemSet createModel(IReview review, PatchSetDetail patchSetDetail) {
		PatchSet patchSet = patchSetDetail.getPatchSet();
		IReviewItemSet itemSet = IReviewsFactory.INSTANCE.createReviewItemSet();
		itemSet.setName(NLS.bind(Messages.PatchSetContentRemoteFactory_Patch_Set, patchSet.getPatchSetId()));
		itemSet.setCreationDate(patchSet.getCreatedOn());
		itemSet.setId(Integer.toString(patchSet.getPatchSetId()));
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
		return Integer.toString(remoteObject.getPatchSet().getPatchSetId());
	}

	@Override
	public String getLocalKeyForRemoteKey(PatchSetDetail remoteKey) {
		return getLocalKeyForRemoteObject(remoteKey);
	}

	@Override
	public PatchSetDetail getRemoteObjectForLocalKey(IReview parentObject, String localKey) {
		GerritReviewRemoteFactory reviewFactory = ((GerritRemoteFactoryProvider) getFactoryProvider())
				.getReviewFactory();
		RemoteEmfConsumer<IRepository, IReview, String, GerritChange, String, Date> reviewConsumer = reviewFactory
				.getConsumerForModel(parentObject.getRepository(), parentObject);
		try {
			if (reviewConsumer != null) {
				GerritChange change = reviewConsumer.getRemoteObject();
				if (change != null) {
					for (PatchSetDetail patchSetDetail : change.getPatchSetDetails()) {
						if (patchSetDetail.getPatchSet().getPatchSetId() == Integer.parseInt(localKey)) {
							return patchSetDetail;
						}
					}
				}
			}
		} catch (NumberFormatException e) {
			//ignore;
		} finally {
			if (reviewConsumer != null) {
				reviewConsumer.release();
			}
		}
		return null;
	}
}
