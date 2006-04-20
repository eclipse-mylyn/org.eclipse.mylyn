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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.ide.ui.MarkerViewLabelProvider;
import org.eclipse.mylar.internal.ide.ui.ProblemsListDoiSorter;
import org.eclipse.mylar.internal.ide.ui.MarkerInterestFilter;
import org.eclipse.mylar.internal.ui.actions.AbstractApplyMylarAction;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.views.markers.internal.TableView;
import org.eclipse.ui.views.markers.internal.TableViewLabelProvider;
import org.eclipse.ui.views.markers.internal.TaskView;

/**
 * @author Mik Kersten
 */
public class ApplyMylarToMarkerTasksAction extends AbstractApplyMylarAction {

	private static ApplyMylarToMarkerTasksAction INSTANCE;
	
	private static final String TARGET_ID = "org.eclipse.ui.views.TaskList";

	private StructuredViewer cachedTasksViewer = null;

	private ProblemsListDoiSorter interestSorter = new ProblemsListDoiSorter();

	public ApplyMylarToMarkerTasksAction() {
		super(new MarkerInterestFilter());
		INSTANCE = this;
	}

	/**
	 * HACK: changing accessibility
	 */
	@Override
	public List<StructuredViewer> getViewers() {
		List<StructuredViewer> viewers = new ArrayList<StructuredViewer>();
		if (cachedTasksViewer == null) {
			try {
				IViewPart viewPart = getView(TARGET_ID);
				if (viewPart instanceof TaskView) {
					Class infoClass = TableView.class;
					Method method = infoClass.getDeclaredMethod("getViewer", new Class[] {});
					method.setAccessible(true);
					cachedTasksViewer = (StructuredViewer) method.invoke(viewPart, new Object[] {});
					updateMarkerViewLabelProvider(cachedTasksViewer);
				} 
			} catch (Exception e) {
				MylarStatusHandler.log(e, "couldn't get problmes viewer");
			}
		}
		if (cachedTasksViewer != null)
			viewers.add(cachedTasksViewer);
		return viewers;
	}

	protected void updateMarkerViewLabelProvider(StructuredViewer viewer) {
		IBaseLabelProvider currentProvider = viewer.getLabelProvider();
		if (!(currentProvider instanceof MarkerViewLabelProvider)) {
			viewer.setLabelProvider(new MarkerViewLabelProvider((TableViewLabelProvider) currentProvider));
		}
	}

	public static ApplyMylarToMarkerTasksAction getDefault() {
		return INSTANCE;
	}

	@Override
	public void update() {
		super.update();
		cachedTasksViewer = null;
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

	public void propertyChange(PropertyChangeEvent event) {
		// ignore
	}

	@Override
	protected boolean installInterestFilter(final StructuredViewer viewer) {
		super.installInterestFilter(viewer);
		return true;
	}

	@Override
	protected void uninstallInterestFilter(StructuredViewer viewer) {
		super.uninstallInterestFilter(viewer);
	}

	protected void verifySorterInstalled(StructuredViewer viewer) {
		if (viewer != null && viewer.getSorter() != interestSorter) {
			viewer.setSorter(interestSorter);
		}
	}
	
	@Override
	public List<Class> getPreservedFilters() {
		return Collections.emptyList();
	}
}
