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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.mylyn.internal.gerrit.core.GerritUtil;
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
public class UserRemoteFactory extends AbstractRemoteEmfFactory<EObject, IUser, Account.Id, Account.Id, String> {

	private final AccountInfoCache cache;

	public UserRemoteFactory(GerritRemoteFactoryProvider gerritRemoteFactoryProvider, AccountInfoCache cache) {
		super(gerritRemoteFactoryProvider.getService(), null, ReviewsPackage.Literals.USER__ID);
		this.cache = cache;
	}

	@Override
	public Id retrieve(Id remoteKey, IProgressMonitor monitor) throws CoreException {
		return remoteKey;
	}

	@Override
	public IUser create(EObject item, Id id) {
		AccountInfo info = cache.get(id);
		IUser user = IReviewsFactory.INSTANCE.createUser();
		user.setDisplayName(GerritUtil.getUserLabel(info));
		if (id != null) {
			user.setId(Integer.toString(id.get()));
		}
		return user;
	}

	@Override
	public boolean isAsynchronous() {
		return false;
	}

	@Override
	public boolean update(EObject item, IUser object, Id id) {
		return false;
	}
}