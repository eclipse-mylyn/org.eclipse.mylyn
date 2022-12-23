/*******************************************************************************
 * Copyright (c) 2013 Ericsson and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Miles Parker (Tasktop Technologies) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.core.spi.remote.review;

import org.eclipse.mylyn.reviews.core.model.IRepository;

/**
 * Supports decoupling of Reviews from remote API.
 * 
 * @author Miles Parker
 */
public interface IReviewRemoteFactoryProvider {

	ReviewRemoteFactory<?, ?> getReviewFactory();

	ReviewItemSetRemoteFactory<?, ?> getReviewItemSetFactory();

	ReviewItemSetContentRemoteFactory<?, ?> getReviewItemSetContentFactory();

	IRepository getRoot();
}
