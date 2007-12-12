/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.ide.ui.actions;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.mylyn.internal.ide.ui.MarkerViewLabelProvider;
import org.eclipse.ui.views.markers.internal.TableViewLabelProvider;

/**
 * @author Mik Kersten
 */
public class FocusProblemsListAction extends AbstractFocusMarkerViewAction {

	@Override
	public void update() {
		super.update();
		cachedViewer = null;
		for (StructuredViewer viewer : getViewers()) {
			if (viewer instanceof TableViewer) {
				TableViewer tableViewer = (TableViewer) viewer;
				if (!(tableViewer.getLabelProvider() instanceof MarkerViewLabelProvider)) {
					tableViewer.setLabelProvider(new MarkerViewLabelProvider(
							(TableViewLabelProvider) tableViewer.getLabelProvider()));
				}
			}
		}
	}
}
