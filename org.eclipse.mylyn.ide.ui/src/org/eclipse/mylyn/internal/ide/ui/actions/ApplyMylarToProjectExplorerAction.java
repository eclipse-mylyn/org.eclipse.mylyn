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
import org.eclipse.mylar.internal.ui.actions.AbstractApplyMylarAction;
import org.eclipse.mylar.provisional.ui.InterestFilter;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.navigator.CommonNavigator;

/**
 * @author Mik Kersten
 */
public class ApplyMylarToProjectExplorerAction extends AbstractApplyMylarAction {

	private static ApplyMylarToProjectExplorerAction INSTANCE;

	private static final String TARGET_ID = "org.eclipse.ui.navigator.ProjectExplorer";
	
	public ApplyMylarToProjectExplorerAction() {
		super(new InterestFilter());
		INSTANCE = this;
	}

	protected ApplyMylarToProjectExplorerAction(InterestFilter filter) {
		super(filter);
	}

	@Override
	public List<StructuredViewer> getViewers() {
		List<StructuredViewer> viewers = new ArrayList<StructuredViewer>();
		
		IViewPart view = super.getView(TARGET_ID);
		if (view instanceof CommonNavigator) {
			CommonNavigator navigator = (CommonNavigator)view;
			viewers.add(navigator.getCommonViewer());
		}
		return viewers;
	}

	public static ApplyMylarToProjectExplorerAction getDefault() {
		return INSTANCE;
	}

	public void propertyChange(PropertyChangeEvent event) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public List<Class> getPreservedFilters() {
		return Collections.emptyList();
	}
}
