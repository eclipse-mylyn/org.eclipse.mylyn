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

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.mylar.internal.context.ui.actions.AbstractApplyMylarAction;
import org.eclipse.mylar.internal.ide.ui.MarkerInterestFilter;
import org.eclipse.mylar.internal.ide.ui.MarkerViewLabelProvider;
import org.eclipse.ui.views.markers.internal.TableViewLabelProvider;

/**
 * @author Mik Kersten
 */
public abstract class AbstractApplyMylartoMarkerViewAction extends AbstractApplyMylarAction {

	protected StructuredViewer cachedViewer = null;
	
	public AbstractApplyMylartoMarkerViewAction() {
		super(new MarkerInterestFilter(), true, true);
	}
	
	/**
	 * HACK: should use platform decorating label provider
	 * @param viewer
	 */
	protected void updateMarkerViewLabelProvider(StructuredViewer viewer) {
		IBaseLabelProvider currentProvider = viewer.getLabelProvider();
		if (currentProvider instanceof TableViewLabelProvider && !(currentProvider instanceof MarkerViewLabelProvider)) {
			viewer.setLabelProvider(new MarkerViewLabelProvider((TableViewLabelProvider) currentProvider));
		}
	}
	
	@Override
	public void update() {
		super.update();
		cachedViewer = null;
		for (StructuredViewer viewer : getViewers()) {
			if (viewer instanceof TableViewer) {
				TableViewer tableViewer = (TableViewer) viewer;
				if (tableViewer != null && !(tableViewer.getLabelProvider() instanceof MarkerViewLabelProvider)) {
					tableViewer.setLabelProvider(new MarkerViewLabelProvider((TableViewLabelProvider) tableViewer
							.getLabelProvider()));
				}
			}
		}
	}
	
}