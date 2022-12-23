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

import java.util.Date;

import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.AbstractRemoteEmfFactory;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.AbstractRemoteEmfFactoryProvider;
import org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage;

public abstract class ReviewRemoteFactory<RemoteType, RemoteKey>
		extends AbstractRemoteEmfFactory<IRepository, IReview, String, RemoteType, RemoteKey, Date> {

	public ReviewRemoteFactory(AbstractRemoteEmfFactoryProvider<IRepository, IReview> factoryProvider) {
		super(factoryProvider, ReviewsPackage.Literals.REPOSITORY__REVIEWS, ReviewsPackage.Literals.CHANGE__ID);
	}

}
