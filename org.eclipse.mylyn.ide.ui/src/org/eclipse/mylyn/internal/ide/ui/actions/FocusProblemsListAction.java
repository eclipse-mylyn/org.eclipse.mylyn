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
				if (tableViewer != null && !(tableViewer.getLabelProvider() instanceof MarkerViewLabelProvider)) {
					tableViewer.setLabelProvider(new MarkerViewLabelProvider((TableViewLabelProvider) tableViewer
							.getLabelProvider()));
				}
			}
		}
	}
	
//	protected void updateMarkerViewLabelProvider(StructuredViewer viewer) {
//		IBaseLabelProvider currentProvider = viewer.getLabelProvider();
//		if (currentProvider instanceof TableViewLabelProvider && !(currentProvider instanceof MarkerViewLabelProvider)) {
//			viewer.setLabelProvider(new MarkerViewLabelProvider((TableViewLabelProvider) currentProvider));
//		}
//	}

//	private MarkerFilter defaultFilter = null;

//	private MarkerViewerInterestSorter interestSorter = new MarkerViewerInterestSorter();
	
//	public void propertyChange(PropertyChangeEvent event) {
//		// ignore
//	}

//	@Override
//	protected boolean installInterestFilter(final StructuredViewer viewer) {
//		super.installInterestFilter(viewer);
////		toggleMarkerFilter(false);
//		return true;
//	}
//
//	@Override
//	protected void uninstallInterestFilter(StructuredViewer viewer) {
//		super.uninstallInterestFilter(viewer);
////		toggleMarkerFilter(true);
//	}

//	/**
//	 * HACK: using reflection to gain accessibility
//	 */
//	protected void toggleMarkerFilter(boolean enabled) {
//		try {
//			IViewPart view = super.getPartForAction();
//			if (view instanceof ProblemView) {
//				Class viewClass = view.getClass();
//
//				try {
//					// 3.1 way of removing existing filter
//					Field problemFilter = viewClass.getDeclaredField("problemFilter");
//					problemFilter.setAccessible(true);
//					defaultFilter = (MarkerFilter) problemFilter.get(view);
//
//					Class filterClass = defaultFilter.getClass().getSuperclass();
//					Method method = filterClass.getDeclaredMethod("setEnabled", new Class[] { boolean.class });
//					method.setAccessible(true);
//					method.invoke(defaultFilter, new Object[] { enabled });
//					Method refresh = view.getClass().getSuperclass().getDeclaredMethod("refresh", new Class[] {});
//					refresh.setAccessible(true);
//					refresh.invoke(view, new Object[] {});
//				} catch (NoSuchFieldException nfe) {
//					// 3.2 way
//				}
//			}
//		} catch (Exception e) {
//			MylarStatusHandler.fail(e, "Couldn't toggle problem filter (not yet supported on Eclipse 3.2)", false);
//		}
//	}

//	protected void verifySorterInstalled(StructuredViewer viewer) {
//		if (viewer != null && viewer.getSorter() != interestSorter) {
//			viewer.setSorter(interestSorter);
//		}
//	}
}
