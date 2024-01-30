/*******************************************************************************
 * Copyright (c) 2010, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.repositories.ui;

import org.eclipse.mylyn.commons.repositories.core.RepositoryCategory;
import org.eclipse.mylyn.commons.workbench.GradientDrawer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.part.IShowInTargetList;
import org.eclipse.ui.themes.IThemeManager;

/**
 * @author Steffen Pingel
 */
public class RepositoriesView extends CommonNavigator {

	private final RepositoryCategory rootCategory;

	public RepositoriesView() {
		rootCategory = new RepositoryCategory(RepositoryCategory.ID_CATEGORY_ROOT, Messages.RepositoriesView_Root, 0);
	}

	@Override
	protected Object getInitialInput() {
		return rootCategory;
	}

	@Override
	public void createPartControl(Composite aParent) {
		super.createPartControl(aParent);
		getCommonViewer().expandAll();
	}

	@Override
	protected CommonViewer createCommonViewer(Composite aParent) {
		CommonViewer viewer = super.createCommonViewer(aParent);
		IThemeManager themeManager = getSite().getWorkbenchWindow().getWorkbench().getThemeManager();
		new GradientDrawer(themeManager, viewer) {
			@Override
			protected boolean shouldApplyGradient(org.eclipse.swt.widgets.Event event) {
				return event.item.getData() instanceof RepositoryCategory;
			}
		};
		return viewer;
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		// FIXME read targets from extension point?
		if (adapter == IShowInTargetList.class) {
			return (IShowInTargetList) () -> new String[] { "org.eclipse.mylyn.builds.navigator.builds" };
		}
		return super.getAdapter(adapter);
	}

}
