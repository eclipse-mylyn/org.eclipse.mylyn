/*******************************************************************************
 * Copyright (c) 2013 Ericsson and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.remote;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IUser;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfConsumer;
import org.eclipse.mylyn.reviews.spi.edit.remote.review.ReviewsRemoteEditFactoryProvider;

import com.google.gerrit.common.data.AccountInfo;
import com.google.gerrit.common.data.AccountInfoCache;
import com.google.gerrit.reviewdb.Account;
import com.google.gerrit.reviewdb.Account.Id;

/**
 * Implements a reviews factory provider for Gerrit remote API.
 * 
 * @author Miles Parker
 */
public class GerritRemoteFactoryProvider extends ReviewsRemoteEditFactoryProvider {

	private final GerritClient client;

	private final GerritReviewRemoteFactory reviewFactory = new GerritReviewRemoteFactory(this);

	private final PatchSetDetailRemoteFactory reviewItemSetFactory = new PatchSetDetailRemoteFactory(this);

	private final PatchSetContentIdRemoteFactory reviewItemSetContentFactory = new PatchSetContentIdRemoteFactory(this);

	private final GerritUserRemoteFactory userFactory = new GerritUserRemoteFactory(this);

	public GerritRemoteFactoryProvider(GerritClient client) {
		super(client.getRepository());
		this.client = client;
	}

	@Override
	public GerritReviewRemoteFactory getReviewFactory() {
		return reviewFactory;
	}

	@Override
	public PatchSetDetailRemoteFactory getReviewItemSetFactory() {
		return reviewItemSetFactory;
	}

	@Override
	public PatchSetContentIdRemoteFactory getReviewItemSetContentFactory() {
		return reviewItemSetContentFactory;
	}

	public GerritUserRemoteFactory getUserFactory(AccountInfoCache cache) {
		userFactory.getCache().merge(cache);
		return userFactory;
	}

	void pullUser(final IRepository parent, final AccountInfoCache cache, final Id id, final IProgressMonitor monitor)
			throws CoreException {
		modelExec(() -> {
			if (id != null) {
				final RemoteEmfConsumer<IRepository, IUser, String, AccountInfo, Id, String> userConsumer = getUserFactory(
						cache).getConsumerForRemoteKey(parent, id);
				try {
					userConsumer.pull(false, monitor);
				} catch (CoreException e) {
					StatusHandler.log(e.getStatus());
				}
			}
		}, true);
	}

	IUser createUser(IRepository parent, AccountInfoCache cache, Account.Id id) {
		if (id != null) {
			final RemoteEmfConsumer<IRepository, IUser, String, AccountInfo, Id, String> userConsumer = getUserFactory(
					cache).getConsumerForRemoteKey(parent, id);
			userConsumer.applyModel(false);
			return userConsumer.getModelObject();
		}
		return null;
	}

	public GerritClient getClient() {
		return client;
	}
}
