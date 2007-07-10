/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.ide.ui.actions;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.mylyn.internal.ide.ui.MarkerViewLabelProvider;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.views.markers.internal.TableView;
import org.eclipse.ui.views.markers.internal.TableViewLabelProvider;

/**
 * @author Mik Kersten
 */
public class FocusProblemsListAction extends AbstractFocusMarkerViewAction {

	/**
	 * HACK: changing accessibility
	 */
	@Override
	public List<StructuredViewer> getViewers() {
		List<StructuredViewer> viewers = new ArrayList<StructuredViewer>();
		if (cachedViewer == null) {
			try {
				IViewPart viewPart = super.getPartForAction();
				if (viewPart != null) {
					Class<?> infoClass = TableView.class;// problemView.getClass();
					Method method = infoClass.getDeclaredMethod("getViewer", new Class[] {});
					method.setAccessible(true);
					cachedViewer = (StructuredViewer) method.invoke(viewPart, new Object[] {});
					if (!cachedViewer.getControl().isDisposed()) {
						updateMarkerViewLabelProvider(cachedViewer);
					}
				}
			} catch (Exception e) {
				StatusHandler.log(e, "couldn't get problems view viewer");
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
