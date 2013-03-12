/*******************************************************************************
 * Copyright (c) 2013 Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.remote;

import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.reviews.core.spi.remote.JobRemoteService;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.ReviewsRemoteFactoryProvider;

import com.google.gerrit.common.data.AccountInfoCache;

/**
 * Implements a reviews factory provider for Gerrit remote API.
 * 
 * @author Miles Parker
 */
public class GerritRemoteFactoryProvider extends ReviewsRemoteFactoryProvider {

	private final GerritClient client;

	ReviewRemoteFactory reviewRemoteFactory = new ReviewRemoteFactory(this);

	ReviewItemSetRemoteFactory reviewSetFactory = new ReviewItemSetRemoteFactory(this);

	PatchSetContentRemoteFactory reviewItemSetContentFactory = new PatchSetContentRemoteFactory(this);

	public GerritRemoteFactoryProvider(JobRemoteService service, GerritClient client) {
		super(service);
		this.client = client;
	}

	@Override
	public ReviewRemoteFactory getReviewFactory() {
		return reviewRemoteFactory;
	}

	@Override
	public ReviewItemSetRemoteFactory getReviewItemSetFactory() {
		return reviewSetFactory;
	}

	@Override
	public PatchSetContentRemoteFactory getReviewItemSetContentFactory() {
		return reviewItemSetContentFactory;
	}

	public UserRemoteFactory getUserFactory(AccountInfoCache cache) {
		return new UserRemoteFactory(this, cache);
	}

	public GerritClient getClient() {
		return client;
	}
}
