/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/*
 * Created on May 6, 2005
 */
package org.eclipse.mylyn.internal.ide.ui;

import java.lang.reflect.Method;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.ide.ui.AbstractMarkerInterestFilter;
import org.eclipse.ui.views.markers.MarkerItem;

/**
 * @author Mik Kersten
 */
public class MarkerInterestFilter extends AbstractMarkerInterestFilter {

	@Override
	public boolean select(Viewer viewer, Object parent, Object element) {

		if (element instanceof MarkerItem) {
			if (element.getClass().getSimpleName().equals("MarkerCategory")) {

				// HACK: using reflection to gain accessibily
				Class<?> clazz;
				try {
					clazz = Class.forName("org.eclipse.ui.internal.views.markers.MarkerCategory");
					Method method = clazz.getDeclaredMethod("getChildren", new Class[] {});
					method.setAccessible(true);
					Object result = method.invoke(element, new Object[] {});
					System.err.println(">>>>>> " + result.getClass());
				} catch (Exception e) {
					e.printStackTrace();
				}

				return true;
			} else if (element.getClass().getSimpleName().equals("MarkerEntry")) {
				return isInteresting(((MarkerItem) element).getMarker(), viewer, parent);
			}
		}

		return false;
//			return true;
		// NOTE: code commented out below did a look-down the children, which may be too expensive
//			if (element instanceof MarkerNode) {
//				MarkerNode markerNode = (MarkerNode) element;
//				MarkerNode[] children = markerNode.getChildren();
//				for (int i = 0; i < children.length; i++) {
//					MarkerNode node = children[i];
//					if (node instanceof ConcreteMarker) {
//						return isInteresting((ConcreteMarker) node, viewer, parent);
//					} else {
//						return true;
//					}
//				}
//			}
//		} else {
//			ConcreteMarker marker = (ConcreteMarker) element;
//			return isInteresting((ConcreteMarker) element, viewer, parent);
//		}
	}

	@Override
	protected boolean isImplicitlyInteresting(IMarker marker) {
		try {
			Object severity = marker.getAttribute(IMarker.SEVERITY);
			return severity != null && severity.equals(IMarker.SEVERITY_ERROR);
		} catch (CoreException e) {
			// ignore
		}
		return false;
	}
}
