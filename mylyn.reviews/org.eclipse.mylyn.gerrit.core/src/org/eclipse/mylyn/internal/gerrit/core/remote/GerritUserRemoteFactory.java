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

import java.util.ArrayList;

import org.apache.commons.lang3.ObjectUtils;
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
public class GerritUserRemoteFactory
		extends AbstractRemoteEmfFactory<IRepository, IUser, String, AccountInfo, Account.Id, String> {

	private final AccountInfoCache cache = new AccountInfoCache(new ArrayList<>());

	public GerritUserRemoteFactory(GerritRemoteFactoryProvider gerritRemoteFactoryProvider) {
		super(gerritRemoteFactoryProvider, ReviewsPackage.Literals.REPOSITORY__USERS, ReviewsPackage.Literals.USER__ID);
	}

	@Override
	public AccountInfo pull(IRepository parent, Id id, IProgressMonitor monitor) throws CoreException {
		return cache.get(id);
	}

	@Override
	public IUser createModel(IRepository repository, AccountInfo info) {
		IUser user = IReviewsFactory.INSTANCE.createUser();
		user.setId(info.getId().toString());
		repository.getUsers().add(user);
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
	public boolean updateModel(IRepository item, IUser user, AccountInfo info) {
		String gerritLabel = GerritUtil.getUserLabel(info);
		boolean changed = false;
		if (!gerritLabel.equals(user.getDisplayName())) {
			user.setDisplayName(gerritLabel);
			changed = true;
		}
		String gerritEmail = info.getPreferredEmail();
		if (!ObjectUtils.equals(gerritEmail, user.getEmail())) {
			user.setEmail(gerritEmail);
			changed = true;
		}
		return changed;
	}

	@Override
	public Id getRemoteKey(AccountInfo info) {
		return info.getId();
	}

	@Override
	public String getLocalKeyForRemoteKey(Id remoteKey) {
		return Integer.toString(remoteKey.get());
	}

	AccountInfoCache getCache() {
		return cache;
	}

}