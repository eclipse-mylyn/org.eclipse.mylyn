/*******************************************************************************
 * Copyright (c) 2011, 2014 SAP and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Sascha Scholz (SAP) - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.GerritConfigX;

import com.google.gerrit.reviewdb.Account;
import com.google.gerrit.reviewdb.Project;

/**
 * @author Sascha Scholz
 * @author Steffen Pingel
 */
public final class GerritConfiguration {

	private GerritConfigX gerritConfig;

	private List<Project> projects;

	private Account account;

	GerritConfiguration() {
		// no-args constructor needed by gson
	}

	public GerritConfiguration(GerritConfigX gerritConfig, List<Project> projects, Account account) {
		Assert.isNotNull(gerritConfig, "gerritConfig must not be null"); //$NON-NLS-1$
		Assert.isNotNull(projects, "projects must not be null"); //$NON-NLS-1$
		this.gerritConfig = gerritConfig;
		this.projects = projects;
		this.account = account;
	}

	/**
	 * @return the Gerrit configuration instance, never null
	 */
	public GerritConfigX getGerritConfig() {
		return gerritConfig;
	}

	/**
	 * @return the list of visible Gerrit projects, never null
	 */
	public List<Project> getProjects() {
		if (projects != null) {
			return projects;
		} else {
			return Collections.emptyList();
		}
	}

	/**
	 * @return the account instance, null if not authenticated
	 */
	public Account getAccount() {
		return account;
	}

}
