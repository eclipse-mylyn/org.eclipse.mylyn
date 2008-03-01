/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.ide.ui.actions;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.mylyn.context.ui.AbstractFocusViewAction;
import org.eclipse.mylyn.internal.ide.ui.IdeUiBridgePlugin;
import org.eclipse.mylyn.internal.ide.ui.MarkerInterestFilter;
import org.eclipse.mylyn.internal.ide.ui.MarkerViewLabelProvider;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.internal.views.markers.ExtendedMarkersView;
import org.eclipse.ui.internal.views.markers.MarkersTreeViewer;
import org.eclipse.ui.views.markers.internal.TableViewLabelProvider;

/**
 * @author Mik Kersten
 */
public abstract class AbstractFocusMarkerViewAction extends AbstractFocusViewAction {

	protected StructuredViewer cachedViewer = null;

	public AbstractFocusMarkerViewAction() {
		super(new MarkerInterestFilter(), true, true, false);
	}

	/**
	 * HACK: should use platform decorating label provider
	 * 
	 * @param viewer
	 */
	protected void updateMarkerViewLabelProvider(StructuredViewer viewer) {
		IBaseLabelProvider currentProvider = viewer.getLabelProvider();
		if (currentProvider instanceof TableViewLabelProvider && !(currentProvider instanceof MarkerViewLabelProvider)) {
			viewer.setLabelProvider(new MarkerViewLabelProvider((TableViewLabelProvider) currentProvider));
		}
	}

	/**
	 * HACK: changing accessibility
	 */
	@Override
	public final List<StructuredViewer> getViewers() {
		List<StructuredViewer> viewers = new ArrayList<StructuredViewer>();
		if (cachedViewer == null) {
			try {
				IViewPart viewPart = super.getPartForAction();
				if (viewPart != null) {
					// NOTE: following code is Eclipse 3.4 specific
					Class<?> clazz = ExtendedMarkersView.class;
					Field field = clazz.getDeclaredField("viewer");
					field.setAccessible(true);
					cachedViewer = (MarkersTreeViewer) field.get(viewPart);
					if (!cachedViewer.getControl().isDisposed()) {
						updateMarkerViewLabelProvider(cachedViewer);
					}
				}
			} catch (Exception e) {
				StatusHandler.log(new Status(IStatus.ERROR, IdeUiBridgePlugin.PLUGIN_ID,
						"Could not get problems view viewer", e));
			}
		}
		if (cachedViewer != null) {
			viewers.add(cachedViewer);
		}
		return viewers;
	}

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
