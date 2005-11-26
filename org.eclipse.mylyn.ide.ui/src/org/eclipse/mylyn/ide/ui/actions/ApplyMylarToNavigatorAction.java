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

package org.eclipse.mylar.ide.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.mylar.ide.ui.NavigatorRefreshListener;
import org.eclipse.mylar.ui.InterestFilter;
import org.eclipse.mylar.ui.actions.AbstractApplyMylarAction;
import org.eclipse.ui.views.navigator.ResourceNavigator;

/**
 * @author Mik Kersten
 */
public class ApplyMylarToNavigatorAction extends AbstractApplyMylarAction {

	private static ApplyMylarToNavigatorAction INSTANCE;
	
	public ApplyMylarToNavigatorAction() {
		super(new InterestFilter());
		INSTANCE = this;
	}
	
	@Override
	public List<StructuredViewer> getViewers() {
		List<StructuredViewer> viewers = new ArrayList<StructuredViewer>();
		ResourceNavigator navigator = NavigatorRefreshListener.getResourceNavigator();
        if (navigator != null) viewers.add(navigator.getTreeViewer());
		return viewers;
	}

//	@Override
//	public void refreshViewer() {
//		ResourceNavigator navigator = NavigatorRefreshListener.getResourceNavigator();
//        if (navigator != null) navigator.getTreeViewer().refresh();
//	}

	public static ApplyMylarToNavigatorAction getDefault() {
		return INSTANCE;
	}

	public void propertyChange(PropertyChangeEvent event) {
		// TODO Auto-generated method stub
		
	}
}
