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

package org.eclipse.mylyn.internal.commons.ui.repositories;

import org.eclipse.mylyn.commons.ui.repositories.RepositoryViewModel;
import org.eclipse.ui.navigator.CommonNavigator;

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

}
