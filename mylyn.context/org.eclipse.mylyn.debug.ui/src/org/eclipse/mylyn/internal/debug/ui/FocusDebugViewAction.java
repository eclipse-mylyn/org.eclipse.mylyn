/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.debug.ui;

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
		List<StructuredViewer> viewers = new ArrayList<>();
		IViewPart viewPart = super.getPartForAction();
		if (viewPart instanceof IDebugView view) {
			Viewer viewer = view.getViewer();
			if (viewer instanceof StructuredViewer) {
				viewers.add((StructuredViewer) viewer);
			}
		}
		return viewers;
	}

}
