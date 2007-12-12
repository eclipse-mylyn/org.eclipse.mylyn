/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.ide.ui.actions;


/**
 * @author Mik Kersten
 */
public class FocusTaskMarkersViewAction extends AbstractFocusMarkerViewAction {

	public FocusTaskMarkersViewAction() {
		super();
	}
	
//	/**
//	 * HACK: changing accessibility
//	 */
//	@Override
//	public List<StructuredViewer> getViewers() {
//		List<StructuredViewer> viewers = new ArrayList<StructuredViewer>();
//		if (cachedViewer == null) {
//			try {
//				IViewPart viewPart = super.getPartForAction();
//				if (viewPart instanceof TaskView) {
//					Class<?> infoClass = TableView.class;
//					Method method = infoClass.getDeclaredMethod("getViewer", new Class[] {});
//					method.setAccessible(true);
//					cachedViewer = (StructuredViewer) method.invoke(viewPart, new Object[] {});
//					updateMarkerViewLabelProvider(cachedViewer);
//				}
//			} catch (Exception e) {
//				StatusHandler.log(e, "couldn't get task view list viewer");
//			}
//		}
//		if (cachedViewer != null)
//			viewers.add(cachedViewer);
//		return viewers;
//	}

//	protected void updateMarkerViewLabelProvider(StructuredViewer viewer) {
//		IBaseLabelProvider currentProvider = viewer.getLabelProvider();
//		if (currentProvider instanceof TableViewLabelProvider && !(currentProvider instanceof MarkerViewLabelProvider)) {
//			viewer.setLabelProvider(new MarkerViewLabelProvider((TableViewLabelProvider) currentProvider));
//		}
//	}

//	public void propertyChange(PropertyChangeEvent event) {
//		// ignore
//	}

//	@Override
//	protected boolean installInterestFilter(final StructuredViewer viewer) {
//		super.installInterestFilter(viewer);
//		return true;
//	}
//
//	@Override
//	protected void uninstallInterestFilter(StructuredViewer viewer) {
//		super.uninstallInterestFilter(viewer);
//	}

//	@Override
//	public List<Class> getPreservedFilters() {
//		return Collections.emptyList();
//	}
}
