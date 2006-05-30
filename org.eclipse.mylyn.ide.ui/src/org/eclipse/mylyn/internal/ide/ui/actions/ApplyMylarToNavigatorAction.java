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
import org.eclipse.mylar.internal.ide.ui.IdeUiUtil;
import org.eclipse.mylar.internal.ui.actions.AbstractApplyMylarAction;
import org.eclipse.mylar.provisional.ui.InterestFilter;
import org.eclipse.ui.views.navigator.ResourceNavigator;

/**
 * @author Mik Kersten
 */
public class ApplyMylarToNavigatorAction extends AbstractApplyMylarAction {

	public ApplyMylarToNavigatorAction() {
		super(new InterestFilter());
	}

	@Override
	public List<StructuredViewer> getViewers() {
		List<StructuredViewer> viewers = new ArrayList<StructuredViewer>();
		ResourceNavigator navigator = IdeUiUtil.getNavigatorFromActivePage();
		if (navigator != null)
			viewers.add(navigator.getTreeViewer());
		return viewers;
	}

	public void propertyChange(PropertyChangeEvent event) {
		// ignore
	}
	
	@Override
	public List<Class> getPreservedFilters() {
		return Collections.emptyList();
	}
}
