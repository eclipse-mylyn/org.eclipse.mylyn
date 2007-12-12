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
public class FocusBookmarkMarkersViewAction extends AbstractFocusMarkerViewAction {

	public FocusBookmarkMarkersViewAction() {
		super();
	}
	
//	@Override
//	public List<StructuredViewer> getViewers() {
//		List<StructuredViewer> viewers = new ArrayList<StructuredViewer>();
//		if (cachedViewer == null) {
//			try {
//				IViewPart viewPart = super.getPartForAction();
//				System.err.println(">>> " + viewPart.getClass());
//				if (viewPart instanceof  BookmarkView) {
//					Class<?> infoClass = TableView.class;
//					Method method = infoClass.getDeclaredMethod("getViewer", new Class[] {});
//					method.setAccessible(true);
//					cachedViewer = (StructuredViewer) method.invoke(viewPart, new Object[] {});
//					updateMarkerViewLabelProvider(cachedViewer);
//				}
//			} catch (Exception e) {
//				StatusHandler.log(e, "couldn't get bookmarks view viewer");
//			}
//		}
//		if (cachedViewer != null)
//			viewers.add(cachedViewer);
//		return viewers;
//	}
}
