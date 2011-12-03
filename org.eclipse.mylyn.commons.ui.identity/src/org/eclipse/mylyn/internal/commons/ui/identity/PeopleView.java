/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.ui.identity;

import org.eclipse.mylyn.commons.workbench.GradientDrawer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.themes.IThemeManager;

/**
 * @author Steffen Pingel
 */
public class PeopleView extends CommonNavigator {

	private final PeopleCategory rootCategory;

	public PeopleView() {
		rootCategory = new PeopleCategory();
	}

	@Override
	public void createPartControl(Composite aParent) {
		super.createPartControl(aParent);
		// TODO e3.5 replace by overriding getInitialInput()
		getCommonViewer().setInput(rootCategory);
		getCommonViewer().expandAll();
	}

	@Override
	protected CommonViewer createCommonViewer(Composite aParent) {
		CommonViewer viewer = super.createCommonViewer(aParent);
		IThemeManager themeManager = getSite().getWorkbenchWindow().getWorkbench().getThemeManager();
		new GradientDrawer(themeManager, viewer) {
			@Override
			protected boolean shouldApplyGradient(org.eclipse.swt.widgets.Event event) {
				return event.item.getData() instanceof PeopleCategory;
			}
		};
		return viewer;
	}

}
