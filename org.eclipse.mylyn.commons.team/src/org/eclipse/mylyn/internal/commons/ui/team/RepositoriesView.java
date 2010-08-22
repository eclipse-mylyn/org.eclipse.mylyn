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

import org.eclipse.mylyn.commons.repositories.RepositoryCategory;
import org.eclipse.mylyn.internal.provisional.commons.ui.GradientDrawer;
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
		rootCategory = new RepositoryCategory(RepositoryCategory.ID_CATEGORY_ROOT, "Root", 0);
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
			return new IShowInTargetList() {
				public String[] getShowInTargetIds() {
					return new String[] { "org.eclipse.mylyn.builds.navigator.builds" }; //$NON-NLS-1$
				}

			};
		}
		return super.getAdapter(adapter);
	}

}
