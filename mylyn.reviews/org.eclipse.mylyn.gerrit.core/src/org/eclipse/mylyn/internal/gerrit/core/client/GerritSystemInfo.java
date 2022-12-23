/*******************************************************************************
 * Copyright (c) 2010, 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client;

import java.util.List;

import org.osgi.framework.Version;

import com.google.gerrit.reviewdb.Account;
import com.google.gerrit.reviewdb.ContributorAgreement;

/**
 * @author Steffen Pingel
 */
public class GerritSystemInfo {

	private final Version version;

	private final Account account;

	private final List<ContributorAgreement> contributorAgreements;

	public GerritSystemInfo(Version version, List<ContributorAgreement> contributorAgreements, Account account) {
		this.version = version;
		this.contributorAgreements = contributorAgreements;
		this.account = account;
	}

	public Version getVersion() {
		return version;
	}

	public List<ContributorAgreement> getContributorAgreements() {
		return contributorAgreements;
	}

	public String getFullName() {
		return (account != null)
				? getAccountName()
				: org.eclipse.mylyn.internal.gerrit.core.Messages.GerritUtil_Anonymous;
	}

	private String getAccountName() {
		return (account.getFullName() != null) ? account.getFullName() : account.getUserName();
	}

	public GerritCapabilities getCapabilities() {
		return new GerritCapabilities(version);
	}

}
