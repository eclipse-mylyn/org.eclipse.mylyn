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
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.mylar.context.ui.InterestFilter;
import org.eclipse.mylar.internal.context.ui.actions.AbstractAutoApplyMylarAction;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.navigator.CommonNavigator;

/**
 * @author Mik Kersten
 */
public class ApplyMylarToProjectExplorerAction extends AbstractAutoApplyMylarAction {

//	private boolean wasLinkingEnabled = false;
//	
//	@Override
//	public void update(boolean on) {
//		super.update(on);
//		IViewPart view = super.getPartForAction();
//		if (view instanceof CommonNavigator) {
//			CommonNavigator navigator = (CommonNavigator)view;
//			if (on) {
//				wasLinkingEnabled = navigator.isLinkingEnabled();
//				navigator.setLinkingEnabled(true);
//			} else {
//				navigator.setLinkingEnabled(wasLinkingEnabled);
//			}
//		}
//	}
  
	public ApplyMylarToProjectExplorerAction() {
		super(new InterestFilter());
	}

	protected ApplyMylarToProjectExplorerAction(InterestFilter filter) {
		super(filter);
	}

	@Override
	public List<StructuredViewer> getViewers() {
		List<StructuredViewer> viewers = new ArrayList<StructuredViewer>();
		
		IViewPart view = super.getPartForAction();
		if (view instanceof CommonNavigator) {
			CommonNavigator navigator = (CommonNavigator)view;
			viewers.add(navigator.getCommonViewer());
		}
		return viewers;
	}

	public void propertyChange(PropertyChangeEvent event) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public List<Class> getPreservedFilters() {
		return Collections.emptyList();
	}
}
