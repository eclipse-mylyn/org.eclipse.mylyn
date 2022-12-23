/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * Manages a list of plug-in IDs that have been black listed for contributions.
 */
public class ContributorBlackList {

	/**
	 * Plug-in ids of connector extensions that are black listed.
	 */
	private final Set<String> disabledContributors = new HashSet<String>();

	public boolean isDisabled(IConfigurationElement element) {
		return disabledContributors.contains(element.getContributor().getName());
	}

	public Set<String> getDisabledContributors() {
		return Collections.unmodifiableSet(new HashSet<String>(disabledContributors));
	}

	public void disableContributor(String pluginId) {
		disabledContributors.add(pluginId);
	}

	public void merge(ContributorBlackList blackList) {
		disabledContributors.addAll(blackList.getDisabledContributors());
	}

}
