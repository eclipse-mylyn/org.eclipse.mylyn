/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.java.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.ui.IDebugView;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.context.ui.AbstractFocusViewAction;
import org.eclipse.mylyn.context.ui.InterestFilter;
import org.eclipse.ui.IViewPart;

/**
 * @author Mik Kersten
 */
public class FocusDebugViewAction extends AbstractFocusViewAction {

	public FocusDebugViewAction() {
		super(new InterestFilter(), true, true, false);
	}

	@Override
	public List<StructuredViewer> getViewers() {
		List<StructuredViewer> viewers = new ArrayList<StructuredViewer>();
		IViewPart viewPart = super.getPartForAction();
		if (viewPart instanceof IDebugView) {
			IDebugView view = (IDebugView) viewPart;
			Viewer viewer = view.getViewer();
			if (viewer instanceof StructuredViewer) {
				viewers.add((StructuredViewer) viewer);
			}
		}
		return viewers;
	}

}
