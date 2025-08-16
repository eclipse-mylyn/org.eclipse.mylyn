/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
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
import org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage;

public abstract class ReviewItemSetContentRemoteFactory<RemoteType, RemoteKey>
		extends AbstractRemoteEmfFactory<IReviewItemSet, List<IFileItem>, String, RemoteType, RemoteKey, Long> {

	public ReviewItemSetContentRemoteFactory(AbstractRemoteEmfFactoryProvider<IRepository, IReview> factoryProvider) {
		super(factoryProvider, ReviewsPackage.Literals.REVIEW_ITEM_SET__ITEMS, ReviewsPackage.Literals.REVIEW_ITEM__ID);
	}

}
