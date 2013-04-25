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

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.gerrit.core.GerritUtil;
import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IReviewsFactory;
import org.eclipse.mylyn.reviews.core.model.IUser;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.AbstractRemoteEmfFactory;
import org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage;

import com.google.gerrit.common.data.AccountInfo;
import com.google.gerrit.common.data.AccountInfoCache;
import com.google.gerrit.reviewdb.Account;
import com.google.gerrit.reviewdb.Account.Id;

/**
 * Manages retrieval of user information from Gerrit API.
 * 
 * @author Miles Parker
 */
public class GerritUserRemoteFactory extends
		AbstractRemoteEmfFactory<IRepository, IUser, AccountInfo, Account.Id, String> {

	private final AccountInfoCache cache = new AccountInfoCache(new ArrayList<AccountInfo>());

	public GerritUserRemoteFactory(GerritRemoteFactoryProvider gerritRemoteFactoryProvider) {
		super(gerritRemoteFactoryProvider, ReviewsPackage.Literals.REPOSITORY__USERS,
				ReviewsPackage.Literals.USER__ID);
	}

	@Override
	public AccountInfo pull(IRepository parent, Id id, IProgressMonitor monitor) throws CoreException {
		return cache.get(id);
	}

	@Override
	public IUser createModel(IRepository group, AccountInfo info) {
		IUser user = IReviewsFactory.INSTANCE.createUser();
		user.setDisplayName(GerritUtil.getUserLabel(info));
		user.setId(info.getId() + "");
		user.setEmail(info.getPreferredEmail());
		group.getUsers().add(user);
		return user;
	}

	@Override
	public boolean isPullNeeded(IRepository parent, IUser user, AccountInfo remote) {
		return true;
	}

	@Override
	public boolean isAsynchronous() {
		return false;
	}

	@Override
	public boolean updateModel(IRepository item, IUser object, AccountInfo info) {
		return false;
	}

	@Override
	public Id getRemoteKey(AccountInfo info) {
		return info.getId();
	}

	@Override
	public String getLocalKeyForRemoteKey(Id remoteKey) {
		return Integer.toString(remoteKey.get());
	}

	public AccountInfoCache getCache() {
		return cache;
	}
}