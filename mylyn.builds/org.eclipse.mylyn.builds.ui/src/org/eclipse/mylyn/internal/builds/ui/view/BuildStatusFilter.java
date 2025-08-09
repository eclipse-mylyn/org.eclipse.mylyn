/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.view;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylyn.builds.core.BuildStatus;
import org.eclipse.mylyn.builds.core.IBuildPlan;

/**
 * @author Steffen Pingel
 */
public class BuildStatusFilter extends ViewerFilter {

	private Set<BuildStatus> filtered = Collections.emptySet();

	public BuildStatusFilter() {
	}

	public void addFiltered(BuildStatus status) {
		if (filtered.isEmpty()) {
			filtered = EnumSet.of(status);
		} else {
			filtered.add(status);
		}
	}

	public Set<BuildStatus> getFiltered() {
		return filtered;
	}

	public void removeFiltered(BuildStatus status) {
		filtered.remove(status);
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (!filtered.isEmpty()) {
			if (element instanceof IBuildPlan) {
				return !filtered.contains(((IBuildPlan) element).getStatus());
			}
		}
		return true;
	}

	public void setFiltered(EnumSet<BuildStatus> statuses) {
		filtered = new HashSet<>(statuses);
	}

}
