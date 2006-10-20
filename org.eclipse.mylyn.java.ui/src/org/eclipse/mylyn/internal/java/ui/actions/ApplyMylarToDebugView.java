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

package org.eclipse.mylar.internal.java.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.internal.ui.views.launch.LaunchView;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.mylar.context.ui.InterestFilter;
import org.eclipse.mylar.internal.context.ui.actions.AbstractApplyMylarAction;
import org.eclipse.ui.IViewPart;

/**
 * @author Mik Kersten
 */
public class ApplyMylarToDebugView extends AbstractApplyMylarAction {

	public ApplyMylarToDebugView() {
		super(new InterestFilter());
	}
	
	@Override
	public List<StructuredViewer> getViewers() {
		List<StructuredViewer> viewers = new ArrayList<StructuredViewer>();
		IViewPart view = super.getPartForAction();
		if (view instanceof LaunchView) {
			LaunchView launchView = (LaunchView)view;
			viewers.add((StructuredViewer)launchView.getViewer());
		}
		return viewers;
	}
	
//	@Override
//	public List<Class> getPreservedFilters() {
//		return Collections.emptyList();
//	}

}
