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

package org.eclipse.mylyn.reviews.edit.remote;

import org.eclipse.emf.ecore.EFactory;
import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IReviewsFactory;
import org.eclipse.mylyn.reviews.core.spi.remote.review.IReviewRemoteFactoryProvider;
import org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage;

/**
 * Supports decoupling of Reviews from remote API as well as job management.
 * 
 * @author Miles Parker
 */
public abstract class ReviewsRemoteEditFactoryProvider extends AbstractRemoteEditFactoryProvider<IRepository> implements
		IReviewRemoteFactoryProvider {

	public ReviewsRemoteEditFactoryProvider() {
		super((EFactory) IReviewsFactory.INSTANCE, ReviewsPackage.Literals.REPOSITORY);
	}
}
