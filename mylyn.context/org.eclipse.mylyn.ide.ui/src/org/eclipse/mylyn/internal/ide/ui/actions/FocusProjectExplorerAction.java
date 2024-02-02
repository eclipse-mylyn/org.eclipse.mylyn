/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.ide.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.mylyn.context.ui.InterestFilter;
import org.eclipse.mylyn.resources.ui.FocusCommonNavigatorAction;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.navigator.CommonNavigator;

/**
 * @author Mik Kersten
 */
public class FocusProjectExplorerAction extends FocusCommonNavigatorAction {

	public FocusProjectExplorerAction() {
		super(new InterestFilter(), true, true, true);
	}

	protected FocusProjectExplorerAction(InterestFilter filter) {
		super(filter, true, true, true);
	}

	@Override
	public List<StructuredViewer> getViewers() {
		List<StructuredViewer> viewers = new ArrayList<>();

		IViewPart view = super.getPartForAction();
		if (view instanceof CommonNavigator navigator) {
			viewers.add(navigator.getCommonViewer());
		}
		return viewers;
	}

}
