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

package org.eclipse.mylyn.internal.commons.ui.team;

import org.eclipse.mylyn.commons.ui.team.RepositoryViewModel;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.part.IShowInTargetList;

/**
 * @author Steffen Pingel
 */
public class RepositoriesView extends CommonNavigator {

	private final RepositoryViewModel root;

	public RepositoriesView() {
		root = new RepositoryViewModel();
	}

	@Override
	protected Object getInitialInput() {
		return root;
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		// FIXME read targets from extension point?
		if (adapter == IShowInTargetList.class) {
			return new IShowInTargetList() {
				public String[] getShowInTargetIds() {
					return new String[] { "org.eclipse.mylyn.builds.navigator.builds" }; //$NON-NLS-1$
				}

			};
		}
		return super.getAdapter(adapter);
	}

}
