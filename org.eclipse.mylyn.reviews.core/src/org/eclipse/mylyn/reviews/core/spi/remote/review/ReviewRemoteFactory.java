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

import java.util.Date;

import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.AbstractRemoteEmfFactory;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.AbstractRemoteEmfFactoryProvider;
import org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage;

public abstract class ReviewRemoteFactory<RemoteType, RemoteKey> extends
		AbstractRemoteEmfFactory<IRepository, IReview, String, RemoteType, RemoteKey, Date> {

	public ReviewRemoteFactory(AbstractRemoteEmfFactoryProvider<IRepository, IReview> factoryProvider) {
		super(factoryProvider, ReviewsPackage.Literals.REPOSITORY__REVIEWS, ReviewsPackage.Literals.CHANGE__ID);
	}

}
