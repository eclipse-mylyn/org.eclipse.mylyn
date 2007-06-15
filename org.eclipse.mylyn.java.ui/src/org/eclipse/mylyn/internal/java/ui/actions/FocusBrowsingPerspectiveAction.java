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

package org.eclipse.mylyn.internal.java.ui.actions;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.mylyn.context.ui.InterestFilter;
import org.eclipse.mylyn.internal.context.ui.actions.AbstractFocusViewAction;
import org.eclipse.mylyn.internal.monitor.core.util.StatusManager;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * This class is a bit weird since it doesn't obey the same contract as the
 * other subclasses
 * 
 * @author Shawn Minto
 * @author Mik Kersten
 */
public class FocusBrowsingPerspectiveAction extends AbstractFocusViewAction implements
		IWorkbenchWindowActionDelegate {

	private String packageViewerWrapperClassName = "org.eclipse.jdt.internal.ui.browsing.PackageViewerWrapper";

	private String[] viewNames = { "org.eclipse.jdt.ui.MembersView", "org.eclipse.jdt.ui.PackagesView",
			"org.eclipse.jdt.ui.TypesView" };

	private String[] classNames = { "org.eclipse.jdt.internal.ui.browsing.MembersView",
			"org.eclipse.jdt.internal.ui.browsing.PackagesView", "org.eclipse.jdt.internal.ui.browsing.TypesView" };

	private IWorkbenchWindow initWindow;
	
	public FocusBrowsingPerspectiveAction() {
		super(new InterestFilter(), true, true, false);
		globalPrefId = PREF_ID_PREFIX + "javaBrowsing";
	}
	
	public void init(IWorkbenchWindow window) {
		initWindow = window;
		IWorkbenchPage activePage = initWindow.getActivePage();
		super.viewPart = activePage.findView(viewNames[0]);
	}

	@Override
	public List<StructuredViewer> getViewers() {
		List<StructuredViewer> viewers = new ArrayList<StructuredViewer>();
		for (int i = 0; i < viewNames.length; i++) {
			StructuredViewer viewer = getBrowsingViewerFromActivePerspective(viewNames[i], classNames[i]);
			if (viewer != null)
				viewers.add(viewer);
		}
		return viewers;
	}

	/**
	 * HACK: using reflection
	 */
	private StructuredViewer getBrowsingViewerFromActivePerspective(String id, String className) {
		IWorkbenchPage activePage = initWindow.getActivePage();
		if (activePage == null)
			return null;
		try {
			IViewPart viewPart = activePage.findView(id);
			Class<?> sub = Class.forName(className);

			if (sub.isInstance(viewPart)) {
				IViewPart view = viewPart;
				if (view != null) {
					try {
						Class<?> clazz = sub.getSuperclass();
						Method method = clazz.getDeclaredMethod("getViewer", new Class[] {});
						method.setAccessible(true);

						// TODO: weird since the packagesView uses a viewer that
						// wraps another viewer
						if (id.compareTo("org.eclipse.jdt.ui.PackagesView") != 0) {
							return (StructuredViewer) method.invoke(sub.cast(view), new Object[] {});
						} else {
							StructuredViewer viewer = (StructuredViewer) method.invoke(sub.cast(view), new Object[] {});
							if (viewer != null
									&& viewer.getClass().getCanonicalName().compareTo(packageViewerWrapperClassName) == 0) {
								clazz = viewer.getClass();
								method = clazz.getDeclaredMethod("getViewer", new Class[] {});
								method.setAccessible(true);
								return (StructuredViewer) method.invoke(viewer, new Object[] {});
							} else {
								return viewer;
							}
						}
					} catch (Exception e) {
						StatusManager.log(e, "couldn't get " + id + " view tree viewer");
						return null;
					}
				} else {
					return null;
				}

			}
		} catch (Exception e) {
			StatusManager.log(e, "couldn't get " + id + " view tree viewer");
		}
		return null;
	}

	public void propertyChange(PropertyChangeEvent event) {
		// TODO Auto-generated method stub
	}

}
