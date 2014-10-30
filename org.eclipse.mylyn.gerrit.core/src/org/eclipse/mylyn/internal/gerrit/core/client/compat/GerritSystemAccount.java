/*******************************************************************************
 * Copyright (c) 2014 Ron Peters and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client.compat;

import com.google.gerrit.common.data.AccountInfo;
import com.google.gerrit.reviewdb.Account;

/**
 * AccountInfo representing the Gerrit system.
 */
public final class GerritSystemAccount extends org.eclipse.mylyn.internal.gerrit.core.client.rest.AccountInfo {
	private final AccountInfo gerritSystemAccountInfo;

	/**
	 * AccountInfo representing the Gerrit system
	 */
	public static final GerritSystemAccount GERRIT_SYSTEM = new GerritSystemAccount();

	/**
	 * Name for the Gerrit system
	 */
	public static final String GERRIT_SYSTEM_NAME = "Gerrit Code Review"; //$NON-NLS-1$

	private static final int GERRIT_SYSTEM_ID = -2;

	private GerritSystemAccount() {
		Account gerritSystemAccount = new Account(new Account.Id(GERRIT_SYSTEM_ID));
		gerritSystemAccount.setFullName(GERRIT_SYSTEM_NAME);
		gerritSystemAccount.setUserName(GERRIT_SYSTEM_NAME);
		gerritSystemAccount.setContactFiled();
		gerritSystemAccountInfo = new AccountInfo(gerritSystemAccount);
	}

	@Override
	public String getName() {
		return GerritSystemAccount.GERRIT_SYSTEM_NAME;
	}

	@Override
	public int getId() {
		return GERRIT_SYSTEM_ID;
	}

	/**
	 * @return AccountInfo representing the Gerrit system
	 */
	public AccountInfo getGerritSystemAccountInfo() {
		return gerritSystemAccountInfo;
	}
}