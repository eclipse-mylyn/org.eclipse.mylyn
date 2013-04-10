/*******************************************************************************
 * Copyright (c) 2013 Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Miles Parker (Tasktop Technologies) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.core.spi.remote.emf;

import java.util.List;

import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewGroup;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.core.model.IReviewsFactory;
import org.eclipse.mylyn.reviews.core.spi.remote.AbstractRemoteFactoryProvider;
import org.eclipse.mylyn.reviews.core.spi.remote.JobRemoteService;

/**
 * Supports decoupling of Reviews from remote API as well as job management.
 * 
 * @author Miles Parker
 */
public abstract class ReviewsRemoteFactoryProvider extends AbstractRemoteFactoryProvider {

	private final IReviewGroup reviews;

	public ReviewsRemoteFactoryProvider(JobRemoteService service) {
		super(service);
		this.reviews = IReviewsFactory.INSTANCE.createReviewGroup();
	}

	public abstract AbstractRemoteEmfFactory<IReviewGroup, IReview, ?, String, String> getReviewFactory();

	public abstract AbstractRemoteEmfFactory<IReview, IReviewItemSet, ?, ?, String> getReviewItemSetFactory();

	public abstract AbstractRemoteEmfFactory<IReviewItemSet, List<IFileItem>, ?, ?, String> getReviewItemSetContentFactory();

	public IReviewGroup getGroup() {
		return reviews;
	}
}
