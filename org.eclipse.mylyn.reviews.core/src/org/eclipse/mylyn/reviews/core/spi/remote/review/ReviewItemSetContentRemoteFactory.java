/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.core.spi.remote.review;

import java.util.List;

import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.AbstractRemoteEmfFactory;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.AbstractRemoteEmfFactoryProvider;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfClient;
import org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage;

public abstract class ReviewItemSetContentRemoteFactory<RemoteType, RemoteKey> extends
		AbstractRemoteEmfFactory<IReviewItemSet, List<IFileItem>, String, RemoteType, RemoteKey, Long> {

	public abstract static class Client extends RemoteEmfClient<IReviewItemSet, List<IFileItem>, String, Long> {
	}

	public ReviewItemSetContentRemoteFactory(AbstractRemoteEmfFactoryProvider<IRepository, IReview> factoryProvider) {
		super(factoryProvider, ReviewsPackage.Literals.REVIEW_ITEM_SET__ITEMS, ReviewsPackage.Literals.REVIEW_ITEM__ID);
	}

	@Override
	public Long getModelCurrentValue(IReviewItemSet parentObject, List<IFileItem> items) {
		if (items == null || items.isEmpty()) {
			return null;
		}
		int currentCommentSize = 0;
		int currentDraftSize = 0;
		for (IFileItem item : items) {
			currentCommentSize += item.getAllComments().size();
			currentDraftSize += item.getAllDrafts().size();
		}
		return new Long(currentCommentSize + currentDraftSize * Integer.MAX_VALUE);
	}
}
