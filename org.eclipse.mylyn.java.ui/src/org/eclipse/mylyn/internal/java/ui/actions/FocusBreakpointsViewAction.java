package org.eclipse.mylyn.internal.java.ui.actions;

/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.internal.ui.views.breakpoints.BreakpointsView;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.mylyn.ide.ui.AbstractFocusMarkerViewAction;
import org.eclipse.mylyn.internal.java.ui.BreakpointsInterestFilter;
import org.eclipse.ui.IViewPart;

/**
 * @author Mik Kersten
 */
public class FocusBreakpointsViewAction extends AbstractFocusMarkerViewAction {

	public FocusBreakpointsViewAction() {
		super(new BreakpointsInterestFilter(), true, true, false);
	}

	@Override
	public final List<StructuredViewer> getViewers() {
		List<StructuredViewer> viewers = new ArrayList<StructuredViewer>();
		IViewPart viewPart = super.getPartForAction();
		if (viewPart instanceof BreakpointsView) {
			BreakpointsView view = (BreakpointsView) viewPart;
			updateMarkerViewLabelProvider(view.getCheckboxViewer());
			viewers.add(view.getCheckboxViewer());
		}
		return viewers;
	}
}
