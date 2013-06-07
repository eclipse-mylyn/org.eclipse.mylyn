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

package org.eclipse.mylyn.reviews.core.spi.remote.review;

import java.util.List;

import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.AbstractRemoteEmfFactory;

/**
 * Supports decoupling of Reviews from remote API.
 * 
 * @author Miles Parker
 */
public interface IReviewRemoteFactoryProvider {

	AbstractRemoteEmfFactory<IRepository, IReview, ?, String, String> getReviewFactory();

	AbstractRemoteEmfFactory<IReview, IReviewItemSet, ?, ?, String> getReviewItemSetFactory();

	AbstractRemoteEmfFactory<IReviewItemSet, List<IFileItem>, ?, String, String> getReviewItemSetContentFactory();

	IRepository getRoot();
}
