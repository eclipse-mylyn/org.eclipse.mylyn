/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client;

import com.google.gerrit.reviewdb.Account;

/**
 * @author Steffen Pingel
 */
public class GerritSystemInfo {

	private final Account account;

	public GerritSystemInfo(Account account) {
		this.account = account;
	}

	public String getFullName() {
		return (account != null) ? account.getFullName() : "Anonymous";
	}

}
