/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.ide.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.mylar.context.ui.InterestFilter;
import org.eclipse.mylar.internal.context.ui.actions.AbstractAutoFocusViewAction;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.views.navigator.ResourceNavigator;

/**
 * @author Mik Kersten
 */
public class FocusNavigatorAction extends AbstractAutoFocusViewAction {

	public FocusNavigatorAction() {
		super(new InterestFilter(), true, true, true);
	}

	@Override
	public List<StructuredViewer> getViewers() {
		List<StructuredViewer> viewers = new ArrayList<StructuredViewer>();
		IViewPart part = super.getPartForAction();
		if (part instanceof ResourceNavigator) {
			viewers.add(((ResourceNavigator)part).getTreeViewer());
		}
		return viewers;
//		ResourceNavigator navigator = IdeUiUtil.getNavigatorFromActivePage();
//		if (navigator != null)
//			viewers.add(navigator.getTreeViewer());
//		return viewers;
	}

	public void propertyChange(PropertyChangeEvent event) {
		// ignore
	}
	
}
