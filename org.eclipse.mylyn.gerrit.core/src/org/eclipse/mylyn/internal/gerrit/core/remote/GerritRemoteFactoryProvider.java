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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IUser;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfConsumer;
import org.eclipse.mylyn.reviews.edit.remote.ReviewsRemoteEditFactoryProvider;

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

	private final GerritReviewRemoteFactory gerritReviewRemoteFactory = new GerritReviewRemoteFactory(this);

	private final PatchSetDetailRemoteFactory reviewSetFactory = new PatchSetDetailRemoteFactory(this);

	private final PatchSetContentIdRemoteFactory reviewItemSetContentFactory = new PatchSetContentIdRemoteFactory(this);

	private final GerritUserRemoteFactory userFactory = new GerritUserRemoteFactory(this);

	public GerritRemoteFactoryProvider(GerritClient client) {
		this.client = client;
	}

	@Override
	public GerritReviewRemoteFactory getReviewFactory() {
		return gerritReviewRemoteFactory;
	}

	@Override
	public PatchSetDetailRemoteFactory getReviewItemSetFactory() {
		return reviewSetFactory;
	}

	@Override
	public PatchSetContentIdRemoteFactory getReviewItemSetContentFactory() {
		return reviewItemSetContentFactory;
	}

	public GerritUserRemoteFactory getUserFactory(AccountInfoCache cache) {
		userFactory.getCache().merge(cache);
		return userFactory;
	}

	IUser pullUser(IRepository parent, AccountInfoCache cache, Id id, IProgressMonitor monitor)
			throws CoreException {
		if (id != null) {
			final RemoteEmfConsumer<IRepository, IUser, AccountInfo, Id, String> userConsumer = getUserFactory(cache).getConsumerForRemoteKey(
					parent, id);
			if (userConsumer.getModelObject() == null) {
				userConsumer.pull(false, monitor);
			}
			return userConsumer.getModelObject();
		}
		return null;
	}

	IUser createUser(IRepository parent, AccountInfoCache cache, Account.Id id) {
		if (id != null) {
			final RemoteEmfConsumer<IRepository, IUser, AccountInfo, Id, String> userConsumer = getUserFactory(cache).getConsumerForRemoteKey(
					parent, id);
			userConsumer.applyModel(false);
			return userConsumer.getModelObject();
		}
		return null;
	}

	public GerritClient getClient() {
		return client;
	}
}
