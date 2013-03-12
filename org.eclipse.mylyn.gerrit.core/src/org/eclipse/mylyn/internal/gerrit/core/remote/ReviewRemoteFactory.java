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
import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritChange;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritException;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.ChangeDetailX;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewGroup;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.core.model.IReviewsFactory;
import org.eclipse.mylyn.reviews.core.model.ITopic;
import org.eclipse.mylyn.reviews.core.model.IUser;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.AbstractRemoteEmfFactory;
import org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage;

import com.google.gerrit.common.data.AccountInfo;
import com.google.gerrit.common.data.PatchSetDetail;
import com.google.gerrit.reviewdb.Change;
import com.google.gerrit.reviewdb.ChangeMessage;
import com.google.gerrit.reviewdb.UserIdentity;

/**
 * Manages retrieval of Review information from Gerrit API. Also creates, adds, and updates contents of review sets for
 * each patch set, but does not retrieve the patch set details or contents.
 * 
 * @author Miles Parker
 * @author Steffen Pingel
 */
public class ReviewRemoteFactory extends AbstractRemoteEmfFactory<IReviewGroup, IReview, GerritChange, String, String> {

	private final GerritRemoteFactoryProvider gerritFactoryProvider;

	public ReviewRemoteFactory(GerritRemoteFactoryProvider gerritRemoteFactoryProvider) {
		super(gerritRemoteFactoryProvider.getService(), ReviewsPackage.Literals.REVIEW_GROUP__REVIEWS,
				ReviewsPackage.Literals.REVIEW__ID);
		this.gerritFactoryProvider = gerritRemoteFactoryProvider;
	}

	@Override
	public GerritChange retrieve(String remoteKey, IProgressMonitor monitor) throws CoreException {
		try {
			return gerritFactoryProvider.getClient().getChange(remoteKey, monitor);
		} catch (GerritException e) {
			throw GerritConnector.toCoreException(null, e);
		}
	}

	@Override
	public IReview create(IReviewGroup parent, GerritChange gerritChange) {
		final ChangeDetailX detail = gerritChange.getChangeDetail();
		Change change = detail.getChange();

		IReview review = IReviewsFactory.INSTANCE.createReview();
		review.setId(detail.getChange().getId().get() + "");
		AccountInfo owner = detail.getAccounts().get(change.getOwner());
		IUser reviewAuthor = gerritFactoryProvider.getUserFactory(detail.getAccounts()).get(review, owner.getId());
		review.setOwner(reviewAuthor);
		review.setCreationDate(change.getCreatedOn());
		return review;
	}

	@Override
	public boolean update(IReviewGroup parent, IReview review, GerritChange gerritChange) {
		ChangeDetailX detail = getRemoteObject(review).getChangeDetail();
		Change change = detail.getChange();

		review.setModificationDate(change.getLastUpdatedOn());

		int oldTopicCount = review.getTopics().size();
		int topicIndex = 0;
		for (ChangeMessage message : detail.getMessages()) {
			if (topicIndex++ < oldTopicCount) {
				continue;
			}
			ITopic topic = review.createTopicComment(null, message.getMessage());
			topic.setDraft(false);
			topic.setCreationDate(message.getWrittenOn());
			for (IComment comment : topic.getComments()) {
				comment.setDraft(false);
			}
			if (message.getAuthor() != null) {
				topic.setAuthor(gerritFactoryProvider.getUserFactory(detail.getAccounts()).get(topic,
						message.getAuthor()));
				//TODO Should be handled by edit framework
				for (IComment comment : topic.getComments()) {
					comment.setAuthor(topic.getAuthor());
				}
			}
		}

		int oldPatchCount = review.getItems().size();
		int patchIndex = 0;
		for (PatchSetDetail patchSetDetail : gerritChange.getPatchSetDetails()) {
			if (patchIndex++ < oldPatchCount) {
				continue;
			}
			ReviewItemSetRemoteFactory itemSetFactory = gerritFactoryProvider.getReviewItemSetFactory();
			IReviewItemSet itemSet = itemSetFactory.get(review, patchSetDetail);
			UserIdentity author = patchSetDetail.getInfo().getAuthor();
			itemSet.setAddedBy(gerritFactoryProvider.getUserFactory(detail.getAccounts()).get(itemSet,
					author.getAccount()));
			UserIdentity committer = patchSetDetail.getInfo().getCommitter();
			itemSet.setCommittedBy(gerritFactoryProvider.getUserFactory(detail.getAccounts()).get(itemSet,
					committer.getAccount()));
			itemSet.setModificationDate(author.getDate());
			review.getItems().add(itemSet);
		}

		return review.getTopics().size() > oldTopicCount || review.getItems().size() > oldPatchCount;
	}
}