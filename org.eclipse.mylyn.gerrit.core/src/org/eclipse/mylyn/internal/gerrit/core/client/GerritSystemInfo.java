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

import java.util.List;

import com.google.gerrit.reviewdb.Account;
import com.google.gerrit.reviewdb.ContributorAgreement;

/**
 * @author Steffen Pingel
 */
public class GerritSystemInfo {

	private final Account account;

	private final List<ContributorAgreement> contributorAgreements;

	public GerritSystemInfo(List<ContributorAgreement> contributorAgreements, Account account) {
		this.contributorAgreements = contributorAgreements;
		this.account = account;
	}

	public List<ContributorAgreement> getContributorAgreements() {
		return contributorAgreements;
	}

	public String getFullName() {
		return (account != null) ? account.getFullName() : "Anonymous";
	}

}
