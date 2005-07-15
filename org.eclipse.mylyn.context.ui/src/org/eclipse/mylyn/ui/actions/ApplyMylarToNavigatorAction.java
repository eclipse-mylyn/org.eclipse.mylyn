/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.ui.actions;


import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.mylar.ui.InterestFilter;
import org.eclipse.mylar.ui.resources.NavigatorRefreshListener;
import org.eclipse.ui.views.navigator.ResourceNavigator;

/**
 * @author Mik Kersten
 */
public class ApplyMylarToNavigatorAction extends AbstractApplyMylarAction {

	public static ApplyMylarToNavigatorAction INSTANCE;
	
	public ApplyMylarToNavigatorAction() {
		super(new InterestFilter());
		INSTANCE = this;
	}
	
	@Override
	protected StructuredViewer getViewer() {
		ResourceNavigator navigator = NavigatorRefreshListener.getResourceNavigator();
        if (navigator != null) {
			return navigator.getTreeViewer();
		} else {
			return null;
		}
	}

	@Override
	public void refreshViewer() {
		ResourceNavigator navigator = NavigatorRefreshListener.getResourceNavigator();
        if (navigator != null) navigator.getTreeViewer().refresh();
	}

	public static ApplyMylarToNavigatorAction getDefault() {
		return INSTANCE;
	}
}
